package com.ande.buyb2c.order;
/**
 * @author chengzb
 * @date 2018年1月31日下午2:36:15
 */
public enum PayType {
WXPAY("1","微信"),
ALIPAY("2","支付宝");
	private String code;
	private String msg;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	private PayType(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
