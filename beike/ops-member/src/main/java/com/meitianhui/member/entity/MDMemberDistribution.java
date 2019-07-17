package com.meitianhui.member.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 会员分销实体
 * @author yinti
 *
 */
public class MDMemberDistribution implements Serializable {

	/**
	 */
	private static final long serialVersionUID = 1L;
	
	private String memberId; //会员id
	private String parentId;//上级ID
	private String grandId; // 爷爷级ID
	private String topId;//顶层id
	private String managerId;//掌柜Id
	private String generalId;//关联的最后的掌柜ID
	private Integer distrLevel;//分销层级
	private Integer registLevel;// 自身注册时所在等级  '68元：1 ；168元：2；368元：3'
	private BigDecimal rechargeMoney;//充值金额
	private BigDecimal receiveMoney;//获取金额
	private Date  createTime;//创建时间
	private Date  updateTime;//修改时间
	private Integer status;//状态
	private String remark;//备注
	private List<MDMemberDistribution>  disrtList;
	
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getGrandId() {
		return grandId;
	}
	public void setGrandId(String grandId) {
		this.grandId = grandId;
	}
	public String getTopId() {
		return topId;
	}
	public void setTopId(String topId) {
		this.topId = topId;
	}
	public String getManagerId() {
		return managerId;
	}
	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}
	public String getGeneralId() {
		return generalId;
	}
	public void setGeneralId(String generalId) {
		this.generalId = generalId;
	}
	public Integer getDistrLevel() {
		return distrLevel;
	}
	public void setDistrLevel(Integer distrLevel) {
		this.distrLevel = distrLevel;
	}
	public Integer getRegistLevel() {
		return registLevel;
	}
	public void setRegistLevel(Integer registLevel) {
		this.registLevel = registLevel;
	}
	public BigDecimal getRechargeMoney() {
		return rechargeMoney;
	}
	public void setRechargeMoney(BigDecimal rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}
	public BigDecimal getReceiveMoney() {
		return receiveMoney;
	}
	public void setReceiveMoney(BigDecimal receiveMoney) {
		this.receiveMoney = receiveMoney;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public List<MDMemberDistribution> getDisrtList() {
		return disrtList;
	}
	public void setDisrtList(List<MDMemberDistribution> disrtList) {
		this.disrtList = disrtList;
	}
	@Override
	public String toString() {
		return "MDMemberDistribution [memberId=" + memberId + ", parentId=" + parentId + ", grandId=" + grandId
				+ ", topId=" + topId + ", managerId=" + managerId + ", generalId=" + generalId + ", distrLevel="
				+ distrLevel + ", registLevel=" + registLevel + ", rechargeMoney=" + rechargeMoney + ", receiveMoney="
				+ receiveMoney + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status
				+ ", remark=" + remark + ", disrtList=" + disrtList + ", getMemberId()=" + getMemberId()
				+ ", getParentId()=" + getParentId() + ", getGrandId()=" + getGrandId() + ", getTopId()=" + getTopId()
				+ ", getManagerId()=" + getManagerId() + ", getGeneralId()=" + getGeneralId() + ", getDistrLevel()="
				+ getDistrLevel() + ", getRegistLevel()=" + getRegistLevel() + ", getRechargeMoney()="
				+ getRechargeMoney() + ", getReceiveMoney()=" + getReceiveMoney() + ", getCreateTime()="
				+ getCreateTime() + ", getStatus()=" + getStatus() + ", getRemark()=" + getRemark()
				+ ", getUpdateTime()=" + getUpdateTime() + ", getDisrtList()=" + getDisrtList() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
	

}
