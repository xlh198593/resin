package com.ande.buyb2c.order;
/**
 * @author chengzb
 * @date 2018年1月31日下午2:36:15
 */
public enum RefundOrderCode {
WAIT_REFUND("1","待付款"),
ALREADY_REFUND("2","已退款"),
CANCEL_REFUND_APPLICATION("3","取消退款申请");
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
	private RefundOrderCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
