package com.mtalk.service;


import com.mtalk.entity.GroupMessage;
import com.mtalk.entity.Result;
import com.mtalk.entity.SingleMessage;
import org.springframework.stereotype.Service;

@Service
public interface ITalkService {
    Result createSingleChat(String talkId);

    Result createGroupChat(String groupId);
    Result sentSingleMessage(SingleMessage singleMessage);

    Result sentGroupMessage(GroupMessage groupMessage);

    Result getGroupHistory(String chatType,String groupId);
    Result getSingleHistory(String chatType,String to);
}
