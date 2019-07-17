package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现管理表
 */
public class FdWithdraw implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 编号
	 */
	private String id;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 申请人真实姓名
	 */
	private String proposer;
	/**
	 * 申请时间
	 */
	private Date add_time;
	/**
	 * 提现金额
	 */
	private BigDecimal amount;
	/**
	 * 手续费
	 */
	private BigDecimal poundage;
	/**
	 * 收款银行类型
	 */
	private String bank_code;
	/**
	 * 收款银行帐号
	 */
	private String bank_account;
	/**
	 * 状态0=提现待处理,1=驳回,2=己打款
	 */
	private Integer status;
	/**
	 * 打款成功上传凭据
	 */
	private String proof_img;
	/**
	 * 操作人标识
	 */
	private String member_id;
	/**
	 * 操作日期
	 */
	private Date opt_time;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 驳回原因
	 */
	private String reject_text;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Date getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getPoundage() {
		return poundage;
	}
	public void setPoundage(BigDecimal poundage) {
		this.poundage = poundage;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getProof_img() {
		return proof_img;
	}
	public void setProof_img(String proof_img) {
		this.proof_img = proof_img;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public Date getOpt_time() {
		return opt_time;
	}
	public void setOpt_time(Date opt_time) {
		this.opt_time = opt_time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getReject_text() {
		return reject_text;
	}
	public void setReject_text(String reject_text) {
		this.reject_text = reject_text;
	}
}
