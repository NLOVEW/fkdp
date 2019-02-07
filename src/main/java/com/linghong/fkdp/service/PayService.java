package com.linghong.fkdp.service;

import com.alibaba.fastjson.JSON;
import com.linghong.fkdp.pojo.*;
import com.linghong.fkdp.repository.*;
import com.linghong.fkdp.utils.BeanUtil;
import com.linghong.fkdp.utils.IDUtil;
import com.nhb.pay.alipay.AliPayConfig;
import com.nhb.pay.alipay.AliPayService;
import com.nhb.pay.alipay.AliTransactionType;
import com.nhb.pay.alipay.AliTransferResult;
import com.nhb.pay.common.bean.PayOrder;
import com.nhb.pay.common.bean.TransferOrder;
import com.nhb.pay.common.http.HttpConfig;
import com.nhb.pay.common.type.MethodType;
import com.nhb.pay.common.utils.SignUtils;
import com.nhb.pay.wxpay.WxPayConfig;
import com.nhb.pay.wxpay.WxPayService;
import com.nhb.pay.wxpay.WxTransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/13 10:34
 * @Version 1.0
 * @Description:
 */
@Service
@Transactional(rollbackOn = Exception.class)
@Scope("prototype")
public class PayService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static AliPayService aliPayService = null;
    private static WxPayService wxPayService = null;
    private static AliPayConfig aliPayConfig = new AliPayConfig();
    private static WxPayConfig wxPayConfig = new WxPayConfig();
    private static HttpConfig httpConfig = new HttpConfig();

    @Resource
    private BillRepository billRepository;
    @Resource
    private WalletRepository walletRepository;
    @Resource
    private RedisService redisService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private GoodsOrderRepository goodsOrderRepository;
    @Resource
    private AddressRepository addressRepository;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private LinkManRepository linkManRepository;
    @Resource
    private GoodsService goodsService;

    @Value("${mq.pay.exchange}")
    private String exchange;
    @Value("${mq.pay.error.queue}")
    private String errorOrderQueue;
    @Value("${mq.pay.error.routeKey}")
    private String errorRouteKey;

    /**
     * 初始化支付信息
     */
    @PostConstruct
    public void init() {
        //todo 上线时 修改所有信息 支付宝配置文件-----------------------------------
        aliPayConfig.setPid("2088331994426085");
        aliPayConfig.setAppId("2019010562862213");
        //aliPayConfig.setKeyPublic("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyTbBrE5t/7+gTMj/u93+7GSV+r4UxV+oFiNJWjf0bAs7yATL31F04FTsuS88nHfrLaAHh4Pkyd+PF1yOq5ZmGwdbplBjC3l44civ26QVsx/PmVT/nhvph4O7KF0xrhhFasC9iZ23VJ5l7unwZx+pmoSr1VvuLCB5WVj+7VnRpBMQU2gRseHJ7RvbkCR7LINzReRy8aE1N3UD/XVMAY344MfKFOZXVkTPqHkBT4/Rh/DgQOud9Q8jyZUiHTKLoOjnYGy8YzxLA/LiSjksIbLAAnjk3PwCn9LyTf8nHdOr8MwzgRKi5z4WgJPppzUW3OMtzdsaV3rRBorWcYLzW7kpcwIDAQAB");
        aliPayConfig.setKeyPublic("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlBbRR+gvqF75c6tUXofOExH2/IQGbJLTyQ3MEHdG7Nj3sBil5oAlXnLCSQhFxT9PBWDLoGCnOuXiy+Jglg5qH91P5mMTMRfVSc25ljBvDT+4l9p9bDFLr87bawRE/oKOlQfwVo3pmsviVZHU+yv9NegYOROtt+ivyukEQlUvDeLR41pLbPSYRAzk49ACPdh9A3j5OWGqbz7z9LJLnmYf59IHTN/4RsNUHHdV0Dgf5xA84u9kaQelraIDZbAL8X8uJj9KofUmdXSRu2qSeOly7sjbv1NmxohC5oaelqThmtJeKDHvaAS/c87X4FHoANX56FVdbDk7ZX7RJqGymVuZQwIDAQAB");
       // aliPayConfig.setKeyPrivate("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDp4P1EFIm2TqCIKSC3hH8BhbH61QrlMZkLP+WvhJQYOxmbg/XgiOKLgtyfasA0DoiZNgxoWjmmRwvcXV8G+r2UWLc9zhU1pttZPrljbr03qqwqeiZKRoanM5fxE3XxxVuEARV0iuk+8wtgN3D8k2Qy7YFQJGF18xR84QzgH933fk02PdScbm4W5WBDnd6Nil6MBuTVLE+YzX7/55dHEY33WUdWdgDJZRsPp3ASwG2Q2/4ZRQxFH/wi5ZpgeXbxn7V2Ugz5IKcyXfe8hzDL215tKRiaXW1Uc1TLdhl7ptZGQ6EYeaVhPXiNFECtlOJq+rtzVpxtREvwCAAu6Jd7S9yVAgMBAAECggEAPrgWUzaKw7gMNteIrY47nQXkd9bACDynxSuKheow6NqYgGr/+gnbbvxV++5dwtgO679hzm47vYqZ9KuZez6srmaoJUvpxiyL8D/w6WheF9qONwnw0MRfAeHFImJMo9yAMUM1JL7H1BjLeS8E/sbr/PhSUFKhTEc2vFKB6GzDUsYp4vayKDcb1L642xyrukiHPioZ9LhqbIUlufgiXCnHmQupN6eKDD9XklnWm2SoK1sD1LJNtnfkAQAqahaI3V4K/yBhBSeoERjtHtcdwMd3D6YGNsYTydbbQpGVjtzPAP56hlBcUH6a5BQ4n0UbnP1dwmEhALd2QvNR1J7ht/ZKFQKBgQD2L0amklUWAilnA59KEnfPvy2l4JxLgT0OKQY6tfyVx+mnO1vvz16NTH7XgYMBEtnkqPRfF3Exq5wQSBDy4C1ROqm9IL/jiFcCBh9sLI6X7Ufyr1vY71357nHr0OIzhW3dj5botVj7GkQDKA+PkFjDK0qW/XIPeRulo4ZrzLWw9wKBgQDzNByzUisdNO6VDUgzG1NTZ2esA7/oSUUoyyRcbmmWcvJD7TyQM1K5BzwZxAOAKAWFCwUsl4L4Evt5MeZWxNSMKAgJ2N0GXbrVfuAxOp2TfLthTp6q164yMD/iF2Wm4NvjezWyVOPfZVaPIObcOdZadgE430KSFA3q0rBfDx3H0wKBgDujEDZh1ILUWzs9QWUsPCHzahd45ePUlvnLLlsiouGV4CBG3OqnDXylP0TtdtLXwjkGRMyUXTNvIws+qfxsGg5Ha78JI/L2oD05Rntp/9EGBhvgJxvSQK2++ZHqtCFO/WJi2mfdJoUNw9QP7FUW3qnI3vTcyLi+2deyWWnb2VfZAoGAJsSk2pT7mHFKoun2G2d8tevi49HJMq8TXjhbuHxFSp1SLy/PGrGDu0kC9JyBblly381s6rnQP9lOyvgAXs6LjzgGaANw4EXqtYO/Gznbypn0iMNumY8+DnQQiurt4lcv1iha2+2aUY8m6DP3eR2jJNJwyW8VfpHB0i/5u31+Ys8CgYEA3gYjy77XJsKK7DlnLtohMqgfxW/21nbEDsKXlestGhP7smX8mr/lfuYtdGozyXCIY8oSlVHn0N5pcGYXimSrhwWDgwyIFBh7d4/vzO73AdqLj5ZDQo7SF+H5G29DM3U364K/zWuluQfzvvp1KcacKMN3PwWRsLREylOcD8rad3Q=");

        aliPayConfig.setKeyPrivate("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCibxqBuDvdJeGm" +
                "tljjQZQRdrWAmE3rMaI1WoPdG5EDSlOFZmNs4KPQt8Tt5rqB1T6ACYC4ofqAP5n3" +
                "FqMjTfu0UNJyA9/Q7aui5nYot4y3KEAKgWZ6oZ2a4JZ8cJv69lOzm9om5PwTJvwh" +
                "mXnfMxfe4zmQSMB96eDSzzE41WW4kukBJw4VFI8RdmxIl1SUgyX4K/g4C86KA1a+" +
                "9sWRu6GX4swkUkDPq4mzhLLMK3xA7N+eYwfsaroE/bztcINWjOmyL0e1JdTtw+Ln" +
                "Ll1hN2YgEwwZgea4XchTBL6YzBdhTALPZ4e3d2XFXK2tfYQKE2uO5mZMxz7e5AMz" +
                "Dw4pnczTAgMBAAECggEAQPEereJUvNQaZabd3u4nHtxRMLqz3GaZra8krVAKKECd" +
                "J+aJtcwScq4mY98tjh8Nv+8MscTA/GlxSyKX0tOo4Ky3lrIJ1+RODPELKLhJ4CiN" +
                "giW+LSVUd8l3IQV8t1eom1SiYY6HYbjicSSFX0F0N76tEwyjOTQ6mM4RpeFbq/Ed" +
                "eEb8xisFA764+CYpu5P845n+LCViQ9idCWr7GdkeHujFQq6aqZbOiwNQrUEOAKqS" +
                "HSUIs35WrRmSGRvS6AeTDr5WLHfx1cogNHajBBbwhMlmwcVTAposEuJXTTJZlMfl" +
                "F2jpBzkTypFV+Od31lJdbH+In9inHYY2FgczTU5W4QKBgQDOx+qAufp+g8L+N+YK" +
                "skBCSL96iHR8dk96edc5zpipK0Dz779kLdDJKZenQIuxSn/qpEQGQpAElx/4oMXT" +
                "9bHufpIc6s+sCOO+xQMsq3h+m6J14hOhhwcXjl2kc4FVLdh2g4W7SfiypOOjSbot" +
                "osDiK+NSl5DYbRCE/2eTBGHoOQKBgQDJGO5/RoIWMXjrMapzWe4UP1S2XxuvE+Ov" +
                "2qqcThE/Wyd/90HGc698+DHApCFuhO3HoFdD3I0/oAE4mQ0hQwMqWYdF2JmXTOjW" +
                "M0InytmSOcu/ZXyk+x8rWttA/ycC7XZh9X5VSbwbBvEhlPiyAvDgMl3K4vG+uhLs" +
                "23SVqC2lawKBgQCyzDJ9NFLg6lXFM9h6Dz95ESZgcXUn2bVHlesAVR+zx27Wfsvj" +
                "d6o9BvXW7PzdRCnvXjJjp1KYZYSDm6DzclN0gYUd8/u2Kcmjvv7LtUjvUOvdOqr2" +
                "1KbRO4zFEgsW+BlgAMG1/HRLJEChhLKzmzNlXcWmOOBKA6RxG9cXTIbMIQKBgQC9" +
                "aBUinadi/d/VLmdL1FUwjuHcD8KEaWhXts5hAAqwsMuIdLtoDogoXaCGwFCYxu0q" +
                "y/PFgBMt7SeZRlaekH6HweFuQO+RGShor8jMr/uzBWrJb+4xejFGZYIDOw9ka4LI" +
                "CzCdZiCdGDG/no+LIsf7cpTyxkRJjvrKSn0JhGjR1wKBgGQQW/kBLGn0AGPc7vxk" +
                "khUP9yMzdV7/sIk9fOtilgvQpsjvU3IQckKxPN0GwQQwzEvqwqrVhu5oCmRJl3WI" +
                "gV8jRQkz4/3VNQ1XKA1Waki03G4bqUyJPLRbqk2G5UtpJmqzVp2MC2UN0fngICaj" +
                "dTQCEylCdPdEG9Igb8ymNcCs");
        aliPayConfig.setNotifyUrl("http://39.104.127.252:8181/fkdp/pay/aliPayCallBack");
        //aliPayConfig.setReturnUrl("http://vckvvv.natappfree.cc/pay/aliPayCallBack");
        aliPayConfig.setSignType(SignUtils.RSA2.name());
        aliPayConfig.setSeller("2088331994426085");
        aliPayConfig.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境
        aliPayConfig.setTest(false);
        //最大连接数
        httpConfig.setMaxTotal(20);
        //默认的每个路由的最大连接数
        httpConfig.setDefaultMaxPerRoute(10);
        aliPayService = new AliPayService(aliPayConfig, httpConfig);

        //todo 微信配置文件
        wxPayConfig.setMchId("1512315091");
        wxPayConfig.setAppid("wx236c30256bf4f508");
        //wxPayConfig.setKeyPublic("T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
        wxPayConfig.setSecretKey("GUI20180323fashion698700HENANGUI");
        wxPayConfig.setNotifyUrl("http://vckvvv.natappfree.cc/pay/wxPayCallBack");
        wxPayConfig.setReturnUrl("http://vckvvv.natappfree.cc/pay/wxPayCallBack");
        wxPayConfig.setSignType(SignUtils.MD5.name());
        wxPayConfig.setInputCharset("utf-8");
        wxPayService = new WxPayService(wxPayConfig);
    }


    public List<Bill> getBills(Long userId) {
        return billRepository.findAllByUser_UserId(userId);
    }

    public Bill getDetailBill(Long billId) {
        return billRepository.findById(billId).get();
    }

    public Wallet getWallet(Long userId) {
        Wallet wallet = walletRepository.findByUser_UserId(userId);
        return wallet;
    }

    public String aliRecharge(Long userId, BigDecimal price) {
        PayOrder payOrder = new PayOrder("充值", "充值", price, IDUtil.getOrderId(), AliTransactionType.WAP);
        User user = userRepository.findById(userId).get();
        Map<String, Object> orderInfo = aliPayService.orderInfo(payOrder);
        Bill bill = new Bill();
        bill.setOutTradeNo(payOrder.getOutTradeNo());
        bill.setPrice(payOrder.getPrice());
        bill.setType(1);
        bill.setIntroduce("通过支付宝  充值" + bill.getPrice() + "元");
        bill.setTime(new Date());
        bill.setUser(user);
        redisService.set(payOrder.getOutTradeNo(), bill);
        return aliPayService.buildRequest(orderInfo, MethodType.POST);
    }

    public boolean aliRechargeCallBack(HttpServletRequest request) {
        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = aliPayService.getParameter2Map(request.getParameterMap(), request.getInputStream());
            if (null == params) {
                return false;
            }
            //校验
            if (aliPayService.verify(params)) {
                String outTradeNo = (String) params.get("out_trade_no");
                Bill bill = (Bill) redisService.get(outTradeNo);
                if (bill != null) {
                    Wallet wallet = walletRepository.findByUser_UserId(bill.getUser().getUserId());
                    wallet.setUser(bill.getUser());
                    wallet.setUpdateTime(new Date());
                    wallet.setBalance(wallet.getBalance().add(bill.getPrice()));
                    billRepository.save(bill);
                    redisService.del(outTradeNo);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String wxRecharge(Long userId,
                             BigDecimal price,
                             HttpServletRequest request) {
        PayOrder payOrder = new PayOrder("钱包充值", "钱包充值", price, IDUtil.getOrderId(), WxTransactionType.MWEB);
        User user = userRepository.findById(userId).get();
        StringBuffer requestURL = request.getRequestURL();
        //设置网页地址
        payOrder.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ?
                requestURL.indexOf("/") : requestURL.length()));
        payOrder.setWapName("钱包充值");
        Map<String, Object> orderInfo = wxPayService.orderInfo(payOrder);
        Bill bill = new Bill();
        bill.setType(1);
        bill.setOutTradeNo(payOrder.getOutTradeNo());
        bill.setIntroduce("通过微信充值 " + price + " 元");
        bill.setTime(new Date());
        bill.setUser(user);
        redisService.set(payOrder.getOutTradeNo(), bill);
        return wxPayService.buildRequest(orderInfo, MethodType.POST);
    }


    public boolean wxRechargeCallBack(HttpServletRequest request) {
        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = wxPayService.getParameter2Map(request.getParameterMap(), request.getInputStream());
            if (null == params) {
                return false;
            }
            //校验
            if (wxPayService.verify(params)) {
                String outTradeNo = (String) params.get("out_trade_no");
                Bill bill = (Bill) redisService.get(outTradeNo);
                if (bill != null) {
                    Wallet wallet = walletRepository.findByUser_UserId(bill.getUser().getUserId());
                    wallet.setUser(bill.getUser());
                    wallet.setUpdateTime(new Date());
                    wallet.setBalance(wallet.getBalance().add(bill.getPrice()));
                    billRepository.save(bill);
                    redisService.del(outTradeNo);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean aliTransfer(Long userId, TransferOrder transferOrder) {
        transferOrder.setOutNo(IDUtil.getOrderId());
        User user = userRepository.findById(userId).get();
        Wallet wallet = walletRepository.findByUser_UserId(user.getUserId());
        if (wallet.getBalance().compareTo(transferOrder.getAmount()) >= 0) {
            AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
            if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
                Bill bill = new Bill();
                bill.setType(4);
                bill.setUser(user);
                bill.setTime(new Date());
                bill.setOutTradeNo(transferOrder.getOutNo());
                bill.setPrice(transferOrder.getAmount());
                bill.setIntroduce("钱包余额转账到支付宝 " + bill.getPrice() + " 元");
                billRepository.save(bill);
                wallet.setBalance(wallet.getBalance().subtract(transferOrder.getAmount()));
                return true;
            }
        }
        return false;
    }


    public String aliPay(GoodsOrder goodsOrder, Long addressId) {
        GoodsOrder target = goodsOrderRepository.findById(goodsOrder.getGoodsOrderId()).get();
        BeanUtil.copyPropertiesIgnoreNull(goodsOrder, target);
        //先设置成自取 当addressId 和 商家发货条件满足时在更改
        if (goodsOrder.getPickUp().equals(0)) {
            LinkMan linkMan = new LinkMan();
            linkMan.setMobilePhone(goodsOrder.getLinkMan().getMobilePhone());
            linkMan.setUserName(goodsOrder.getLinkMan().getUserName());
            target.setLinkMan(linkMan);
        }
        //选择商家发货后  填写收货人信息
        if (addressId != null && goodsOrder.getPickUp().equals(1)) {
            Address address = addressRepository.findById(addressId).get();
            GoodsExpress goodsExpress = new GoodsExpress();
            goodsExpress.setAddress(address);
            goodsExpress.setCreateTime(new Date());
            target.setGoodsExpress(goodsExpress);
        }
        //支付宝支付
        target.setPayType(0);
        PayOrder payOrder = new PayOrder("支付", "支付", target.getPrice(), target.getGoodsOrderId(), AliTransactionType.WAP);
        Map<String, Object> orderInfo = aliPayService.orderInfo(payOrder);
        Bill bill = new Bill();
        bill.setOutTradeNo(target.getGoodsOrderId());
        bill.setUser(target.getUser());
        bill.setType(0);
        bill.setTime(new Date());
        bill.setPrice(target.getPrice());
        bill.setIntroduce("支付宝支付 " + target.getPrice() + " 元");
        redisService.set(target.getGoodsOrderId(), bill);
        goodsOrderRepository.save(target);
        return aliPayService.buildRequest(orderInfo, MethodType.POST);
    }


    public boolean aliPayCallBack(HttpServletRequest request) {
        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = aliPayService.getParameter2Map(request.getParameterMap(), request.getInputStream());
            if (null == params) {
                return false;
            }
            //校验
            if (aliPayService.verify(params)) {
                logger.info("支付宝支付回调获取的数据：{}",params);
                String outTradeNo = (String) params.get("out_trade_no");
                Bill bill = (Bill) redisService.get(outTradeNo);
                if (bill != null) {
                    billRepository.save(bill);
                    GoodsOrder goodsOrder = goodsOrderRepository.findById(outTradeNo).get();
                    goodsOrder.setStatus(1);
                    logger.info("支付完成", goodsOrder.getGoodsOrderId());
                    redisService.del(outTradeNo);
                    //支付完成后  7天没有退换/款  则自动付款给商家
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            GoodsOrder target = goodsOrderRepository.findById(outTradeNo).get();
                            //先判断商家是否发货 并且买家没有确认收货
                            if (target.getStatus().equals(2) && !target.getStatus().equals(3)) {
                                //判断是否申请退换货 退款
                                if (target.getBackGoods() == null){
                                    TransferOrder transferOrder = new TransferOrder();
                                    transferOrder.setPayeeAccount(target.getGoods().getMerchant().getUser().getMobilePhone());
                                    transferOrder.setOutNo(IDUtil.getOrderId());
                                    transferOrder.setAmount(target.getPrice());
                                    transferOrder.setRemark("商铺收入");
                                    AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
                                    if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
                                        Bill bill = new Bill();
                                        bill.setType(2);
                                        bill.setUser(target.getGoods().getMerchant().getUser());
                                        bill.setTime(new Date());
                                        bill.setOutTradeNo(transferOrder.getOutNo());
                                        bill.setPrice(transferOrder.getAmount());
                                        bill.setIntroduce("退款到支付宝账号：" + target.getGoods().getMerchant().getUser().getMobilePhone() + "   " + bill.getPrice() + " 元");
                                        billRepository.save(bill);
                                    }
                                }
                            }
                        }
                    }, 10 * 24 * 60 * 60 * 1000);
                    return true;
                }
                //失败的话 恢复商品数量
                logger.info("支付失败，进行rabbitMQ处理");
                //rabbitTemplate.convertAndSend(exchange, errorRouteKey, outTradeNo);
                goodsService.dealErrorOrder(outTradeNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String wxPay(GoodsOrder goodsOrder, Long addressId, HttpServletRequest request) {
        GoodsOrder target = goodsOrderRepository.findById(goodsOrder.getGoodsOrderId()).get();
        BeanUtil.copyPropertiesIgnoreNull(goodsOrder, target);
        //先设置成自取 当addressId 和 商家发货条件满足时在更改
        if (goodsOrder.getPickUp().equals(0)) {
            LinkMan linkMan = new LinkMan();
            linkMan.setMobilePhone(goodsOrder.getLinkMan().getMobilePhone());
            linkMan.setUserName(goodsOrder.getLinkMan().getUserName());
            target.setLinkMan(linkMan);
        }
        //选择商家发货后  填写收货人信息
        if (addressId != null && goodsOrder.getPickUp().equals(1)) {
            Address address = addressRepository.findById(addressId).get();
            GoodsExpress goodsExpress = new GoodsExpress();
            goodsExpress.setAddress(address);
            goodsExpress.setCreateTime(new Date());
            target.setGoodsExpress(goodsExpress);
        }
        //微信支付
        target.setPayType(1);
        PayOrder payOrder = new PayOrder("支付", "支付", target.getPrice(), target.getGoodsOrderId(), WxTransactionType.MWEB);
        User user = target.getUser();
        StringBuffer requestURL = request.getRequestURL();
        //设置网页地址
//        payOrder.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ?
//                requestURL.indexOf("/") : requestURL.length()));
//        payOrder.setWapName("支付");
        Map<String, Object> orderInfo = wxPayService.orderInfo(payOrder);
        Bill bill = new Bill();
        bill.setType(0);
        bill.setOutTradeNo(payOrder.getOutTradeNo());
        bill.setIntroduce("通过微信支付 " + target.getPrice() + " 元");
        bill.setTime(new Date());
        bill.setUser(user);
        redisService.set(target.getGoodsOrderId(), bill);
        return wxPayService.buildRequest(orderInfo, MethodType.POST);
    }

    public boolean wxPayCallBack(HttpServletRequest request) {
        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = wxPayService.getParameter2Map(request.getParameterMap(), request.getInputStream());
            if (null == params) {
                return false;
            }
            //校验
            if (wxPayService.verify(params)) {
                String outTradeNo = (String) params.get("out_trade_no");
                Bill bill = (Bill) redisService.get(outTradeNo);
                if (bill != null) {
                    GoodsOrder goodsOrder = goodsOrderRepository.findById(outTradeNo).get();
                    goodsOrder.setStatus(1);
                    billRepository.save(bill);
                    redisService.del(outTradeNo);
                    return true;
                }
                //失败的话 恢复商品数量
               // rabbitTemplate.convertAndSend(exchange, errorRouteKey, outTradeNo);
                goodsService.dealErrorOrder(outTradeNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "${mq.pay.back.queue}", autoDelete = "false"),
//            exchange = @Exchange(name = "${mq.pay.exchange}", type = ExchangeTypes.DIRECT),
//            key = "${mq.pay.back.routeKey}"
//    ))
    public void backOrderTransfer(String orderId) {
        GoodsOrder order = goodsOrderRepository.findById(orderId).get();
        TransferOrder transferOrder = new TransferOrder();
        transferOrder.setPayeeAccount(order.getUser().getMobilePhone());
        transferOrder.setOutNo(IDUtil.getOrderId());
        transferOrder.setAmount(order.getPrice());
        transferOrder.setRemark("退款");
        AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
        if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
            Bill bill = new Bill();
            bill.setType(3);
            bill.setUser(order.getUser());
            bill.setTime(new Date());
            bill.setOutTradeNo(transferOrder.getOutNo());
            bill.setPrice(transferOrder.getAmount());
            bill.setIntroduce("退款到支付宝账号：" + order.getUser().getMobilePhone() + "   " + bill.getPrice() + " 元");
            billRepository.save(bill);
        }
    }

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "${mq.pay.sure.queue}", autoDelete = "false"),
//            exchange = @Exchange(name = "${mq.pay.exchange}", type = ExchangeTypes.DIRECT),
//            key = "${mq.pay.sure.routeKey}"
//    ))
    public void sureOrder(String orderId) {
        logger.info("确定收货订单{}",orderId);
        GoodsOrder order = goodsOrderRepository.findById(orderId).get();
        logger.info("-------------------------------------------------------------");
        TransferOrder transferOrder = new TransferOrder();
        transferOrder.setPayeeAccount(order.getGoods().getMerchant().getUser().getMobilePhone());
        transferOrder.setOutNo(IDUtil.getOrderId());
        transferOrder.setAmount(order.getPrice());
        transferOrder.setRemark("退款");
        AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
        if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
            Bill bill = new Bill();
            bill.setType(3);
            bill.setUser(order.getGoods().getMerchant().getUser());
            bill.setTime(new Date());
            bill.setOutTradeNo(transferOrder.getOutNo());
            bill.setPrice(transferOrder.getAmount());
            bill.setIntroduce("退款到支付宝账号：" + order.getGoods().getMerchant().getUser().getMobilePhone() + "   " + bill.getPrice() + " 元");
            billRepository.save(bill);
        }
        order.setStatus(3);
    }
}
