package NIOTest.Test1;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //创建Selector
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0,null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        Worker worker = new Worker("worker-0");
        worker.register();
        //监听事件Key
        while(true){
            log.debug("Before boss.select();...");
            boss.select();
            log.debug("After boss.select();...");
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                //是连接事件
                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept(); //但其实这里Selector只绑定了一个ssc
                    sc.configureBlocking(false);
                    log.debug("Before register...{}",sc.getRemoteAddress());
                    sc.register(worker.selector,SelectionKey.OP_READ,null);
                    log.debug("After register...{}",sc.getRemoteAddress());
                }
            }
        }

    }
    // 处理读写
    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;

        private volatile boolean start = false;

        public Worker(String name){
            this.name = name;
        }

        public void register() throws IOException {
            if(!start){ //第一次初始化
                // 但这个Selector现在还没有注册连接
                thread = new Thread(this,name);
                selector = Selector.open();
                thread.start();
                start = true;
            }
        }

        @Override
        public void run(){
            while(true){
                try {
                    log.debug("Before selector.select();");
                    selector.select();
                    log.debug("After selector.select();");
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()){
                        SelectionKey key = iter.next();
                        iter.remove();
                        // 是读事件
                        if(key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}",channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            while(buffer.hasRemaining()){
                                byte x = buffer.get();
                                log.debug("buffer:{}",(char) x);
                            }
                        }

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}


