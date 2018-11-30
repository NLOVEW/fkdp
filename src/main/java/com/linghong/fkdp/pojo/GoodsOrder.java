package com.linghong.fkdp.pojo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/27 10:02
 * @Version 1.0
 * @Description: 订单
 */
@Entity
@Table(name = "goods_order")
public class GoodsOrder {
    private String goodsOrderId;
    private Goods goods;
    private User user;//购买者
    private Integer pickUp;//取货方式  0上门取货 1商家发货
    private LinkMan linkMan;//上门取货人信息
    private GoodsExpress goodsExpress;//物流信息
    private Integer number;
    private BigDecimal price;
    private Integer payType;//支付方式  0支付宝 1微信 2钱包
    /**
     * 0确定订单-->1买家已支付-->2卖家发货/等待上门取货-->3买家确认收货-->4取消订单(30min内未支付)
     */
    private Integer status;
    private BackGoods backGoods;
    private Date createTime;

    @Id
    @Column(name = "goodsOrderId",length = 32,unique = true)
    public String getGoodsOrderId() {
        return goodsOrderId;
    }

    public void setGoodsOrderId(String goodsOrderId) {
        this.goodsOrderId = goodsOrderId;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "goodsId")
    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "backGoodsId")
    public BackGoods getBackGoods() {
        return backGoods;
    }

    public void setBackGoods(BackGoods backGoods) {
        this.backGoods = backGoods;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "goodsExpressId")
    public GoodsExpress getGoodsExpress() {
        return goodsExpress;
    }

    public void setGoodsExpress(GoodsExpress goodsExpress) {
        this.goodsExpress = goodsExpress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getPickUp() {
        return pickUp;
    }

    public void setPickUp(Integer pickUp) {
        this.pickUp = pickUp;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "linkManId")
    public LinkMan getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(LinkMan linkMan) {
        this.linkMan = linkMan;
    }

    @Override
    public String toString() {
        return "GoodsOrder{" +
                "goodsOrderId='" + goodsOrderId + '\'' +
                ", goods=" + goods +
                ", user=" + user +
                ", pickUp=" + pickUp +
                ", linkMan=" + linkMan +
                ", goodsExpress=" + goodsExpress +
                ", number=" + number +
                ", price=" + price +
                ", payType=" + payType +
                ", status=" + status +
                ", backGoods=" + backGoods +
                ", createTime=" + createTime +
                '}';
    }
}
