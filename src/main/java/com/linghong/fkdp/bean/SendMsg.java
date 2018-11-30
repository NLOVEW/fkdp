package com.linghong.fkdp.bean;
/**
 * 发送普通消息参数封装的实体类
 * @author Administrator
 *
 */
public class SendMsg {
	/**
	 * 必须参数	发送者accid，用户帐号，最大32字符，必须保证一个APP内唯一
	 */
	private String from;
	
	/**
	 * 必须参数	0：点对点个人消息，1：群消息（高级群），其他返回414
	 */
	private String ope;
	
	/**
	 * 必须参数	ope==0是表示accid即用户id，ope==1表示tid即群id
	 */
	private String to;
	
	/**
	 * 必须参数	0 表示文本消息,1 表示图片，2 表示语音，3 表示视频，4 表示地理位置信息，6 表示文件，
	 * 100 自定义消息类型（特别注意，对于未对接易盾反垃圾功能的应用，该类型的消息不会提交反垃圾系统检测）
	 */
	private String type;
	
	/**
	 * 必须参数	请参考下方消息示例说明中对应消息的body字段，最大长度5000字符，为一个JSON串
	 */
	private String body;
	
	private String antispam;//	String	否	对于对接了易盾反垃圾功能的应用，本消息是否需要指定经由易盾检测的内容（antispamCustom）。
	//true或false, 默认false。只对消息类型为：100 自定义消息类型 的消息生效。antispamCustom	String	否	在antispam参数为true时生效。
	//自定义的反垃圾检测内容, JSON格式，长度限制同body字段，不能超过5000字符，要求antispamCustom格式如下：
	//{"type":1,"data":"custom content"}
	//字段说明：1. type: 1：文本，2：图片。2. data: 文本内容or图片地址。
	
	private String option;//String	否	发消息时特殊指定的行为选项,JSON格式，可用于指定消息的漫游，存云端历史，发送方多端同步，推送，消息抄送等特殊行为;option中字段不填时表示默认值 ，option示例:
	//{"push":false,"roam":true,"history":false,"sendersync":true,"route":false,"badge":false,"needPushNick":true}
	//字段说明：1. roam: 该消息是否需要漫游，默认true（需要app开通漫游消息功能）； 2. history: 该消息是否存云端历史，默认true；
	// 3. sendersync: 该消息是否需要发送方多端同步，默认true； 4. push: 该消息是否需要APNS推送或安卓系统通知栏推送，默认true； 5. route: 该消息是否需要抄送第三方；默认true (需要app开通消息抄送功能);
	// 6. badge:该消息是否需要计入到未读计数中，默认true;7. needPushNick: 推送文案是否需要带上昵称，不设置该参数时默认true;8. persistent: 是否需要存离线消息，不设置该参数时默认true。
	private String pushcontent;//	String	否	ios推送内容，不超过150字符，option选项中允许推送（push=true），此字段可以指定推送内容
	private String payload;//	String	否	ios 推送对应的payload,必须是JSON,不能超过2k字符
	private String ext;//	String	否	开发者扩展字段，长度限制1024字符
	private String forcepushlist;//	String	否	发送群消息时的强推（@操作）用户列表，格式为JSONArray，如["accid1","accid2"]。若forcepushall为true，则forcepushlist为除发送者外的所有有效群成员
	private String forcepushcontent;//	String	否	发送群消息时，针对强推（@操作）列表forcepushlist中的用户，强制推送的内容
	private String forcepushall;//	String	否	发送群消息时，强推（@操作）列表是否为群里除发送者外的所有有效成员，true或false，默认为false
	private String bid;//	String	否	可选，反垃圾业务ID，实现“单条消息配置对应反垃圾”，若不填则使用原来的反垃圾配置
	private String useYidun;//	int	否	可选，单条消息是否使用易盾反垃圾，可选值为0。 0：（在开通易盾的情况下）不使用易盾反垃圾而是使用通用反垃圾，包括自定义消息。
	//若不填此字段，即在默认情况下，若应用开通了易盾反垃圾功能，则使用易盾反垃圾来进行垃圾消息的判断
	private String markRead;//	int	否	可选，群消息是否需要已读业务（仅对群消息有效），0:不需要，1:需要
	public SendMsg() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getOpe() {
		return ope;
	}
	public void setOpe(String ope) {
		this.ope = ope;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
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
	public String getAntispam() {
		return antispam;
	}
	public void setAntispam(String antispam) {
		this.antispam = antispam;
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
	public String getForcepushlist() {
		return forcepushlist;
	}
	public void setForcepushlist(String forcepushlist) {
		this.forcepushlist = forcepushlist;
	}
	public String getForcepushcontent() {
		return forcepushcontent;
	}
	public void setForcepushcontent(String forcepushcontent) {
		this.forcepushcontent = forcepushcontent;
	}
	public String getForcepushall() {
		return forcepushall;
	}
	public void setForcepushall(String forcepushall) {
		this.forcepushall = forcepushall;
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
	public String getMarkRead() {
		return markRead;
	}
	public void setMarkRead(String markRead) {
		this.markRead = markRead;
	}


	
}
