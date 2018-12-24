package com.linghong.fkdp.service;

import com.linghong.fkdp.constant.UrlConstant;
import com.linghong.fkdp.pojo.User;
import com.linghong.fkdp.pojo.Wallet;
import com.linghong.fkdp.repository.UserRepository;
import com.linghong.fkdp.repository.WalletRepository;
import com.linghong.fkdp.utils.*;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/27 15:28
 * @Version 1.0
 * @Description:
 */
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private UserRepository userRepository;
    @Resource
    private WalletRepository walletRepository;

    public User register(User user) {
        User secondUser = userRepository.findByMobilePhone(user.getMobilePhone());
        if (secondUser == null){
            Subject subject = SecurityUtils.getSubject();
            ByteSource salt = ByteSource.Util.bytes(user.getMobilePhone());
			UsernamePasswordToken token = new UsernamePasswordToken(user.getMobilePhone(), user.getPassword());
            String md5 = new SimpleHash("MD5", user.getPassword(), salt, 2).toHex();
            user.setPassword(md5);
            user.setAuth(false);
            user.setCreateTime(new Date());
            user = userRepository.save(user);
			Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setCreateTime(new Date());
            wallet.setWalletId(IDUtil.getId());
            wallet.setBalance(new BigDecimal(0));
            walletRepository.save(wallet);
            subject.login(token);
            return user;
        }
        return null;
    }

    public User login(User user) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()){
            UsernamePasswordToken token = new UsernamePasswordToken(user.getMobilePhone(),user.getPassword() );
            subject.login(token);
            logger.info("进行登录");
        }
        user = userRepository.findByMobilePhone(user.getMobilePhone());
        return user;
    }

    public User findUserByUserId(Long userId) {
        User user = userRepository.findById(userId).get();
        return user;
    }

    public User updateUserMessage(User user,HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        User target = userRepository.findById(userId).get();
        BeanUtil.copyPropertiesIgnoreNull(user,target);
        userRepository.save(target);
        return target;
    }

    public User uploadAvatar(String base64Avatar,HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        logger.info("userId:{}", userId);
        User user = userRepository.findById(userId).get();
        user.setAvatar(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64Avatar));
        userRepository.save(user);
        return user;
    }

    public boolean updatePassword(String mobilePhone,String password) {
        User user = userRepository.findByMobilePhone(mobilePhone);
        ByteSource salt = ByteSource.Util.bytes(user.getMobilePhone());
        String md5 = new SimpleHash("MD5", password, salt, 2).toHex();
        user.setPassword(md5);
        userRepository.save(user);
        return true;
    }

    public boolean uploadIdCard(HttpServletRequest request,String base64IdCard, String idCardNumber) {
        Long userId = JwtUtil.getUserId(request);
        User user = userRepository.findById(userId).get();
        if (idCardNumber != null){
            boolean flag = IdCardUtil.idCardValidate(idCardNumber);
            if (!flag){
                return false;
            }
            user.setIdCardNumber(idCardNumber);
        }
        if (base64IdCard != null){
            user.setIdCardPath(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64IdCard));
        }
        userRepository.save(user);
        return true;
    }

    public User getCurrentUserMessage(HttpServletRequest request) {
        Claims parameter = JwtUtil.getParameterByHttpServletRequest(request);
        Long userId = (Long) parameter.get("userId");
        User user = userRepository.findById(userId).get();
        return user;
    }
}
