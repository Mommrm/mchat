package com.mtalk;


import com.mtalk.webSocket.netty.NettyWebSocketStarter;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("initRun")
public class initRun implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(initRun.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private NettyWebSocketStarter nettyWebSocketStarter;

    // 异步启动Netty服务器
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try{
            nettyWebSocketStarter.startNetty();
        }
        catch (Exception e){
            logger.error("Netty启动异常");
        }
    }
}
