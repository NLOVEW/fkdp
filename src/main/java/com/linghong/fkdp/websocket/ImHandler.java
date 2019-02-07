package com.linghong.fkdp.websocket;

import com.alibaba.fastjson.JSON;
import com.linghong.fkdp.enums.MsgActionEnum;
import com.linghong.fkdp.pojo.ImMsg;
import com.linghong.fkdp.service.ImMsgService;
import com.linghong.fkdp.utils.IDUtil;
import com.linghong.fkdp.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Description: 处理消息的handler
 * TextWebSocketFrame：
 * 	在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ImHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 用于记录和管理所有客户端的channel
	public static ChannelGroup users =
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
		// 获取客户端传输过来的消息
		String content = msg.text();
		Channel currentChannel = ctx.channel();
		// 1. 获取客户端发来的消息
		DataContent dataContent = JSON.parseObject(content, DataContent.class);
		logger.info("接收到客户端消息：{}",dataContent.toString());
		Integer action = dataContent.getAction();
		// 2. 判断消息类型，根据不同的类型来处理不同的业务
		if (action == MsgActionEnum.CONNECT.type) {
			// 	2.1  当websocket 第一次open的时候，初始化channel，把用的channel和userId关联起来
			String senderId = dataContent.getImMsg().getSenderId();
			UserChannelMap.put(senderId, currentChannel);
			//TODO 测试打印出来
			UserChannelMap.output();
		} else if (action == MsgActionEnum.CHAT.type) {
			//  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
			ImMsg imMsg = dataContent.getImMsg();
			String receiverId = imMsg.getReceiverId();
			//获取消息接口
			ImMsgService imMsgService = (ImMsgService) SpringUtil.getBean("imMsgServiceImpl");
			//判断接收者是否在线
			Channel receiverChannel = UserChannelMap.get(receiverId);
			//设置消息Id
			imMsg.setMsgId(IDUtil.getImId());
			imMsg.setCreateTime(LocalDateTime.now());
			if (receiverChannel == null){//说明接收者离线
				//接收者离线状态将消息设置为未读标记 并保存到数据库
				imMsg.setStatus(0);
				imMsgService.save(imMsg);
			}else {//不为空
				// 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
				Channel channel = users.find(receiverChannel.id());
				if (channel != null){//在线的话直接推送信息
					DataContent sendMsg = new DataContent();
					sendMsg.setAction(5);
					sendMsg.setImMsg(imMsg);
					channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(sendMsg)));
					//设置消息已读
					imMsg.setStatus(1);
					imMsgService.save(imMsg);
				}else {//还是说明离线
					//接收者离线状态将消息设置为未读标记 并保存到数据库
					imMsg.setStatus(0);
					imMsgService.save(imMsg);
				}
			}
		} else if (action == MsgActionEnum.SIGNED.type) {
			//签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
			ImMsgService imMsgService = (ImMsgService) SpringUtil.getBean("imMsgServiceImpl");
			// 扩展字段在signed类型的消息中，代表需要去签收的消息id，逗号间隔
			String msgIdsStr = dataContent.getExtend();
			String msgIds[] = msgIdsStr.split(",");
			List<String> msgIdList = new ArrayList<>();
			for (String mid : msgIds) {
				if (StringUtils.isNotBlank(mid)) {
					msgIdList.add(mid);
				}
			}
			logger.info("需要改变签收状态的id:{}", msgIdList.toString());
			if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
				// 批量签收
				imMsgService.updateMsgSigned(msgIdList);
			}
		} else if (action == MsgActionEnum.KEEPALIVE.type) {
			//心跳类型的消息
			logger.info("收到来自channel为[" + currentChannel + "]的心跳包...");
		}
	}
	
	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channel，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		users.add(ctx.channel());
		ctx.channel().writeAndFlush(new TextWebSocketFrame("您已连接webSocket,请先发送action为1的指令"));
		logger.info("向客户端发送连接通知");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().writeAndFlush(new TextWebSocketFrame("您已连接webSocket,请先发送action为1的指令"));
		logger.info("向客户端发送连接通知");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		String channelId = ctx.channel().id().asShortText();
		logger.info("客户端被移除，channelId为：" + channelId);
		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
		users.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		// 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
		ctx.channel().close();
		users.remove(ctx.channel());
	}
}
