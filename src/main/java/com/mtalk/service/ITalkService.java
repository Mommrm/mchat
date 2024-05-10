package com.mtalk.service;


import com.mtalk.entity.Result;
import com.mtalk.entity.SingleMessage;
import org.springframework.stereotype.Service;

@Service
public interface ITalkService {
    Result createSingleChat(String talkId);
    Result sentSingleMessage(SingleMessage singleMessage);
}
