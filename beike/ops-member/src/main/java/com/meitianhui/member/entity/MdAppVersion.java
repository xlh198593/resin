package com.meitianhui.member.entity;

import java.io.Serializable;
import java.util.Date;
/**
 * app版本类
 */
public class MdAppVersion  implements Serializable{

	private static final long serialVersionUID = -9162914096289985241L;

	private Integer id;
	
	//版本名称
	private String  version_name;
	
	//手机类型，1:IOS,2:android
	private Integer app_type;
	
	//版本号 v2018.01.1
	private String  version_no;
	
	//版本值，用于比较版本号
	private Integer version_value;
	
	//更新类型，1普通更新，2强制更新
	private String update_type;
	
	//更新状态，1开启，2关闭
	private Integer update_status;
	
	//创建时间
	private Date create_time;
	
	//创建时间字符串
	private String create_time_str;
	//更新的内容
	private String update_content; 
	//安卓下载路径
	private String download_path;
	

	public String getUpdate_content() {
		return update_content;
	}

	public void setUpdate_content(String update_content) {
		this.update_content = update_content;
	}

	public String getDownload_path() {
		return download_path;
	}

	public void setDownload_path(String download_path) {
		this.download_path = download_path;
	}

	public String getCreate_time_str() {
		return create_time_str;
	}

	public void setCreate_time_str(String create_time_str) {
		this.create_time_str = create_time_str;
	}

	public Integer getUpdate_status() {
		return update_status;
	}

	public void setUpdate_status(Integer update_status) {
		this.update_status = update_status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public Integer getApp_type() {
		return app_type;
	}

	public void setApp_type(Integer app_type) {
		this.app_type = app_type;
	}

	public String getVersion_no() {
		return version_no;
	}

	public void setVersion_no(String version_no) {
		this.version_no = version_no;
	}

	public Integer getVersion_value() {
		return version_value;
	}

	public void setVersion_value(Integer version_value) {
		this.version_value = version_value;
	}

	public String getUpdate_type() {
		return update_type;
	}

	public void setUpdate_type(String update_type) {
		this.update_type = update_type;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	
	
	
}
