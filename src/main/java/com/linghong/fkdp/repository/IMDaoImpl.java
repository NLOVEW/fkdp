package com.linghong.fkdp.repository;


import com.linghong.fkdp.utils.EncodeUtil;
import com.linghong.fkdp.utils.IDUtil;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class IMDaoImpl {

	/**
	 * Post请求用于增删改
	 * 
	 * @param url
	 *            访问的地址
	 * @param map
	 *            参数
	 * @return
	 */
	public String IMPost(String url, Map<String, String> map,String appKey,String appSecret)
			throws Exception {
		// 1、 创建httpClient对象
		HttpClient client = HttpClients.createDefault();
		// 2、 请求对象
		HttpPost httpPost = new HttpPost(url);
		//3、设置请求头
		 String nonce = IDUtil.getUUID();
		String curTime = String.valueOf(System.currentTimeMillis() / 1000);
		 String plaintext = new StringBuffer(appSecret).append(nonce).append(curTime).toString();
		 String checksum = EncodeUtil.encode(plaintext, "SHA1");
		 //SHA1(AppSecret + Nonce + CurTime),三个参数拼接的字符串，进行SHA1哈希计算，转化成16进制字符(String，小写)
		 httpPost.addHeader("AppKey", appKey);//开发者平台分配的appkey
		 httpPost.addHeader("Nonce", nonce);//随机数（最大长度128个字符）
		 httpPost.addHeader("CurTime", curTime);//当前UTC时间戳，从1970年1月1日0点0 分0 秒开始到现在的秒数(String)
		httpPost.addHeader("CheckSum", checksum);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		System.out.println(httpPost+"-------------IMDaoImpl");
		// 4、 绑定参数
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (String key : map.keySet()) {
			if (map.get(key) == null || "".equals(map.get(key))) {
				continue;
			}
			nameValuePairs.add(new BasicNameValuePair(key, map.get(key)));
		}
		
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
				Charsets.UTF_8));
		
		// 5、 发送请求
		HttpResponse httpResponse = client.execute(httpPost);
		
		// 6、 处理结果数据
		HttpEntity httpEntity = httpResponse.getEntity();
		String string = EntityUtils.toString(httpEntity,"utf-8");
		return string;
	}

	public String IMPostObject(String url, Map<String, Object> map,
			String appKey, String appSecret) throws Exception {
		// 1、 创建httpClient对象
				HttpClient client = HttpClients.createDefault();
				
				// 2、 请求对象
				HttpPost httpPost = new HttpPost(url);
				
				//3、设置请求头
				 String nonce = IDUtil.getUUID();
				String curTime = String.valueOf(System.currentTimeMillis() / 1000);
				 String plaintext = new StringBuffer(appSecret).append(nonce).append(curTime).toString();
				 String checksum = EncodeUtil.encode(plaintext, "SHA1");
				 //SHA1(AppSecret + Nonce + CurTime),三个参数拼接的字符串，进行SHA1哈希计算，转化成16进制字符(String，小写)
				 httpPost.addHeader("AppKey", appKey);//开发者平台分配的appkey
				 httpPost.addHeader("Nonce", nonce);//随机数（最大长度128个字符）
				 httpPost.addHeader("CurTime", curTime);//当前UTC时间戳，从1970年1月1日0点0 分0 秒开始到现在的秒数(String)
				httpPost.addHeader("CheckSum", checksum);
				httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
				System.out.println(httpPost+"-------------IMDaoImpl");
				// 4、 绑定参数
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				for (String key : map.keySet()) {
					if (map.get(key) == null || "".equals(map.get(key))) {
						continue;
					}
					nameValuePairs.add(new BasicNameValuePair(key, (String) map.get(key)));
				}
				
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						Charsets.UTF_8));
				
				// 5、 发送请求
				HttpResponse httpResponse = client.execute(httpPost);
				// 6、 处理结果数据
				HttpEntity httpEntity = httpResponse.getEntity();
				String string = EntityUtils.toString(httpEntity,"utf-8");
				return string;
	}


}
