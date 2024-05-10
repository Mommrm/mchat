package com.mtalk.webSocket.netty.utils;

import com.mtalk.utils.LocalUser;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SingleChatUtils {

    @Resource
    private UserChannelUtil userChannelUtil;

    private static final Map<String, ChannelGroup> SingleGroup = new HashMap<>();

    public static ChannelGroup CreateSingleChat(String groupId){
        // 传入的groupId是从数据库查询的 可信
        // 根据groupId 分解出用户id
        String[] ids = getIdsByGroupId(groupId);
        Channel channel1 = UserChannelUtil.getChannel(ids[0]);
        Channel channel2 = UserChannelUtil.getChannel(ids[1]);
        // 先判断是否有对应的group
        ChannelGroup channelGroup = SingleGroup.get(groupId);
        if(channelGroup == null){
            // 没有就创建一个 并放入私聊Map 然后返回
            ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE); //创建一个对应的Group
            // 往group中添加channel
            group.add(channel1);
            group.add(channel2);
            SingleGroup.put(groupId,group);
            return group;
        }
        channelGroup.add(channel1);
        channelGroup.add(channel2);
        // 已经有该group 直接返回
        return channelGroup;
    }

    public ChannelGroup GetGroupByGroupId(String groupId){
        ChannelGroup group = SingleGroup.get(groupId);
        return group;
    }

    private static String[] getIdsByGroupId(String groupId){
        String[] parts = groupId.split("-");
        String[] result = new String[2];
        String part1 = parts[0] + "-" + parts[1] + "-" + parts[2] + "-" + parts[3] + "-" + parts[4];
        String part2 = parts[5] + "-" + parts[6] + "-" + parts[7] + "-" + parts[8] + "-" + parts[9];
        result[0] = part1;
        result[1] = part2;
        return result;
    }
}
