package com.linghong.fkdp.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linghong.fkdp.bean.CreateUser;
import com.linghong.fkdp.bean.SendAttachMsg;
import com.linghong.fkdp.bean.SendBatchMsg;
import com.linghong.fkdp.bean.SendMsg;
import com.linghong.fkdp.repository.IMDaoImpl;
import org.springframework.stereotype.Service;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("imServiceImpl")
public class IMServiceImpl {
    private String appKey = "65d982d7d8714b64b7a5f65e341ce50e";
    private String appSecret = "e962667d1fea";
    private String url;
    private IMDaoImpl dao = new IMDaoImpl();

    /**
     * 创建IM账号
     * 必须参数accid账号需要唯一，其他的不需要可以为空
     */
    public JSONObject createUser(CreateUser createUser) throws Exception {
        //1设置访问路径
        url = "https://api.netease.im/nimserver/user/create.action";
        //将参数转为map
        Map<String, String> map = objectToMap(createUser);
        //调用dao执行操作
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }


    /**
     * 网易云通信ID更新
     */
    public JSONObject updateAccid(String accid, String token) throws Exception {
        //1设置访问路径
        url = "https://api.netease.im/nimserver/user/update.action";
        //将参数封装map
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        map.put("token", token);
        map.put("name", "为什么跟新不了");
        //调用dao执行操作
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }


    /**
     * 随机更新并获取新token
     */
    public JSONObject updateToken(String accid) throws Exception {
        url = "https://api.netease.im/nimserver/user/refreshToken.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 封禁网易云通信ID
     * accid 停用的账号
     * needkick 是否剔除账号 默认 false
     */
    public JSONObject disableUser(String accid, String needkick)
            throws Exception {
        url = "https://api.netease.im/nimserver/user/block.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        map.put("needkick", needkick);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 解封网易云通信ID
     * accid 解封的账号
     */
    public JSONObject restoreUser(String accid) throws Exception {
        url = "https://api.netease.im/nimserver/user/unblock.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }


    /**
     * 更新用户名片
     */
    public JSONObject updateUser(CreateUser createUser) throws Exception {
        url = "https://api.netease.im/nimserver/user/updateUinfo.action";
        Map<String, String> map = objectToMap(createUser);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 批量获取用户名片
     */
    public JSONObject selectUser(JSONArray array) throws Exception {
        //public JSONObject selectUser(String array) throws Exception {
        url = "https://api.netease.im/nimserver/user/getUinfos.action";
        Map<String, String> map = new HashMap<String, String>();
        String string = array.toString();
        map.put("accids", string);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 加好友
     * String accid 发起者的id
     * String faccid 接受者id
     * String type 1直接加好友，2请求加好友，3同意加好友，4拒绝加好友
     * String msg 加好友对应的请求消息，第三方组装，最长256字符
     */
    public JSONObject addFriend(String accid, String faccid, String type,
                                String msg) throws Exception {
        url = "https://api.netease.im/nimserver/friend/add.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        map.put("faccid", faccid);
        map.put("type", type);
        map.put("msg", msg);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 更新好友相关信息
     * String accid 必须 发起者accid
     * String faccid  必须 要修改朋友的accid
     * String alias		否 给好友增加备注名，限制长度128
     * String ex		否修改ex字段，限制长度256
     * String serverex	否修改serverex字段，限制长度256此字段client端只读，server端读写
     */
    public JSONObject updateFriend(String accid, String faccid, String alias,
                                   String ex, String serverex) throws Exception {
        url = "https://api.netease.im/nimserver/friend/update.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        map.put("faccid", faccid);
        map.put("alias", alias);
        map.put("ex", ex);
        map.put("serverex", serverex);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 删除好友
     * String accid发起者accid
     * String faccid  要删除朋友的accid
     */
    public JSONObject deleteFriend(String accid, String faccid)
            throws Exception {
        url = "https://api.netease.im/nimserver/friend/delete.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        map.put("faccid", faccid);

        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;

    }

    /**
     * 获取好友关系
     * String accid  必须 	发起者accid
     * String updatetime 	必须	更新时间戳，接口返回该时间戳之后有更新的好友列表
     * String createtime 		【Deprecated】定义同updatetime
     */
    public JSONObject findFriend(String accid) throws Exception {
        url = "https://api.netease.im/nimserver/friend/get.action";

        long time = 0;
        long time1 = new Date().getTime();
        String updatetime = 0 + "";
        String createtime = "" + time1;
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        map.put("updatetime", updatetime);
        map.put("createtime", createtime);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        System.out.println(result.toString() + "------------------------result.toString()");
        return result;
    }

    /**
     * 设置黑名单/静音
     * String accid		用户帐号
     * String targetAcc	被加黑或加静音的帐号
     * String relationType	本次操作的关系类型,1:黑名单操作，2:静音列表操作
     * String value			操作值，0:取消黑名单或静音，1:加入黑名单或静音
     */
    public JSONObject setSpecialRelation(String accid, String targetAcc,
                                         String relationType, String value) throws Exception {
        url = "https://api.netease.im/nimserver/user/setSpecialRelation.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        map.put("targetAcc", targetAcc);
        map.put("relationType", relationType);
        map.put("value", value);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 查看指定用户的黑名单和静音列表
     * String accid		用户帐号
     */
    public JSONObject listBlackAndMuteList(String accid) throws Exception {
        url = "https://api.netease.im/nimserver/user/listBlackAndMuteList.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("accid", accid);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 发送普通消息
     */
    public JSONObject sendMsg(SendMsg sendMsg) throws Exception {
        url = "https://api.netease.im/nimserver/msg/sendMsg.action";
        Map<String, String> map = objectToMap(sendMsg);
        JSONObject sendMsgResult = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return sendMsgResult;
    }

    /**
     * 批量发送点对点普通消息..........此接口受频率控制，一个应用一分钟最多调用120次......  慎用
     */
    public JSONObject sendBatchMsg(SendBatchMsg sendBatchMsg) throws Exception {
        url = "https://api.netease.im/nimserver/msg/sendBatchMsg.action";
        Map<String, String> map = objectToMap(sendBatchMsg);
        JSONObject sendBatchMsgResult = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return sendBatchMsgResult;
    }

    /**
     * 发送自定义系统通知
     * 如某个用户给另一个用户发送好友请求信息等，具体attach为请求消息体，第三方可以自行扩展，建议是json格式
     *
     * @throws Exception
     */
    public JSONObject sendAttachMsg(SendAttachMsg sendAttachMsg) throws Exception {
        url = "https://api.netease.im/nimserver/msg/sendAttachMsg.action";
        Map<String, String> map = objectToMap(sendAttachMsg);
        JSONObject sendAttachMsgResult = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return sendAttachMsgResult;
    }

    /**
     * 文件上传
     * String content  必须参数    字符流base64串(Base64.encode(bytes)) ，最大15M的字符流
     * String type 上传文件类型可选
     * String ishttps  返回的url是否需要为https的url，true或false，默认false可选
     */
    public JSONObject upload(String content, String type, String ishttps)
            throws Exception {
        url = "https://api.netease.im/nimserver/msg/upload.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("content", content);
        map.put("type", type);
        map.put("ishttps", ishttps);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 可以对应用内的所有用户发送广播消息
     * String body广播消息内容，最大4096字符
     * 一个应用一分钟最多调用10次，一天最多调用1000次
     */
    public JSONObject broadcastMsg(String body) throws Exception {
        url = "https://api.netease.im/nimserver/msg/broadcastMsg.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("body", body);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }
//------------------------------------历史消息查询----------------------------------------------

    /**
     * 单聊云端历史消息查询
     * String from		发送者accid
     * String to		接收者accid
     * String begintime	开始时间，ms
     * String endtime		截止时间，ms
     * String limit		本次查询的消息条数上限(最多100条),小于等于0，或者大于100，会提示参数错误
     */
    public JSONObject querySessionMsg(String from, String to, String begintime,
                                      String endtime, String limit) throws Exception {
        url = "https://api.netease.im/nimserver/history/querySessionMsg.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("from", from);
        map.put("to", to);
        map.put("begintime", begintime);
        map.put("endtime", endtime);
        map.put("limit", limit);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 批量查询广播消息
     * String broadcastId	查询的起始ID，0表示查询最近的limit条。默认0。
     * String limit		本次查询的消息条数上限(最多100条),小于等于0，或者大于100，会提示参数错误
     * String type	查询的类型，1表示所有，2表示查询存离线的，3表示查询不存离线的。默认1。
     */
    public JSONObject queryBroadcastMsg(String broadcastId, String limit,
                                        String type) throws Exception {
        url = "https://api.netease.im/nimserver/history/queryBroadcastMsg.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("broadcastId", broadcastId);
        map.put("limit", limit);
        map.put("type", type);
        JSONObject result = JSON.parseObject(dao.IMPost(url, map, appKey, appSecret));
        return result;
    }

    /**
     * 实体类转map
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, String> objectToMap(Object obj) throws Exception {
        if (obj == null)
            return null;

        Map<String, String> map = new HashMap<String, String>();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            String value = (String) (getter != null ? getter.invoke(obj) : null);
            map.put(key, value);
        }

        return map;
    }


}

