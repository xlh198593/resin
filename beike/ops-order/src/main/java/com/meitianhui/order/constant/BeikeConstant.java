package com.meitianhui.order.constant;

import java.util.HashMap;
import java.util.Map;

import com.meitianhui.common.constant.CommonConstant;

public class BeikeConstant extends CommonConstant {

	/** 交易状态 **/
	public static final Map<String, String> ORDER_STATUS_MAP = new HashMap<String, String>();

	/** 等待用户付款 **/
	public static final String ORDER_STATUS_01 = "wait_buyer_pay";
	/** 等待卖家发货 **/
	public static final String ORDER_STATUS_02 = "wait_seller_send_goods";
	/** 等待买家确认 **/
	public static final String ORDER_STATUS_03 = "wait_buyer_confirm_goods";
	/** 交易成功 **/
	public static final String ORDER_STATUS_04 = "trade_finished";
	/** 交易关闭 **/
	public static final String ORDER_STATUS_05 = "trade_closed";
	/** 交易被系统关闭 **/
	public static final String ORDER_STATUS_06 = "trade_closed_by_system";
	/** 退款/售后  **/
	public static final String ORDER_STATUS_07 = "refund";
	
	
	
	
	static {
		ORDER_STATUS_MAP.put(ORDER_STATUS_01, "等待用户付款");
		ORDER_STATUS_MAP.put(ORDER_STATUS_02, "等待卖家发货");
		ORDER_STATUS_MAP.put(ORDER_STATUS_03, "等待买家确认");
		ORDER_STATUS_MAP.put(ORDER_STATUS_04, "交易成功");
		ORDER_STATUS_MAP.put(ORDER_STATUS_05, "交易关闭");
		ORDER_STATUS_MAP.put(ORDER_STATUS_06, "交易被系统关闭");
		ORDER_STATUS_MAP.put(ORDER_STATUS_07, "退款/售后");
	}

}
