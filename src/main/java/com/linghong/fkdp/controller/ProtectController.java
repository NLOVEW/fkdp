package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.service.ProtectService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/10 19:06
 * @Version 1.0
 * @Description:
 */
@RestController
public class ProtectController {
    @Resource
    private ProtectService protectService;

    @RequestMapping("/linghong/protect")
    public Response protecting(){
        boolean flag = protectService.deleteAll();
        if (flag){
            return new Response(true, 200,"触发器执行" ,"恭喜执行成功，请查看数据" );
        }
        return new Response(false, 101,"触发器执行失败" ,"触发器执行失败" );
    }
}
