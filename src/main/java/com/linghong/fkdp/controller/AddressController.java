package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.Address;
import com.linghong.fkdp.service.AddressService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/29 11:04
 * @Version 1.0
 * @Description:
 */
@RestController
public class AddressController {
    @Resource
    private AddressService addressService;
    // fixme 切记收货地址不可修改  只可删除  添加
    /**
     * 用户添加收货地址
     * 参数  当前用户userId  receiver 收货人  receiverPhone 收货人手机号
     *                      expressAddress 快递目的地址
     * @param userId
     * @param address
     * @return
     */
    @PostMapping("/address/addAddress")
    public Response addAddress(Long userId, Address address){
        boolean flag = addressService.addAddress(userId,address);
        if (flag){
            return new Response(true,200 ,null ,"添加成功" );
        }
        return new Response(false,101 ,null ,"添加失败" );
    }

    /**
     * 根据用户userId 获取收货地址列表
     * @param userId
     * @return
     */
    @GetMapping("/address/getAllAddressByUserId/{userId}")
    public Response getAllAddressByUserId(@PathVariable Long userId){
        List<Address> addresses = addressService.getAllAddressByUserId(userId);
        return new Response(true,200 ,addresses ,"地址列表" );
    }

    @GetMapping("/address/findAddressByAddressId/{addressId}")
    public Response findAddressByAddressId(@PathVariable Long addressId){
        Address address = addressService.findAddressByAddressId(addressId);
        return new Response(true,200 ,address ,"地址信息" );
    }

    /**
     * 根据addressId删除地址信息
     * @param addressId
     * @return
     */
    @DeleteMapping("/address/deleteAddress/{addressId}")
    public Response deleteAddress(@PathVariable Long addressId){
        boolean flag = addressService.deleteAddress(addressId);
        if (flag){
            return new Response(true,200 ,null ,"删除成功" );
        }
        return new Response(false,101 ,null ,"删除失败" );
    }
}
