package com.mtalk.mapper;


import com.mtalk.entity.Relation;
import com.mtalk.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper{

    User SelectByUser(@Param("userName") String userName , @Param("userPassword") String userPassword);

    boolean CreateUser(User user);

    boolean DeleteRelation(@Param("friendId") String friendId,@Param("myId") String myId);

    User GetUserInfoById(@Param("userId") String userId);

    User getUserById(@Param("friendId") String friendId);
}
