package com.meitianhui.member.entity;

import java.io.Serializable;
import java.util.Date;

public class MdHongbaoActivity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 红包活动表id
	 */
	private Long activity_id;
	/**
	 * 用户id
	 */
	private String member_id;
	/**
	 * 邀请人id
	 */
	private String parent_id;
	/**
	 * 是否已激活过礼券(''N''否 ''Y''是)
	 */
	private String is_activation;
	/**
	 * 是否已抽取红包(''Y''是 ''N''否)
	 */
	private String is_use;
	/**
	 * 创建日期
	 */
	private Date created_date;
	/**
	 * 修改日期
	 */
	private Date modified_date;
	/**
	 * 备注
	 */
	private String remark;

	public Long getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(Long activity_id) {
		this.activity_id = activity_id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getIs_activation() {
		return is_activation;
	}

	public void setIs_activation(String is_activation) {
		this.is_activation = is_activation;
	}

	public String getIs_use() {
		return is_use;
	}

	public void setIs_use(String is_use) {
		this.is_use = is_use;
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

}
