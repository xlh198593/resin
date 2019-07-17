package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员资产日志
 * 
 * @author Tiny
 *
 */
public class FDMemberAsset implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 会员资产标识 **/
	private String asset_id;
	/** 会员分类，引用字典分类（HYFL **/
	private String member_type_key;
	/** 会员标识 **/
	private String member_id;
	/** 现金余额 **/
	private BigDecimal cash_balance;
	/** 次邀余额 **/
	private BigDecimal  invite_balance;
	/** 冻结资金 **/
	private BigDecimal cash_froze;
	/**礼券数量*/
	private Integer gift_certificates_368;
	/**礼券数量*/
	private Integer gift_certificates_188;
	/**礼券数量*/
	private Integer gift_certificates_68;
	/** 积分 **/
	private BigDecimal point;
	/** 金币 **/
	private Integer gold;
	/**贝壳金额*/
	private BigDecimal shell;
	/**现金券金额*/
	private BigDecimal cash_coupon;
	/** 经验值 **/
	private Integer experience;
	/** 创建日期 **/
	private Date created_date;
	/** 修改日期 **/
	private Date modified_date;
	/** 备注 **/
	private String remark;
	
	public String getAsset_id() {
		return asset_id;
	}
	public void setAsset_id(String asset_id) {
		this.asset_id = asset_id;
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
	public BigDecimal getCash_balance() {
		return cash_balance;
	}
	public void setCash_balance(BigDecimal cash_balance) {
		this.cash_balance = cash_balance;
	}
	public BigDecimal getInvite_balance() {
		return invite_balance;
	}
	public void setInvite_balance(BigDecimal invite_balance) {
		this.invite_balance = invite_balance;
	}
	public BigDecimal getCash_froze() {
		return cash_froze;
	}
	public void setCash_froze(BigDecimal cash_froze) {
		this.cash_froze = cash_froze;
	}
	public Integer getGold() {
		return gold;
	}
	public void setGold(Integer gold) {
		this.gold = gold;
	}
	public BigDecimal getShell() {
		return shell;
	}
	public void setShell(BigDecimal shell) {
		this.shell = shell;
	}
	public BigDecimal getCash_coupon() {
		return cash_coupon;
	}
	public void setCash_coupon(BigDecimal cash_coupon) {
		this.cash_coupon = cash_coupon;
	}
	public Integer getExperience() {
		return experience;
	}
	public void setExperience(Integer experience) {
		this.experience = experience;
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
	public Integer getGift_certificates_368() {
		return gift_certificates_368;
	}
	public void setGift_certificates_368(Integer gift_certificates_368) {
		this.gift_certificates_368 = gift_certificates_368;
	}
	public Integer getGift_certificates_188() {
		return gift_certificates_188;
	}
	public void setGift_certificates_188(Integer gift_certificates_188) {
		this.gift_certificates_188 = gift_certificates_188;
	}
	public Integer getGift_certificates_68() {
		return gift_certificates_68;
	}
	public void setGift_certificates_68(Integer gift_certificates_68) {
		this.gift_certificates_68 = gift_certificates_68;
	}
	public BigDecimal getPoint() {
		return point;
	}
	public void setPoint(BigDecimal point) {
		this.point = point;
	}
	
}
