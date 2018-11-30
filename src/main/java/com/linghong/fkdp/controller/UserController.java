package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.User;
import com.linghong.fkdp.service.UserService;
import com.linghong.fkdp.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/27 15:27
 * @Version 1.0
 * @Description:
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     * 参数： mobilePhone password
     * @param user
     * @return
     */
    @PostMapping("/user/register")
    public Response register(User user){
        user = userService.register(user);
        if (user != null){
            Map<String,Object> map = new HashMap<>();
            map.put("mobilePhone", user.getMobilePhone());
            map.put("userId", user.getUserId());
            String jwt = JwtUtil.createJWT(map);
            return new Response(true,200 ,jwt ,"token" );
        }
        return new Response(false,101 , null,"账号已被注册" );
    }

    /**
     * 登录
     * 参数： mobilePhone password
     * @param user
     * @return
     */
    @PostMapping("/user/login")
    public Response login(User user){
        user = userService.login(user);
        if (user != null){
            Map<String,Object> map = new HashMap<>();
            map.put("mobilePhone", user.getMobilePhone());
            map.put("userId", user.getUserId());
            String jwt = JwtUtil.createJWT(map);
            return new Response(true,200 ,jwt ,"token" );
        }
        return new Response(false,101 , null,"账号或密码错误" );
    }

    /**
     * 完善个人信息
     * 参数 ： userId  sign  nickName  sex age address
     * @param user
     * @return
     */
    @PostMapping("/user/updateUserMessage")
    public Response updateUserMessage(User user){
        boolean flag = userService.updateUserMessage(user);
        if (flag){
            return new Response(true,200 ,null , "更新完成");
        }
        return new Response(false,101 , null, "无此用户");
    }

    /**
     * 更新头像
     * @param userId
     * @param base64Avatar
     * @return
     */
    @PostMapping("/user/uploadAvatar")
    public Response uploadAvatar(Long userId ,String base64Avatar){
        boolean flag = userService.uploadAvatar(userId,base64Avatar);
        if (flag){
            return new Response(true,200 ,null ,"更新完成" );
        }
        return new Response(false,101 , null, "无此用户");
    }

    /**
     * 更新 密码
     * @param userId
     * @param password
     * @return
     */
    @PostMapping("/user/updatePassword")
    public Response updatePassword(Long userId,String password){
        boolean flag = userService.updatePassword(userId,password);
        if (flag){
            return new Response(true, 200, null,"更新完成" );
        }
        return new Response(false,101 , null, "无此用户");
    }

    /**
     * 上传身份证号 身份证照片一张
     * @param userId
     * @param base64IdCard
     * @param idCardNumber
     * @return
     */
    @PostMapping("/user/uploadIdCard")
    public Response uploadIdCard(Long userId,
                                 @RequestParam(required = false) String base64IdCard,
                                 @RequestParam(required = false) String idCardNumber){
        boolean flag = userService.uploadIdCard(userId,base64IdCard,idCardNumber);
        if (flag){
            return new Response(true,200 ,null ,"完善成功" );
        }
        return new Response(false,101 ,null ,"身份证不匹配" );
    }

    /**
     * 根据userId查询个人信息
     * @param userId
     * @return
     */
    @GetMapping("/user/findUserByUserId/{userId}")
    public Response findUserByUserId(@PathVariable Long userId){
        User user = userService.findUserByUserId(userId);
        if (user != null){
            return new Response(true,200 ,user ,"查询结果" );
        }
        return new Response(false,101 ,null ,"无此用户" );
    }



}
