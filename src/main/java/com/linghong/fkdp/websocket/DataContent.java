package com.linghong.fkdp.websocket;


import com.alibaba.fastjson.JSON;
import com.linghong.fkdp.pojo.ImMsg;

import java.io.Serializable;

public class DataContent implements Serializable {

    private static final long serialVersionUID = 8021381444738260454L;

    private Integer action;        // 动作类型
    private ImMsg imMsg;    // 用户的聊天内容entity
    private String extend;        // 扩展字段

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ImMsg getImMsg() {
        return imMsg;
    }

    public void setImMsg(ImMsg imMsg) {
        this.imMsg = imMsg;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    @Override
    public String toString() {
        return "DataContent{" +
                "action=" + action +
                ", imMsg=" + imMsg +
                ", extend='" + extend + '\'' +
                '}';
    }

    public static void main(String[] args) {
        DataContent dataContent = new DataContent();
        ImMsg imMsg = new ImMsg();
        imMsg.setSenderId("10086");
        imMsg.setReceiverId("10010");
        imMsg.setMsg("测试数据");
        dataContent.setImMsg(imMsg);
        dataContent.setAction(2);
        System.out.println(JSON.toJSONString(dataContent));
        String s = "{\"action\":2,\"imMsg\":{\"msg\":\"测试数据\",\"receiverId\":\"10010\",\"senderId\":\"10086\"}}";
        System.out.println(JSON.parseObject(s, DataContent.class).toString());
    }
}
