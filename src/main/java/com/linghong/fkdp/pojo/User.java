package com.linghong.fkdp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/27 09:37
 * @Version 1.0
 * @Description: 用户
 */
@Entity
@Table(name = "user")
public class User implements Serializable {
    private Long userId;     //主键Id
    private String userName; //用户名
    private String nickName; //昵称
    private String mobilePhone;//手机号
    @JsonIgnore
    private String password;//密码
    private String sign;//签名
    private String avatar;//头像
    private String sex;//性别
    private String address;//住址
    @JsonIgnore
    private String idCardNumber;//身份证号
    @JsonIgnore
    private String idCardPath;//身份证照片
    private Boolean auth;//是否被认证
    private OpenUser openUser;
    private Date createTime;

    @Id
    @GeneratedValue
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getIdCardPath() {
        return idCardPath;
    }

    public void setIdCardPath(String idCardPath) {
        this.idCardPath = idCardPath;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "openUserId")
    public OpenUser getOpenUser() {
        return openUser;
    }

    public void setOpenUser(OpenUser openUser) {
        this.openUser = openUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", password='" + password + '\'' +
                ", sign='" + sign + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                ", idCardPath='" + idCardPath + '\'' +
                ", auth=" + auth +
                ", openUser='" + openUser + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}

