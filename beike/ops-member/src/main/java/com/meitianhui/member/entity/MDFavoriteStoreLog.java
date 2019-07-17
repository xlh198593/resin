package com.meitianhui.member.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 绑定店东操作日志
 * 
 * @author Tiny
 *
 */
public class MDFavoriteStoreLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	/** 消费者标识 **/
	private String consumer_id;
	/** 消费者手机号码 **/
	private String consumer_mobile;
	/** 加盟店标识 **/
	private String stores_id;
	/** 加盟店手机号码 **/
	private String stores_mobile;
	/** 是否是领了么默认门店 **/
	private String operator;
	/** 创建时间 **/
	private Date created_date;
	/** 备注 **/
	private String remark;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getConsumer_mobile() {
		return consumer_mobile;
	}
	public void setConsumer_mobile(String consumer_mobile) {
		this.consumer_mobile = consumer_mobile;
	}
	public String getStores_mobile() {
		return stores_mobile;
	}
	public void setStores_mobile(String stores_mobile) {
		this.stores_mobile = stores_mobile;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getConsumer_id() {
		return consumer_id;
	}
	public void setConsumer_id(String consumer_id) {
		this.consumer_id = consumer_id;
	}
	public String getStores_id() {
		return stores_id;
	}
	public void setStores_id(String stores_id) {
		this.stores_id = stores_id;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
