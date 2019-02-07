package com.linghong.fkdp.service;

import com.linghong.fkdp.pojo.*;
import com.linghong.fkdp.repository.*;
import com.linghong.fkdp.utils.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/27 20:47
 * @Version 1.0
 * @Description:
 */
@Service
public class AdminService {
    @Resource
    private AdminRepository adminRepository;
    @Resource
    private BackGoodsRepository backGoodsRepository;
    @Resource
    private BillRepository billRepository;
    @Resource
    private GoodsOrderRepository goodsOrderRepository;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private MerchantRepository merchantRepository;
    @Resource
    private MessageBackRepository messageBackRepository;
    @Resource
    private UserRepository userRepository;

    public Admin login(String mobilePhone, String password) {
        Admin admin = adminRepository.findByMobilePhone(mobilePhone);
        if (admin.getPassword().equals(MD5Util.md5(password))){
            return admin;
        }
        return null;
    }

    public Map<String, Object> index() {
        Map<String,Object> result = new HashMap<>();
        List<Goods> goodsList = goodsRepository.findAll();
        result.put("goodsList", goodsList);
        List<GoodsOrder> goodsOrderList = goodsOrderRepository.findAll();
        result.put("goodsOrderList", goodsOrderList);
        List<User> userList = userRepository.findAll();
        result.put("userList", userList);
        List<Merchant> merchantList = merchantRepository.findAll();
        result.put("merchantList",merchantList);
        List<MessageBack> messageBackList = messageBackRepository.findAll();
        result.put("messageBackList", messageBackList);
        List<BackGoods> backGoodsList = backGoodsRepository.findAll();
        result.put("backGoodsList", backGoodsList);


        return result;
    }

    public void updatePassword(Admin admin) {
        adminRepository.save(admin);
    }
}
