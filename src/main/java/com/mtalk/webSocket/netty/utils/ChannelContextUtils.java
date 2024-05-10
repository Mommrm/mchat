package com.mtalk.webSocket.netty.utils;


import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChannelContextUtils {
    private static final Logger logger = LoggerFactory.getLogger(ChannelContextUtils.class);



    // 把userId添加到channel中的Attribute中
    public static void addUserId(String userId, Channel channel){
        String channelId = channel.id().toString();
        AttributeKey attributeKey = null;
        // 判断是否存在此key
        // 不存在就实例化一个 key为channelId
        if(!AttributeKey.exists(channelId)){
            attributeKey = AttributeKey.newInstance(channelId); // key是channelId
        }
        // 存在直接把值更新为channelId
        else {
            attributeKey = AttributeKey.valueOf(channelId);
        }
        // 设置key为channelId的value值为userId
        channel.attr(attributeKey).set(userId);
    }

    // 获取该channel中的对象 channel是用户和服务器进行连接 因此每个用户都独立一个channel
    public static String getUserId(Channel channel){
        // 结构 channel key 是 AttributeKey类型 value 是 Attribute<Object> 类型
        Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
        logger.info(String.valueOf(attribute));
        return attribute.get();
    }
}
