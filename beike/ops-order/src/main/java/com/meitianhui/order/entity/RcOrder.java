package com.meitianhui.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易流水
 * 
 * @author Tiny
 *
 */
public class RcOrder implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 订单标识 **/
	private Long order_id;
	/**订单编号**/
	private String order_no;
	/** 交易号 **/
	private String transaction_no;
	/**订单日期**/
	private Date order_date;
	/** 充值金额(默认为本币) **/
	private BigDecimal amount;
	/** 积分 **/
	private Integer reward_point;
	/** 贝壳 **/
	private Integer reward_shell;
	/** 支付方式 **/
	private String payment_way_key;
	/** 会员分类  式',可选值：stores（便利店）、supplier（供应商）、partner（联盟商）**/
	private String member_type_key;
	/** 会员标识 **/
	private String member_id;
	/** 会员信息 **/
	private String member_info;
	/** 状态，可选值：non_paid(未支付),paid(已支付)、cancelled(订单取消) **/
	private String status;
	/**创建时间**/
	private Date created_date;
	/**修改时间**/
	private Date modified_date;
	/** 备注 **/
	private String remark;
	/** 数据来源，引用字典分类（SJLY） **/
	private String data_source;
	/** 交易类型，引用字典分类（JYLX） **/
	private String trade_type_key;
	
	public String getTrade_type_key() {
		return trade_type_key;
	}
	public void setTrade_type_key(String trade_type_key) {
		this.trade_type_key = trade_type_key;
	}
	public String getData_source() {
		return data_source;
	}
	public void setData_source(String data_source) {
		this.data_source = data_source;
	}
	public Long getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public String getTransaction_no() {
		return transaction_no;
	}
	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}
	public Date getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Integer getReward_point() {
		return reward_point;
	}
	public void setReward_point(Integer reward_point) {
		this.reward_point = reward_point;
	}
	public Integer getReward_shell() {
		return reward_shell;
	}
	public void setReward_shell(Integer reward_shell) {
		this.reward_shell = reward_shell;
	}
	public String getPayment_way_key() {
		return payment_way_key;
	}
	public void setPayment_way_key(String payment_way_key) {
		this.payment_way_key = payment_way_key;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
