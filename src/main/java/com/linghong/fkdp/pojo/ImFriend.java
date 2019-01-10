package com.linghong.fkdp.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;


public class ImFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "friend_id", type = IdType.INPUT)
    private String friendId;

    private String fromUserId;

    private String toUserId;

    private Integer status;//请求加好友0  已经是好友1

    private LocalDateTime createTime;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ImFriend{" +
        "friendId=" + friendId +
        ", fromUserId=" + fromUserId +
        ", toUserId=" + toUserId +
        ", status=" + status +
        ", createTime=" + createTime +
        "}";
    }
}
