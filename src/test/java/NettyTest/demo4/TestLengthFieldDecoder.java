package NettyTest.demo4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.swing.*;

public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                // 解析包最大长度、长度偏移量、长度内容的长度、长度结束要调整的字节、读取跳过的字节
                new LengthFieldBasedFrameDecoder(1024,0,4,1,0),
                new LoggingHandler(LogLevel.DEBUG)
        );
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer,"hello world");
        send(buffer,"Hi!");
        embeddedChannel.writeInbound(buffer);

    }

    public static void send(ByteBuf buf,String cotent){
        byte[] bytes = cotent.getBytes();
        int length = bytes.length;
        buf.writeInt(length); // 发送包的长度字段
        buf.writeByte(1); // 版本号
        buf.writeBytes(bytes);
    }
}
