package com.linghong.fkdp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghong.fkdp.mapper.ImUserMapper;
import com.linghong.fkdp.pojo.ImUser;
import com.linghong.fkdp.utils.IDUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Scope(value = "prototype")
@Service("imUserServiceImpl")
public class ImUserServiceImpl extends ServiceImpl<ImUserMapper, ImUser> implements ImUserService {
    @Resource
    private ImUserMapper imUserMapper;

    @Override
    public ImUser findImUserMessageByMobilePhone(String mobilePhone) {
        QueryWrapper<ImUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user_mobile_phone", mobilePhone);
        ImUser imUser = imUserMapper.selectOne(wrapper);
        return imUser;
    }

    @Override
    public ImUser findImUserMessageByImUserId(String imUserId) {
        ImUser imUser = imUserMapper.selectById(imUserId);
        return imUser;
    }

    @Override
    public int addImUser(ImUser imUser) {
        imUser.setUserId(IDUtil.getImId());
        int insert = imUserMapper.insert(imUser);
        return insert;
    }
}
