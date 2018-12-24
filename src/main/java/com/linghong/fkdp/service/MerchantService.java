package com.linghong.fkdp.service;

import com.linghong.fkdp.constant.UrlConstant;
import com.linghong.fkdp.pojo.Merchant;
import com.linghong.fkdp.pojo.User;
import com.linghong.fkdp.repository.MerchantRepository;
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
 * @Date: 2018/11/28 14:43
 * @Version 1.0
 * @Description:
 */
@Service
public class MerchantService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private MerchantRepository merchantRepository;
    @Resource
    private UserRepository userRepository;

    public boolean addMerchant(Merchant merchant, String base64License, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        Merchant byUser_userId = merchantRepository.findByUser_UserId(userId);
        if (byUser_userId != null){
            return false;
        }
        User user = userRepository.findById(userId).get();
        merchant.setUser(user);
        if (base64License != null){
            merchant.setBusinessLicense(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64License));
        }
        merchant.setCreateTime(new Date());
        merchant.setAuth(false);
        merchantRepository.save(merchant);
        return true;
    }

    public Merchant findMerchantByUserId(Long userId) {
        Merchant merchant = merchantRepository.findByUser_UserId(userId);
        return merchant;
    }
}
