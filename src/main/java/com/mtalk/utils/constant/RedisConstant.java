package com.mtalk.utils.constant;

public class RedisConstant {
    // KEY
    public static final String USER_CACHE_KEY = "key:user:";
    public static final String MESSAGE_FRIEND_KEY = "key:friends:"; // 好友申请缓存
    public static final String USER_MESSAGE_KEY = "user:message:"; // 用户消息缓存
    public static final String GROUP_MESSAGE_KEY = "group:message"; //群组消息缓存 只有群主或管理员可以查看
    public static final String GROUP_APPLY_KEY = "group:apply:"; // 群组申请消息缓存
    public static final String INVITE_USER_KEY = "user:group:"; // 用户邀请消息缓存 只能根据用户ID来查看消息


    // Time
    public static final long USER_CACHE_TIME = 7l;
    public static final long MESSAGE_CACHE_TIME = 7l;
    public static final long USER_MESSAGE_CACHE_TIME = 1l;
}
