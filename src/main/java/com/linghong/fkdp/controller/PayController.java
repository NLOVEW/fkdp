package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.Bill;
import com.linghong.fkdp.pojo.GoodsOrder;
import com.linghong.fkdp.pojo.Wallet;
import com.linghong.fkdp.service.PayService;
import com.nhb.pay.common.bean.TransferOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/28 15:44
 * @Version 1.0
 * @Description:
 */
@RestController
@Scope("prototype")
public class PayController {
    @Resource
    private PayService payService;

    /**
     * 根据userId 查询其所有订单
     *
     * @param userId
     * @return
     */
    @GetMapping("/pay/getBills/{userId}")
    public Response getBills(@PathVariable Long userId) {
        List<Bill> bills = payService.getBills(userId);
        return new Response(true, 200, bills, "查询结果");
    }

    /**
     * 根据billId 查询详细的账单信息
     *
     * @param billId
     * @return
     */
    @GetMapping("/pay/getDetailBill/{billId}")
    public Response getDetailBill(@PathVariable Long billId) {
        Bill bill = payService.getDetailBill(billId);
        return new Response(true, 200, bill, "查询结果");
    }

    /**
     * 根据userId 查询钱包
     *
     * @param userId
     * @return
     */
    @GetMapping("/pay/getWallet/{userId}")
    public Response getWallet(@PathVariable Long userId) {
        Wallet wallet = payService.getWallet(userId);
        return new Response(true, 200, wallet, "查询结果");
    }

    //todo ------------------以下请求  金额全部是转到平台账号中---------------
    /**
     * 支付宝充值
     *
     * @param userId
     * @param price
     * @return
     */
    @PostMapping("/pay/aliRecharge")
    public String aliRecharge(Long userId, BigDecimal price) {
        String result = payService.aliRecharge(userId, price);
        return result;
    }

    /**
     * 支付宝支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/pay/aliCallBack")
    public Response aliCallBack(HttpServletRequest request) {
        boolean flag = payService.aliRechargeCallBack(request);
        if (flag) {
            return new Response(true, 200, null, "支付成功");
        }
        return new Response(false, 101, null, "支付失败");
    }

    /**
     * 微信充值
     *
     * @param userId
     * @param price
     * @param request
     * @return
     */
    @PostMapping("/pay/wxRecharge")
    public String wxRecharge(Long userId, BigDecimal price, HttpServletRequest request) {
        String result = payService.wxRecharge(userId, price, request);
        return result;
    }

    /**
     * 微信支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/pay/wxRechargeCallBack")
    public Response wxRechargeCallBack(HttpServletRequest request) {
        boolean flag = payService.wxRechargeCallBack(request);
        if (flag) {
            return new Response(true, 200, null, "支付成功");
        }
        return new Response(false, 101, null, "支付失败");
    }

    /**
     * 支付宝支付
     * 参数：pickUp(取货方式  0上门取货 1商家发货)
     *       如果选择商家发货  请带上 addressId
     *       如果选择上门取货  请携带 linkMan.userName linkMan.mobilePhone
     *
     * @return
     */
    @PostMapping("/pay/aliPay")
    public String aliPay(GoodsOrder goodsOrder,
                         @RequestParam(required = false) Long addressId) {
        return payService.aliPay(goodsOrder, addressId);
    }

    /**
     * 支付宝支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/pay/aliPayCallBack")
    public Response aliPayCallBack(HttpServletRequest request) {
        boolean flag = payService.aliPayCallBack(request);
        if (flag) {
            return new Response(true, 200, null, "支付成功");
        }
        return new Response(false, 101, null, "支付失败");
    }

    /**
     * 微信支付
     *  参数：pickUp(取货方式  0上门取货 1商家发货)
     *      如果选择商家发货  请带上 addressId
     *      如果选择上门取货  请携带 linkMan.userName linkMan.mobilePhone
     *
     * @return
     */
    @PostMapping("/pay/wxPay")
    public String wxPay(GoodsOrder goodsOrder,
                        @RequestParam(required = false) Long addressId,
                        HttpServletRequest request) {
        return payService.wxPay(goodsOrder, addressId,  request);
    }

    public Response wxPayCallBack(HttpServletRequest request) {
        boolean flag = payService.wxPayCallBack(request);
        if (flag) {
            return new Response(true, 200, null, "支付成功");
        }
        return new Response(false, 101, null, "支付失败");
    }

    //todo ------------------------------结束-----------------------------

    //todo -----------------------------以下转到固定的用户账号中---------------
    /**
     * 转账到支付宝
     * 参数：userId payeeAccount(目标手机号) amount（金额）
     *
     * @param userId
     * @param
     * @return
     */
    @PostMapping("/pay/aliTransfer")
    public Response aliTransfer(Long userId, TransferOrder transferOrder) {
        boolean flag = payService.aliTransfer(userId, transferOrder);
        if (flag) {
            return new Response(true, 200, null, "转账成功");
        }
        return new Response(false, 101, null, "转账失败");
    }

}
