package com.ande.buyb2c.order;
/**
 * @author chengzb
 * @date 2018年1月31日下午2:36:15
 */
public enum PayCode {
WAIT_PAY("1","待付款"),
ALREADY_PAY("2","已付款"),
ALREADY_REFUND("3","已退款");
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
	private PayCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
