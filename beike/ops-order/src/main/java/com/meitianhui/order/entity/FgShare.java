package com.meitianhui.order.entity;

import java.io.Serializable;
import java.util.Date;

public class FgShare implements Serializable {

	
	private static final long serialVersionUID = -7145373628387228764L;
	private Integer share_id;
	private String share_name;
	private String share_content;
	private String picture_link;
	private Integer share_status;
	private Date create_time;
	
	public Integer getShare_id() {
		return share_id;
	}
	public void setShare_id(Integer share_id) {
		this.share_id = share_id;
	}
	public String getShare_name() {
		return share_name;
	}
	public void setShare_name(String share_name) {
		this.share_name = share_name;
	}
	public String getShare_content() {
		return share_content;
	}
	public void setShare_content(String share_content) {
		this.share_content = share_content;
	}
	public String getPicture_link() {
		return picture_link;
	}
	public void setPicture_link(String picture_link) {
		this.picture_link = picture_link;
	}
	public Integer getShare_status() {
		return share_status;
	}
	public void setShare_status(Integer share_status) {
		this.share_status = share_status;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	
	
}


