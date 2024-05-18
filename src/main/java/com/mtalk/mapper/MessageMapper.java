package com.mtalk.mapper;


import com.mtalk.entity.GroupMessage;
import com.mtalk.entity.Message;
import com.mtalk.entity.SingleMessage;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;


@Mapper
public interface MessageMapper {
    List<Message> SearchHistoryByGroupId(String chatType, String groupId, Timestamp sendTime);

    boolean InsertGroupHistory(GroupMessage groupMessage);

    boolean InsertSingleHistory(SingleMessage singleMessage);
}
