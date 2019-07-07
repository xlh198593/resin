package com.ande.buyb2c.goods.entity;

public class GoodsAttributeVal {
    private Integer goodsAttributeValId;


    private Integer attributeValId;
    private String goodsAttributeVal;
    private Integer goodsAttributeId;

    public String getGoodsAttributeVal() {
		return goodsAttributeVal;
	}

	public void setGoodsAttributeVal(String goodsAttributeVal) {
		this.goodsAttributeVal = goodsAttributeVal;
	}

	public Integer getGoodsAttributeValId() {
        return goodsAttributeValId;
    }

    public void setGoodsAttributeValId(Integer goodsAttributeValId) {
        this.goodsAttributeValId = goodsAttributeValId;
    }


    public Integer getAttributeValId() {
        return attributeValId;
    }

    public void setAttributeValId(Integer attributeValId) {
        this.attributeValId = attributeValId;
    }

    public Integer getGoodsAttributeId() {
        return goodsAttributeId;
    }

    public void setGoodsAttributeId(Integer goodsAttributeId) {
        this.goodsAttributeId = goodsAttributeId;
    }
}