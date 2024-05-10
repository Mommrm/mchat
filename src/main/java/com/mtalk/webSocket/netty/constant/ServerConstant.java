package com.mtalk.webSocket.netty.constant;

public class ServerConstant {
    public static final int HEART_BEAT_TIME = 60 * 30; // 心跳时间 半个小时
    public static final int HEART_WAIT_TIME = HEART_BEAT_TIME / 2; // 心跳时间 的一半

    public static final String WEBSOCKET_PATH = "/ws";

    public static final int MAX_FRAME_SIZE = 64 * 1024;
    public static final int SERVER_PORT = 5051;
}
