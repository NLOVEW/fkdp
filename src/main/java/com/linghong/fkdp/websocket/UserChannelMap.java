package com.linghong.fkdp.websocket;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description: 用户id和channel的关联关系处理
 */
public class UserChannelMap {
	private static Logger logger = LoggerFactory.getLogger(UserChannelMap.class);

	private static ConcurrentMap<String, Channel> manager = new ConcurrentHashMap<>();

	public static void put(String userId, Channel channel) {
		manager.put(userId, channel);
	}
	
	public static Channel get(String userId) {
		return manager.get(userId);
	}
	
	public static void output() {
		manager.forEach((key,value)->{
			logger.info("userId:{} <----> channelId:{}",key,value);
		});
	}
}
