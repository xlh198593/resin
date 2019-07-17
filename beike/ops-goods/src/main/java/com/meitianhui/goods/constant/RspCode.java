package com.meitianhui.goods.constant;

import com.meitianhui.common.constant.CommonRspCode;

/**
 * 业务处理异常码及描述</br>
 * 业务码规则:英文或英文简写描述,单词和单词之间用下划线("_")分开</br>
 * 此类需要继承公共异常码CommonRepCode
 * 
 * @author tiny
 * 
 */
public class RspCode extends CommonRspCode {

	/** 门店商品异常 **/
	public static String STORES_GOODS_ERROR = "stores_goods_error";
	/** 商品编码错误 **/
	public static String GOODS_CODE_ERROR = "goods_code_error";

	/** 商品编码已存在 **/
	public static String GOODS_CODE_EXIST = "goods_code_exist";
	/** 商品已存在 **/
	public static String ITEM_STORE_EXIST = "item_store_exist";
	/** 商品规格属性已设置 **/
	public static String GOODS_PROPS_EXIST = "goods_props_exist";
	/** 商品规格属性不存在 **/
	public static String GOODS_PROPS_NOT_EXIST = "goods_props_not_exist";
	/** 商品类别不存在 **/
	public static String GOODS_CAT_NOT_EXIST = "goods_cat_not_exist";
	/** 商品不存在 **/
	public static String ITEM_STORE_NOT_EXIST = "item_store_not_exist";
	/** 商品已存在 **/
	public static String PS_GOODS_EXIST = "ps_goods_exist";
	/** 商品不存在 **/
	public static String PS_GOODS_NOT_EXIST = "ps_goods_not_exist";
	/** 商品可销售库存不足 **/
	public static String GOODS_SALE_ERROR = "goods_sale_error";
	/** 验证码错误 **/
	public static String SKU_CODE_ERROR = "sku_code_error";
	/** 验证码失效 **/
	public static String SKU_CODE_EXPIRED = "sku_code_expired";
	/** 非本店优惠券 **/
	public static String COUPON_OF_STORES_ERROR = "coupon_of_stores_error";
	/** 优惠券信息不存在 **/
	public static String COUPON_NOT_EXIST = "coupon_not_exist";
	/** 优惠券类型错误 **/
	public static String COUPON_CATEGORY_ERROR = "coupon_category_error";

	/** 活动不存在 **/
	public static String ACTIVITY_NOT_EXIST = "activity_not_exist";
	/** 活动状态错误 **/
	public static String ACTIVITY_STATUS_ERROR = "activity_status_error";
	/** 中奖号码错误 **/
	public static String ACTIVITY_LUCK_CODE_ERROR = "activity_luck_code_error";
	/** 活动还在进行中 **/
	public static String ACTIVITY_PROCESSING = "activity_processing";
	/** 广告不存在 **/
	public static String ADVERT_NOT_EXIST = "advert_not_exist";

	/** 商品错误 **/
	public static String PS_GOODS_ERROR = "ps_goods_error";
	/** 新品秀活动异常 **/
	public static String PP_ACTIVITY_ERROR = "pp_activity_error";
	/** 抽奖活动异常 **/
	public static String PL_ACTIVITY_ERROR = "pl_activity_error";

	/** 会员权益异常 **/
	public static String GD_BENEFIT_ERROR = "gd_benefit_error";
	/** 权益活动异常 **/
	public static String GD_ACTIVITY_ERROR = "gd_activity_error";

	/** 红包活动异常 **/
	public static String GC_ACTIVITY = "gc_activity";

	/**http请求错误*/
	public static String HTTP_REQUEST_ERROR = "http_request_error";

	/**http响应数据格式错误*/
	public static String RESPONSE_DATA_ERROR = "response_data_error";

	/**查询区域未知*/
	public static String AREA_NO_EXIT_ERROR = "area_no_exit_error";

	/**查询店铺信息失败*/
	public static String STORE_NO_EXIT_ERROR = "store_no_exit_error";


	static {
		MSG.put(GOODS_CODE_EXIST, "商品编码已存在");
		MSG.put(ITEM_STORE_EXIST, "商品已存在");
		MSG.put(ITEM_STORE_NOT_EXIST, "商品不存在");
		MSG.put(PS_GOODS_EXIST, "商品已存在");
		MSG.put(PS_GOODS_NOT_EXIST, "商品不存在");
		MSG.put(GOODS_SALE_ERROR, "商品可销售库存不足");
		MSG.put(SKU_CODE_ERROR, "验证码错误");
		MSG.put(SKU_CODE_EXPIRED, "验证码失效");
		MSG.put(COUPON_NOT_EXIST, "优惠券信息不存在");
		MSG.put(COUPON_CATEGORY_ERROR, "优惠券类型错误");
		MSG.put(ACTIVITY_NOT_EXIST, "活动商品不存在");
		MSG.put(ACTIVITY_PROCESSING, "活动还在进行中");
		MSG.put(ACTIVITY_LUCK_CODE_ERROR, "中奖号码错误");
		MSG.put(ADVERT_NOT_EXIST, "广告不存在");
		MSG.put(GC_ACTIVITY, "红包活动异常");
		MSG.put(HTTP_REQUEST_ERROR, "http请求错误");
		MSG.put(RESPONSE_DATA_ERROR, "http响应数据格式错误");
		MSG.put(AREA_NO_EXIT_ERROR, "查询区域未知");
		MSG.put(STORE_NO_EXIT_ERROR, "查询店铺信息失败");
	}

}
