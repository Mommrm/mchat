package com.mtalk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleMessage extends Message{
    private String from; // 从哪发出的ID
    private String to; // 发往对象的ID


}
