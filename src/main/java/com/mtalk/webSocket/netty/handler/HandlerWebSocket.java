package com.mtalk.webSocket.netty.handler;

import cn.hutool.json.JSONUtil;
import com.mtalk.entity.User;
import com.mtalk.webSocket.netty.utils.ChannelContextUtils;
import com.mtalk.webSocket.netty.utils.UserChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


import static com.mtalk.utils.constant.RedisConstant.USER_CACHE_KEY;

@ChannelHandler.Sharable
@Component
public class HandlerWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserChannelUtil userChannelUtil;
    private static final Logger logger = LoggerFactory.getLogger(HandlerWebSocket.class);

    // 服务器收到消息，转发到对应的Group
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Channel channel = ctx.channel();
        String userId = ChannelContextUtils.getUserId(channel);
        logger.info("收到消息");
    }
    // 连接建立触发器
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        Channel channel = ctx.channel();
    }
    // 连接关闭触发器
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 断开连接时把channel和userId的绑定关系解除
        String userId = ChannelContextUtils.getUserId(ctx.channel());
        UserChannelUtil.unbind(userId,ctx.channel());
        logger.info("有连接断开......");
    }

    // 用户事件触发器
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 连接三次握手成功
        if( evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            WebSocketServerProtocolHandler.HandshakeComplete complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            // 获取并解析token
            String url = complete.requestUri();
            String token = getToken(url);
            if(token == null){
                ctx.channel().close();
                return;
            }
            String userJson = stringRedisTemplate.opsForValue().get(USER_CACHE_KEY + token);
            // 该用户没有登录 (无需判断此用户是否存在，因为能发起聊天建立请求的一定是登录过的用户,伪造或者没有登录的都不能建立)
            if(userJson == null || userJson.isEmpty() ){
                logger.info("没有该用户");
                ctx.channel().close();
                return;
            }

            User user = JSONUtil.toBean(userJson, User.class);
            logger.info("{}建立连接成功",user.getUserName());


            // 绑定userId和对应channel对应的关系
            UserChannelUtil.bind(user.getUserId(),ctx.channel());
            // 每一个Channel保存着对应的userId
            ChannelContextUtils.addUserId(user.getUserId(),ctx.channel());
        }
    }

    // 解析token
    private String getToken(String url){
        if(url.isEmpty() || url.indexOf("?") == -1){
            return null;
        }
        String[] strings = url.split("\\?");
        if(strings.length != 2){
            return null;
        }
        String[] strings1 = strings[1].split("=");
        if(strings1.length != 2){
            return null;
        }
        return strings1[1];
    }
}
