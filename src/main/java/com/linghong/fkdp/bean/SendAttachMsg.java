package com.linghong.fkdp.bean;

/**
 * 发送自定义系统通知
 * 
 * @author Administrator
 *
 */
public class SendAttachMsg {
	/**
	 * 必须参数 发送者accid，用户帐号，最大32字符，APP内唯一
	 */
	private String from;

	/**
	 * 必须参数 0：点对点自定义通知，1：群消息自定义通知，其他返回414
	 */
	private String msgtype;

	/**
	 * 必须参数 msgtype==0是表示accid即用户id，msgtype==1表示tid即群id
	 */
	private String to;

	/**
	 * 必须参数 自定义通知内容，第三方组装的字符串，建议是JSON串，最大长度4096字符
	 */
	private String attach;
	
	
	private String pushcontent;// String 否 iOS推送内容，第三方自己组装的推送内容,不超过150字符
	private String payload; // String 否 iOS推送对应的payload,必须是JSON,不能超过2k字符
	private String sound;// String 否
							// 如果有指定推送，此属性指定为客户端本地的声音文件名，长度不要超过30个字符，如果不指定，会使用默认声音
	private String save;// int 否 1表示只发在线，2表示会存离线，其他会报414错误。默认会存离线
	private String option;// String 否
							// 发消息时特殊指定的行为选项,Json格式，可用于指定消息计数等特殊行为;option中字段不填时表示默认值。
							// option示例：
							// {"badge":false,"needPushNick":false,"route":false}
							// 字段说明：
							// 1. badge:该消息是否需要计入到未读计数中，默认true;
							// 2. needPushNick:
							// 推送文案是否需要带上昵称，不设置该参数时默认false(ps:注意与sendMsg.action接口有别);
							// 3. route: 该消息是否需要抄送第三方；默认true (需要app开通消息抄送功能)

	public SendAttachMsg() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getPushcontent() {
		return pushcontent;
	}

	public void setPushcontent(String pushcontent) {
		this.pushcontent = pushcontent;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

}
