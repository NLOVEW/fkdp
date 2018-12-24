package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.MessageBack;
import com.linghong.fkdp.service.MessageBackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/28 15:14
 * @Version 1.0
 * @Description:
 */
@RestController
public class MessageBackController {
    @Resource
    private MessageBackService messageBackService;

    /**
     * 提交意见反馈
     * 参数：messageType  message
     * @param request
     * @param messageBack
     * @return
     */
    @PostMapping("/messageBack/sendMessageBack")
    public Response sendMessageBack(MessageBack messageBack,
                                    @RequestParam(required = false) String base64Image,
                                    HttpServletRequest request){
        boolean flag = messageBackService.sendMessageBack(messageBack,base64Image,request);
        if (flag){
            return new Response(true,200 ,null ,"提交成功" );
        }
        return new Response(false,101 , null,"提交失败" );
    }
}
