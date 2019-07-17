package com.meitianhui.infrastructure.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 尹体
 *
 */
public class UserWechat implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private String userWechatId;
	
	/**
	 * 用户手机号
	 */
	private String mobile;
	
	/**
	 * 用户微信唯一标识
	 */
	private String openId;
	/**
	 *微信昵称 
	 */
	private String nickName;

	/**
	 * 性别
	 */
	private Integer sex;
	
	/**
	 * 城市
	 */
	private String city;
	
	/**
	 * 省份
	 */
	private String province;

	/**
	 *国家 
	 */
	private String country;
	
	/**
	 *头像 
	 */
	private String headImgUrl;
	
	/**
	 *微信授权标识 
	 */
	private String unionId;
	
	/**
	 *创建时间 
	 */
	private Date createTime;
	
	/**
	 *状态 
	 */
	private Integer status;
	
	/**
	 * 备注
	 */
	private String remark;

	public String getUserWechatId() {
		return userWechatId;
	}

	public void setUserWechatId(String userWechatId) {
		this.userWechatId = userWechatId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
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

	@Override
	public String toString() {
		return "UserWechat [userWechatId=" + userWechatId + ", mobile=" + mobile + ", openId=" + openId + ", nickName="
				+ nickName + ", sex=" + sex + ", city=" + city + ", province=" + province + ", country=" + country
				+ ", headImgUrl=" + headImgUrl + ", unionId=" + unionId + ", createTime=" + createTime + ", status="
				+ status + ", remark=" + remark + "]";
	}


	
	
}
