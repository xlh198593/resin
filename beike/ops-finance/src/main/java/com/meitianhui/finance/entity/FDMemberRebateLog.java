package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 	会员推荐返佣日志
 * @author YINTI
 *
 */
public class FDMemberRebateLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private  Integer  id;
	private  String member_id;
	private  String category; //'操作，income（入账）、expenditure（出账）'
	private  BigDecimal pre_balance;//操作前金额
	private  BigDecimal cash_money;//操作金额
	private  BigDecimal balance;//操作后金额
	private  Date create_time;
	private  String remark;
	private  String mobile;//分佣来源
	private  String inviteMobile;//邀请人
	private  String type;//直邀  direct 间邀 direct 次邀 nextdirect
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public BigDecimal getCash_money() {
		return cash_money;
	}
	public void setCash_money(BigDecimal cash_money) {
		this.cash_money = cash_money;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getInviteMobile() {
		return inviteMobile;
	}
	public void setInviteMobile(String inviteMobile) {
		this.inviteMobile = inviteMobile;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
