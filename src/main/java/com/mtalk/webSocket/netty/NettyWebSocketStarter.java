package com.mtalk.webSocket.netty;

import com.mtalk.webSocket.netty.handler.HandlerHeartBeat;
import com.mtalk.webSocket.netty.handler.HandlerWebSocket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.mtalk.webSocket.netty.constant.ServerConstant.*;

// 服务器
@Component
public class NettyWebSocketStarter {
    private static final Logger logger = LoggerFactory.getLogger(NettyWebSocketStarter.class);

    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private static EventLoopGroup workGroup = new NioEventLoopGroup();

    @PreDestroy
    public void close(){
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        logger.info("已经关闭Netty服务器");
    }

    @Resource
    private HandlerWebSocket handlerWebSocket;
    @Resource
    private HandlerHeartBeat handlerHeartBeat;
    // 启动Netty服务器入口 异步启动
    public void startNetty(){
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))  // 方便调试
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            logger.info("channelId:{}",ch.id());
                            ChannelPipeline pipeline = ch.pipeline();
                            // http 解码
                            pipeline.addLast(new HttpServerCodec());
                            // http 最大内容容量
                            pipeline.addLast(new HttpObjectAggregator(MAX_FRAME_SIZE));
                            // 心跳  long readerIdleTime， long writerIdleTime, long allIdleTime, TimeUnit unit(读超时时间，写超时时间 所有类型超时时间 单位）
                            pipeline.addLast(new IdleStateHandler(HEART_BEAT_TIME,HEART_BEAT_TIME,HEART_BEAT_TIME, TimeUnit.SECONDS));
                            pipeline.addLast(handlerHeartBeat); // 自定义的心跳处理器
                            // 将http协议升级为WebSocket协议
                            pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH,null,true,MAX_FRAME_SIZE,true,true,10000L));
                            pipeline.addLast(handlerWebSocket);  // 自定义的WebSocket处理器
                        }
                    });
            logger.info("Netty服务器启动成功");
            ChannelFuture channelFuture = serverBootstrap.bind(SERVER_PORT).sync();
            channelFuture.channel().closeFuture().sync();
            logger.info("等待用户建立");
        }catch (Exception e){
            logger.error("启动Netty服务器失败");
        }
        finally {
            logger.error("Netty服务器关闭中...");
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            logger.error("Netty服务器已关闭");
        }
    }


}
