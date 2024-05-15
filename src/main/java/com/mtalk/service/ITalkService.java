package com.mtalk.service;


import com.mtalk.entity.GroupMessage;
import com.mtalk.entity.Result;
import com.mtalk.entity.SingleMessage;
import org.springframework.stereotype.Service;

@Service
public interface ITalkService {
    Result createSingleChat(String talkId);

    Result createGroupChat(String groupId,String leaderId);
    Result sentSingleMessage(SingleMessage singleMessage);

    Result sentGroupMessage(GroupMessage groupMessage);
}
