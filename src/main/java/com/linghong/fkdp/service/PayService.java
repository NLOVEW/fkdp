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
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/13 10:34
 * @Version 1.0
 * @Description:
 */
@Service
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
    private AmqpTemplate amqpTemplate;
    @Resource
    private LinkManRepository linkManRepository;

    private Timer timer;

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
        aliPayConfig.setPid("2088102175938099");
        aliPayConfig.setAppId("2016091600527218");
        aliPayConfig.setKeyPublic("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArp1iacBfaj6BopBsp7HesXIcX4CRrkFIRcayABGcaa3rTITBJlTRwdRU0osw2lcLGELrCcuxQ/NlqEA65iIBxbWoToM5TCgKUIjR3tDVRxoEGLCcxRdQjD90OAD5McqlXEfZtBQXbn57Ao6hr5AvLy+d8dEDnkclu3ASojqkOeoA6jqscNonfaY+w19yGBdovb1xwv+9E1bPj888T8OgZsJB/rRRKME/DdoH5q44F8uDnQFdkGlP5OU8+OmYzgJORvdZW1CSRchThI6uP2KXqatU3DhehHrxIwd20J//H1FtSCbxFmyHtjWakVZtOJsSsbzX+gttz56Ww2/qH1jFLQIDAQAB");
        aliPayConfig.setKeyPrivate("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCSUM9YjT3D9CKxRI3YBCNzz8CqLL5T753dPKyMT6136o5o9sLTVUmc2y5+Umrh/heCSwKbmkwnaVQ1XMMnLE8al4hl3zE7DwWvBkOUE5gdR3KRz1TwWo6FQ/FANQSH+ymbO7nlTurfrGfeDnamBf8i3EXQfVjpRLT/gVOyZ1mfi44hdwX02LH0eGvqpPrSpgXi7mbiUazERDwBJVWhQKts1lGhE7MMWdU9yyQWD5gAMkTmsDtqEnWrguP7ebzuyqNSadw4wi1wONp/nba7a1CHnqDA6uIU4t5DqwS0IQHX00Wt+1lsEaru7RqQQoBQTGi8sat/G4GjYhZgKeXmz06hAgMBAAECggEATjM4jrh2gYuzGxFryj9z/0rTS2C8nLndosfx5NAVA5luYbU5LaBQxq/yqns8OusF/5I9o5KVDx9hbV/VwdMRuEGzGddvRYRi4kezyKmsTEHKfxnT6N5Ne/ZzM4DimhboBtqmIWPLbp6DqRAL4/Iiiaw2+BZ2db5OCp1BcYGBSh6KdhQrDccCHTaq0nKmaGj42xunSmz5Ij42gYfIdllAmsxb5cQB4K7t1VtuFlcKFxC5APn7OcbRR6Q9JMzP8BZL3qt3otccz3gQ2eLCHaJIv3yhjJ4YIWMcxPh1VycCPaJLHwKdK9sfL3Aa7q4KUj4wnEweFHdQve2GNxT+JKSHDQKBgQDGp7029tzqWAOnh3gN/zAHIFpxbRA35BiEwZOlQgv8loj78K30OmGQx9c9Yq/hRJhl2yVccQgXbWqFcjMtCbI2PvloNWGuu7BuaNUcFzpHRcDJPSKPn4Jq9buHQxvMP9h6ikY2mjN7JZTkW+o0w7Z/oi73ICJtNqXMlahfis1TPwKBgQC8jUWXwPaRyAZKE4tYuSAFhvyhXxgMiSxj2ek3pY25fEZBPvBOKr32artm7r24x0n4/jRMb6q2TvPT3nVPY7dNQRvJnfxkFTZslhplJ2D8Q6HrAU+mnxIhOkumV/EbkffSGFg0ijJ6HIHvISNScGvwJUGai5bhGCaMMWcjlBFGHwKBgHYHT1qKzbLGXFV7HY9jYQu640GlBQ/QC+lEgg6b8Tc/0V+vHguPbExzD6U/LzRuZwWNcOM9nQseTT3AHjvSGPo17EVIAiOpDChtTMEF4/BYtzRZiGFA6mHWYnb5Hlj6TgpwgUsLzy6Jo68SFVMIPTQUDkdx5kJxR40IFiO9+kRNAoGBAITd8Fp9ycL09ymgGCPPYHdEpiV5A1NiHnvGhQeHjBVXM5KqrWAH0pEgqSphtiPNm5zwVR4/2kZZ4Iw+SOBG3lZ8OP6ca8yC/jUKmVMKqtdZOXKHG//IPFhZ4hE4C1loRQAx04ZClEtkZ1OBQIjJW+Z/+njTQOEhyZglAA8cOgf1AoGBAJjxSvMr3K0BSkg9s2efsmwy/BW9khOJ+3OKlLdKz7WnPb7LTdky2SQbUGruT+vlDscCLwnV3YHu1JK3RD0RGciiVOEMit5RzTNpX63JtWrhw+CSA2SeWFlpzF7Vis2M9S1+W0EjWm/HzAhncBDPA2H47SyuWq2Guu9RdsFxJOJX");
        aliPayConfig.setNotifyUrl("http://www.alipay.com");
        aliPayConfig.setReturnUrl("http://2509d113.all123.net/aliPayBack");
        aliPayConfig.setSignType(SignUtils.RSA2.name());
        aliPayConfig.setSeller("2088102175938099");
        aliPayConfig.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境
        aliPayConfig.setTest(true);
        //最大连接数
        httpConfig.setMaxTotal(20);
        //默认的每个路由的最大连接数
        httpConfig.setDefaultMaxPerRoute(10);
        aliPayService = new AliPayService(aliPayConfig, httpConfig);

        //todo 微信配置文件
        wxPayConfig.setMchId("1473426802");
        wxPayConfig.setAppid("wx8397f8696b538317");
        wxPayConfig.setKeyPublic("T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
        wxPayConfig.setSecretKey("T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
        wxPayConfig.setNotifyUrl("异步通知地址");
        wxPayConfig.setReturnUrl("同步通知地址");
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
                String outTradeNo = (String) params.get("out_trade_no");
                Bill bill = (Bill) redisService.get(outTradeNo);
                if (bill != null) {
                    billRepository.save(bill);
                    GoodsOrder goodsOrder = goodsOrderRepository.findById(outTradeNo).get();
                    goodsOrder.setStatus(1);
                    //支付完成后  7天没有退换/款  则自动付款给商家
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            GoodsOrder target = goodsOrderRepository.findById(outTradeNo).get();
                            //先判断商家是否发货 并且买家没有确认收货
                            if (target.getStatus().equals(2) && !target.getStatus().equals(3)) {
                                //判断是否申请退换货 退款
                                if (target.getBackGoods() == null){
                                    TransferOrder transferOrder = new TransferOrder();
                                    transferOrder.setPayeeAccount(target.getGoods().getUser().getMobilePhone());
                                    transferOrder.setOutNo(IDUtil.getOrderId());
                                    transferOrder.setAmount(target.getPrice());
                                    transferOrder.setRemark("商铺收入");
                                    AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
                                    if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
                                        Bill bill = new Bill();
                                        bill.setType(2);
                                        bill.setUser(target.getGoods().getUser());
                                        bill.setTime(new Date());
                                        bill.setOutTradeNo(transferOrder.getOutNo());
                                        bill.setPrice(transferOrder.getAmount());
                                        bill.setIntroduce("退款到支付宝账号：" + target.getGoods().getUser().getMobilePhone() + "   " + bill.getPrice() + " 元");
                                        billRepository.save(bill);
                                    }
                                }
                            }
                        }
                    }, 10 * 24 * 60 * 60 * 1000);
                    redisService.del(outTradeNo);
                    return true;
                }
                //失败的话 恢复商品数量
                amqpTemplate.convertAndSend(exchange, errorRouteKey, outTradeNo);
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
        payOrder.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ?
                requestURL.indexOf("/") : requestURL.length()));
        payOrder.setWapName("支付");
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
                amqpTemplate.convertAndSend(exchange, errorRouteKey, outTradeNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${mq.pay.back.queue}", autoDelete = "false"),
            exchange = @Exchange(name = "${mq.pay.exchange}", type = ExchangeTypes.DIRECT),
            key = "${mq.pay.back.routeKey}"
    ))
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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${mq.pay.sure.queue}", autoDelete = "false"),
            exchange = @Exchange(name = "${mq.pay.exchange}", type = ExchangeTypes.DIRECT),
            key = "${mq.pay.sure.routeKey}"
    ))
    public void sureOrder(String orderId) {
        GoodsOrder order = goodsOrderRepository.findById(orderId).get();
        TransferOrder transferOrder = new TransferOrder();
        transferOrder.setPayeeAccount(order.getGoods().getUser().getMobilePhone());
        transferOrder.setOutNo(IDUtil.getOrderId());
        transferOrder.setAmount(order.getPrice());
        transferOrder.setRemark("退款");
        AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
        if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
            Bill bill = new Bill();
            bill.setType(3);
            bill.setUser(order.getGoods().getUser());
            bill.setTime(new Date());
            bill.setOutTradeNo(transferOrder.getOutNo());
            bill.setPrice(transferOrder.getAmount());
            bill.setIntroduce("退款到支付宝账号：" + order.getGoods().getUser().getMobilePhone() + "   " + bill.getPrice() + " 元");
            billRepository.save(bill);
        }
    }
}
