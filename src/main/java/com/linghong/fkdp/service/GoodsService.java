package com.linghong.fkdp.service;

import com.linghong.fkdp.constant.UrlConstant;
import com.linghong.fkdp.pojo.*;
import com.linghong.fkdp.repository.*;
import com.linghong.fkdp.utils.BeanUtil;
import com.linghong.fkdp.utils.FastDfsUtil;
import com.linghong.fkdp.utils.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/28 14:38
 * @Version 1.0
 * @Description:
 */
@Service
public class GoodsService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private BillRepository billRepository;
    @Resource
    private WalletRepository walletRepository;
    @Resource
    private GoodsExpressRepository goodsExpressRepository;
    @Resource
    private GoodsOrderRepository goodsOrderRepository;
    @Resource
    private MerchantRepository merchantRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${mq.pay.error.queue}", autoDelete = "false"),
            exchange = @Exchange(name = "${mq.pay.exchange}", type = ExchangeTypes.DIRECT),
            key = "${mq.pay.error.routeKey}"
    ))
    public void dealErrorOrder(String goodsOrderId) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        Goods goods = goodsOrder.getGoods();
        goods.setNumber(goods.getNumber().intValue() + 1);
    }

    public boolean addGoods(Long userId, Goods goods, String base64Images) {
        User user = userRepository.findById(userId).get();
        Merchant merchant = merchantRepository.findByUser_UserId(userId);
        if (!merchant.getAuth()) {
            return false;
        }
        goods.setUser(user);
        goods.setGoodsId(IDUtil.getId());
        String[] split = base64Images.split("。");
        Set<Image> images = new HashSet<>();
        for (String base64 : split) {
            Image image = new Image();
            image.setCreateTime(new Date());
            image.setImagePath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(base64));
            images.add(image);
        }
        goods.setImages(images);
        goods.setCreateTime(new Date());
        goods.setObtained(false);
        goodsRepository.save(goods);
        //开启任务线程
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            String id = goods.getGoodsId();

            @Override
            public void run() {
                //在开始时间  以20S减价一次
                Goods target = goodsRepository.findById(id).get();
                target.setNowPrice(target.getOriginalPrice().subtract(target.getDownPrice()));
                //到达结束竞拍时间  停止任务
                if (target.getEndTime().compareTo(new Date(System.currentTimeMillis())) <= 0) {
                    this.cancel();
                }
            }
        }, goods.getStartTime(), 20 * 1000);
        return true;
    }

    public boolean updateGoods(Goods goods, String base64Images) {
        Goods target = goodsRepository.findById(goods.getGoodsId()).get();
        BeanUtil.copyPropertiesIgnoreNull(goods, target);
        Set<Image> images = target.getImages();
        String[] split = base64Images.split("。");
        for (String base64 : split) {
            Image image = new Image();
            image.setCreateTime(new Date());
            image.setImagePath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(base64));
            images.add(image);
        }
        target.setImages(images);
        return true;
    }

    public boolean deleteGoods(String goodsId) {
        Goods goods = goodsRepository.findById(goodsId).get();
        goods.setObtained(true);
        return true;
    }

    public Goods findGoodsByGoogsId(String goodsId) {
        return goodsRepository.findById(goodsId).get();
    }

    public List<Goods> findGoodsByUserId(Long userId) {
        List<Goods> goods = goodsRepository.findAllByUser_UserId(userId);
        return goods;
    }

    public Map<String, Object> getAuctionIndex(String goodsId) {
        Goods goods = goodsRepository.findById(goodsId).get();
        List<GoodsOrder> goodsOrders = goodsOrderRepository.findAllByGoods_GoodsId(goodsId);
        Map<String, Object> result = new HashMap<>();
        result.put("商品基本信息", goods);
        result.put("参与竞拍人数", goodsOrders.size());
        return result;
    }

    public List<String> getAllGoodsId() {
        List<Goods> goods = goodsRepository.findAll()
                .stream()
                .filter(goods1 -> {
                    if (goods1.getNumber().intValue() > 0 && !goods1.getObtained()) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        for (Goods goods1 : goods) {
            result.add(goods1.getGoodsType());
        }
        return result;
    }

    public List<Goods> findByCityAndType(String city, String goodsType) {
        try {
            String decodeCity = URLDecoder.decode(city, "UTF-8");
            String decodeType = URLDecoder.decode(goodsType, "UTF-8");
            Specification<Goods> specification = (root, query, builder) -> {
                Predicate type = builder.like(root.get("goodsType").as(String.class), "%" + decodeType + "%");
                Predicate address = builder.like(root.get("address").as(String.class), "%" + decodeCity + "%");
                return builder.and(type, address);
            };
            List<Goods> goods = goodsRepository.findAll(specification)
                    .stream()
                    .filter(goods1 -> {
                        if (goods1.getNumber().intValue() > 0 && !goods1.getObtained()) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            return goods;
        } catch (Exception e) {
            logger.error("解码失败");
        }
        return null;
    }

    public List<Goods> findByType(String goodsType) {
        try {
            String decodeType = URLDecoder.decode(goodsType, "UTF-8");
            Specification<Goods> specification = (root, query, builder) -> {
                Predicate type = builder.like(root.get("goodsType").as(String.class), "%" + decodeType + "%");
                return type;
            };
            List<Goods> goods = goodsRepository.findAll(specification).stream()
                    .filter(goods1 -> {
                        if (goods1.getNumber().intValue() > 0 && !goods1.getObtained()) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            return goods;
        } catch (Exception e) {
            logger.error("解码失败");
        }
        return null;
    }

    public List<Goods> findByCityAndKey(String city, String key) {
        try {
            String decodeCity = URLDecoder.decode(city, "UTF-8");
            String decodeKey = URLDecoder.decode(key, "UTF-8");
            Specification<Goods> specification = (root, query, builder) -> {
                Predicate title = builder.like(root.get("title").as(String.class), "%" + decodeKey + "%");
                Predicate introduce = builder.like(root.get("introduce").as(String.class), "%" + decodeKey + "%");
                Predicate address = builder.like(root.get("address").as(String.class), "%" + decodeCity + "%");
                return builder.and(address,builder.or(title, introduce));
            };
            List<Goods> goods = goodsRepository.findAll(specification)
                    .stream()
                    .filter(goods1 -> {
                        if (goods1.getNumber().intValue() > 0 && !goods1.getObtained()) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            return goods;
        } catch (Exception e) {
            logger.error("解码失败");
        }
        return null;
    }

    public List<Goods> findByKey(String key) {
        try {
            String decodeKey = URLDecoder.decode(key, "UTF-8");
            Specification<Goods> specification = (root, query, builder) -> {
                Predicate title = builder.like(root.get("title").as(String.class), "%" + decodeKey + "%");
                Predicate introduce = builder.like(root.get("introduce").as(String.class), "%" + decodeKey + "%");
                return builder.or(title, introduce);
            };
            List<Goods> goods = goodsRepository.findAll(specification)
                    .stream()
                    .filter(goods1 -> {
                        if (goods1.getNumber().intValue() > 0 && !goods1.getObtained()) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            return goods;
        } catch (Exception e) {
            logger.error("解码失败");
        }
        return null;
    }
}
