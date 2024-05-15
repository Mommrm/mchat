package com.mtalk.mapper;

import com.mtalk.entity.ChatGroup;
import com.mtalk.entity.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMapper {
    boolean InsertGroup(ChatGroup chatGroup);

    boolean BrokeGroup(@Param("groupId") String groupId);

    ChatGroup SearchGroupById(@Param("groupId") String groupId);

    List<ChatGroup> SearchGroupByName(@Param("searchName") String searchName);

    List<ChatGroup> SearchGroupByLeaderId(@Param("leaderId") String leaderId);

    boolean ChangeGroupName(@Param("newName")String newName,@Param("groupId")String groupId);
}
