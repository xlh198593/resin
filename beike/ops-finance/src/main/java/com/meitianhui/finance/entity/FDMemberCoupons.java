package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员礼券关系表
 * 
 * @author Tiny
 *
 */
public class FDMemberCoupons implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 会员礼券id **/
	private String member_coupons_id;
	/** 交易编号 **/
	private String member_id;
	/** 礼券有效期(开始时间) **/
	private Date coupons_validity_start;
	/** 礼券有限期(结束时间 ) **/
	private Date coupons_validity;
	/** 礼券签到(结束时间 )**/
	private Date coupons_validity_end;
	/** 礼券key **/
	private String coupons_key;
	/** 创建时间 **/
	private Date created_date;
	/** 礼券数量 **/
	private Integer coupons_mun;
	private Integer shell_count;
	private Integer repair_count;
	private Integer repair_count_all;
	private String is_first;
	private String sign_data_repair;
	/** 签到时间 **/
	private String sign_data;
	/** 是否激活(''Y''是, ''N''否) **/
	private String is_activate;

	public Date getCoupons_validity_end() {
		return coupons_validity_end;
	}

	public void setCoupons_validity_end(Date coupons_validity_end) {
		this.coupons_validity_end = coupons_validity_end;
	}
	public String getIs_activate() {
		return is_activate;
	}
	public void setIs_activate(String is_activate) {
		this.is_activate = is_activate;
	}
	public String getSign_data() {
		return sign_data;
	}
	public void setSign_data(String sign_data) {
		this.sign_data = sign_data;
	}
	public Date getCoupons_validity_start() {
		return coupons_validity_start;
	}
	public void setCoupons_validity_start(Date coupons_validity_start) {
		this.coupons_validity_start = coupons_validity_start;
	}
	public String getMember_coupons_id() {
		return member_coupons_id;
	}
	public void setMember_coupons_id(String member_coupons_id) {
		this.member_coupons_id = member_coupons_id;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public Date getCoupons_validity() {
		return coupons_validity;
	}
	public void setCoupons_validity(Date coupons_validity) {
		this.coupons_validity = coupons_validity;
	}
	public String getCoupons_key() {
		return coupons_key;
	}
	public void setCoupons_key(String coupons_key) {
		this.coupons_key = coupons_key;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Integer getCoupons_mun() {
		return coupons_mun;
	}
	public void setCoupons_mun(Integer coupons_mun) {
		this.coupons_mun = coupons_mun;
	}

	public Integer getRepair_count() {
		return repair_count;
	}

	public void setRepair_count(Integer repair_count) {
		this.repair_count = repair_count;
	}

	public String getIs_first() {
		return is_first;
	}

	public void setIs_first(String is_first) {
		this.is_first = is_first;
	}

	public String getSign_data_repair() {
		return sign_data_repair;
	}

	public void setSign_data_repair(String sign_data_repair) {
		this.sign_data_repair = sign_data_repair;
	}

	public Integer getRepair_count_all() {
		return repair_count_all;
	}

	public void setRepair_count_all(Integer repair_count_all) {
		this.repair_count_all = repair_count_all;
	}


	public Integer getShell_count() {
		return shell_count;
	}

	public void setShell_count(Integer shell_count) {
		this.shell_count = shell_count;
	}
}
