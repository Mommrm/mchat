package NettyTest.ByteBufTest.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class testCapacity {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(); // 默认分配256字节的缓冲区
        StringBuilder sb = new StringBuilder();
        System.out.println(buf); // 256
        for (int i = 0; i < 1025; i++) {
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes());
        // 自动扩容
        System.out.println(buf); // 512
    }
}
