package com.mtalk.webSocket.netty.utils;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class UserChannelUtil {
    private static final Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();


    public static void bind(String userId, Channel channel){
        userIdChannelMap.put(userId,channel);
    }
    // 获取userId对应的channel 需要连接后才能获取
    public static Channel getChannel(String userId){
        return userIdChannelMap.get(userId);
    }

    public static void unbind(String userId){
        userIdChannelMap.remove(userId);
    }
}
