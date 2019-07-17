package com.meitianhui.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务处理异常码及描述</br>
 * 业务码规则:英文或英文简写描述,单词和单词之间用下划线("_")分开</br>
 * 此类定义一些常用的异常码,所有业务的异常码都要集成此类
 * 
 * @author tiny
 * 
 */
public class CommonRspCode {

	/** 异常描述 **/
	public static Map<String, String> MSG = new HashMap<String, String>();
	public static String RESPONSE_SUCC = "succ";
	public static String RESPONSE_FAIL = "fail";

	/** 系统内部异常 **/
	public static String SYSTEM_ERROR = "system_error";
	/** 服务接口错误 **/
	public static String SYSTEM_SERVICE_ERROR = "system_service_error";
	/** 参数缺失 **/
	public static String SYSTEM_PARAM_MISS = "system_param_miss";
	/** 参数格式错误 **/
	public static String SYSTEM_BEAN_TO_MAP_ERROR = "system_bean_to_map_error";
	
	/** 手机号码已存在 **/
	public static String SYSTEM_MOBILE_EXIST_ERROR = "system_mobile_exist_error";
	
	/** 系统没有图片验证码 **/
	public static String SYSTEM_NO_IMAGE_CODE_ERROR = "system_no_image_code_error";
	
	/** 分页对象不存在 **/
	public static String SYSTEM_PAGE_NOT_EXIST = "system_page_not_exist";
	/** 没有参数更新 **/
	public static String SYSTEM_NO_PARAMS_UPDATE = "system_no_params_update";
	/** http连接异常 **/
	public static String HTTP_ERROR = "http_error";
	/** Json异常 **/
	public static String JSON_ERROR = "json_error";

	/** 微信账号不存在 **/
	public static String WECHAT_NOT_EXIST="wechat_not_exist";
	/** 账号不存在 **/
	public static String USER_NOT_EXIST = "user_not_exist";
	/** 账号已存在 **/
	public static String USER_EXIST = "user_exist";

	/** 会员状态异常 **/
	public static String MEMBER_STATUS_ERROR = "member_status_error";

	/** 正在处理中 **/
	public static String PROCESSING = "processing";
	
	/** 手机今天已经使用5次  **/
	public static String MOBILE_USE_BUSY="mobile_use_busy";
	
	/** 微信已经绑定其他用户  **/
	public static String USER_WECHAT_EXIST="user_wechat_exist";
	
	/** 此手机号已经绑定微信  **/
	public static String MOBILE_WECHAT_EXIST="mobile_wechat_exist";
	
	static {
		MSG.put(SYSTEM_ERROR, "服务器开小差了,请稍后重试");
		MSG.put(SYSTEM_SERVICE_ERROR, "服务接口错误");
		MSG.put(SYSTEM_PARAM_MISS, "业务参数缺失");
		MSG.put(SYSTEM_BEAN_TO_MAP_ERROR, "实体类转换MAP出错");
		MSG.put(SYSTEM_PAGE_NOT_EXIST, "分页参数不存在");
		MSG.put(SYSTEM_NO_PARAMS_UPDATE, "没有参数更新");
		MSG.put(WECHAT_NOT_EXIST, "请绑定手机号");
		MSG.put(USER_NOT_EXIST, "该账号不存在");
		MSG.put(USER_EXIST, "该账号已存在");
		MSG.put(PROCESSING, "操作失败,请刷新后重试");
		MSG.put(MOBILE_USE_BUSY, "您获取过于频繁，请明日再试");
		MSG.put(USER_WECHAT_EXIST, "微信已经绑定其他用户");
		MSG.put(MOBILE_WECHAT_EXIST, "此手机号已经绑定 ");
	}

}
