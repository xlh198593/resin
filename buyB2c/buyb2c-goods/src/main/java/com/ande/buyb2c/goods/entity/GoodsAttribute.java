package com.ande.buyb2c.goods.entity;

import java.util.List;

public class GoodsAttribute {
    private Integer goodsAttributeId;

private String goodsAttributeName;//冗余
    private Integer goodsId;

    private Integer attributeId;

    private String goodsAttributeType;

    private String goodsAttributeVal;

    private List<GoodsAttributeVal> goodsAttributeValList;
    
    public String getGoodsAttributeName() {
		return goodsAttributeName;
	}

	public void setGoodsAttributeName(String goodsAttributeName) {
		this.goodsAttributeName = goodsAttributeName;
	}

	public List<GoodsAttributeVal> getGoodsAttributeValList() {
		return goodsAttributeValList;
	}

	public void setGoodsAttributeValList(
			List<GoodsAttributeVal> goodsAttributeValList) {
		this.goodsAttributeValList = goodsAttributeValList;
	}

	public Integer getGoodsAttributeId() {
        return goodsAttributeId;
    }

    public void setGoodsAttributeId(Integer goodsAttributeId) {
        this.goodsAttributeId = goodsAttributeId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public String getGoodsAttributeType() {
        return goodsAttributeType;
    }

    public void setGoodsAttributeType(String goodsAttributeType) {
        this.goodsAttributeType = goodsAttributeType == null ? null : goodsAttributeType.trim();
    }

    public String getGoodsAttributeVal() {
        return goodsAttributeVal;
    }

    public void setGoodsAttributeVal(String goodsAttributeVal) {
        this.goodsAttributeVal = goodsAttributeVal == null ? null : goodsAttributeVal.trim();
    }
}