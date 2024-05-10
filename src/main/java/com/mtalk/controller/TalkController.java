package com.mtalk.controller;

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
     * 发起私聊请求,建立私聊通道
     * @param userId: 私聊对方id
     * @return
     */
    @PostMapping("create/single")
    public Result createSingle(@RequestParam("userId") String userId){
        return talkServiceImpl.createSingleChat(userId);
    }

    /**
     * 发送消息
     * @param singleMessage
     * @return
     */
    @PostMapping("sent/single/message")
    public Result sentMessageToSingleGroup(@RequestBody SingleMessage singleMessage){
        return talkServiceImpl.sentSingleMessage(singleMessage);
    }


}
