package com.mtalk.entity;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class Message {
    private String messageId;
    private String senderName;
    private String message;
    private String messageType;
    private String chatType;
    private Timestamp sendTime;

    // messageType
    public static final String TEXT = "TEXT";
    public static final String IMAGE = "IMAGE";

    // chatType
    public static final String SINGLE = "SINGLE";
    public static final String GROUP = "GROUP";

    //暴露给外部的合并groupId
    public String combineToSingleGroupId(String from, String to){
        return from + "-" + to;
    }

    public Timestamp getSentTime(){
        sendTime = Timestamp.valueOf(LocalDateTime.now());
        return sendTime;
    }

    public void setSentTime(Timestamp sendTime){
        this.sendTime = sendTime;
    }
}
