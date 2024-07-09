package com.mtalk.webSocket.netty.utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupChatUtils {
    private static final Logger logger = LoggerFactory.getLogger(GroupChatUtils.class);

    private static final Map<String, ChannelGroup> Groups = new HashMap<>();

    /**
     * 创建ChannelGroup并把群主的Channel放入ChannelGroup中(只能创建群的时候创建一次，不然会出现一个groupId对应多个ChannelGroup的情况)
     * @param groupId
     * @param leaderId
     * @return
     */
    public static ChannelGroup CreateGroupChat(String groupId,String leaderId){
        // 判断当前groupId是否已经加入Map
        ChannelGroup channelGroup = Groups.get(groupId);
        // 根据用户id获取channel
        Channel channel = UserChannelUtil.getChannel(leaderId);
        // 群主不在线
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
    // 把对应的用户Id加入到ChannelGroup中 single user situation
    public static void addChannelToGroupsById(String groupId,String userId){
        Channel channel = UserChannelUtil.getChannel(userId);
        if(channel == null){
            logger.info("该用户未连接到Netty服务器");
        }
        // 获取对应的ChannelGroup来把对应的用户id的channel加入group
        ChannelGroup group = GetGroupByGroupId(groupId);
        // channel心跳停止后需要把channel手动移除
        group.add(channel);
        Groups.put(groupId,group);
    }

    public static ChannelGroup GetGroupByGroupId(String groupId){
        ChannelGroup group = Groups.get(groupId);
        if(group == null){
            ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            Groups.put(groupId,channelGroup);
            return channelGroup;
        }
        return group;
    }
}
