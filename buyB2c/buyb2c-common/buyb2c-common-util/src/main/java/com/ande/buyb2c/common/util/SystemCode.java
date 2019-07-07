package com.ande.buyb2c.common.util;
/**
 * 
 * @author adayang
 * @date 2017年2月28日 下午2:57:39
 * Copyright: Copyright (c) 2016
 * Company:深圳安德互联有限公司 2016 ~ 2017 版权所有
 */
public enum SystemCode {
	FAILURE(0,"请求失败"),
	SUCCESS(1,"请求成功"),
	 
	NOT_ALLOW_OPERATION(99,"不允许的操作"),
	
	ADDRESS_EXIT(98,"默认地址已存在"),
	//验证码
	SMS_OK(100,"验证码正确"),
	SMS_ERROR(101,"验证码错误"),
	PHONE_IS_NULL(102,"手机号为空"),
	PHONE_OR_CODE_IS_NULL(103,"手机号或验证码为空"),
	

	NO_LOGIN(301,"用户未登录或登录超时"),
	REQUEST_OFFEN(303,"请求频繁"),
	
	USER_NOT_EXISTS(411,"用户名不存在或密码错误"),
	WRONG_PASSWORD(412,"用户名密码错误"),

	
	//上传文件
	NULL_ARGUMENT(601,"上传文件无法获取"),
	FILE_SIZE_OUT(602,"文件大小超出5M"),

	FIELD_REPETITION(701,"字段重复");
	private int code;
	private String msg;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	private SystemCode(int code,String msg){
		this.code=code;
		this.msg=msg;
	}
}
