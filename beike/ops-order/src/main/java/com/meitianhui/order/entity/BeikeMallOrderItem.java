package com.meitianhui.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.omg.CORBA.INTERNAL;

/**
 * 免费领样订单商品列表
 * 
 * @author Tiny
 *
 */
public class BeikeMallOrderItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/** 订单商品标识 **/
	private String order_item_id;
	/** 订单标识**/
	private String order_id;
	/** 商品标识**/
	private String goods_id;
	/** 关联gddb库ps_goods_skuid表skuid **/
	private String sku_id;
	/** 商品编码**/
	private String goods_code;
	/** 商品标题**/
	private String goods_title;
	/** 商品图片**/
	private String goods_pic_info;
	/** 供应商id**/
	private String supplier_id;
	/** 供应商名称**/
	private String supplier;
	/** 生产商**/
	private String manufacturer;
	/** 数量**/
	private Integer qty;
	/** 规格**/
	private String specification;
	/** 单位 **/
	private String goods_unit;
	/** 售价 **/
	private BigDecimal sale_price;
	/** 贝壳数量**/
	private Integer beike_amount;
	/** 服务费用（比如邮费等） **/
	private BigDecimal service_price;
	/** vip价 **/
	private BigDecimal vip_price;
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
	public String getSku_id() {
		return sku_id;
	}
	public void setSku_id(String sku_id) {
		this.sku_id = sku_id;
	}
	public String getGoods_code() {
		return goods_code;
	}
	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
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
	public String getSupplier_id() {
		return supplier_id;
	}
	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public Integer getQty() {
		return qty;
	}
	public void setQty(Integer qty) {
		this.qty = qty;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getGoods_unit() {
		return goods_unit;
	}
	public void setGoods_unit(String goods_unit) {
		this.goods_unit = goods_unit;
	}
	public BigDecimal getSale_price() {
		return sale_price;
	}
	public void setSale_price(BigDecimal sale_price) {
		this.sale_price = sale_price;
	}
	public Integer getBeike_amount() {
		return beike_amount;
	}
	public void setBeike_amount(Integer beike_amount) {
		this.beike_amount = beike_amount;
	}
	public BigDecimal getService_price() {
		return service_price;
	}
	public void setService_price(BigDecimal service_price) {
		this.service_price = service_price;
	}
	public BigDecimal getVip_price() {
		return vip_price;
	}
	public void setVip_price(BigDecimal vip_price) {
		this.vip_price = vip_price;
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
