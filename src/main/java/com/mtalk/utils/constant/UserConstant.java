package com.mtalk.utils.constant;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserConstant {
    public static final String DEFAULT_AVATAR_PATH = "../../../resources/images/avatars/defaultAvatar.jpg";
    public static final String USER_BRIEF = "默认签名";

    public static Timestamp getJoinTime(){
        return Timestamp.valueOf(LocalDateTime.now());
    }
}
