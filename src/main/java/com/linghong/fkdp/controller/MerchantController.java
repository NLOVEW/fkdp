package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.Merchant;
import com.linghong.fkdp.service.MerchantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/28 14:43
 * @Version 1.0
 * @Description:
 */
@RestController
public class MerchantController {
    @Resource
    private MerchantService merchantService;

    /**
     * 用户入驻商铺
     * 参数：merchantName mobilePhone  address
     * @param userId
     * @param merchant
     * @return
     */
    @PostMapping("/merchant/addMerchant")
    public Response addMerchant(Long userId,
                                Merchant merchant,
                                @RequestParam(required = false) String base64License){
        boolean flag = merchantService.addMerchant(userId,merchant,base64License);
        if (flag){
            return new Response(true,200 ,null ,"请等待认证" );
        }
        return new Response(false,101 ,null ,"一个账号只允许添加一个商铺" );
    }

    @GetMapping("/merchant/findMerchantByUserId")
    public Response findMerchantByUserId(Long userId){
        Merchant merchant = merchantService.findMerchantByUserId(userId);
        if (merchant != null){
            return new Response(true,200 , null, "查询结果");
        }
        return new Response(false,101 , null,"没有入驻商铺" );
    }
}
