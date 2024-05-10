package com.mtalk.webSocket.netty.Handler;

import com.mtalk.webSocket.netty.utils.ChannelContextUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
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

    // 事件触发器
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx , Object evt){
        Channel channel = ctx.channel();
        // 如果是超过空闲时间事件
        if(evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            // 长时间服务器没有读取事件发生 关闭对应的ctx
            if(e.state() == IdleState.READER_IDLE){
                String userId = ChannelContextUtils.getUserId(channel);
                logger.info("{}心跳超时",userId);
                ctx.writeAndFlush("heart");
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
