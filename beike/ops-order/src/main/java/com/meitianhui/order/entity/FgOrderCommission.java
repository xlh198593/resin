package com.meitianhui.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 免费领样订单信息
 * 
 * @author Tiny
 *
 */
public class FgOrderCommission implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 佣金ID **/
	private Integer commission_id;
	/** 订单号 **/
	private String order_id;
	/** 订单用户member_id **/
	private String member_id;
	/** 关联店东id **/
	private String stores_id;
	/** 下单用户登陆手机号 **/
	private String member_phone;
	/** 佣金 **/
	private BigDecimal amount;
	/** 返佣状态（normal:正常返佣,revoke:撤销返佣,cash:已转到零钱) **/
	private String commision_status;
	/** 创建时间 **/
	private Date created_time;
	/** 更新日期 **/
	private Date modified_date;
	/** 注释 **/
	private String remark;

	public Integer getCommission_id() {
		return commission_id;
	}

	public void setCommission_id(Integer commission_id) {
		this.commission_id = commission_id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getStores_id() {
		return stores_id;
	}

	public void setStores_id(String stores_id) {
		this.stores_id = stores_id;
	}

	public String getMember_phone() {
		return member_phone;
	}

	public void setMember_phone(String member_phone) {
		this.member_phone = member_phone;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCommision_status() {
		return commision_status;
	}

	public void setCommision_status(String commision_status) {
		this.commision_status = commision_status;
	}

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
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

	@Override
	public String toString() {
		return "FgOrderCommission [commission_id=" + commission_id + ", order_id=" + order_id + ", member_id="
				+ member_id + ", stores_id=" + stores_id + ", member_phone=" + member_phone + ", amount=" + amount
				+ ", commision_status=" + commision_status + ", created_time=" + created_time + ", modified_date="
				+ modified_date + ", remark=" + remark + "]";
	}
	
	
}
