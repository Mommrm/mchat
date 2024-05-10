package com.mtalk.mapper;

import com.mtalk.entity.Relation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RelationMapper {
    boolean createRelation(Relation relation);
    List<Relation> GetFriendList(@Param("myId") String myId);
    List<Relation> checkRelation(@Param("userId") String userId);

    String getGroupByUserId(@Param("friendId") String friendId,@Param("userId") String userId);
}
