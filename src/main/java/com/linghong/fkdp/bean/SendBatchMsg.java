package com.linghong.fkdp.bean;
/**
 * 批量发送点对点普通消息--参数实体
 * @author Administrator
 *
 */
public class SendBatchMsg {
	/**
	 * 必须参数	发送者accid，用户帐号，最大32字符，必须保证一个APP内唯一
	 */
	private String fromAccid;
	/**
	 * 必须参数	["aaa","bbb"]（JSONArray对应的accid，如果解析出错，会报414错误），限500人
	 */
	private String toAccids;
	/**
	 * 必须参数	0 表示文本消息,1 表示图片，2 表示语音，3 表示视频，4 表示地理位置信息，6 表示文件，100 自定义消息类型
	 */
	private String type;	
	/**
	 * 请参考下方消息示例说明中对应消息的body字段，
		最大长度5000字符，为一个json串
	 */
	private String body;	
	/**
	 * 发消息时特殊指定的行为选项,Json格式，可用于指定消息的漫游，存云端历史，发送方多端同步，推送，消息抄送等特殊行为;option中字段不填时表示默认值 option示例:
		{"push":false,"roam":true,"history":false,"sendersync":true,"route":false,"badge":false,"needPushNick":true}
			字段说明：
				1. roam: 该消息是否需要漫游，默认true（需要app开通漫游消息功能）； 
				2. history: 该消息是否存云端历史，默认true；
				3. sendersync: 该消息是否需要发送方多端同步，默认true；
				4. push: 该消息是否需要APNS推送或安卓系统通知栏推送，默认true；
				5. route: 该消息是否需要抄送第三方；默认true (需要app开通消息抄送功能);
				6. badge:该消息是否需要计入到未读计数中，默认true;
				7. needPushNick: 推送文案是否需要带上昵称，不设置该参数时默认true;
				8. persistent: 是否需要存离线消息，不设置该参数时默认true。
	 */
	private String option;
	/**
	 *ios推送内容，不超过150字符，option选项中允许推送（push=true），此字段可以指定推送内容
	 */
	private String pushcontent;
	/**
	 * ios 推送对应的payload,必须是JSON,不能超过2k字符
	 */
	private String payload;
	/**
	 * 开发者扩展字段，长度限制1024字符
	 */
	private String ext;
	/**
	 * 可选，反垃圾业务ID，实现“单条消息配置对应反垃圾”，若不填则使用原来的反垃圾配置
	 */
	private String bid;
	/**
	 * 可选，单条消息是否使用易盾反垃圾，可选值为0。 0：（在开通易盾的情况下）不使用易盾反垃圾而是使用通用反垃圾，包括自定义消息。
		若不填此字段，即在默认情况下，若应用开通了易盾反垃圾功能，则使用易盾反垃圾来进行垃圾消息的判断
	 */
	private String useYidun;
	public SendBatchMsg() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getFromAccid() {
		return fromAccid;
	}
	public void setFromAccid(String fromAccid) {
		this.fromAccid = fromAccid;
	}
	public String getToAccids() {
		return toAccids;
	}
	public void setToAccids(String toAccids) {
		this.toAccids = toAccids;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
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
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getUseYidun() {
		return useYidun;
	}
	public void setUseYidun(String useYidun) {
		this.useYidun = useYidun;
	}
	
	
}
