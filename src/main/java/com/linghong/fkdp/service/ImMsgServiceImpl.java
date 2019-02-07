package com.linghong.fkdp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghong.fkdp.mapper.ImFriendMapper;
import com.linghong.fkdp.mapper.ImMsgMapper;
import com.linghong.fkdp.mapper.ImUserMapper;
import com.linghong.fkdp.pojo.ImMsg;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope(value = "prototype")
@Service("imMsgServiceImpl")
public class ImMsgServiceImpl extends ServiceImpl<ImMsgMapper, ImMsg> implements ImMsgService {
    @Resource
    private ImMsgMapper imMsgMapper;
    @Resource
    private ImUserMapper imUserMapper;
    @Resource
    private ImFriendMapper imFriendMapper;

    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        List<ImMsg> imMsgs = imMsgMapper.selectBatchIds(msgIdList);
        for (ImMsg imMsg : imMsgs) {
            imMsg.setStatus(1);
            imMsgMapper.updateById(imMsg);
        }
    }

    @Override
    public Map<String, Object> getOldMessage(String senderId, String receiverId) {
        QueryWrapper<ImMsg> wrapper = new QueryWrapper<>();
        wrapper.or(i -> i.eq("sender_id", senderId).eq("receiver_id", receiverId))
                .or(i ->
                        i.eq("sender_id", receiverId)
                                .eq("receiver_id", senderId)
                )
                .orderByDesc("create_time");
        List<ImMsg> imMsgs = imMsgMapper.selectList(wrapper);
        int temp = 0;
        for (ImMsg imMsg : imMsgs) {
            if (imMsg.getStatus().equals(0)) {
                temp++;
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("聊天记录", imMsgs);
        result.put("未读记录数量", temp);
        return result;
    }
}
