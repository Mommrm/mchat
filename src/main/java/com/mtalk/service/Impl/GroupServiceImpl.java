package com.mtalk.service.Impl;

import cn.hutool.core.annotation.MirrorFor;
import cn.hutool.core.util.RadixUtil;
import cn.hutool.core.util.RandomUtil;
import com.mtalk.entity.ChatGroup;
import com.mtalk.entity.GroupMember;
import com.mtalk.entity.Result;
import com.mtalk.entity.User;
import com.mtalk.mapper.GroupMapper;
import com.mtalk.mapper.GroupMemberMapper;
import com.mtalk.mapper.UserMapper;
import com.mtalk.service.IGroupService;
import com.mtalk.utils.LocalUser;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.TimeUnit;

import static com.mtalk.entity.GroupMember.*;
import static com.mtalk.utils.constant.RedisConstant.*;

public class GroupServiceImpl implements IGroupService {

    @Resource
    private GroupMapper groupMapper;
    @Resource
    private GroupMemberMapper groupMemberMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserMapper userMapper;

    @Override
    public Result CreateGroup(ChatGroup chatGroup) {
        // chatgroup信息
        String groupId = createNumId(10);
        chatGroup.setGroupId(groupId);
        if (LocalUser.getLocalUser().getUserId() != chatGroup.getLeaderId()){
            return new Result("该用户未登录，不能创建群组");
        }
        GroupMember groupMember = new GroupMember(groupId,LEADER_MEMBER, chatGroup.getLeaderId(), chatGroup.getGroupLeader());

        if(groupMapper.InsertGroup(chatGroup) && groupMemberMapper.InsertMember(groupMember)){
            return new Result("创建失败");
        }
        return new Result("创建 " + groupId + " 群组成功");
    }

    @Override
    public Result InviteGroup(String groupId,String userId) {
        if (!checkArg(groupId))
            return new Result("未知错误");
        String myId = LocalUser.getLocalUser().getUserId();
        // 请求保存7天
        stringRedisTemplate.opsForHash().put(userId,groupId,myId + ":邀请您入群");
        stringRedisTemplate.expire(userId,MESSAGE_CACHE_TIME, TimeUnit.DAYS);
        return new Result("邀请已发送");
    }

    @Override
    public Result ApplyGroup(String groupId,String message) {
        if (!checkArg(groupId))
            return new Result("未知错误");
        String myId = LocalUser.getLocalUser().getUserId();
        stringRedisTemplate.opsForHash().put(groupId,myId,message);
        stringRedisTemplate.expire(groupId,MESSAGE_CACHE_TIME, TimeUnit.DAYS);
        return null;
    }

    @Override
    public Result BrokeGroup(String groupId) {
        if (!checkArg(groupId))
            return new Result("未知错误");
        String myId = LocalUser.getLocalUser().getUserId();
        // 是群主 才能解散群组
        if (getMemberType(groupId,myId).equals(LEADER_MEMBER)) {
            groupMapper.BrokeGroup(groupId);
            return new Result("解散成功");
        }
        return new Result("你无权解散该群组");
    }

    @Override
    public Result SearchGroup(String groupId) {
        if (!checkArg(groupId))
            return new Result("未知错误");
        ChatGroup chatGroup = groupMapper.SearchGroupById(groupId);
        if (chatGroup == null) {
            return new Result("搜索无结果");
        }
        return new Result(chatGroup);
    }

    @Override
    public Result ExitGroup(String groupId) {
        if (!checkArg(groupId))
            return new Result("未知错误");
        String myId = LocalUser.getLocalUser().getUserId();
        // 判断用户身份
        String memberType = getMemberType(groupId,myId);
        if (memberType.equals(LEADER_MEMBER)){
            new Result("群主无法退出");
        }
        groupMemberMapper.DeleteMember(groupId);
        return new Result("退出成功");
    }
    // 处理群申请消息
    @Override
    public Result HandleGroup(String groupId,String userId,String handleCode) {
        if (!checkArg(groupId))
            return new Result("未知错误");
        String myId = LocalUser.getLocalUser().getUserId();
        // 判断用户身份
        String memberType = getMemberType(groupId,myId);
        // 不是管理员或者群主
        if (!memberType.equals(LEADER_MEMBER) || !memberType.equals(ADMIN_MEMBER)){
            new Result("权限不足");
        }
        // handleCode == 1 同意进入
        if (handleCode.equals("1")) {
            User user = userMapper.getUserById(userId);
            GroupMember groupMember = new GroupMember(groupId,NORMAL_MEMBER,userId,user.getUserName());
            groupMemberMapper.InsertMember(groupMember);
        }
        // handleCode == 2 不同意进入 并发送拒绝请求
        else if (handleCode.equals("2")) {
            stringRedisTemplate.opsForHash().delete(groupId,userId);
            stringRedisTemplate.opsForValue().set(USER_MESSAGE_KEY + userId,"groupId"+"拒绝了您的加入请求",USER_MESSAGE_CACHE_TIME,TimeUnit.DAYS);
        }
        return new Result("处理完毕");
    }

    private String createNumId(int length){
        int randomNum = RandomUtil.randomInt(1,5);
        StringBuilder sb = new StringBuilder(randomNum);
        for(int i=0;i<length-1;i++){
            randomNum = RandomUtil.randomInt();
            sb.append(randomNum);
        }
        return sb.toString();
    }

    private boolean checkArg(String groupId) {
        ChatGroup group = groupMapper.SearchGroupById(groupId);
        if(group == null){
            return false;
        }
        String myId = LocalUser.getLocalUser().getUserId();
        if(myId == null){
            return false;
        }
        return true;
    }

    private String getMemberType(String groupId,String memberId){
        GroupMember member = groupMemberMapper.SearchMemberById(groupId,memberId);
        String memberType = member.getMemberType();
        if (memberType == null || memberType.isEmpty()){
            return null;
        }
        return memberType;
    }
}
