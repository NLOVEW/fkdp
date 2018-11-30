package com.linghong.fkdp.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 账单
 */
@Entity
@Table(name = "bill")
public class Bill implements Serializable {
    private Long billId;       //主键
    private User user;     //账户拥有者
    private Integer type;      //类型 0代表支出 1代表充值 2代表收入 3代表退款 4转账
    private BigDecimal price;  //金额
    private String outTradeNo; //第三方订单号
    private Date time;         //时间
    private String introduce;  //详细介绍 例如 时间+类型+目标账户


    @Id
    @GeneratedValue
    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
}
