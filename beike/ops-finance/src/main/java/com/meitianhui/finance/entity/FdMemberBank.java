package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员_银行绑定表
 */
public class FdMemberBank implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 编号
	 */
	private String id;
	/**
	 * 会员分类
	 */
	private String member_type_key;
	/**
	 * 会员标识
	 */
	private String member_id;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 申请人真实姓名
	 */
	private String proposer;
	/**
	 * 收款银行类型
	 */
	private String bank_code;
	/**
	 * 收款银行帐号
	 */
	private String bank_account;
	/**
	 * 创建日期
	 */
	private Date created_date;
	/**
	 * 备注
	 */
	private String remark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getProposer() {
		return proposer;
	}

	public void setProposer(String proposer) {
		this.proposer = proposer;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getBank_account() {
		return bank_account;
	}

	public void setBank_account(String bank_account) {
		this.bank_account = bank_account;
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
