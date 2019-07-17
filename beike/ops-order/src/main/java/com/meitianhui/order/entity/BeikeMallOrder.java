package com.meitianhui.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 贝壳商城订单信息
 * 
 * @author Tiny
 *
 */
public class BeikeMallOrder implements Serializable {

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
	/** 订单商品数量 **/
	private Integer item_num;
	/** 支付方式 **/
	private String payment_way_key;
	/** 商户订单号 **/
	private String transaction_no;
	/** 订单金额 **/
	private BigDecimal sale_fee;
	/** 邮寄费用 **/
	private BigDecimal delivery_fee;
	/** 贝壳抵扣数量 **/
	private Integer beike_credit;
	/** 会员价格 **/
	private BigDecimal vip_fee;
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
	/** 结算状态 **/
	private String settle_status;
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
	/** 红包金额 **/
	private BigDecimal  redpacket_fee;
	/** 人民币金额 **/
	private BigDecimal  trade_fee;
	/** 支付时间 **/
	private Date  pay_date;
	/** 支付时间 **/
	private BigDecimal  actual_price;
	
	
	public BigDecimal getActual_price() {
		return actual_price;
	}

	public void setActual_price(BigDecimal actual_price) {
		this.actual_price = actual_price;
	}

	public Date getPay_date() {
		return pay_date;
	}

	public void setPay_date(Date pay_date) {
		this.pay_date = pay_date;
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

	public Integer getItem_num() {
		return item_num;
	}

	public void setItem_num(Integer item_num) {
		this.item_num = item_num;
	}

	public String getPayment_way_key() {
		return payment_way_key;
	}

	public void setPayment_way_key(String payment_way_key) {
		this.payment_way_key = payment_way_key;
	}

	public String getTransaction_no() {
		return transaction_no;
	}

	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
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

	public Integer getBeike_credit() {
		return beike_credit;
	}

	public void setBeike_credit(Integer beike_credit) {
		this.beike_credit = beike_credit;
	}

	public BigDecimal getVip_fee() {
		return vip_fee;
	}

	public void setVip_fee(BigDecimal vip_fee) {
		this.vip_fee = vip_fee;
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

	public String getSettle_status() {
		return settle_status;
	}

	public void setSettle_status(String settle_status) {
		this.settle_status = settle_status;
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

	public BigDecimal getRedpacket_fee() {
		return redpacket_fee;
	}

	public void setRedpacket_fee(BigDecimal redpacket_fee) {
		this.redpacket_fee = redpacket_fee;
	}

	public BigDecimal getTrade_fee() {
		return trade_fee;
	}

	public void setTrade_fee(BigDecimal trade_fee) {
		this.trade_fee = trade_fee;
	}
	
}
