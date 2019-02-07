package com.linghong.fkdp.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/27 20:44
 * @Version 1.0
 * @Description:
 */
@Entity
@Table(name = "admin")
public class Admin implements Serializable {
    private Long adminId;
    private String mobilePhone;
    private String password;
    private String updateTime;

    @Id
    @GeneratedValue
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
