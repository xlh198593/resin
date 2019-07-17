package com.meitianhui.common.constant;

public enum SendMsgEnum {
	deliverGoods("发货提醒"),
	memberRenew("会员续费成功提醒"),
	dredgeMember("开通会员提醒"),
	giftPastDue("礼券快过期提醒"),
	memberPastDue("会员过期提醒"),
	memberExpire("会员即将到期"),
	arrivalNotice("到账通知"),
	telephoneCharge("充值话费");
	
	private  final  String name;

	private SendMsgEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
