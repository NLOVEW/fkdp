package com.linghong.fkdp.service;

import com.linghong.fkdp.bean.Express;
import com.linghong.fkdp.constant.UrlConstant;
import com.linghong.fkdp.dto.GoodsExpressMessage;
import com.linghong.fkdp.pojo.*;
import com.linghong.fkdp.repository.*;
import com.linghong.fkdp.utils.FastDfsUtil;
import com.linghong.fkdp.utils.IDUtil;
import com.linghong.fkdp.utils.JwtUtil;
import com.linghong.fkdp.utils.SomeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/29 10:55
 * @Version 1.0
 * @Description:
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class GoodsOrderService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private GoodsOrderRepository goodsOrderRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private AddressRepository addressRepository;
    @Resource
    private GoodsExpressRepository goodsExpressRepository;
    @Resource
    private BackGoodsRepository backGoodsRepository;
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private PayService payService;

    @Value("${mq.pay.exchange}")
    private String exchange;
    @Value("${mq.pay.back.queue}")
    private String backOrderQueue;
    @Value("${mq.pay.back.routeKey}")
    private String backRouteKey;
    @Value("${mq.pay.sure.queue}")
    private String sureOrderQueue;
    @Value("${mq.pay.sure.routeKey}")
    private String sureRouteKey;

    public GoodsOrder pushGoodsOrder(GoodsOrder goodsOrder, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        User user = userRepository.findById(userId).get();
        if (!user.getAuth()) {
            return null;
        }
        Goods goods = goodsRepository.findById(goodsOrder.getGoods().getGoodsId()).get();
        goods.setNumber(goods.getNumber().intValue() - 1);
        if (goods.getNumber().intValue() <= 0 || goods.getStartTime().compareTo(new Date()) >=0 || goods.getEndTime().compareTo(new Date()) <= 0) {
            goods.setObtained(true);
            return null;
        }
        goodsOrder.setGoods(goods);
        goodsOrder.setUser(user);
        goodsOrder.setStatus(0);
        goodsOrder.setNumber(1);
        goodsOrder.setCreateTime(new Date());
        goodsOrder.setGoodsOrderId(IDUtil.getOrderId());
        goodsOrder = goodsOrderRepository.save(goodsOrder);

        Timer timer = new Timer();
        String goodsOrderId = goodsOrder.getGoodsOrderId();
        //开启定时任务  30min 没有支付则自动取消订单
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GoodsOrder target = goodsOrderRepository.findById(goodsOrderId).get();
                if (target.getStatus().intValue() <= 0 && target.getStatus().intValue() != 4) {
                    target.setStatus(4);
                    goodsRepository.findById(target.getGoods().getGoodsId()).get();
                }
            }
        }, 30 * 60 * 1000);
        return goodsOrder;
    }

    public List<GoodsOrder> getOrderByStatus(Long userId, Integer status) {
        logger.info("数据：{},{}", userId,status);
        try {
            List<GoodsOrder> orders = goodsOrderRepository.findAllByUser_UserIdAndStatusOrderByCreateTimeDesc(userId, status);
            orders = orders.stream().filter(order -> {
                if (order.getPickUp().equals(1) && order.getBackGoods() == null) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getOrderByPickUp(Long userId) {
        List<GoodsOrder> orders = goodsOrderRepository.findAllByUser_UserIdAndPickUpOrderByCreateTimeDesc(userId, 0);
        List<GoodsOrder> completeOrder = orders.stream().filter(order -> {
            return order.getStatus().equals(3);
        }).collect(Collectors.toList());
        List<GoodsOrder> disCompleteOrder = orders.stream().filter(order -> {
            if (order.getStatus().intValue() >= 1 && order.getStatus().intValue() <= 2) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("已完成订单", completeOrder);
        result.put("待完成订单", disCompleteOrder);
        return result;
    }

    public GoodsExpressMessage getExpressByGoodsOrderId(String goodsOrderId) {
        logger.info("查询订单物流：{}",goodsOrderId);
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        GoodsExpress goodsExpress = goodsOrder.getGoodsExpress();
        logger.info("物流信息{}", goodsExpress);
        String expressNumber = goodsExpress.getExpressNumber();
        String expressType = goodsExpress.getExpressType();
        if (expressNumber == null || expressType == null) {
            return null;
        } else {
            Express express = SomeUtil.getExpress(expressType, expressNumber);
            logger.info("查询的物流：{}", express.toString());
            GoodsExpressMessage expressMessage = new GoodsExpressMessage();
            expressMessage.setAddress(goodsExpress.getAddress());
            expressMessage.setCreateTime(goodsExpress.getCreateTime());
            expressMessage.setExpressData(express);
            expressMessage.setExpressType(goodsExpress.getExpressType());
            expressMessage.setGoodsExpressId(goodsExpress.getGoodsExpressId());
            expressMessage.setExpressNumber(goodsExpress.getExpressNumber());
            logger.info("物流新的---信息{}", expressMessage.toString());
            return expressMessage;
        }
    }

    public GoodsOrder getGoodsOrderByOrderId(String orderId) {
        return goodsOrderRepository.findById(orderId).get();
    }

    public boolean applyBackGoods(String goodsOrderId,
                                  String base64Images,
                                  BackGoods backGoods) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        backGoods.setCreateTime(new Date());
        if (base64Images != null){
            Set<Image> images = new HashSet<>();
            String[] split = base64Images.split("。");
            for (String base64 : split) {
                Image image = new Image();
                image.setCreateTime(new Date());
                image.setImagePath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(base64));
                images.add(image);
            }
            backGoods.setImages(images);
        }
        backGoods.setBackStatus(0);
        goodsOrder.setBackGoods(backGoods);
        goodsOrderRepository.save(goodsOrder);
        return true;
    }

    public List<GoodsOrder> getApplyBackOrderBySeller(Long userId) {
        List<GoodsOrder> goodsOrders = goodsOrderRepository.findAllByGoods_Merchant_User_UserId(userId);
        List<GoodsOrder> orders = goodsOrders.stream().filter(order -> {
            if (order.getBackGoods() != null && order.getBackGoods().getBackStatus().equals(0)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return orders;
    }

    public Map<Integer, List<GoodsOrder>> getApplyBackGoodsByBuyer(Long userId) {
        List<GoodsOrder> goodsOrders = goodsOrderRepository.findAllByUser_UserId(userId);
        Map<Integer, List<GoodsOrder>> result = goodsOrders.stream()
                .filter(order -> {
                    if (order.getBackGoods() != null) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.groupingBy((order) -> {
                    return order.getBackGoods().getBackStatus();
                }));
        result.forEach((key,value)->{
            logger.info("退换货订单：{}",value);
        });
        return result;
    }

    public boolean dealBackOrderByOrderId(String orderId, Integer status) {
        GoodsOrder order = goodsOrderRepository.findById(orderId).get();
        //根据backType 进行退换货.款
        if (status.equals(3)) {
            //0退换   1退款
            if (order.getBackGoods().getBackType().equals(0)) {
                order.getBackGoods().setBackStatus(status);
            } else {
                //处理方法 com.linghong.fkdp.service.PayService.backOrderTransfer
                //amqpTemplate.convertAndSend(exchange, backRouteKey, orderId);
                payService.backOrderTransfer(orderId);
            }
        }
        order.getBackGoods().setBackStatus(status);
        return true;
    }

    /**
     * @param orderId
     * @return
     * @see com.linghong.fkdp.service.PayService#sureOrder(java.lang.String)
     */
    public boolean surePickUp(String orderId) {
        logger.info("确定收货Id:{}", orderId);
        //amqpTemplate.convertAndSend(exchange, sureRouteKey, orderId);
        payService.sureOrder(orderId);
        return true;
    }

    public List<GoodsOrder> getAllCompleteOrder(Long userId) {
        List<GoodsOrder> orders = goodsOrderRepository.findAllByUser_UserId(userId);
        orders = orders.stream().filter(order -> {
          if (order.getStatus().equals(3) || order.getStatus().equals(4) || order.getBackGoods().getBackStatus().equals(2) || order.getBackGoods().getBackStatus().equals(3)){
              return true;
          }
          return false;
        }).collect(Collectors.toList());
        return orders;
    }

    public boolean sendGoods(String goodsOrderId, String expressNumber, String expressType) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        if (expressNumber != null && expressType != null){
            GoodsExpress goodsExpress = new GoodsExpress();
            goodsExpress.setExpressType(expressType);
            goodsExpress.setExpressNumber(expressNumber);
            goodsOrder.setGoodsExpress(goodsExpress);
        }
        goodsOrder.setStatus(2);
        goodsOrderRepository.save(goodsOrder);
        return true;
    }
}
