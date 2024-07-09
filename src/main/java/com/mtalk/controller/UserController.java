package com.mtalk.controller;


import com.mtalk.entity.Result;
import com.mtalk.entity.Relation;
import com.mtalk.entity.User;
import com.mtalk.service.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@CrossOrigin
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    //TODO 注册用户
    @PostMapping("register")
    public Result RegisterUser(@RequestBody User user){
        System.out.println(user);
        return userServiceImpl.RegisterUser(user);
    }

    //TODO 登录用户
    @PostMapping("land")
    public Result LandUser(@RequestParam("userName") String userName,@RequestParam("userPassword") String userPassword) throws URISyntaxException {
        return userServiceImpl.LandUser(userName,userPassword);
    }

    /**
     * 添加好友请求，需要对方接受或拒绝
     * @param userId: 对方id
     * @param message: 添加备注
     * @return
     */
    // TODO 添加好友
    @PostMapping("add/friend")
    public Result AddFriend(@RequestParam("userId") String userId,
                            @RequestParam("message") String message){
        return new Result(userServiceImpl.AddFriend(userId,message));
    }

    @PostMapping("get/requestlist")
    public Result getFriendRequestList(){
        return userServiceImpl.getFriendRequestList();
    }

    /**
     * 处理好友请求
     * @param relation 关系处理
     * @return
     */
    @PostMapping("handle/request")
    public Result HandleRequest(@RequestBody Relation relation){
        return userServiceImpl.handleRelation(relation);
    }
    /**
     * 删除好友
     * @param friendId: 对方id
     * @return
     */
    //TODO 删除好友
    @DeleteMapping("delete/friend")
    public Result DeleteFriend(@RequestParam("friendId") String friendId){
        return userServiceImpl.DeleteFriend(friendId);
    }

    /**
     * 获取用户的好友列表
     * @return
     */
    @GetMapping("get/friendlist")
    public Result GetFriendList(){
        return userServiceImpl.GetFriendList();
    }

    /**
     * 获取好友详情
     */
    @GetMapping("get/friend/info")
    public Result GetFriendInfo(@RequestParam("friendId") String friendId){
        return userServiceImpl.getFriendInfo(friendId);
    }
}
