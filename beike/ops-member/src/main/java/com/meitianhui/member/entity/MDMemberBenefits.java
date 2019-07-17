package com.meitianhui.member.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 */
public class MDMemberBenefits implements Serializable {

	/**
	 * 会员权益表
	 */
	private static final long serialVersionUID = 4862072875024499105L;

	// 会员权益ID
	private Long benefits_id;

	// 会员权益等级
	private String benefits_type;

	// 成长值区间
	private String growth_range;

	// 每日成长值
	private Integer daily_growth;

	// 首次购买会员比列(例如1:1则为1, 2:1 则为0.5")
	private Double first_purchase;

	// 购物赠送成长值比列
	private Double shopping;

	// 邀请好友注册
	private Integer invite_register;

	// 邀请好友下单比列
	private Double invite_order;

	// 分享商品
	private Integer share_goods;

	// 绑定微信
	private Integer binding_wechat;

	// 完善个人信息
	private Integer perfect_information;

	// 购买天数
	private Integer shopping_day;

	// 浏览商品时长
	private String browse_goods_time;

	// 每月礼券
	private String monthly_gift_certificates;

	// 生日礼券
	private String birthday_gift_certificates;

	// 免邮礼券
	private Integer postage_gift_certificates;

	// 备注
	private Integer remake;

	// 修改时间
	private Date modified_date;

	// 创建时间
	private Date create_time;

	public Long getBenefits_id() {
		return benefits_id;
	}

	public void setBenefits_id(Long benefits_id) {
		this.benefits_id = benefits_id;
	}

	public String getBenefits_type() {
		return benefits_type;
	}

	public void setBenefits_type(String benefits_type) {
		this.benefits_type = benefits_type;
	}

	public String getGrowth_range() {
		return growth_range;
	}

	public void setGrowth_range(String growth_range) {
		this.growth_range = growth_range;
	}

	public Integer getDaily_growth() {
		return daily_growth;
	}

	public void setDaily_growth(Integer daily_growth) {
		this.daily_growth = daily_growth;
	}

	public Double getFirst_purchase() {
		return first_purchase;
	}

	public void setFirst_purchase(Double first_purchase) {
		this.first_purchase = first_purchase;
	}

	public Double getShopping() {
		return shopping;
	}

	public void setShopping(Double shopping) {
		this.shopping = shopping;
	}

	public Integer getInvite_register() {
		return invite_register;
	}

	public void setInvite_register(Integer invite_register) {
		this.invite_register = invite_register;
	}

	public Double getInvite_order() {
		return invite_order;
	}

	public void setInvite_order(Double invite_order) {
		this.invite_order = invite_order;
	}

	public Integer getShare_goods() {
		return share_goods;
	}

	public void setShare_goods(Integer share_goods) {
		this.share_goods = share_goods;
	}

	public Integer getBinding_wechat() {
		return binding_wechat;
	}

	public void setBinding_wechat(Integer binding_wechat) {
		this.binding_wechat = binding_wechat;
	}

	public Integer getPerfect_information() {
		return perfect_information;
	}

	public void setPerfect_information(Integer perfect_information) {
		this.perfect_information = perfect_information;
	}

	public Integer getShopping_day() {
		return shopping_day;
	}

	public void setShopping_day(Integer shopping_day) {
		this.shopping_day = shopping_day;
	}

	public String getBrowse_goods_time() {
		return browse_goods_time;
	}

	public void setBrowse_goods_time(String browse_goods_time) {
		this.browse_goods_time = browse_goods_time;
	}

	public String getMonthly_gift_certificates() {
		return monthly_gift_certificates;
	}

	public void setMonthly_gift_certificates(String monthly_gift_certificates) {
		this.monthly_gift_certificates = monthly_gift_certificates;
	}

	public String getBirthday_gift_certificates() {
		return birthday_gift_certificates;
	}

	public void setBirthday_gift_certificates(String birthday_gift_certificates) {
		this.birthday_gift_certificates = birthday_gift_certificates;
	}

	public Integer getPostage_gift_certificates() {
		return postage_gift_certificates;
	}

	public void setPostage_gift_certificates(Integer postage_gift_certificates) {
		this.postage_gift_certificates = postage_gift_certificates;
	}

	public Integer getRemake() {
		return remake;
	}

	public void setRemake(Integer remake) {
		this.remake = remake;
	}

	public Date getModified_date() {
		return modified_date;
	}

	public void setModified_date(Date modified_date) {
		this.modified_date = modified_date;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

}
