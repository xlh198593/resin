package com.meitianhui.member.entity;

import java.io.Serializable;
import java.util.Date;

public class MdMemberMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 唯一标识
	 */
	private String message_id;
	/**
	 * 消息类型(order 订单物流 ,finance 账户变动, notice 官方公告, special 优惠活动)
	 */
	private String message_type;
	/**
	 * 用户id
	 */
	private String member_id;
	/**
	 * 消息分类
	 */
	private String message_category;
	/**
	 * 消息标题
	 */
	private String message_title;
	/**
	 * 消息副标题
	 */
	private String message_subtitle;
	/**
	 * 消息文本
	 */
	private String message_text;
	/**
	 * 消息图片
	 */
	private String message_pic;
	/**
	 * 自定义参数
	 */
	private String message_param;
	/**
	 * 读取状态(Y(是) ,N(否))
	 */
	private String is_read;
	/**
	 * 显示状态(Y(是) ,N(否))
	 */
	private String is_show;
	/**
	 * 创建日期
	 */
	private Date created_date;
	/**
	 * 修改日期
	 */
	private Date modified_date;
	/**
	 * 备注
	 */
	private String remark;
	public String getMessage_id() {
		return message_id;
	}
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}
	public String getMessage_type() {
		return message_type;
	}
	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getMessage_category() {
		return message_category;
	}
	public void setMessage_category(String message_category) {
		this.message_category = message_category;
	}
	public String getMessage_title() {
		return message_title;
	}
	public void setMessage_title(String message_title) {
		this.message_title = message_title;
	}
	public String getMessage_subtitle() {
		return message_subtitle;
	}
	public void setMessage_subtitle(String message_subtitle) {
		this.message_subtitle = message_subtitle;
	}
	public String getMessage_text() {
		return message_text;
	}
	public void setMessage_text(String message_text) {
		this.message_text = message_text;
	}
	public String getMessage_pic() {
		return message_pic;
	}
	public void setMessage_pic(String message_pic) {
		this.message_pic = message_pic;
	}
	public String getMessage_param() {
		return message_param;
	}
	public void setMessage_param(String message_param) {
		this.message_param = message_param;
	}
	public String getIs_read() {
		return is_read;
	}
	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}
	public String getIs_show() {
		return is_show;
	}
	public void setIs_show(String is_show) {
		this.is_show = is_show;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Date getModified_date() {
		return modified_date;
	}
	public void setModified_date(Date modified_date) {
		this.modified_date = modified_date;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
