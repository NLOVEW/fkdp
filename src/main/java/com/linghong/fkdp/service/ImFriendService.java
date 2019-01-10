package com.linghong.fkdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linghong.fkdp.pojo.ImFriend;
import com.linghong.fkdp.pojo.ImUser;

import java.util.List;


public interface ImFriendService extends IService<ImFriend> {

    List<ImUser> findFriendsByMobilePhone(String mobilePhone);

    List<ImUser> findFriendsByImUserId(String imUserId);

    String addFriend(String fromMobilePhone, String toMobilePhone, Integer status);

    boolean deleteFriend(String fromMobilePhone, String toMobilePhone);
}
