package com.mtalk.controller;

import com.mtalk.entity.ChatGroup;
import com.mtalk.entity.Result;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("group")
public class GroupController {

    /**
     * 创建群组
     * @param group
     * @return
     */
    @PostMapping("create")
    public Result createGroup(@RequestBody ChatGroup group){
        return new Result("功能未完成");
    }

    /**
     * 邀请请求
     * @return
     */
    @PostMapping("invite")
    public Result inviteGroup(){
        return new Result("功能未完成");
    }

    /**
     * 处理请求
     * @return
     */
    @PostMapping("handle")
    public Result handleGroup(){
        return new Result("功能未完成");
    }
    /**
     * 搜索请求
     * @return
     */
    @PostMapping("search")
    public Result searchGroup(){
        return new Result("功能未完成");
    }
    /**
     * 退出群组
     * @return
     */
    @PostMapping("exit")
    public Result exitGroup(){
        return new Result("功能未完成");
    }
    /**
     * 解散群组
     * @return
     */
    @PostMapping("broke")
    public Result brokeGroup(){
        return new Result("功能未完成");
    }

}
