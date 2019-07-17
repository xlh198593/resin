package com.meitianhui.order.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 贝壳商城订单礼券关系表
 */
public class HongBaoOrderCoupons implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 订单礼券id */
	private Long order_coupons_id;
	/** 订单编号 */
	private String order_no;
	/** 礼券id */
	private String coupons_id;
	/** 礼券key */
	private String coupons_key;
	/** 礼券名称 */
	private String coupons_name;
	/** 礼券满足的金额 */
	private Integer coupons_amount;
	/** 礼券减去的金额 */
	private Integer coupons_subtract;
	/** 礼券有限期 */
	private Date coupons_validity;
	/** 创建时间 */
	private Date created_date;
	
	public Long getOrder_coupons_id() {
		return order_coupons_id;
	}
	public void setOrder_coupons_id(Long order_coupons_id) {
		this.order_coupons_id = order_coupons_id;
	}
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public String getCoupons_id() {
		return coupons_id;
	}
	public void setCoupons_id(String coupons_id) {
		this.coupons_id = coupons_id;
	}
	public String getCoupons_key() {
		return coupons_key;
	}
	public void setCoupons_key(String coupons_key) {
		this.coupons_key = coupons_key;
	}
	public Date getCoupons_validity() {
		return coupons_validity;
	}
	public void setCoupons_validity(Date coupons_validity) {
		this.coupons_validity = coupons_validity;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public String getCoupons_name() {
		return coupons_name;
	}
	public void setCoupons_name(String coupons_name) {
		this.coupons_name = coupons_name;
	}
	public Integer getCoupons_amount() {
		return coupons_amount;
	}
	public void setCoupons_amount(Integer coupons_amount) {
		this.coupons_amount = coupons_amount;
	}
	public Integer getCoupons_subtract() {
		return coupons_subtract;
	}
	public void setCoupons_subtract(Integer coupons_subtract) {
		this.coupons_subtract = coupons_subtract;
	}
	
}
