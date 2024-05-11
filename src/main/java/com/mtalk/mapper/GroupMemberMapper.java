package com.mtalk.mapper;

import com.mtalk.entity.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMemberMapper {

    boolean InsertMember(GroupMember groupMember);

    boolean DeleteMember(@Param("userId") String userId);

    boolean ModifyMemberType(@Param("memberId") String memberId,@Param("memberType") String memberType);

    GroupMember SearchMemberById(@Param("groupId") String groupId,@Param("userId") String userId);

    List<GroupMember> SearchMemberByName(@Param("groupId") String groupId,@Param("searhName") String searhName);
}
