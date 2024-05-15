package com.mtalk.controller;

import com.mtalk.entity.ChatGroup;
import com.mtalk.entity.Result;
import com.mtalk.service.Impl.GroupServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("group")
public class GroupController {


    @Resource
    private GroupServiceImpl groupService;
    /**
     * 创建群组
     * @param group
     * @return
     */
    @PostMapping("create")
    public Result createGroup(@RequestBody ChatGroup group){
        return groupService.CreateGroup(group);
    }

    /**
     * 邀请请求
     * @return
     */
    @PostMapping("invite")
    public Result inviteGroup(@RequestParam("groupId")String groupId,@RequestParam("userId") String userId){
        return groupService.InviteGroup(groupId,userId);
    }
    /**
     * 申请入群
     */
    @PostMapping("apply")
    public Result applyGroup(@RequestParam("groupId")String groupId,@RequestParam("message") String message){
        return groupService.ApplyGroup(groupId,message);
    }

    /**
     * 处理请求
     * @param groupId 加入群组的Id
     * @param userId 申请加入用户的Id
     * @param handleCode 处理码 1为同意进入 2为不同意进入
     * @return
     */
    @PostMapping("handle")
    public Result handleGroup(@RequestParam("groupId")String groupId,@RequestParam("userId")String userId,@RequestParam("handleCode") String handleCode){
        return groupService.HandleGroup(groupId,userId,handleCode);
    }
    /**
     * 搜索请求
     * @return
     */
    @PostMapping("search")
    public Result searchGroup(@RequestParam("groupId")String groupId){
        return groupService.SearchGroupByGroupId(groupId);
    }
    /**
     * 退出群组
     * @return
     */
    @PostMapping("exit")
    public Result exitGroup(@RequestParam("groupId")String groupId){
        return groupService.ExitGroup(groupId);
    }
    /**
     * 解散群组
     * @return
     */
    @PostMapping("broke")
    public Result brokeGroup(@RequestParam("groupId")String groupId){
        return groupService.BrokeGroup(groupId);
    }

    /**
     * 查看对应群组请求列表
     * @param groupId 获取群组申请列表
     * @return
     */
    @GetMapping("apply/list")
    public Result getGroupApplyList(@RequestParam("groupId") String groupId){
        return groupService.GetApplyListByGroupId(groupId);
    }

    /**
     * 查看对应用户邀请请求列表
     * @return
     */
    @GetMapping("invite/list")
    public Result getGroupInviteList(){
        return groupService.GetInviteListByGroupId();
    }

    @PostMapping("invite/handle")
    public Result handleInvite(@RequestParam("groupId") String groupId,@RequestParam("handleCode")String handleCode){
        return groupService.HandleInvite(groupId,handleCode);
    }

    /**
     * 获取登录用户加入的所有群组
     * @return
     */
    @GetMapping("user/list")
    public Result getUserList(){
        return groupService.GetMySelfGroups();
    }

    @PutMapping("change/name")
    public Result changeGroupName(@RequestParam("newName")String newName,@RequestParam("groupId")String groupId){
        return groupService.ChanegGroupName(newName,groupId);
    }
}
