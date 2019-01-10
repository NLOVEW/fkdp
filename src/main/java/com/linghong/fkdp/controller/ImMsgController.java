package com.linghong.fkdp.controller;


import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.service.ImMsgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class ImMsgController {
    @Resource(name = "imMsgServiceImpl")
    private ImMsgService imMsgService;

    /**
     * 获取与好友的历史聊天记录 包括未读记录
     * @param senderId     本用户Id
     * @param receiverId   对方Id
     * @return
     */
    @GetMapping("/im/getOldMessage/{senderId}/{receiverId}")
    public Response getOldMessage(@PathVariable String senderId,
                                  @PathVariable String receiverId) {
        Map<String,Object> imMsgList = imMsgService.getOldMessage(senderId,receiverId);
        if (imMsgList != null && imMsgList.size() > 0){
            return new Response(true, 200, imMsgList, "历史记录");
        }
        return new Response(false, 101, null, "历史记录");
    }
}

