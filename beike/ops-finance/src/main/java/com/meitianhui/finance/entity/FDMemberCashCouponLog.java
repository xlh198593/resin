package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FDMemberCashCouponLog  implements Serializable{

	private static final long serialVersionUID = 8039306802575596645L;

	private String log_id;
	private String member_type_key;
	private String member_id;
	private String category;
	private BigDecimal pre_balance;
	private BigDecimal amount;
	private BigDecimal balance;
	private String transaction_no;
	private Date tracked_date;
	private String remark;
	
	
	public String getLog_id() {
		return log_id;
	}
	public void setLog_id(String log_id) {
		this.log_id = log_id;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public BigDecimal getPre_balance() {
		return pre_balance;
	}
	public void setPre_balance(BigDecimal pre_balance) {
		this.pre_balance = pre_balance;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getTransaction_no() {
		return transaction_no;
	}
	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}
	public Date getTracked_date() {
		return tracked_date;
	}
	public void setTracked_date(Date tracked_date) {
		this.tracked_date = tracked_date;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
