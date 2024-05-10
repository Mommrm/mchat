package com.mtalk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroup {
    private String channelGroupId;
    private Integer groupId;
    private String groupName;
    private String groupLeader;
    private String leaderId;
    private String groupBrief;
    private int groupNum;
}
