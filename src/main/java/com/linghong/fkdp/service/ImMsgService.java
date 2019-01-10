package com.linghong.fkdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linghong.fkdp.pojo.ImMsg;

import java.util.List;
import java.util.Map;


public interface ImMsgService extends IService<ImMsg> {
    void updateMsgSigned(List<String> msgIdList);

    Map<String,Object> getOldMessage(String senderId, String receiverId);
}
