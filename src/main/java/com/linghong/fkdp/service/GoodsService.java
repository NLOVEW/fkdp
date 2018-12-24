package com.linghong.fkdp.service;

import com.linghong.fkdp.constant.UrlConstant;
import com.linghong.fkdp.pojo.*;
import com.linghong.fkdp.repository.*;
import com.linghong.fkdp.utils.BeanUtil;
import com.linghong.fkdp.utils.FastDfsUtil;
import com.linghong.fkdp.utils.IDUtil;
import com.linghong.fkdp.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
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
@Transactional(rollbackFor = Exception.class)
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
    @Resource
    private ImageRepository imageRepository;

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

    public boolean addGoods(Goods goods, String base64Images, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        Merchant merchant = merchantRepository.findByUser_UserId(userId);
        if (!merchant.getAuth()) {
            return false;
        }
        goods.setMerchant(merchant);
        goods.setGoodsId(IDUtil.getId());
        String[] split = base64Images.split("。");
        Set<Image> images = new HashSet<>();
        for (String base64 : split) {
            Image image = new Image();
            image.setCreateTime(new Date());
            image.setImagePath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(base64));
            images.add(image);
        }
        goods.setNowPrice(goods.getOriginalPrice());
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
                logger.info("线程开启");
                //在开始时间  以20S减价一次
                Goods target = goodsRepository.findById(id).get();
                //到达结束竞拍时间  停止任务
                if (target.getEndTime().compareTo(new Date(System.currentTimeMillis())) <= 0 || target.getUpdateTime() != null || target.getObtained()) {
                    target.setUpdateTime(null);
                    goodsRepository.save(target);
                    this.cancel();
                }
                if (!target.getObtained()){
                    target.setNowPrice(target.getNowPrice().subtract(target.getDownPrice()));
                    goodsRepository.save(target);
                }
            }
        }, goods.getStartTime(), 20 * 1000);
        return true;
    }

    public boolean updateGoods(Goods goods, String base64Images) {
        Goods target = goodsRepository.findById(goods.getGoodsId()).get();
        Set<Image> images = target.getImages();
        imageRepository.deleteAll(images);
        images = new HashSet<>();
        String[] split = base64Images.split("。");
        for (String base64 : split) {
            Image image = new Image();
            image.setCreateTime(new Date());
            image.setImagePath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(base64));
            images.add(image);
        }
        target.setImages(images);
        //开始时间和新的结束时间相比较
        if (goods.getStartTime().compareTo(target.getStartTime()) != 0){
            target.setUpdateTime(new Date());
            //开启任务线程
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                String id = goods.getGoodsId();
                @Override
                public void run() {
                    logger.info("线程开启");
                    //在开始时间  以20S减价一次
                    Goods target = goodsRepository.findById(id).get();
                    //到达结束竞拍时间  停止任务
                    if (target.getEndTime().compareTo(new Date(System.currentTimeMillis())) <= 0 || target.getUpdateTime() != null || target.getObtained()) {
                        target.setUpdateTime(null);
                        goodsRepository.save(target);
                        this.cancel();
                    }
                    if (!target.getObtained()){
                        target.setNowPrice(target.getNowPrice().subtract(target.getDownPrice()));
                        goodsRepository.save(target);
                    }
                }
            }, goods.getStartTime(), 20 * 1000);
        }
        if (goods.getOriginalPrice().compareTo(target.getOriginalPrice()) != 0){
            target.setNowPrice(goods.getOriginalPrice());
        }
        BeanUtil.copyPropertiesIgnoreNull(goods, target);
        goodsRepository.save(target);
        return true;
    }

    public boolean deleteGoods(String goodsId) {
        Goods goods = goodsRepository.findById(goodsId).get();
        goods.setObtained(true);
        goodsRepository.save(goods);
        return true;
    }

    public Goods findGoodsByGoodsId(String goodsId) {
        return goodsRepository.findById(goodsId).get();
    }

    public List<Goods> findGoodsByUserId(Long userId) {
        List<Goods> goods = goodsRepository.findAllByMerchant_User_UserId(userId);
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
            result.add(goods1.getGoodsId());
        }
        return result;
    }

    public List<Goods> findByCityAndType(String city, String goodsType) {
        try {
            String decodeCity = URLDecoder.decode(city, "UTF-8");
            String decodeType = URLDecoder.decode(goodsType, "UTF-8");
            Specification<Goods> specification = (root, query, builder) -> {
                Predicate type = builder.like(root.get("goodsType").as(String.class), "%" + decodeType + "%");
                return type;
            };
            List<Goods> goods = goodsRepository.findAll(specification)
                    .stream()
                    .filter(goods1 -> {
                        if (goods1.getNumber().intValue() > 0 && !goods1.getObtained() && goods1.getMerchant().getAddress().contains(decodeCity)) {
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
                return builder.or(title, introduce);
            };
            List<Goods> goods = goodsRepository.findAll(specification)
                    .stream()
                    .filter(goods1 -> {
                        if (goods1.getNumber().intValue() > 0 && !goods1.getObtained() && goods1.getMerchant().getAddress().contains(decodeCity)) {
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
