package com.linghong.fkdp.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/29 10:48
 * @Version 1.0
 * @Description: 收货地址
 */
@Entity
@Table(name = "address")
public class Address implements Serializable {
    private Long addressId;
    private User user;
    private String receiver;//收货人
    private String receiverPhone;//收货人手机号
    private String expressAddress;//快递目的地址
    private Boolean userful;//是否可用
    private Date createTime;

    @Id
    @GeneratedValue
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getExpressAddress() {
        return expressAddress;
    }

    public void setExpressAddress(String expressAddress) {
        this.expressAddress = expressAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getUserful() {
        return userful;
    }

    public void setUserful(Boolean userful) {
        this.userful = userful;
    }
}
