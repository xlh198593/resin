package com.ande.buyb2c.attribute.vo;
/**
 * @author chengzb
 * @date 2018年1月30日下午3:15:10
 */
public class AttributeValVo {
	private Integer attributeId;
	private Integer attributeValId;//属性值表  的属性值id
	private String attributeVal;//属性值表  的属性
	private boolean isChoice=false;//是否选中 
	private String attributeName;//属性表   的属性名
	private String attributeType;//属性表   的属性类型 1 复选框 2文本框
	
	
	//冗余
	private Integer goodsAttributeValId;
	
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
	public Integer getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(Integer attributeId) {
		this.attributeId = attributeId;
	}
	public Integer getGoodsAttributeValId() {
		return goodsAttributeValId;
	}
	public void setGoodsAttributeValId(Integer goodsAttributeValId) {
		this.goodsAttributeValId = goodsAttributeValId;
	}
	public boolean isChoice() {
		return isChoice;
	}
	public void setChoice(boolean isChoice) {
		this.isChoice = isChoice;
	}
	public Integer getAttributeValId() {
		return attributeValId;
	}
	public void setAttributeValId(Integer attributeValId) {
		this.attributeValId = attributeValId;
	}
	public String getAttributeVal() {
		return attributeVal;
	}
	public void setAttributeVal(String attributeVal) {
		this.attributeVal = attributeVal;
	}
	
	
	
}
