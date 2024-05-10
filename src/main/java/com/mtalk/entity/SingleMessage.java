package com.mtalk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleMessage {
    private String message;
    private String type;
    private String from; // 从哪发出的ID
    private String to; // 发往对象的ID

    private static final String TEXT = "TEXT";
    private static final String IMAGE = "IMAGE";

    //暴露给外部的合并groupId
    public String combineToGroupId(String from,String to){
        String groupId = from + "-" + to;
        return groupId;
    };
}
