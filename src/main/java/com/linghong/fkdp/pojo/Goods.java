package com.linghong.fkdp.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/27 09:45
 * @Version 1.0
 * @Description: 商品
 */
@Entity
@Table(name = "goods")
public class Goods implements Serializable {
    private String goodsId;
    private Merchant merchant;
    private String title;//标题
    private String goodsType;//商品类型
    private BigDecimal originalPrice;//原价
    private BigDecimal nowPrice;//现价
    private BigDecimal bigPrice;//最高价
    private BigDecimal smallPrice;//最低价
    private BigDecimal downPrice;//每20秒降价的金额
    private Integer number;//数量
    private String introduce;//简介
    private Set<Image> images;
    private Set<GoodsImage> goodsImages;
    private BigDecimal empressPrice;//运费
    private Date startTime;
    private Date endTime;
    private Boolean obtained;//是否删除 true 删除 false 不删除
    private Date updateTime;//修改时间
    private Date createTime;//创建时间

    @Id
    @Column(name = "goodsId",length = 32,unique = true)
    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "merchantId")
    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "goodsId")
    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "goodsId")
    public Set<GoodsImage> getGoodsImages() {
        return goodsImages;
    }

    public void setGoodsImages(Set<GoodsImage> goodsImages) {
        this.goodsImages = goodsImages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(BigDecimal nowPrice) {
        this.nowPrice = nowPrice;
    }

    public BigDecimal getBigPrice() {
        return bigPrice;
    }

    public void setBigPrice(BigDecimal bigPrice) {
        this.bigPrice = bigPrice;
    }

    public BigDecimal getSmallPrice() {
        return smallPrice;
    }

    public void setSmallPrice(BigDecimal smallPrice) {
        this.smallPrice = smallPrice;
    }

    public BigDecimal getDownPrice() {
        return downPrice;
    }

    public void setDownPrice(BigDecimal downPrice) {
        this.downPrice = downPrice;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public BigDecimal getEmpressPrice() {
        return empressPrice;
    }

    public void setEmpressPrice(BigDecimal empressPrice) {
        this.empressPrice = empressPrice;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getObtained() {
        return obtained;
    }

    public void setObtained(Boolean obtained) {
        this.obtained = obtained;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goodsId='" + goodsId + '\'' +
                ", merchant=" + merchant +
                ", title='" + title + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", originalPrice=" + originalPrice +
                ", nowPrice=" + nowPrice +
                ", bigPrice=" + bigPrice +
                ", smallPrice=" + smallPrice +
                ", downPrice=" + downPrice +
                ", number=" + number +
                ", introduce='" + introduce + '\'' +
                ", images=" + images +
                ", empressPrice=" + empressPrice +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", obtained=" + obtained +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
