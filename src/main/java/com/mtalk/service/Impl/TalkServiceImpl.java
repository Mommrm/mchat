package com.mtalk.service.Impl;

import com.mtalk.entity.Result;
import com.mtalk.entity.SingleMessage;
import com.mtalk.mapper.RelationMapper;
import com.mtalk.service.ITalkService;
import com.mtalk.utils.LocalUser;
import com.mtalk.webSocket.netty.utils.SingleChatUtils;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;



@Service
public class TalkServiceImpl implements ITalkService {
    @Resource
    private RelationMapper relationMapper;
    @Resource
    private SingleChatUtils singleChatUtils;
    @Override
    public Result createSingleChat(String talkId) {
        String myId = LocalUser.getLocalUser().getUserId();
        // 数据库中的groupId是 添加好友后就自动生成的 互为好友的私聊通道是独立的
        String groupId = relationMapper.getGroupByUserId(talkId, myId);
        if(groupId == null || groupId.isEmpty()){
            return new Result("你们还不是好友关系");
        }
        // 根据groupId获取对应的ChannelGroup
        ChannelGroup group = singleChatUtils.CreateSingleChat(groupId);
        return new Result("私聊通道建立完毕");
    }

    @Override
    public Result sentSingleMessage(SingleMessage singleMessage) {
        String to = singleMessage.getTo();
        String from = singleMessage.getFrom();
        ChannelGroup singleGroup = singleChatUtils.GetGroupByGroupId(singleMessage.combineToGroupId(from,to));
        // 向组里发送消息
        singleGroup.writeAndFlush(new TextWebSocketFrame(singleMessage.getMessage()));
        return new Result("发送成功");
    }
}
