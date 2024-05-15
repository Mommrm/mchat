package com.mtalk.service;

import com.mtalk.entity.ChatGroup;
import com.mtalk.entity.Result;

public interface IGroupService {

    Result CreateGroup(ChatGroup chatGroup);

    Result InviteGroup(String groupId,String userId);

    Result ApplyGroup(String groupId,String message);

    Result BrokeGroup(String groupId);

    Result SearchGroupByGroupId(String groupId);

    Result ExitGroup(String groupId);

    Result HandleGroup(String groupId,String userId,String handleCode);

    Result GetApplyListByGroupId(String groupId);
    Result GetInviteListByGroupId();

    Result HandleInvite(String groupId,String handleCode);

    Result GetMySelfGroups();

    Result ChanegGroupName(String newName,String groupId);
}
