package NettyTest.demo3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;


@Slf4j
public class Server {
    public static void main(String[] args) {
        EventLoopGroup group = new DefaultEventLoopGroup();
        // 固定写法
        new ServerBootstrap()
                // 第一个参数是Boss负责处理accept事件， 第二个参数是worker，负责其他事件
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //不固定写法
                        ch.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                // 这里最好不要默认字符集
                                log.debug(buf.toString(Charset.defaultCharset()));
                                // 一定要写这个代码，不然下一个处理器不会接收到msg消息
                                ctx.fireChannelRead(msg); //将消息传递给下一个Handler
                            }
                        }).addLast(group,"handler2",new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
