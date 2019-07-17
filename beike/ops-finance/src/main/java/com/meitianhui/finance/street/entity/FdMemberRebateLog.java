package com.meitianhui.finance.street.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * fd_member_rebate_log
 * @author 
 */
public class FdMemberRebateLog implements Serializable {
    private Long id;

    /**
     * 会员id
     */
    private String memberId;

    private String mobile;

    /**
     * '操作，expenditure（出账）、income(入账）'
     */
    private String category;

    /**
     * 发生前余额
     */
    private BigDecimal preBalance;

    /**
     * 返佣
     */
    private BigDecimal cashMoney;

    /**
     * 发生后余额
     */
    private BigDecimal balance;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 邀请人
     */
    private String inviteMobile;

    /**
     * 直邀 ：direct  间邀 ：indirect 次邀:nextdirect
     */
    private String type;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPreBalance() {
        return preBalance;
    }

    public void setPreBalance(BigDecimal preBalance) {
        this.preBalance = preBalance;
    }

    public BigDecimal getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(BigDecimal cashMoney) {
        this.cashMoney = cashMoney;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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