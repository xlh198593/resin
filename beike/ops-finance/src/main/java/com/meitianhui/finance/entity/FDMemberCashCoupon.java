package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 现金券实体类
 * @author Administrator
 *
 */
public class FDMemberCashCoupon implements Serializable {

	private static final long serialVersionUID = -1808886019607325385L;

	private String coupon_no;
	private String member_id;
	private Byte amount;
	private Byte status ;
	private Date created_time;
	private Date modified_time;
	private String remark;
	
	
	public String getCoupon_no() {
		return coupon_no;
	}
	public void setCoupon_no(String coupon_no) {
		this.coupon_no = coupon_no;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public Byte getAmount() {
		return amount;
	}
	public void setAmount(Byte amount) {
		this.amount = amount;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public Date getCreated_time() {
		return created_time;
	}
	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}
	public Date getModified_time() {
		return modified_time;
	}
	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
}
