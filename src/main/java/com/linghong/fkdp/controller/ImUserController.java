package com.linghong.fkdp.controller;


import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.ImUser;
import com.linghong.fkdp.service.ImUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ImUserController {
    @Resource(name = "imUserServiceImpl")
    private ImUserService imUserService;

    /**
     * 根据手机号 获取即时通信的信息
     * @param mobilePhone
     * @return
     */
    @GetMapping("/im/findImUserMessageByMobilePhone/{mobilePhone}")
    public Response findImUserMessageByMobilePhone(@PathVariable String mobilePhone){
        ImUser imUser = imUserService.findImUserMessageByMobilePhone(mobilePhone);
        if (imUser == null){
            return new Response(false, 101, null,"此用户不存在" );
        }
        return new Response(true, 200, imUser, "即时通信相关信息");
    }

    /**
     * 根据主键 获取即时通信的信息
     * @param imUserId
     * @return
     */
    @GetMapping("/im/findImUserMessageByImUserId/{imUserId}")
    public Response findImUserMessageByImUserId(@PathVariable String imUserId){
        ImUser imUser = imUserService.findImUserMessageByImUserId(imUserId);
        if (imUser == null){
            return new Response(false, 101, null,"此用户不存在" );
        }
        return new Response(true, 200, imUser, "即时通信相关信息");
    }


}

