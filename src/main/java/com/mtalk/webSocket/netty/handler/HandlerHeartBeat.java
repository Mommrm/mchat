package com.mtalk.webSocket.netty.handler;

import com.mtalk.webSocket.netty.utils.ChannelContextUtils;
import com.mtalk.webSocket.netty.utils.GroupChatUtils;
import com.mtalk.webSocket.netty.utils.UserChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@ChannelHandler.Sharable
// Duplex 双工
public class HandlerHeartBeat extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(HandlerHeartBeat.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx , Object evt){
        Channel channel = ctx.channel();
        String userId = UserChannelUtil.getUserId(channel);

        // 如果是超过空闲时间事件
        if(evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            // 长时间服务器没有读取事件发生 关闭对应的ctx
            if(e.state() == IdleState.READER_IDLE){
                logger.info("{}心跳超时",userId);
                channel.writeAndFlush(new TextWebSocketFrame("heart")); // 发送消息已经断开连接 并告知重新连接
                channel.close();
            }
            // 写空闲 没有服务器向客户端转发数据
            else if(e.state() == IdleState.WRITER_IDLE){
                ctx.writeAndFlush("heart");
                channel.close();
            }
        }
    }
}
