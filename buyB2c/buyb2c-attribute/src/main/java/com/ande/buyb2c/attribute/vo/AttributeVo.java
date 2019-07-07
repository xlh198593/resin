package com.ande.buyb2c.attribute.vo;

import java.util.List;

/**
 * @author chengzb
 * @date 2018年1月30日下午2:30:23
 * 添加商品时 选中平台分类(属性分类) 时查询该分类下的属性以及属性值
 */
public class AttributeVo {
	private Integer attributeId;//属性表(属性分类-属性表-中间表)的   属性id
	private String attributeName;//属性表   的属性名
	private String attributeType;//属性表   的属性类型 1 复选框 2文本框
	private List<AttributeValVo> attributeValList;
	//冗余
	private Integer goodsAttributeId;
	private String goodsAttributeVal;
	
	public Integer getGoodsAttributeId() {
		return goodsAttributeId;
	}
	public void setGoodsAttributeId(Integer goodsAttributeId) {
		this.goodsAttributeId = goodsAttributeId;
	}
	public String getGoodsAttributeVal() {
		return goodsAttributeVal;
	}
	public void setGoodsAttributeVal(String goodsAttributeVal) {
		this.goodsAttributeVal = goodsAttributeVal;
	}
	public List<AttributeValVo> getAttributeValList() {
		return attributeValList;
	}
	public void setAttributeValList(List<AttributeValVo> attributeValList) {
		this.attributeValList = attributeValList;
	}
	public Integer getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(Integer attributeId) {
		this.attributeId = attributeId;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
}
