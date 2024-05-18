package com.mtalk.webSocket.netty.utils;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class UserChannelUtil {
    private static final Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();

    private static final Map<Channel,String> ChannelUserIdMap = new ConcurrentHashMap<>();


    public static void bind(String userId, Channel channel){
        userIdChannelMap.put(userId,channel);
        ChannelUserIdMap.put(channel,userId);
    }
    // 获取userId对应的channel 需要连接后才能获取

    // 可能会获取到null 的channel 因为对应的用户不在线
    public static Channel getChannel(String userId){
        return userIdChannelMap.get(userId);
    }

    public static String getUserId(Channel channel){
        return ChannelUserIdMap.get(channel);
    }

    public static void unbind(String userId,Channel channel){
        userIdChannelMap.remove(userId);
        ChannelUserIdMap.remove(channel);
    }
}
