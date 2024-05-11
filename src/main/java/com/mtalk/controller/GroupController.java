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

    public Result applyGroup(@RequestParam("groupId")String groupId,@RequestParam("message") String message){
        return groupService.ApplyGroup(groupId,message);
    }

    /**
     * 处理请求
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
        return groupService.SearchGroup(groupId);
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

}
