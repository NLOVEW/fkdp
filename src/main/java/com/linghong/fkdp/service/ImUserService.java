package com.linghong.fkdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linghong.fkdp.pojo.ImUser;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-01-06
 */
public interface ImUserService extends IService<ImUser> {
    ImUser findImUserMessageByMobilePhone(String mobilePhone);

    ImUser findImUserMessageByImUserId(String imUserId);

    int addImUser(ImUser imUser);
}
