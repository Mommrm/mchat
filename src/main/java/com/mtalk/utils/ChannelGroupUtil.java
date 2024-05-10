package com.mtalk.utils;

import cn.hutool.core.util.RandomUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType.Attribute;

@Component
//每次实例化一个ChannelGroupUtil都会创建一个ChannelGroup
public class ChannelGroupUtil {
    private ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static Map<String, ChannelGroup> groups = new HashMap<>(); // 复用的Map存放groups
    // 将channel添加进group
    public String addChannel2Group(Channel channel){
        String groupId = RandomUtil.randomString(20); //生成groupId
        group.add(channel);
        group.writeAndFlush(new TextWebSocketFrame("加入group成功"));
        groups.put(groupId,group);
        return groupId;
    }

    // 获取对应的ChannelGroup
    public ChannelGroup getChannelGroup(String groupId){
        return groups.get(groupId);
    }
    // 移除该group
    public void removeGroup(String groupId){
        groups.remove(groupId);
    }
}
