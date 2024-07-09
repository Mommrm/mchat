package NettyTest.demo3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 异步非阻塞，main发起调用，开启其他线程进行NIO连接 如果不调用sync()方法，main线程会直接向下执行，获取到的是没有建立连接的channel
                .connect(new InetSocketAddress("localhost", 8080));
                Channel channel = channelFuture.sync().channel();

        // 开启线程去不断输出数据
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            while(!line.equals("q")){
                channel.writeAndFlush(line);
                line = scanner.nextLine();
            }
            channel.close();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"input").start();
//        ChannelFuture closeFuture = channel.closeFuture();
//        closeFuture.sync();
//        System.out.println("连接已经完毕,正在处理后续操作...");
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("处理关闭后的操作...");
                group.shutdownGracefully();
            }
        });
    }
}
