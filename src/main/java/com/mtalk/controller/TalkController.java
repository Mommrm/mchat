package com.mtalk.controller;

import com.mtalk.entity.GroupMessage;
import com.mtalk.entity.Result;
import com.mtalk.entity.SingleMessage;
import com.mtalk.service.Impl.TalkServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("chat")
public class TalkController {



    @Resource
    private TalkServiceImpl talkServiceImpl;

    /**
     * 获取历史消息 只有在打开聊天界面才开始获取 即当前用户已经登录
     * 获取逻辑 查询比当前时间还要早的消息
     * @param chatType
     * @param groupId
     * @return
     */
    @GetMapping("history/group")
    public Result getGroupHistory(@RequestParam("chatType") String chatType,@RequestParam("groupId") String groupId){
        return talkServiceImpl.getGroupHistory(chatType,groupId);
    }

    /**
     * 获取历史消息 只有在打开聊天界面才开始获取 即当前用户已经登录
     * 获取逻辑 查询比当前时间还要早的消息
     * @param chatType
     * @param to
     * @return
     */
    @GetMapping("history/single")
    public Result getSingleHistory(@RequestParam("chatType") String chatType,@RequestParam("to") String to){
        return talkServiceImpl.getSingleHistory(chatType,to);
    }

    /**
     * 发起私聊请求,建立私聊通道
     * @param userId: 私聊对方id
     * @return
     */
    @PostMapping("create/single")
    public Result createSingle(@RequestParam("userId") String userId){
        return talkServiceImpl.createSingleChat(userId);
    }

    /**
     * 创建对应groupId对应的ChannelGroup
     * @param groupId
     * @return
     */
    @PostMapping("create/group")
    public Result createGroup(@RequestParam("groupId") String groupId){
        return talkServiceImpl.createGroupChat(groupId);
    }

    /**
     * 发送消息(私聊)
     * @param singleMessage
     * @return
     */
    @PostMapping("sent/single/message")
    public Result sentMessageToSingleGroup(@RequestBody SingleMessage singleMessage){
        return talkServiceImpl.sentSingleMessage(singleMessage);
    }

    /**
     * 发送消息(群组)
     * @param groupMessage
     * @return
     */
    @PostMapping("sent/group/message")
    public Result sentMessageToGroup(@RequestBody GroupMessage groupMessage){
        return talkServiceImpl.sentGroupMessage(groupMessage);
    }
}
