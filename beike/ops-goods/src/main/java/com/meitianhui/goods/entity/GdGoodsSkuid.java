package com.meitianhui.goods.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会过商品SKU信息
 * 
 * @author dinglaoban
 *
 */
public class GdGoodsSkuid implements Serializable {

	private static final long serialVersionUID = 1L;
	/** sku_id **/
	private String sku_id;
	/** 商品编码 **/
	private String goods_code;
	/** 商品主属性ID **/
	private String attr_zid;
	/** 商品副属性ID **/
	private String attr_fid;
	
	/** 商品主属性值 **/
	private String prop_zname;
	/** 商品副属性值 **/
	private String prop_fname;
	
	/** 商品主属性值 **/
	private String attr_zvalue;
	/** 商品副属性值 **/
	private String attr_fvalue;
	/** 主属性图片 **/
	private String attr_zpic;
	/** 副属性图片 **/
	private String attr_fpic;
	/** 普通会员价 **/
	private BigDecimal sale_price;
	/** vip会员价 **/
	private BigDecimal vip_price;
	/** 抵扣贝壳 **/
	private Integer beike_credit;
	/** 商品条码 **/
	private String barcode;
	/** 库存 **/
	private String stock;
	/** 创建时间 **/
	private Date created_time;
	/** 修改时间 **/
	private Date modified_time;
	
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
	public String getAttr_zid() {
		return attr_zid;
	}
	public void setAttr_zid(String attr_zid) {
		this.attr_zid = attr_zid;
	}
	public String getAttr_fid() {
		return attr_fid;
	}
	public void setAttr_fid(String attr_fid) {
		this.attr_fid = attr_fid;
	}
	public String getProp_zname() {
		return prop_zname;
	}
	public void setProp_zname(String prop_zname) {
		this.prop_zname = prop_zname;
	}
	public String getProp_fname() {
		return prop_fname;
	}
	public void setProp_fname(String prop_fname) {
		this.prop_fname = prop_fname;
	}
	public String getAttr_zvalue() {
		return attr_zvalue;
	}
	public void setAttr_zvalue(String attr_zvalue) {
		this.attr_zvalue = attr_zvalue;
	}
	public String getAttr_fvalue() {
		return attr_fvalue;
	}
	public void setAttr_fvalue(String attr_fvalue) {
		this.attr_fvalue = attr_fvalue;
	}
	public String getAttr_zpic() {
		return attr_zpic;
	}
	public void setAttr_zpic(String attr_zpic) {
		this.attr_zpic = attr_zpic;
	}
	public String getAttr_fpic() {
		return attr_fpic;
	}
	public void setAttr_fpic(String attr_fpic) {
		this.attr_fpic = attr_fpic;
	}
	public BigDecimal getSale_price() {
		return sale_price;
	}
	public void setSale_price(BigDecimal sale_price) {
		this.sale_price = sale_price;
	}
	public BigDecimal getVip_price() {
		return vip_price;
	}
	public void setVip_price(BigDecimal vip_price) {
		this.vip_price = vip_price;
	}
	public Integer getBeike_credit() {
		return beike_credit;
	}
	public void setBeike_credit(Integer beike_credit) {
		this.beike_credit = beike_credit;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public Date getCreated_time() {
		return created_time;
	}
	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}
	public Date getModified_time() {
		return modified_time;
	}
	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
	}
	

}
