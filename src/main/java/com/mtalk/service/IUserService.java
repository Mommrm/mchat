package com.mtalk.service;


import com.mtalk.entity.Result;
import com.mtalk.entity.Relation;
import com.mtalk.entity.User;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

@Service
public interface IUserService {
    Result RegisterUser(User user);
    Result LandUser(String userName,String userPassword) throws URISyntaxException;
    Result AddFriend(String userId,String message);
    Result getFriendRequestList();
    Result handleRelation(Relation relation);
    Result DeleteFriend(String userId);
    Result GetFriendList();
    Result getFriendInfo(String friendId);
}
