package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.Merchant;
import com.linghong.fkdp.service.MerchantService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
     * @param base64License
     * @param merchant
     * @return
     */
    @PostMapping("/merchant/addMerchant")
    public Response addMerchant(Merchant merchant,
                                @RequestParam(required = false) String base64License,
                                HttpServletRequest request){
        boolean flag = merchantService.addMerchant(merchant,base64License,request);
        if (flag){
            return new Response(true,200 ,null ,"请等待认证" );
        }
        return new Response(false,101 ,null ,"一个账号只允许添加一个商铺" );
    }

    /**
     * 根据用户Id获取其名下的商铺
     * @param userId
     * @return
     */
    @GetMapping("/merchant/findMerchantByUserId/{userId}")
    public Response findMerchantByUserId(@PathVariable Long userId){
        Merchant merchant = merchantService.findMerchantByUserId(userId);
        if (merchant != null){
            return new Response(true,200 , merchant, "查询结果");
        }
        return new Response(false,101 , null,"没有入驻商铺" );
    }
}
