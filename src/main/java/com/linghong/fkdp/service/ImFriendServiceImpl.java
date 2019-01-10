package com.linghong.fkdp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghong.fkdp.mapper.ImFriendMapper;
import com.linghong.fkdp.mapper.ImUserMapper;
import com.linghong.fkdp.pojo.ImFriend;
import com.linghong.fkdp.pojo.ImUser;
import com.linghong.fkdp.utils.IDUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service("imFriendServiceImpl")
public class ImFriendServiceImpl extends ServiceImpl<ImFriendMapper, ImFriend> implements ImFriendService {
    @Resource
    private ImFriendMapper imFriendMapper;
    @Resource
    private ImUserMapper imUserMapper;

    @Override
    public List<ImUser> findFriendsByMobilePhone(String mobilePhone) {
        QueryWrapper<ImUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user_mobile_phone", mobilePhone);
        ImUser imUser = imUserMapper.selectOne(wrapper);
        //获取到好友中间表数据
        List<ImFriend> friends = imFriendMapper.selectList(new QueryWrapper<ImFriend>()
                .eq("from_user_id", imUser.getUserId())
                .or()
                .eq("to_user_id", imUser.getUserId()));
        //根据中间表信息获取好友的详细信息
        List<ImUser> imUsers = new ArrayList<>();
        for (ImFriend friend : friends) {
            ImUser user = null;
            if (friend.getToUserId().equals(imUser.getUserId())){
                user = imUserMapper.selectById(friend.getFromUserId());
            }else {
                user = imUserMapper.selectById(friend.getToUserId());
            }
            imUsers.add(user);
        }
        return imUsers;
    }

    @Override
    public List<ImUser> findFriendsByImUserId(String imUserId) {
        ImUser imUser = imUserMapper.selectById(imUserId);
        //获取到好友中间表数据
        List<ImFriend> friends = imFriendMapper.selectList(new QueryWrapper<ImFriend>()
                .eq("from_user_id", imUser.getUserId())
                .or()
                .eq("to_user_id", imUser.getUserId()));
        //根据中间表信息获取好友的详细信息
        List<ImUser> imUsers = new ArrayList<>();
        for (ImFriend friend : friends) {
            ImUser user = null;
            if (friend.getToUserId().equals(imUserId)){
                user = imUserMapper.selectById(friend.getFromUserId());
            }else {
                user = imUserMapper.selectById(friend.getToUserId());
            }
            imUsers.add(user);
        }
        return imUsers;
    }

    @Override
    public String addFriend(String fromMobilePhone, String toMobilePhone, Integer status) {
        ImUser fromUser = imUserMapper.selectOne(new QueryWrapper<ImUser>().eq("user_mobile_phone", fromMobilePhone));
        ImUser toUser = imUserMapper.selectOne(new QueryWrapper<ImUser>().eq("user_mobile_phone", toMobilePhone));
        //查询是否已经是好友
        List<ImFriend> friends = imFriendMapper.selectList(new QueryWrapper<ImFriend>()
                .eq("from_user_id", fromUser.getUserId())
                .or()
                .eq("to_user_id", fromUser.getUserId()));
        if (friends != null && friends.size() > 0){
            return "已是好友";
        }
        ImFriend friend = new ImFriend();
        friend.setFriendId(IDUtil.getImId());
        friend.setCreateTime(LocalDateTime.now());
        friend.setFromUserId(fromUser.getUserId());
        friend.setToUserId(toUser.getUserId());
        friend.setStatus(status);
        int insert = imFriendMapper.insert(friend);
        if (insert > 0){
            return "添加成功";
        }
        return "添加失败";
    }

    @Override
    public boolean deleteFriend(String fromMobilePhone, String toMobilePhone) {
        ImUser fromUser = imUserMapper.selectOne(new QueryWrapper<ImUser>().eq("user_mobile_phone", fromMobilePhone));
        ImUser toUser = imUserMapper.selectOne(new QueryWrapper<ImUser>().eq("user_mobile_phone", toMobilePhone));
        //查询是否已经是好友
        List<ImFriend> friends = imFriendMapper.selectList(new QueryWrapper<ImFriend>()
                .eq("from_user_id", fromUser.getUserId())
                .or()
                .eq("to_user_id", fromUser.getUserId()));
        if (friends != null && friends.size() > 0){
            for (ImFriend friend : friends){
                if (friend.getFromUserId().equals(toUser.getUserId()) || friend.getToUserId().equals(toUser.getUserId())){
                    imFriendMapper.deleteById(friend.getFriendId());
                }
            }
        }
        return true;
    }
}
