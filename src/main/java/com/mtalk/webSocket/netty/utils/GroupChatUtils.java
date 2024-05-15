package com.mtalk.webSocket.netty.utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GroupChatUtils {
    private static final Logger logger = LoggerFactory.getLogger(GroupChatUtils.class);

    private static final Map<String, ChannelGroup> Groups = new HashMap<>();

    /**
     * 创建ChannelGroup
     * @param groupId
     * @param leaderId
     * @return
     */
    public static ChannelGroup CreateGroupChat(String groupId,String leaderId){
        // 判断当前groupId是否已经加入Map
        ChannelGroup channelGroup = Groups.get(groupId);
        // 根据用户id获取channel
        Channel channel = UserChannelUtil.getChannel(leaderId);
        // 用户不在线
        if (channel == null) {
            return null;
        }
        // 没有该群组 创建一个 并放入Groups
        if (channelGroup == null) {
            ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            group.add(channel); //把群组 创建者加入该channel群组
            Groups.put(groupId,group);
            return group;
        }
        channelGroup.add(channel);
        return channelGroup;
    }

    public static void addChannelToGroupsById(String groupId,String userId){
        Channel channel = UserChannelUtil.getChannel(userId);
        ChannelGroup group = GetGroupByGroupId(groupId);
        Groups.put(userId,group);
    }

    public static ChannelGroup GetGroupByGroupId(String groupId){
        ChannelGroup group = Groups.get(groupId);
        return group;
    }
}
