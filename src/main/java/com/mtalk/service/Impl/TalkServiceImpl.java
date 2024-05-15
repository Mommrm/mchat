package com.mtalk.service.Impl;

import com.mtalk.entity.ChatGroup;
import com.mtalk.entity.GroupMessage;
import com.mtalk.entity.Result;
import com.mtalk.entity.SingleMessage;
import com.mtalk.mapper.GroupMapper;
import com.mtalk.mapper.RelationMapper;
import com.mtalk.service.ITalkService;
import com.mtalk.utils.LocalUser;
import com.mtalk.webSocket.netty.utils.GroupChatUtils;
import com.mtalk.webSocket.netty.utils.SingleChatUtils;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.mtalk.utils.constant.CodeConstant.NOT_CODE;


@Service
public class TalkServiceImpl implements ITalkService {
    @Resource
    private RelationMapper relationMapper;
    @Resource
    private GroupMapper groupMapper;

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
    @Override
    public Result sentSingleMessage(SingleMessage singleMessage) {
        String to = singleMessage.getTo();
        String from = singleMessage.getFrom();
        // 获取两个用户专属的ChannelGroup Netty服务器来转发消息
        ChannelGroup singleGroup = SingleChatUtils.GetGroupByGroupId(singleMessage.combineToGroupId(from,to));
        // 向组里发送消息
        singleGroup.writeAndFlush(new TextWebSocketFrame(singleMessage.getMessage()));
        return new Result("发送成功");
    }
    @Override
    public Result createGroupChat(String groupId, String leaderId) {
        ChatGroup chatGroup = groupMapper.SearchGroupById(groupId);
        if (chatGroup == null) {
            return new Result("未知群组",NOT_CODE);
        }
        ChannelGroup group = GroupChatUtils.CreateGroupChat(groupId, leaderId);
        // 这里还未添加群组中其他的成员
        return new Result("群聊通道建立完毕");
    }
    @Override
    public Result sentGroupMessage(GroupMessage groupMessage) {
        String groupId = groupMessage.getGroupId();
        String memberId = groupMessage.getMemberId();
        String myId = LocalUser.getLocalUser().getUserId();
        // 校验当前登录用户是否和发送消息者id一致
        if (!myId.equals(memberId)) {
            return new Result("未登录无法发送消息");
        }
        return new Result("发送成功");
    }


}
