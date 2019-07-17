package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yinti
 * 话费充值日志
 *
 */
public class FDTelephoneChargeLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer  id;
	private String  memberId;
	private String mobile;
	private String reqStreamId;
	private String orderNo;
	private String transactionNo;
	private String outTradeNo;
	private BigDecimal amount;
	private BigDecimal companyBalance;
	private Date  tradeTime;
	private String tradeStatus;
	private Date  createTime;
	private String remark;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getReqStreamId() {
		return reqStreamId;
	}
	public void setReqStreamId(String reqStreamId) {
		this.reqStreamId = reqStreamId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getCompanyBalance() {
		return companyBalance;
	}
	public void setCompanyBalance(BigDecimal companyBalance) {
		this.companyBalance = companyBalance;
	}
	public Date getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTradeStatus() {
		return tradeStatus;
	}
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

}
