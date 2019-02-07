package com.linghong.fkdp.dto;

import com.linghong.fkdp.bean.Express;
import com.linghong.fkdp.pojo.Address;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/27 10:26
 * @Version 1.0
 * @Description: 商品快递信息
 */

public class GoodsExpressMessage implements Serializable {
    private Long goodsExpressId;
    private Address address;//收货人信息
    private String expressType;//快递公司
    private String expressNumber;//快递单号
    private Express expressData;//详细物流信息(此信息存数据库)
    private Date createTime;

    @Id
    @GeneratedValue
    public Long getGoodsExpressId() {
        return goodsExpressId;
    }

    public void setGoodsExpressId(Long goodsExpressId) {
        this.goodsExpressId = goodsExpressId;
    }

    public Express getExpressData() {
        return expressData;
    }

    public void setExpressData(Express expressData) {
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

    @Override
    public String toString() {
        return "GoodsExpressMessage{" +
                "goodsExpressId=" + goodsExpressId +
                ", address=" + address +
                ", expressType='" + expressType + '\'' +
                ", expressNumber='" + expressNumber + '\'' +
                ", expressData='" + expressData + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
