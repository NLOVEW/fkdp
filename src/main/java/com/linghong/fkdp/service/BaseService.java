package com.linghong.fkdp.service;


import com.linghong.fkdp.utils.SmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * 基本功能Service
 */
@Service("baseService")
@Scope("prototype")
public class BaseService {
    private static Logger logger = LoggerFactory.getLogger(BaseService.class);

    /**
     * 产生手机短信验证码
     *
     * @param mobilePhone
     * @return 正常则返回六位手机验证码，否则返回null
     */
    public String getCode(String mobilePhone) {
        String code = null;
        try {
            //todo 上线时更换公司信息
            code = SmsUtil.sendCode("LTAI9F7kNul9FmTl", "pxTseyiiIMmD9JMjanZuvRWI8ttgkB",
                    "树洞", "SMS_142145653", mobilePhone);
        } catch (Exception e) {
            logger.error("Error:手机验证码发送超时");
        }
        return code;
    }
}
