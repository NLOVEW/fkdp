package com.linghong.fkdp.service;

import com.linghong.fkdp.constant.UrlConstant;
import com.linghong.fkdp.pojo.MessageBack;
import com.linghong.fkdp.pojo.User;
import com.linghong.fkdp.repository.MessageBackRepository;
import com.linghong.fkdp.repository.UserRepository;
import com.linghong.fkdp.utils.FastDfsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/28 15:15
 * @Version 1.0
 * @Description:
 */
@Service
public class MessageBackService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private MessageBackRepository messageBackRepository;
    @Resource
    private UserRepository userRepository;

    public boolean sendMessageBack(Long userId, MessageBack messageBack, String base64Image) {
        User user = userRepository.findById(userId).get();
        messageBack.setUser(user);
        if (base64Image != null){
            messageBack.setImagePath(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64Image));
        }
        messageBackRepository.save(messageBack);
        return true;
    }
}
