package com.mtalk.service.Impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.mtalk.entity.Result;
import com.mtalk.entity.Relation;
import com.mtalk.entity.User;
import com.mtalk.mapper.RelationMapper;
import com.mtalk.mapper.UserMapper;
import com.mtalk.utils.ChannelGroupUtil;
import com.mtalk.utils.LocalUser;
import com.mtalk.utils.constant.CodeConstant;
import com.mtalk.service.IUserService;
import com.mtalk.utils.constant.UserConstant;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.mtalk.utils.constant.CodeConstant.*;
import static com.mtalk.utils.constant.RedisConstant.*;
import static com.mtalk.utils.constant.UserConstant.*;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RelationMapper relationMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ChannelGroupUtil channelGroupUtil;

    @Override
    public Result RegisterUser(User user) {
        String token = RandomUtil.randomString(20);
        // 判断是否有重复用户名已经注册
        if(userMapper.SelectByUser(user.getUserName(),user.getUserPassword()) != null){
            return new Result("该用户已经注册,请去登录!", CodeConstant.REPEAT_CODE);
        }
        // 创建用户并返回登录token，注册成功直接登录
        if (!createUser(user,token)) {
            return new Result("注册失败!", CodeConstant.WORSE_CODE);
        }
        return new Result(token);
    }

    @Override
    public Result LandUser(String userName, String userPassword) throws URISyntaxException {
        String token = RandomUtil.randomString(20); // 生成一个随机token值
        // 简单实现即可
        // 去数据库查找
        User user = userMapper.SelectByUser(userName,userPassword);
        if (user != null) {
            LocalUser.setLocalUser(user);
            //查找出用户,Redis进行缓存
            String json = JSONUtil.toJsonStr(user);
            stringRedisTemplate.opsForValue().set(USER_CACHE_KEY + token,json,USER_CACHE_TIME, TimeUnit.DAYS);
            // 直接返回该token
            return new Result(token);
        }
        return new Result("该用户未注册,请去注册后再试!", CodeConstant.NOT_CODE);
    }

    /**
     * 发送好友申请请求
     * @param userId 对方id
     * @param message 请求消息
     * @return
     */
    @Override
    public Result AddFriend(String userId, String message) {
        String myId = LocalUser.getLocalUser().getUserId();
        if(userId == null || userId.isEmpty())
            return new Result("userId不存在", NOT_CODE);
        if(myId == null || myId.isEmpty())
            return new Result("myId不存在", NOT_CODE);
        // 存往Redis 保存7天
        // 对方id为Rediskey 自己id为发送请求key，值为请求消息 对方查找时可以通过自己id去查找好友申请
        // 同意请求把请求存入MySQL中
        // 无论是否同意，处理了都要把Redis中数据销毁，表明已经处理了该请求
        stringRedisTemplate.opsForHash().put(MESSAGE_FRIEND_KEY + userId,myId,message);
        stringRedisTemplate.expire(MESSAGE_FRIEND_KEY + userId, MESSAGE_CACHE_TIME,TimeUnit.DAYS);
        return new Result("消息发送成功");
    }

    /**
     * 获取用户的好友请求列表
     * @return
     */
    @Override
    public Result getFriendRequestList() {
        String myId = LocalUser.getLocalUser().getUserId();
        Map<Object, Object> requestList = stringRedisTemplate.opsForHash().entries(MESSAGE_FRIEND_KEY + myId);
        return new Result(requestList);
    }


    /**
     * 处理好友请求
     * @return
     */
    @Override
    public Result handleRelation(Relation relation) {
        String myId = relation.getFriendId(); // friendId是相对于userId而言的好友
        String userId = relation.getUserId(); // userId是发起人
        String handleCode = relation.getHandleCode();
        relation.setGroupId(userId + "-" + myId);
        // 检测是否已经是好友
        List<Relation> relations = relationMapper.checkRelation(myId);
        if(relations.size() > 0){
            return new Result("已经是好友,不要重复添加");
        }
        // 同意好友请求 应该双向都建立好友关系 也就是执行两条sql语句
        if(AGREE_FRIEND.equals(handleCode)){
            // 存入关系表
            createBothFriend(relation);
            // 删除好友请求
            stringRedisTemplate.opsForHash().delete(MESSAGE_FRIEND_KEY + myId, userId);
            return new Result("同意好友请求");
        }
        if(DISAGREE_FRIEND.equals(handleCode)){
            stringRedisTemplate.opsForHash().delete(MESSAGE_FRIEND_KEY + myId, userId);
            return new Result(myId + "不同意好友请求");
        }
        return new Result("错误的处理");
    }

    // 删除好友关系 双向删除
    @Override
    public Result DeleteFriend(String friendId) {
        String myId = LocalUser.getLocalUser().getUserId();
        userMapper.DeleteRelation(friendId,myId);
        userMapper.DeleteRelation(myId,friendId);
        return new Result("删除完毕");
    }
    // 获取好友列表 根据用户id
    @Override
    public Result GetFriendList() {
        String myId = LocalUser.getLocalUser().getUserId();
        if (myId.isEmpty()) {
            return new Result("空的ID值");
        }
        List<Relation> relations = relationMapper.GetFriendList(myId);
        if (relations == null){
            return new Result("错误的ID值");
        }
        return new Result(relations);
    }

    @Override
    public Result getFriendInfo(String friendId) {
        if(friendId == null || friendId.isEmpty()){
            return new Result("参数错误");
        }
        User friend = userMapper.getUserById(friendId);
        return new Result(friend);
    }

    // 创建用户并保存登录状态
    private boolean createUser(User user,String token){
        String userId = String.valueOf(UUID.randomUUID());
        String avatar = DEFAULT_AVATAR_PATH;
        String userBrief = USER_BRIEF;
        Timestamp userJoinTime = UserConstant.getJoinTime();
        //填入用户值
        user.setUserId(userId);
        user.setUserAvatar(avatar);
        user.setUserBrief(userBrief);
        user.setUserJoinTime(userJoinTime);
        String userJson = JSONUtil.toJsonStr(user);
        // 创建用户保存登录状态 执行需要登录的请求时可以根据token拿取用户信息
        stringRedisTemplate.opsForValue().set(USER_CACHE_KEY + token,userJson,USER_CACHE_TIME, TimeUnit.DAYS);
        userMapper.CreateUser(user);
        return true;
    }
    // 双向建立好友关系 发起人是userId 接收者是friendId，对于接收者也是userId，发起人是friendId 对应的Name也是如此
    private synchronized void createBothFriend(Relation relation){
        String groupId = relation.getFriendId() + "-" + relation.getUserId();
        Relation relationForFriend = new Relation(
                relation.getUserName(),
                relation.getUserId(),
                relation.getFriendName(),
                relation.getFriendId(),
                groupId,
                relation.getHandleCode(),
                USER_AVATAR
                );

        relationMapper.createRelation(relation); // groupId 是 发起人和接收者的拼接
        relationMapper.createRelation(relationForFriend); // groupId 是 接收者和发起人的拼接
    }

}
