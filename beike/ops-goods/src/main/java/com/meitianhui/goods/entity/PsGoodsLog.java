package com.meitianhui.goods.entity;

import java.io.Serializable;
import java.util.Date;

public class PsGoodsLog implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 日志标识	 **/
	private String log_id;
	/** 商品标识	 **/
	private String goods_id;
	/** 商品编码	 **/
	private String goods_code;
	/** 事件类别	 **/
	private String category;
	/** 发生时间	 **/
	private Date tracked_date;
	/** 事件	 **/
	private String event;
	public String getLog_id() {
		return log_id;
	}
	public void setLog_id(String log_id) {
		this.log_id = log_id;
	}
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_code() {
		return goods_code;
	}
	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Date getTracked_date() {
		return tracked_date;
	}
	public void setTracked_date(Date tracked_date) {
		this.tracked_date = tracked_date;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
}
