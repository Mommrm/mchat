package NettyTest.demo1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class Server {
    public static void main(String[] args) {
        // 1. 启动器，组装Netty,启动服务器
        new ServerBootstrap()
                //  2. Worker组
                .group(new NioEventLoopGroup())
                //  3. 选择服务器的ServerSocketChannel实现
                .channel(NioServerSocketChannel.class)
                //  4. work(clild)负责处理读写，决定worker(clild)能执行哪些操作(handler)
                .childHandler(
                        // channel是和客户端读写的通道，初始化，负责添加别的Handler
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // 添加具体的Handler
                                ch.pipeline().addLast(new StringDecoder()); //将ByteBuf转换为字符串
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 自定义handler

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("ctx" + ctx);
                                        System.out.println("message"  + msg);
                                    }

                                });
                            }
                        })
                .bind(10000);
    }
}
