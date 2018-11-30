package com.linghong.fkdp.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/27 10:43
 * @Version 1.0
 * @Description: 退货 退款
 */
@Entity
@Table(name = "back_goods")
public class BackGoods implements Serializable {
    private Long backGoodsId;
    private Integer backType;//0退换   1退款
    //0等待卖家确认 1卖家同意但未退款或退换 2卖家拒绝 3卖家已退款或退换给买家  如果退换，此平台不再显示物流
    private Integer backStatus;
    private Integer sureGoods;//当前是否已收到货物 0未收到  1已收到
    private String backReason;//退款/退换说明
    private BigDecimal backPrice;//退款金额
    private Set<Image> images;
    private Date createTime;

    @Id
    @GeneratedValue
    public Long getBackGoodsId() {
        return backGoodsId;
    }

    public void setBackGoodsId(Long backGoodsId) {
        this.backGoodsId = backGoodsId;
    }

    public Integer getBackStatus() {
        return backStatus;
    }

    public void setBackStatus(Integer backStatus) {
        this.backStatus = backStatus;
    }

    public Integer getSureGoods() {
        return sureGoods;
    }

    public void setSureGoods(Integer sureGoods) {
        this.sureGoods = sureGoods;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }

    public BigDecimal getBackPrice() {
        return backPrice;
    }

    public void setBackPrice(BigDecimal backPrice) {
        this.backPrice = backPrice;
    }

    @OneToMany(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinColumn(name = "backGoodsId")
    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getBackType() {
        return backType;
    }

    public void setBackType(Integer backType) {
        this.backType = backType;
    }

    @Override
    public String toString() {
        return "BackGoods{" +
                "backGoodsId=" + backGoodsId +
                ", backType=" + backType +
                ", backStatus=" + backStatus +
                ", sureGoods=" + sureGoods +
                ", backReason='" + backReason + '\'' +
                ", backPrice=" + backPrice +
                ", images=" + images +
                ", createTime=" + createTime +
                '}';
    }
}
