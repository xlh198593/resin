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
public class RcOrderTardeLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 日志标识 **/
	private Long log_id;
	/**交易标号(用于out_trade_no)**/
	private String trade_no;
	/** 订单编号 **/
	private String order_no;
	/** 充值金额(默认为本币) **/
	private BigDecimal amount;
	/** 支付方式 **/
	private String payment_way_key;
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
	
	public String getData_source() {
		return data_source;
	}
	public void setData_source(String data_source) {
		this.data_source = data_source;
	}
	public Long getLog_id() {
		return log_id;
	}
	public String getTrade_type_key() {
		return trade_type_key;
	}
	public void setTrade_type_key(String trade_type_key) {
		this.trade_type_key = trade_type_key;
	}
	public void setLog_id(Long log_id) {
		this.log_id = log_id;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getPayment_way_key() {
		return payment_way_key;
	}
	public void setPayment_way_key(String payment_way_key) {
		this.payment_way_key = payment_way_key;
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
