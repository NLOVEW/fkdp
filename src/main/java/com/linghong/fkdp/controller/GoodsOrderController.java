package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.BackGoods;
import com.linghong.fkdp.pojo.GoodsExpress;
import com.linghong.fkdp.pojo.GoodsOrder;
import com.linghong.fkdp.service.GoodsOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/29 10:54
 * @Version 1.0
 * @Description:
 */
@RestController
public class GoodsOrderController {
    @Resource
    private GoodsOrderService goodsOrderService;


    //TODO --------------------消费方相关操作--------------------

    /**
     * 用户下单 当用户点击拍下时 首先其请求此路径  然后返回订单信息
     * <p>
     * 参数： 当前用户userId  goods.goodsId
     * price(拍下时的价钱+运费价钱)
     *
     * @param userId
     * @param goodsOrder
     * @return
     */
    @PostMapping("/order/pushGoodsOrder")
    public Response pushGoodsOrder(Long userId,
                                   GoodsOrder goodsOrder) {
        goodsOrder = goodsOrderService.pushGoodsOrder(userId, goodsOrder);
        if (goodsOrder != null) {
            return new Response(true, 200, goodsOrder, "成功下单");
        }
        return new Response(false, 101, null, "暂未通过身份审核");
    }

    /**
     * 根据订单状态查询
     *
     * @param userId 用户id
     * @param status 1待发货订单  2待收货订单
     * @return
     */
    @GetMapping("/order/getOrderByStatus/{userId}/{status}")
    public Response getOrderByStatus(@PathVariable Long userId,
                                     @PathVariable Integer status) {
        List<GoodsOrder> goodsOrders = goodsOrderService.getOrderByStatus(userId, status);
        return new Response(true, 200, goodsOrders, "订单查询结果");
    }

    /**
     * 上门取货所有订单 完成或未完成的
     *
     * @param userId
     * @return
     */
    @GetMapping("/order/getOrderByPickUp/{userId}")
    public Response getOrderByPickUp(@PathVariable Long userId) {
        Map<String, Object> result = goodsOrderService.getOrderByPickUp(userId);
        return new Response(true, 200, result, "订单查询结果");
    }

    /**
     * 确认收货
     *
     * @param orderId
     * @return
     */
    @GetMapping("/order/surePickUp/{orderId}")
    public Response surePickUp(@PathVariable String orderId) {
        boolean flag = goodsOrderService.surePickUp(orderId);
        if (flag) {
            return new Response(true, 200, null, "操作成功");
        }
        return new Response(false, 101, null, "操作失败");
    }

    /**
     * 买家申请退款 或 退换
     * 参数： goodsOrderId 原来的订单Id
     * backType 0退换 1退款
     * sureGoods 当前是否已收到货物 0未收到  1已收到
     * backReason 退款/退换说明
     * backPrice 退款金额
     * base64Images 上传凭证
     *
     * @param goodsOrderId
     * @param backGoods
     * @return
     */
    @PostMapping("/order/applyBackGoods")
    public Response applyBackGoods(String goodsOrderId,
                                   @RequestParam(required = false) String base64Images,
                                   BackGoods backGoods) {
        boolean flag = goodsOrderService.applyBackGoods(goodsOrderId, base64Images, backGoods);
        if (flag) {
            return new Response(true, 200, null, "请等待商家回复");
        }
        return new Response(false, 101, null, "申请失败");
    }

    /**
     * 根据用户userId 查询其申请的退款情况
     * 数据分为三组  0等待卖家确认 1卖家同意但未退款或换货 2卖家拒绝 3卖家退款或退换
     *
     * @param userId
     * @return
     */
    @GetMapping("/order/getApplyBackGoodsByBuyer/{userId}")
    public Response getApplyBackGoodsByBuyer(@PathVariable Long userId) {
        Map<Integer, List<GoodsOrder>> result = goodsOrderService.getApplyBackGoodsByBuyer(userId);
        return new Response(true, 200, result, "检索结果");
    }

    /**
     * 买家
     * 根据用户userId 查询所有已完成的订单
     *
     * @param userId
     * @return
     */
    @GetMapping("/order/getAllCompleteOrder")
    public Response getAllCompleteOrder(@PathVariable Long userId) {
        List<GoodsOrder> goodsOrders = goodsOrderService.getAllCompleteOrder(userId);
        return new Response(true, 200, goodsOrders, "检索结果");
    }


    //todo ----------------------结束消费方相关操作--------------------

    //TODO ----------------------商家相关操作--------------------------

    /**
     * 商家发货
     * 参数：goodsOrderId
     * expressType 快递公司名称
     * expressNumber 快递单号
     *
     *
     * @return
     */
    @PostMapping("/order/sendGoods")
    public Response sendGoods(String goodsOrderId,
                              @RequestParam(required = false) String expressType,
                              @RequestParam(required = false) String expressNumber) {
        boolean flag = goodsOrderService.sendGoods(goodsOrderId,expressNumber,expressType);
        if (flag) {
            return new Response(true, 200, null, "操作成功");
        }
        return new Response(false, 101, null, "操作失败");
    }

    /**
     * 根据商家用户id 获取买家申请退换货、款的订单
     *
     * @param userId
     * @return
     */
    @GetMapping("/order/getApplyBackOrderBySeller/{userId}")
    public Response getApplyBackOrderBySeller(@PathVariable Long userId) {
        List<GoodsOrder> orders = goodsOrderService.getApplyBackOrderBySeller(userId);
        return new Response(true, 200, orders, "退换、退款申请");
    }

    /**
     * 商家处理退换款订单
     *
     * @param orderId 订单Id
     * @param status  1卖家同意但未退款或退换 2卖家拒绝
     * @return
     */
    @PostMapping("/order/dealBackOrderByOrderId")
    public Response dealBackOrderByOrderId(String orderId, Integer status) {
        boolean flag = goodsOrderService.dealBackOrderByOrderId(orderId, status);
        if (flag) {
            return new Response(true, 200, null, "操作成功");
        }
        return new Response(false, 101, null, "操作失败");
    }


    //todo ----------------------结束商家操作-----------------------


    //todo --------------------订单公共操作接口----------------

    /**
     * 查询物流信息
     *
     * @param goodsOrderId
     * @return
     */
    @GetMapping("/order/getExpressByGoodsOrderId/{goodsOrderId}")
    public Response getExpressByGoodsOrderId(@PathVariable String goodsOrderId) {
        GoodsExpress goodsExpress = goodsOrderService.getExpressByGoodsOrderId(goodsOrderId);
        if (goodsExpress != null) {
            return new Response(true, 200, goodsExpress, "物流详情");
        }
        return new Response(false, 101, null, "商家未填写快递信息");
    }

    /**
     * 根据订单Id 查询订详细信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("/order/getGoodsOrderByOrderId/{orderId}")
    public Response getGoodsOrderByOrderId(@PathVariable String orderId) {
        GoodsOrder goodsOrder = goodsOrderService.getGoodsOrderByOrderId(orderId);
        return new Response(true, 200, goodsOrder, "订单详细信息");
    }
}
