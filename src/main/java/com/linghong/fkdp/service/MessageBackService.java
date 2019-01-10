package com.linghong.fkdp.service;

import com.linghong.fkdp.constant.UrlConstant;
import com.linghong.fkdp.pojo.MessageBack;
import com.linghong.fkdp.pojo.User;
import com.linghong.fkdp.repository.MessageBackRepository;
import com.linghong.fkdp.repository.UserRepository;
import com.linghong.fkdp.utils.FastDfsUtil;
import com.linghong.fkdp.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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

    public boolean sendMessageBack(MessageBack messageBack, String base64Image, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        User user = userRepository.findById(userId).get();
        messageBack.setUser(user);
        if (base64Image != null) {
            String[] split = base64Image.split("。");
            messageBack.setImagePath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(split[0]));
        }
        messageBack.setPushTime(new Date());
        messageBackRepository.save(messageBack);
        return true;
    }
}
