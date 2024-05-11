package com.mtalk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMember {
    private String groupId;
    private String memberType;
    private String memberId;
    private String memberName;

    public static final String NORMAL_MEMBER = "1";
    public static final String ADMIN_MEMBER = "2";
    public static final String LEADER_MEMBER = "3";
}
