package com.mtalk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleMessage extends Message{
    private String groupId; // 与Group的groupId不同 这是私聊的通道Id group中的ID是对外暴露的id
    private String from; // 从哪发出的ID
    private String to; // 发往对象的ID

    @Override
    public String toString() {
        return super.toString() + "SingleMessage{" +
                "groupId='" + groupId + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
