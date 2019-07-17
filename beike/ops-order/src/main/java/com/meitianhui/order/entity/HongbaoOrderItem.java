package com.meitianhui.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包兑订单商品
 * 
 */
public class HongbaoOrderItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/** 订单商品标识 **/
	private String order_item_id;
	/** 订单标识**/
	private String order_id;
	/** 商品标识**/
	private String goods_id;
	/** 商品标题**/
	private String goods_title;
	/** 商品图片**/
	private String goods_pic_info;
	/** 数量**/
	private Integer qty;
	/** 售价 **/
	private BigDecimal sale_price;
	/** 服务费用（比如邮费等） **/
	private BigDecimal service_price;
	/** 创建时间 **/
	private Date created_date;
	/**修改时间**/
	private Date modified_date;
	/** 备注 **/
	private String remark;
	
	public String getOrder_item_id() {
		return order_item_id;
	}
	public void setOrder_item_id(String order_item_id) {
		this.order_item_id = order_item_id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_title() {
		return goods_title;
	}
	public void setGoods_title(String goods_title) {
		this.goods_title = goods_title;
	}
	public String getGoods_pic_info() {
		return goods_pic_info;
	}
	public void setGoods_pic_info(String goods_pic_info) {
		this.goods_pic_info = goods_pic_info;
	}
	public Integer getQty() {
		return qty;
	}
	public void setQty(Integer qty) {
		this.qty = qty;
	}
	public BigDecimal getSale_price() {
		return sale_price;
	}
	public void setSale_price(BigDecimal sale_price) {
		this.sale_price = sale_price;
	}
	public BigDecimal getService_price() {
		return service_price;
	}
	public void setService_price(BigDecimal service_price) {
		this.service_price = service_price;
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
