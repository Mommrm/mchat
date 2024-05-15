package com.mtalk.entity;

import lombok.Data;

@Data
public class Message {
    private String message;
    private String type;

    private static final String TEXT = "TEXT";
    private static final String IMAGE = "IMAGE";

    //暴露给外部的合并groupId
    public String combineToGroupId(String from,String to){
        String groupId = from + "-" + to;
        return groupId;
    };
}
