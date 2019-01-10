package com.linghong.fkdp.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/4 16:55
 * @Version 1.0
 * @Description:
 */
@Entity
@Table(name = "goods_image")
public class GoodsImage implements Serializable {
    private Long goodsImagesId;
    private String path;

    @Id
    @GeneratedValue
    public Long getGoodsImagesId() {
        return goodsImagesId;
    }

    public void setGoodsImagesId(Long goodsImagesId) {
        this.goodsImagesId = goodsImagesId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "GoodsImage{" +
                "goodsImagesId=" + goodsImagesId +
                ", path='" + path + '\'' +
                '}';
    }
}
