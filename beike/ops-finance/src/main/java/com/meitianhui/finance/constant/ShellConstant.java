package com.meitianhui.finance.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meitianhui.common.constant.CommonConstant;

public class ShellConstant extends CommonConstant{
	
	/** 支付方式 **/
	public static final Map<String, String> PAYMENT_WAY_MAP = new HashMap<>();
	/** 支付宝支付 **/
	public static final String PAYMENT_WAY_01 = "ZFFS_01";
	/** 微信支付 **/
	public static final String PAYMENT_WAY_02 = "ZFFS_02";
	/** 支付宝支付 +贝壳支付 **/
	public static final String PAYMENT_WAY_03 = "ZFFS_03";
	/** 微信支付 +贝壳支付 **/
	public static final String PAYMENT_WAY_04 = "ZFFS_04";
	/** 贝壳支付 **/
	public static final String PAYMENT_WAY_07 = "ZFFS_07";
	/** 红包支付 **/
	public static final String PAYMENT_WAY_08 = "ZFFS_08";
	/** 礼券支付 **/
	public static final String PAYMENT_WAY_09 = "ZFFS_09";
	/** H5支付 **/
	public static final String PAYMENT_WAY_10 = "ZFFS_10";
	/** 微信+礼券支付 **/
	public static final String PAYMENT_WAY_22 = "ZFFS_22";
	/** 支付宝+礼券支付 **/
	public static final String PAYMENT_WAY_23 = "ZFFS_23";
	
	/** 交易类型 **/
	public static final Map<String, String> TRADE_TYPE_MAP = new HashMap<>();
	/** 订单支付 **/
	public static final String TRADE_TYPE_01 = "JYLX_01";
	/** 订单退款 **/
	public static final String TRADE_TYPE_02 = "JYLX_02";
	/** 订单结算 **/
	public static final String TRADE_TYPE_03 = "JYLX_03";
	/** 订单冲正 **/
	public static final String TRADE_TYPE_04 = "JYLX_04";
	/** 余额提现 **/
	public static final String TRADE_TYPE_05 = "JYLX_05";
	
	
	/** 订单类型  **/
	public static final Map<String, String> ORDER_TYPE_MAP = new HashMap<>();
	
	/** 话费充值订单 **/
	public static final String ORDER_TYPE_05 = "DDLX_05";
	/** 充值订单 **/
	public static final String ORDER_TYPE_06 = "DDLX_06";
	/** 惠商城订单 **/
	public static final String ORDER_TYPE_08 = "DDLX_08";
	/** 贝壳兑订单 **/
	public static final String ORDER_TYPE_09 = "DDLX_09";
	/** 礼包专区订单 **/
	public static final String ORDER_TYPE_10 = "DDLX_10";
	/** 贝壳商城订单 **/
	public static final String ORDER_TYPE_11 = "DDLX_11";
	/** 贝壳街市订单 **/
	public static final String ORDER_TYPE_12 = "DDLX_12";
	/** 礼包赠送订单 **/
	public static final String ORDER_TYPE_13 = "DDLX_13";
	/** 购买会员赠送 **/
	public static final String ORDER_TYPE_14 = "DDLX_14";
	/** 余额提现订单 **/
	public static final String ORDER_TYPE_15 = "DDLX_15";
	
	/** 每天惠账号 **/
	public static final String MEMBER_ID_MTH = "10000001";
	/** 支付宝账号 **/
	public static final String MEMBER_ID_ZFB = "10000002";
	/** 微信账号 **/
	public static final String MEMBER_ID_WX = "10000003";
	
	/**人民币**/
	public static final String CURRENCY_CNY = "人民币";
	/**贝壳**/
	public static final String CURRENCY_BK = "贝壳";
	/**积分**/
	public static final String CURRENCY_JF = "积分";
	
	
	/**签到天数**/
	public static final int SIGN_IN_DAY = 25;

	/**体验会员签到天数**/
	public static final int SIGN_IN_DAY_EXPERIENCE = 10;

	/** 提现须知  **/
	public static final List<String> ANNUNCIATE = new ArrayList<>();
	
	static{
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_01, "支付宝支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_02, "微信支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_03, "支付宝支付 +贝壳支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_04, "微信支付 +贝壳支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_07, "贝壳支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_08, "红包支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_09, "礼券支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_10, "H5支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_22, "微信+礼券支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_23, "支付宝+礼券支付");
		
		TRADE_TYPE_MAP.put(TRADE_TYPE_01, "订单支付");
		TRADE_TYPE_MAP.put(TRADE_TYPE_02, "订单退款");
		TRADE_TYPE_MAP.put(TRADE_TYPE_03, "订单结算");
		TRADE_TYPE_MAP.put(TRADE_TYPE_04, "订单冲正");
		TRADE_TYPE_MAP.put(TRADE_TYPE_05, "余额提现");
		
		ORDER_TYPE_MAP.put(ORDER_TYPE_06, "充值订单");
		ORDER_TYPE_MAP.put(ORDER_TYPE_08, "惠商城订单");
		ORDER_TYPE_MAP.put(ORDER_TYPE_09, "贝壳兑订单");
		ORDER_TYPE_MAP.put(ORDER_TYPE_10, "礼包专区订单");
		ORDER_TYPE_MAP.put(ORDER_TYPE_11, "贝壳商城订单");
		ORDER_TYPE_MAP.put(ORDER_TYPE_12, "贝壳街市订单");
		ORDER_TYPE_MAP.put(ORDER_TYPE_13, "礼券兑换订单");
		ORDER_TYPE_MAP.put(ORDER_TYPE_14, "购买会员赠送");
		
		ANNUNCIATE.add("1、提现金额：单笔提现金额100~10000元；");
		ANNUNCIATE.add("2、手续费：免手续费；");
		ANNUNCIATE.add("3、到账时间：提交提现申请后，系统会在3个工作日内处理；");
		ANNUNCIATE.add("4、提现次数：每月可提现三次。");
		
	}
	
}
