package com.mtalk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMember {
    private String channelGroupId;
    private Integer groupId;
    private String memberType;
    private String memberId;
    private String memberName;
}
