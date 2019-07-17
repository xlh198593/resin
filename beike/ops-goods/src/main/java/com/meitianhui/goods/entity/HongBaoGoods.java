package com.meitianhui.goods.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预售贝壳商品信息
 * 
 * @author Tiny
 *
 */
public class HongBaoGoods implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 货品标识 **/
	private String goods_id;
	/** 商品编码 **/
	private String goods_code;
	/** 标题 **/
	private String title;
	/** 描述 **/
	private String desc1;
	/** 一级分类 */
	private Integer top_catid;
	/** 二级分类 **/
	private Integer two_catid;
	/** 三级分类 **/
	private Integer three_catid;
	/** 标签 **/
	private String label;
	/** 促销标签 **/
	private String label_promotion;
	/** 来源，可选值：merchants（合作商家）、taobao（淘宝） **/
	private String goods_source;
	/** 推荐展馆 **/
	private String display_area;
	/** 联系人 **/
	private String contact_person;
	/** 联系人电话 **/
	private String contact_tel;
	/**
	 * 货品图片信息，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]}
	 **/
	private String pic_info;
	/**
	 * 详情图片信息，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]}
	 **/
	private String pic_detail_info;
	/** 规格 **/
	private String specification;
	/** 包装 **/
	private String pack;
	/** 进价（成本价） **/
	private BigDecimal cost_price;
	/** 价格 **/
	private BigDecimal hongbao_price;
	/** 市场价格 **/
	private BigDecimal market_price;
	/**销售价格**/
	private BigDecimal sale_price;
	/** 邮费 **/
	private BigDecimal shipping_fee;
	/** 产地 **/
	private String producer;
	/** 供应商名称 **/
	private String supplier;
	/** 供应商id **/
	private String supplier_id;
	/** 生产商 **/
	private String manufacturer;
	/** 起订量 **/
	private Integer min_buy_qty;
	/** 限购量 **/
	private Integer max_buy_qty;
	/** 可销售库存量 **/
	private Integer sale_qty;
	/** 库存量 **/
	private Integer stock_qty;
	/** 单位 **/
	private String goods_unit;
	/** 开卖日期 **/
	private String valid_thru;
	/** 仓库名称 **/
	private String warehouse;
	/** 仓库id **/
	private String warehouse_id;
	/** 配送 **/
	private String delivery;
	/** 配送 **/
	private String delivery_id;
	/** 配送地区 **/
	private String delivery_area;
	/** 支付方式，可选值：online(在线支付)offline(货到付款) **/
	private String payment_way;
	/** 状态，可选值：normal（正常）delete(删除) **/
	private String status;
	/** 创建时间 **/
	private Date created_date;
	/** 修改时间 **/
	private Date modified_date;
	/** 备注 **/
	private String remark;
	/** 上架时间 **/
	private Date online_date;
	/** 下架时间 **/
	private Date offline_date;
	/** 实际销量 **/
	private Integer sales_volume;
	/** 伪销量 **/
	private Integer fake_sales_volume;
	/** 可抵扣的礼券张数*/
	private Integer coupon_price;
	
	
	public Integer getCoupon_price() {
		return coupon_price;
	}

	public void setCoupon_price(Integer coupon_price) {
		this.coupon_price = coupon_price;
	}

	public Integer getSales_volume() {
		return sales_volume;
	}

	public void setSales_volume(Integer sales_volume) {
		this.sales_volume = sales_volume;
	}

	public Integer getFake_sales_volume() {
		return fake_sales_volume;
	}

	public void setFake_sales_volume(Integer fake_sales_volume) {
		this.fake_sales_volume = fake_sales_volume;
	}

	public Integer getTop_catid() {
		return top_catid;
	}

	public void setTop_catid(Integer top_catid) {
		this.top_catid = top_catid;
	}

	public Integer getTwo_catid() {
		return two_catid;
	}

	public void setTwo_catid(Integer two_catid) {
		this.two_catid = two_catid;
	}

	public Integer getThree_catid() {
		return three_catid;
	}

	public void setThree_catid(Integer three_catid) {
		this.three_catid = three_catid;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_code() {
		return goods_code;
	}

	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc1() {
		return desc1;
	}

	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}

	public Date getOnline_date() {
		return online_date;
	}

	public void setOnline_date(Date online_date) {
		this.online_date = online_date;
	}

	public Date getOffline_date() {
		return offline_date;
	}

	public void setOffline_date(Date offline_date) {
		this.offline_date = offline_date;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel_promotion() {
		return label_promotion;
	}

	public void setLabel_promotion(String label_promotion) {
		this.label_promotion = label_promotion;
	}

	public String getDisplay_area() {
		return display_area;
	}

	public void setDisplay_area(String display_area) {
		this.display_area = display_area;
	}

	public String getContact_person() {
		return contact_person;
	}

	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}

	public String getContact_tel() {
		return contact_tel;
	}

	public void setContact_tel(String contact_tel) {
		this.contact_tel = contact_tel;
	}

	public String getPic_info() {
		return pic_info;
	}

	public void setPic_info(String pic_info) {
		this.pic_info = pic_info;
	}

	public String getPic_detail_info() {
		return pic_detail_info;
	}

	public void setPic_detail_info(String pic_detail_info) {
		this.pic_detail_info = pic_detail_info;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public BigDecimal getCost_price() {
		return cost_price;
	}

	public void setCost_price(BigDecimal cost_price) {
		this.cost_price = cost_price;
	}


	public BigDecimal getShipping_fee() {
		return shipping_fee;
	}

	public void setShipping_fee(BigDecimal shipping_fee) {
		this.shipping_fee = shipping_fee;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(String warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Integer getMin_buy_qty() {
		return min_buy_qty;
	}

	public void setMin_buy_qty(Integer min_buy_qty) {
		this.min_buy_qty = min_buy_qty;
	}

	public Integer getMax_buy_qty() {
		return max_buy_qty;
	}

	public void setMax_buy_qty(Integer max_buy_qty) {
		this.max_buy_qty = max_buy_qty;
	}

	public Integer getSale_qty() {
		return sale_qty;
	}

	public void setSale_qty(Integer sale_qty) {
		this.sale_qty = sale_qty;
	}

	public Integer getStock_qty() {
		return stock_qty;
	}

	public void setStock_qty(Integer stock_qty) {
		this.stock_qty = stock_qty;
	}

	public String getGoods_unit() {
		return goods_unit;
	}

	public void setGoods_unit(String goods_unit) {
		this.goods_unit = goods_unit;
	}

	public String getValid_thru() {
		return valid_thru;
	}

	public void setValid_thru(String valid_thru) {
		this.valid_thru = valid_thru;
	}

	public String getDelivery() {
		return delivery;
	}

	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	public String getDelivery_id() {
		return delivery_id;
	}

	public void setDelivery_id(String delivery_id) {
		this.delivery_id = delivery_id;
	}

	public String getDelivery_area() {
		return delivery_area;
	}

	public void setDelivery_area(String delivery_area) {
		this.delivery_area = delivery_area;
	}

	public String getPayment_way() {
		return payment_way;
	}

	public void setPayment_way(String payment_way) {
		this.payment_way = payment_way;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getGoods_source() {
		return goods_source;
	}

	public void setGoods_source(String goods_source) {
		this.goods_source = goods_source;
	}

	public BigDecimal getHongbao_price() {
		return hongbao_price;
	}

	public void setHongbao_price(BigDecimal hongbao_price) {
		this.hongbao_price = hongbao_price;
	}

	public BigDecimal getMarket_price() {
		return market_price;
	}

	public void setMarket_price(BigDecimal market_price) {
		this.market_price = market_price;
	}

	public BigDecimal getSale_price() {
		return sale_price;
	}

	public void setSale_price(BigDecimal sale_price) {
		this.sale_price = sale_price;
	}
}
