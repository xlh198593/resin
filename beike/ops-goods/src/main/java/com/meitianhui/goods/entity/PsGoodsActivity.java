package com.meitianhui.goods.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品活动
 * 
 * @author Tiny
 *
 */
public class PsGoodsActivity implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 活动商品标识 **/
	/**
	 * 活动商品标识
	 **/
	private String goods_activity_id;
	/**
	 * 活动描述，默认每日抢
	 **/
	private String activity_type;
	/**
	 * 商品标识
	 **/
	private String goods_id;
	/**
	 * 商品图片
	 **/
	private String pic_info;
	/**
	 * 排序
	 **/
	private Integer order_no;
	/**
	 * 活动开始时间
	 **/
	private Date start_date;
	/**
	 * 活动结束时间
	 **/
	private Date end_date;
	/**
	 * 活动是否结束，Y（是） N（否）
	 **/
	private String is_finished;
	/**
	 * 创建时间
	 **/
	private Date created_date;
	/**
	 * 备注
	 **/
	private String remark;
	/**
	 * 商品名称
	 **/
	private String goods_title;
	/** 商品价格(现金价) **/
	private BigDecimal goods_price;
	/**
	 * 商品价格(红包价)
	 **/
	private BigDecimal goods_hongbao;
	/**
	 * 商品价格(贝壳价)
	 **/
	private BigDecimal goods_beike;
	/**
	 * 商品价格(市场价)
	 **/
	private BigDecimal market_price;


	/** 商品详情 **/
	private String goods_desc;
	
	
	public String getGoods_activity_id() {
		return goods_activity_id;
	}

	public void setGoods_activity_id(String goods_activity_id) {
		this.goods_activity_id = goods_activity_id;
	}

	public String getActivity_type() {
		return activity_type;
	}

	public void setActivity_type(String activity_type) {
		this.activity_type = activity_type;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getPic_info() {
		return pic_info;
	}

	public void setPic_info(String pic_info) {
		this.pic_info = pic_info;
	}

	public Integer getOrder_no() {
		return order_no;
	}

	public void setOrder_no(Integer order_no) {
		this.order_no = order_no;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public String getIs_finished() {
		return is_finished;
	}

	public void setIs_finished(String is_finished) {
		this.is_finished = is_finished;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getGoods_title() {
		return goods_title;
	}

	public void setGoods_title(String goods_title) {
		this.goods_title = goods_title;
	}

	public BigDecimal getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(BigDecimal goods_price) {
		this.goods_price = goods_price;
	}

	public BigDecimal getGoods_hongbao() {
		return goods_hongbao;
	}

	public void setGoods_hongbao(BigDecimal goods_hongbao) {
		this.goods_hongbao = goods_hongbao;
	}

	public BigDecimal getGoods_beike() {
		return goods_beike;
	}

	public void setGoods_beike(BigDecimal goods_beike) {
		this.goods_beike = goods_beike;
	}

	public BigDecimal getMarket_price() {
		return market_price;
	}

	public void setMarket_price(BigDecimal market_price) {
		this.market_price = market_price;
	}
	public String getGoods_desc() {
		return goods_desc;
	}
	public void setGoods_desc(String goods_desc) {
		this.goods_desc = goods_desc;
	}
	
	
}
