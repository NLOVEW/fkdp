package com.linghong.fkdp.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/29 16:59
 * @Version 1.0
 * @Description:
 */
@Entity
@Table(name = "link_man")
public class LinkMan implements Serializable {
    private Long linkManId;
    private String userName;
    private String mobilePhone;

    @Id
    @GeneratedValue
    public Long getLinkManId() {
        return linkManId;
    }

    public void setLinkManId(Long linkManId) {
        this.linkManId = linkManId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
