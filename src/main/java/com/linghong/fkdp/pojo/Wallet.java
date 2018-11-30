package com.linghong.fkdp.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/2 18:34
 * @Version 1.0
 * @Description:
 */
@Entity
@Table(name = "wallet")
public class Wallet implements Serializable {
    private String walletId;
    private User user;
    private BigDecimal balance;
    private Date createTime;
    private Date updateTime;

    @Id
    @Column(name = "walletId",unique = true,length = 32)
    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
