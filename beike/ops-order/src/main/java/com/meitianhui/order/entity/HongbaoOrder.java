package com.meitianhui.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 礼券专区订单信息
 * 
 * @author Tiny
 *
 */
public class HongbaoOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 订单标识 **/
	private String order_id;
	/** 订单编号 **/
	private String order_no;
	/** 订单日期 **/
	private Date order_date;
	/** 会员分类，可选值：stores（便利店）、supplier（供应商）、partner（联盟商） **/
	private String member_type_key;
	/** 会员标识 **/
	private String member_id;
	/** 会员信息 **/
	private String member_info;
	/** 会员手机号码 **/
	private String member_mobile;
	/** 订单商品描述 **/
	private String desc1;
	/** 支付方式 **/
	private String payment_way_key;
	/** 订单金额 **/
	private BigDecimal sale_fee;
	/** 商品数量 **/
	private Integer qty;
	/** 邮寄费用 **/
	private BigDecimal delivery_fee;
	/** 配送地址 **/
	private String delivery_address;
	/** 联系人 **/
	private String contact_person;
	/** 联系人电话 **/
	private String contact_tel;
	/** 物流信息 **/
	private String logistics;
	/** 数据来源 **/
	private String data_source;
	/** 订单状态 **/
	private String status;
	/** 创建时间 **/
	private Date created_date;
	/** 更新时间 **/
	private Date modified_date;
	/** 备注 **/
	private String remark;
	/** 商家备注 **/
	private String biz_remark;
	/** 订单类型(区分''尖货礼包'' ''精品礼包'')' **/
	private String order_type;

	
	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public String getMember_type_key() {
		return member_type_key;
	}

	public void setMember_type_key(String member_type_key) {
		this.member_type_key = member_type_key;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getMember_info() {
		return member_info;
	}

	public void setMember_info(String member_info) {
		this.member_info = member_info;
	}

	public String getMember_mobile() {
		return member_mobile;
	}

	public void setMember_mobile(String member_mobile) {
		this.member_mobile = member_mobile;
	}

	public String getDesc1() {
		return desc1;
	}

	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}

	public String getPayment_way_key() {
		return payment_way_key;
	}

	public void setPayment_way_key(String payment_way_key) {
		this.payment_way_key = payment_way_key;
	}

	public BigDecimal getSale_fee() {
		return sale_fee;
	}

	public void setSale_fee(BigDecimal sale_fee) {
		this.sale_fee = sale_fee;
	}

	public BigDecimal getDelivery_fee() {
		return delivery_fee;
	}

	public void setDelivery_fee(BigDecimal delivery_fee) {
		this.delivery_fee = delivery_fee;
	}

	public String getDelivery_address() {
		return delivery_address;
	}

	public void setDelivery_address(String delivery_address) {
		this.delivery_address = delivery_address;
	}

	public String getContact_person() {
		return contact_person;
	}

	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}

	public String getContact_tel() {
		return contact_tel;
	}

	public void setContact_tel(String contact_tel) {
		this.contact_tel = contact_tel;
	}

	public String getLogistics() {
		return logistics;
	}

	public void setLogistics(String logistics) {
		this.logistics = logistics;
	}

	public String getData_source() {
		return data_source;
	}

	public void setData_source(String data_source) {
		this.data_source = data_source;
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

	public String getBiz_remark() {
		return biz_remark;
	}

	public void setBiz_remark(String biz_remark) {
		this.biz_remark = biz_remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}
	
}
