package com.linghong.fkdp.controller;


import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.ImUser;
import com.linghong.fkdp.service.ImFriendService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ImFriendController {
    @Resource(name = "imFriendServiceImpl")
    private ImFriendService imFriendService;

    /**
     * 根据手机号获取好友列表
     * @param mobilePhone
     * @return
     */
    @GetMapping("/im/findFriendsByMobilePhone/{mobilePhone}")
    public Response findFriendsByMobilePhone(@PathVariable String mobilePhone){
        List<ImUser> imFriends = imFriendService.findFriendsByMobilePhone(mobilePhone);
        if (imFriends != null && imFriends.size() > 0){
            return  new Response(true,200 ,imFriends , "好友列表");
        }
        return new Response(false, 101, null, "无好友");
    }

    /**
     * 根据id获取好友列表
     * @param imUserId
     * @return
     */
    @GetMapping("/im/findFriendsByImUserId/{imUserId}")
    public Response findFriendsByImUserId(@PathVariable String imUserId){
        List<ImUser> imFriends = imFriendService.findFriendsByImUserId(imUserId);
        if (imFriends != null && imFriends.size() > 0){
            return  new Response(true,200 ,imFriends , "好友列表");
        }
        return new Response(false, 101, null, "无好友");
    }

    /**
     * 添加好友
     * @param fromMobilePhone
     * @param toMobilePhone
     * @param status  0是请求添加好友   1直接添加
     * @return
     */
    @PostMapping("/im/addFriend")
    public Response addFriend(String fromMobilePhone,String toMobilePhone,Integer status){
        String result = imFriendService.addFriend(fromMobilePhone,toMobilePhone,status);
        return  new Response(true, 200, null, result);
    }

    /**
     * 删除好友
     * @param fromMobilePhone
     * @param toMobilePhone
     * @return
     */
    @DeleteMapping("/im/deleteFriend")
    public Response deleteFriend(String fromMobilePhone,String toMobilePhone){
        boolean flag = imFriendService.deleteFriend(fromMobilePhone,toMobilePhone);
        if (flag){
            return new Response(true, 200, null, "删除成功");
        }
        return new Response(false, 101, null, "删除失败");
    }


}

