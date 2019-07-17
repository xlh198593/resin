package com.meitianhui.member.entity;

import java.io.Serializable;

public class MdAppAccessRecord implements Serializable{

	
	private static final long serialVersionUID = -4457781308468383110L;
	private Integer id;
	//app类型，ios或android
	private String app_type;
	//app具体类型，如iphone 6S
	private String app_type_detail;
	//当前app版本号
	private String app_version;
	//会员id
	private String member_id;
	//访问的方法
	private String access_method;
	//创建时间
	private String create_time;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getApp_type() {
		return app_type;
	}
	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}
	public String getApp_type_detail() {
		return app_type_detail;
	}
	public void setApp_type_detail(String app_type_detail) {
		this.app_type_detail = app_type_detail;
	}
	public String getApp_version() {
		return app_version;
	}
	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getAccess_method() {
		return access_method;
	}
	public void setAccess_method(String access_method) {
		this.access_method = access_method;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
	
	
}
