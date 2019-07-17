package com.meitianhui.goods.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会过商品信息
 * 
 * @author dinglaoban
 *
 */
public class GdGoodsItem implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 货品标识 **/
	private String goods_id;
	/** 商品编码 **/
	private String goods_code;
	/** 标题 **/
	private String title;
	/** 描述 **/
	private String desc1;
	/** 商品属性 **/
	private String goods_attr;
	/** 一级分类ID **/
	private String cat_topid;
	/** 二级分类ID **/
	private String cat_twoid;
	/** 三级分类ID **/
	private String cat_threeid;
	/** 一级分类名称 **/
	private String cat_topname;
	/** 二级分类名称 **/
	private String cat_twoname;
	/** 三级分类名称 **/
	private String cat_threename;
	/** 品牌ID **/
	private String brand_id;
	/** 品牌名称**/
	private String brand_name;
	/** 上架时间 **/
	private Date online_date;
	/** 下架时间 **/
	private Date offline_date;
	/** 标签 **/
	private String label;
	/** 促销标签 **/
	private String label_promotion;
	/** 类型，可选值：尾货、新品、预售、试样、二手、其它 **/
	private String category;
	/** 推荐展馆**/
	private String display_area;
	/** 联系人 **/
	private String contact_person;
	/** 联系人电话 **/
	private String contact_tel;
	/** 货品图片信息，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]} **/
	private String pic_info;
	/** 详情图片信息，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]} **/
	private String pic_detail_info;
	/** 规格 **/
	private String specification;
	/** 包装 **/
	private String pack;
	/** 进价（成本价） **/
	private BigDecimal cost_price;
	/** 市场价 **/
	private BigDecimal market_price;
	/** 优惠价 **/
	private BigDecimal discount_price;
	/** 阶梯价 **/
	private String ladder_price;
	/** 参团人数，可选值：2、5、10 **/
	private BigDecimal ts_min_num;
	/** 成团价格 **/
	private BigDecimal ts_price;
	/** 团购时间，单位小时H，可选值：24、48 **/
	private Integer ts_date;
	/** 成本分摊价格 **/
	private BigDecimal cost_allocation;
	/** 商品来源 **/
	private String product_source;
	/** 邮费 **/
	private BigDecimal shipping_fee;
	/** 结算价 **/
	private BigDecimal settled_price;
	/** 服务费 **/
	private BigDecimal service_fee;
	/** 产地 **/
	private String producer;
	/** 生产商 **/
	private String manufacturer;
	/** 可销售库存量 **/
	private Integer sale_qty;
	/** 库存量 **/
	private Integer stock_qty;
	/** 单位 **/
	private String goods_unit;
	/** 有效期 **/
	private String valid_thru;
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
	/** 来至其他平台对比价 **/
	private String parity_price;
	/** 来源平台 **/
	private String parity_platform;
	/** 比价url **/
	private String parity_url;
	/** 佣金 **/
	private String commission_fee;
	/** vip返佣 **/
	private String vip_fee;
	/** 销量 **/
	private String total_sales;
	
	private String nowdiffOffline_date;
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
	public String getGoods_attr() {
		return goods_attr;
	}
	public void setGoods_attr(String goods_attr) {
		this.goods_attr = goods_attr;
	}
	public String getCat_topid() {
		return cat_topid;
	}
	public void setCat_topid(String cat_topid) {
		this.cat_topid = cat_topid;
	}
	public String getCat_twoid() {
		return cat_twoid;
	}
	public void setCat_twoid(String cat_twoid) {
		this.cat_twoid = cat_twoid;
	}
	public String getCat_threeid() {
		return cat_threeid;
	}
	public void setCat_threeid(String cat_threeid) {
		this.cat_threeid = cat_threeid;
	}
	public String getCat_topname() {
		return cat_topname;
	}
	public void setCat_topname(String cat_topname) {
		this.cat_topname = cat_topname;
	}
	public String getCat_twoname() {
		return cat_twoname;
	}
	public void setCat_twoname(String cat_twoname) {
		this.cat_twoname = cat_twoname;
	}
	public String getCat_threename() {
		return cat_threename;
	}
	public void setCat_threename(String cat_threename) {
		this.cat_threename = cat_threename;
	}
	public String getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(String brand_id) {
		this.brand_id = brand_id;
	}
	public String getBrand_name() {
		return brand_name;
	}
	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public BigDecimal getMarket_price() {
		return market_price;
	}
	public void setMarket_price(BigDecimal market_price) {
		this.market_price = market_price;
	}
	public BigDecimal getDiscount_price() {
		return discount_price;
	}
	public void setDiscount_price(BigDecimal discount_price) {
		this.discount_price = discount_price;
	}
	public String getLadder_price() {
		return ladder_price;
	}
	public void setLadder_price(String ladder_price) {
		this.ladder_price = ladder_price;
	}
	public BigDecimal getTs_min_num() {
		return ts_min_num;
	}
	public void setTs_min_num(BigDecimal ts_min_num) {
		this.ts_min_num = ts_min_num;
	}
	public BigDecimal getTs_price() {
		return ts_price;
	}
	public void setTs_price(BigDecimal ts_price) {
		this.ts_price = ts_price;
	}
	public Integer getTs_date() {
		return ts_date;
	}
	public void setTs_date(Integer ts_date) {
		this.ts_date = ts_date;
	}
	public BigDecimal getCost_allocation() {
		return cost_allocation;
	}
	public void setCost_allocation(BigDecimal cost_allocation) {
		this.cost_allocation = cost_allocation;
	}
	public String getProduct_source() {
		return product_source;
	}
	public void setProduct_source(String product_source) {
		this.product_source = product_source;
	}
	public BigDecimal getShipping_fee() {
		return shipping_fee;
	}
	public void setShipping_fee(BigDecimal shipping_fee) {
		this.shipping_fee = shipping_fee;
	}
	public BigDecimal getSettled_price() {
		return settled_price;
	}
	public void setSettled_price(BigDecimal settled_price) {
		this.settled_price = settled_price;
	}
	public BigDecimal getService_fee() {
		return service_fee;
	}
	public void setService_fee(BigDecimal service_fee) {
		this.service_fee = service_fee;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
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
	public String getParity_price() {
		return parity_price;
	}
	public void setParity_price(String parity_price) {
		this.parity_price = parity_price;
	}
	public String getParity_platform() {
		return parity_platform;
	}
	public void setParity_platform(String parity_platform) {
		this.parity_platform = parity_platform;
	}
	public String getParity_url() {
		return parity_url;
	}
	public void setParity_url(String parity_url) {
		this.parity_url = parity_url;
	}
	public String getCommission_fee() {
		return commission_fee;
	}
	public void setCommission_fee(String commission_fee) {
		this.commission_fee = commission_fee;
	}
	public String getVip_fee() {
		return vip_fee;
	}
	public void setVip_fee(String vip_fee) {
		this.vip_fee = vip_fee;
	}
	public String getTotal_sales() {
		return total_sales;
	}
	public void setTotal_sales(String total_sales) {
		this.total_sales = total_sales;
	}
	public String getNowdiffOffline_date() {
		return nowdiffOffline_date;
	}
	public void setNowdiffOffline_date(String nowdiffOffline_date) {
		this.nowdiffOffline_date = nowdiffOffline_date;
	}
}
