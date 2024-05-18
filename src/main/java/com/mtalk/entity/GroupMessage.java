package com.mtalk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessage extends Message{

    private String groupId;  // 发往的群组
    private String memberId; // 发送者的ID
    private String memberName; // 发送者的名字

    @Override
    public String toString() {
        return super.toString() + "GroupMessage{" +
                "groupId='" + groupId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", memberName='" + memberName + '\'' +
                '}';
    }
}
