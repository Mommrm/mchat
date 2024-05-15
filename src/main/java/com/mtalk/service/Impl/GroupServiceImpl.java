package com.mtalk.service.Impl;

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
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mtalk.entity.GroupMember.*;
import static com.mtalk.utils.constant.CodeConstant.*;
import static com.mtalk.utils.constant.RedisConstant.*;
@Service
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
        String groupId = createNumId(12);
        chatGroup.setGroupId(groupId);
        String leaderId = chatGroup.getLeaderId();
        if(groupMapper.SearchGroupByLeaderId(leaderId).size() >= 10){
            return new Result("您创建的群已经到达上限", NOMAL_WORSE_CODE);
        }
        if (!LocalUser.getLocalUser().getUserId().equals(leaderId)){
            return new Result("该用户未登录，不能创建群组", NOMAL_WORSE_CODE);
        }
        GroupMember groupMember = new GroupMember(groupId,LEADER_MEMBER, leaderId, chatGroup.getGroupLeader());

        if(!groupMapper.InsertGroup(chatGroup) || !groupMemberMapper.InsertMember(groupMember)){
            return new Result("创建失败", REPEAT_CODE);
        }
        return new Result("创建 " + groupId + " 群组成功");
    }
    @Override
    public Result InviteGroup(String groupId,String userId) {
        if (!checkArg(groupId))
            return new Result("未知错误", NOMAL_WORSE_CODE);
        String myId = LocalUser.getLocalUser().getUserId();
        // 判断邀请对象是否在邀请群组
        if(groupMemberMapper.SearchMemberById(groupId,userId) != null){
            return new Result("邀请对象已在当前群组,不能重复发送请求", REPEAT_CODE);
        }
        // 请求保存7天
        // 通过邀请对象id获取邀请请求，通过此获取到的groupId，和邀请人id来处理邀请请求
        stringRedisTemplate.opsForHash().put(INVITE_USER_KEY + userId,groupId,myId + ":邀请您入群");
        stringRedisTemplate.expire(userId,MESSAGE_CACHE_TIME, TimeUnit.DAYS);
        return new Result("邀请已发送");
    }

    @Override
    public Result ApplyGroup(String groupId,String message) {
        if (!checkArg(groupId))
            return new Result("未知错误", NOMAL_WORSE_CODE);
        String myId = LocalUser.getLocalUser().getUserId();
        // 证明该群已存在该用户 发送已在群消息
        if(groupMemberMapper.SearchMemberById(groupId,myId) != null){
            return new Result("已在当前群组,不能重复发送请求", REPEAT_CODE);
        }
        stringRedisTemplate.opsForHash().put(GROUP_APPLY_KEY + groupId,myId,message);
        stringRedisTemplate.expire(GROUP_APPLY_KEY + groupId,MESSAGE_CACHE_TIME, TimeUnit.DAYS);
        return new Result("发送成功");
    }

    @Override
    public Result BrokeGroup(String groupId) {
        if (!checkArg(groupId))
            return new Result("未知错误", NOMAL_WORSE_CODE);
        String myId = LocalUser.getLocalUser().getUserId();
        // 是群主 才能解散群组
        if (getMemberType(groupId,myId).equals(LEADER_MEMBER)) {
            groupMapper.BrokeGroup(groupId);
            groupMemberMapper.BrokeGroup(groupId);
            return new Result("解散成功");
        }
        return new Result("你无权解散该群组",NOT_POWER_CODE);
    }

    @Override
    public Result SearchGroupByGroupId(String groupId) {
        if (!checkArg(groupId))
            return new Result("未知错误",NOMAL_WORSE_CODE);
        ChatGroup chatGroup = groupMapper.SearchGroupById(groupId);
        if (chatGroup == null) {
            return new Result("搜索无结果",NOT_CODE);
        }
        return new Result(chatGroup);
    }

    @Override
    public Result ExitGroup(String groupId) {
        if (!checkArg(groupId))
            return new Result("未知错误",NOMAL_WORSE_CODE);
        String myId = LocalUser.getLocalUser().getUserId();
        // 判断用户身份
        String memberType = getMemberType(groupId,myId);
        if (memberType.equals(LEADER_MEMBER)){
            return new Result("群主无法退出",NOT_POWER_CODE);
        }
        groupMemberMapper.DeleteMember(groupId);
        return new Result("退出成功");
    }
    // 处理群申请消息
    @Override
    public Result HandleGroup(String groupId,String userId,String handleCode) {
        if (!checkArg(groupId))
            return new Result("未知错误",NOMAL_WORSE_CODE);
        String myId = LocalUser.getLocalUser().getUserId();
        // 判断用户身份
        String memberType = getMemberType(groupId,myId);
        // 不是管理员或者群主
        if (!memberType.equals(LEADER_MEMBER) || !memberType.equals(ADMIN_MEMBER)){
            new Result("权限不足",NOT_POWER_CODE);
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

    @Override
    public Result GetApplyListByGroupId(String groupId) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(GROUP_APPLY_KEY + groupId);
        if (entries == null){
            return new Result("没有任何申请", NOT_CODE);
        }
        return new Result(entries);
    }

    @Override
    public Result GetInviteListByGroupId() {
        String myId = LocalUser.getLocalUser().getUserId();
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(INVITE_USER_KEY + myId);
        if(entries == null){
            return new Result("没有邀请请求", NOT_CODE);
        }
        return new Result(entries);
    }
    // 处理当前登录用户的邀请请求
    @Override
    public Result HandleInvite(String groupId,String handleCode) {
        // 获取当前登录用户的信息
        String myId = LocalUser.getLocalUser().getUserId();
        String myName = LocalUser.getLocalUser().getUserName();
        // 同意请求
        if(handleCode.equals("1")){
            GroupMember member = new GroupMember(groupId,NORMAL_MEMBER,myId,myName);
            // 把当前用户加入群组
            groupMemberMapper.InsertMember(member);
        }
        //不同意请求
        if(handleCode.equals("2")){
            stringRedisTemplate.opsForValue().set(GROUP_MESSAGE_KEY + groupId,myName + " " + myId + "拒绝了邀请请求");
        }
        //不管是否同意邀请请求 处理后就要删除该邀请请求
        stringRedisTemplate.delete(INVITE_USER_KEY + myId);
        return new Result("处理完毕");
    }

    private String createNumId(int length){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<length-1;i++){
            if(i == 0){
                sb.append(random.nextInt(5) + 1);
                continue;
            }
            sb.append(random.nextInt(10));
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
