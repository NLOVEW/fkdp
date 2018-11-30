package com.linghong.fkdp.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/27 10:26
 * @Version 1.0
 * @Description: 商品快递信息
 */
@Entity
@Table(name = "goods_express")
public class GoodsExpress implements Serializable {
    private Long goodsExpressId;
    private Address address;//收货人信息
    private String expressType;//快递公司
    private String expressNumber;//快递单号
    private String expressData;//详细物流信息(此信息存数据库)
    private Date createTime;

    @Id
    @GeneratedValue
    public Long getGoodsExpressId() {
        return goodsExpressId;
    }

    public void setGoodsExpressId(Long goodsExpressId) {
        this.goodsExpressId = goodsExpressId;
    }

    public String getExpressData() {
        return expressData;
    }

    public void setExpressData(String expressData) {
        this.expressData = expressData;
    }

    public String getExpressType() {
        return expressType;
    }

    public void setExpressType(String expressType) {
        this.expressType = expressType;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "addressId")
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
