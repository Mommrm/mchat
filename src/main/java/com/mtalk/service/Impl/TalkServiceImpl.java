package com.mtalk.service.Impl;

import cn.hutool.json.JSONUtil;
import com.mtalk.entity.*;
import com.mtalk.mapper.GroupMapper;
import com.mtalk.mapper.GroupMemberMapper;
import com.mtalk.mapper.MessageMapper;
import com.mtalk.mapper.RelationMapper;
import com.mtalk.schedule.GroupMsgPersistenceTask;
import com.mtalk.schedule.SingleMsgPersistenceTask;
import com.mtalk.service.ITalkService;
import com.mtalk.utils.LocalUser;
import com.mtalk.webSocket.netty.utils.GroupChatUtils;
import com.mtalk.webSocket.netty.utils.SingleChatUtils;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mtalk.utils.constant.CodeConstant.NOT_CODE;
import static com.mtalk.utils.constant.RedisConstant.*;


@Service
public class TalkServiceImpl implements ITalkService {
    @Resource
    private RelationMapper relationMapper;
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private GroupMemberMapper groupMemberMapper;
    @Resource
    private GroupMsgPersistenceTask groupMsgPersistenceTask;
    @Resource
    private SingleMsgPersistenceTask singleMsgPersistenceTask;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result createSingleChat(String talkId) {
        String myId = LocalUser.getLocalUser().getUserId();
        // 数据库中的groupId是 添加好友后就自动生成的 互为好友的私聊通道是独立的
        String groupId = relationMapper.getGroupByUserId(talkId, myId);
        if(groupId == null || groupId.isEmpty()){
            return new Result("你们还不是好友关系");
        }
        // 根据groupId获取对应的ChannelGroup
        ChannelGroup group = SingleChatUtils.CreateSingleChat(groupId);
        return new Result("私聊通道建立完毕");
    }
    // 创建ChannelGroup对象
    @Override
    public Result createGroupChat(String groupId) {
        // 查看是否已经创建群组
        ChatGroup chatGroup = groupMapper.SearchGroupById(groupId);
        String myId = LocalUser.getLocalUser().getUserId();
        if (chatGroup == null) {
            return new Result("未知群组",NOT_CODE);
        }
        GroupMember groupMember = groupMemberMapper.SearchMemberById(groupId, myId);
        if (groupMember == null) {
            return new Result("不是群组中的成员",NOT_CODE);
        }
        // 将当前登录用户的Id对应的Channel加入到对应groupId的ChannelGroup中
        GroupChatUtils.addChannelToGroupsById(groupId,myId);
        // 这里还未添加群组中其他的成员
        return new Result("群聊通道建立完毕");
    }

    @Override
    public Result sentSingleMessage(SingleMessage singleMessage) {
        String to = singleMessage.getTo();
        String from = LocalUser.getLocalUser().getUserId();
        String groupId = singleMessage.combineToSingleGroupId(from, to);
        // 获取两个用户专属的ChannelGroup Netty服务器来转发消息
        ChannelGroup singleGroup = SingleChatUtils.GetGroupByGroupId(groupId);
        // 向组里发送消息
        singleGroup.writeAndFlush(new TextWebSocketFrame(singleMessage.getMessage()));

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        singleMessage.setGroupId(groupId);
        singleMessage.setSentTime(now);
        singleMessage.setFrom(from);
        singleMessage.setChatType(Message.SINGLE);

        String jsonStr = JSONUtil.toJsonStr(singleMessage);
        stringRedisTemplate.opsForHash().put(CHAT_SINGLE_KEY + groupId,now.toString(),jsonStr);
        stringRedisTemplate.expire(CHAT_SINGLE_KEY + groupId,CHAT_CACHE_TIME, TimeUnit.DAYS);

        singleMsgPersistenceTask.startSaveSingleHistory(groupId);
        return new Result("发送成功");
    }

    // 根据发送消息体中的groupId获取到ChannelGroup来发送数据
    @Override
    public Result sentGroupMessage(GroupMessage groupMessage) {
        String groupId = groupMessage.getGroupId();
        String memberId = groupMessage.getMemberId();
        Timestamp time = groupMessage.getSentTime();
        String myId = LocalUser.getLocalUser().getUserId();
        String message = groupMessage.getMessage();
        // 校验当前登录用户是否和发送消息者id一致
        if (!myId.equals(memberId)) {
            return new Result("未登录无法发送消息");
        }
        // 获取ChannelGroup 来发送对应的消息
        ChannelGroup group = GroupChatUtils.GetGroupByGroupId(groupId);
        group.writeAndFlush(new TextWebSocketFrame(time + " : " + message)); // 发送消息

        // 保存消息到Redis中(保存一天) 然后等待2s作用一起备份到数据库
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        groupMessage.setSentTime(now);
        groupMessage.setChatType(Message.GROUP);
        groupMessage.setSenderName(groupMessage.getMemberName());

        String jsonStr = JSONUtil.toJsonStr(groupMessage);
        stringRedisTemplate.opsForHash().put(CHAT_GROUP_KEY + groupId,now.toString(),jsonStr);
        stringRedisTemplate.expire(CHAT_GROUP_KEY + groupId,CHAT_CACHE_TIME, TimeUnit.DAYS);

        // 定时任务 持久化到数据库(当连接到WebSocket时，读取对应消息，并转发到对应用户中)
        groupMsgPersistenceTask.startSaveGroupHistory(groupId);

        return new Result("发送成功");
    }

    public Result getGroupHistory(String chatType, String groupId){
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        List<Message> historyMessage = messageMapper.SearchHistoryByGroupId(chatType, groupId, now);
        return new Result(historyMessage);
    }

    public Result getSingleHistory(String chatType, String to) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        String myId = LocalUser.getLocalUser().getUserId();
        String myGroupId = myId + "-" + to;
        String toGroupId = to + "-" + myId;
        List<Message> historyMessage = messageMapper.SearchHistoryByGroupId(chatType, myGroupId, now);
        List<Message> temp = messageMapper.SearchHistoryByGroupId(chatType, toGroupId, now);
        historyMessage.addAll(temp);
        return new Result(historyMessage);
    }


}
