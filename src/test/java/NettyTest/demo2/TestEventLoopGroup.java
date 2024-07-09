package NettyTest.demo2;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;

public class TestEventLoopGroup {
    public static void main(String[] args) {
        //1. 创建事件循环组
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(); //处理IO，普通任务，定时任务
        EventLoopGroup defaultLoopGroup = new DefaultEventLoopGroup(); //普通任务，定时任务

        // 2.获取事件循环对象
        EventLoop eventLoop = eventLoopGroup.next();

        // 执行普通任务
        eventLoop.execute(() -> {
            System.out.println("普通任务");
        });

        eventLoop.next().scheduleAtFixedRate( () -> {
            System.out.println("定时任务: 立马执行，间隔5秒");
        },0, 5, TimeUnit.SECONDS);

    }
}
