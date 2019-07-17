package com.meitianhui.member.entity;

import java.io.Serializable;
import java.util.Date;

public class MDMemberDistrbutionInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String memberId;
	private String parentId;
	private Date  createTime;
	private Integer status;//0：还未绑定关系，1：已经绑定关系
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
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
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
	public MDMemberDistrbutionInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MDMemberDistrbutionInfo(Long id, String memberId, String parentId, Date createTime, Integer status) {
		super();
		this.id = id;
		this.memberId = memberId;
		this.parentId = parentId;
		this.createTime = createTime;
		this.status = status;
	}

}
