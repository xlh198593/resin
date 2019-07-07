package com.ande.buyb2c.order;
/**
 * @author chengzb
 * @date 2018年1月31日下午2:36:15
 */
public enum OrderCode {
WAIT_PAY("1","待付款"),
WAIT_SEND("2","待发货"),
WAIT_RECIVE("3","待收货"),
WAIT_EVALUATE("4","待评价"),
ALREADY_COMPLETE("5","已完成"),
CLOSE("6","已关闭"),
ALREADY_CANCEL("7","已取消");
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
	private OrderCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
