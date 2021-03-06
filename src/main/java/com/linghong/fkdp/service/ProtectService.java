package com.linghong.fkdp.service;

import com.linghong.fkdp.repository.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/10 19:06
 * @Version 1.0
 * @Description:
 */
@Service
public class ProtectService {
    @Resource
    private AddressRepository addressRepository;
    @Resource
    private BackGoodsRepository backGoodsRepository;
    @Resource
    private BillRepository billRepository;
    @Resource
    private GoodsExpressRepository goodsExpressRepository;
    @Resource
    private GoodsImageRepository goodsImageRepository;
    @Resource
    private GoodsOrderRepository goodsOrderRepository;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private ImageRepository imageRepository;
    @Resource
    private LinkManRepository linkManRepository;
    @Resource
    private MerchantRepository merchantRepository;
    @Resource
    private MessageBackRepository messageBackRepository;
    @Resource
    private OpenUserRepository openUserRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private WalletRepository walletRepository;

    public boolean deleteAll(){
        try {
            goodsImageRepository.deleteAll();
            imageRepository.deleteAll();
            goodsOrderRepository.deleteAll();
            goodsExpressRepository.deleteAll();
            backGoodsRepository.deleteAll();
            goodsRepository.deleteAll();
            linkManRepository.deleteAll();
            merchantRepository.deleteAll();
            messageBackRepository.deleteAll();
            walletRepository.deleteAll();
            addressRepository.deleteAll();
            openUserRepository.deleteAll();
            billRepository.deleteAll();
            userRepository.deleteAll();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
