package com.mtalk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Relation {
    private String friendName;
    private String friendId;
    private String userName;
    private String userId;
    private String groupId;
    private String handleCode;
    private String userAvatar;
}
