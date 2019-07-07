package com.ande.buyb2c.collection.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Collection {
    private Integer collectionId;

    private Integer goodsId;

    private String goodsName;

    private BigDecimal goodsPrice;

    private String goodsImage;

    private Integer goodTypeId;

    private Integer goodTypeParentId;

    private Integer goodTypeGrandParentId;

    private Integer customerId;

    private Date createTime;

    private Date updateTime;

    private String goodsSaleState;
    private String goodsDelState;
    public String getGoodsSaleState() {
		return goodsSaleState;
	}

	public void setGoodsSaleState(String goodsSaleState) {
		this.goodsSaleState = goodsSaleState;
	}

	public String getGoodsDelState() {
		return goodsDelState;
	}

	public void setGoodsDelState(String goodsDelState) {
		this.goodsDelState = goodsDelState;
	}

	public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage == null ? null : goodsImage.trim();
    }

    public Integer getGoodTypeId() {
        return goodTypeId;
    }

    public void setGoodTypeId(Integer goodTypeId) {
        this.goodTypeId = goodTypeId;
    }

    public Integer getGoodTypeParentId() {
        return goodTypeParentId;
    }

    public void setGoodTypeParentId(Integer goodTypeParentId) {
        this.goodTypeParentId = goodTypeParentId;
    }

    public Integer getGoodTypeGrandParentId() {
        return goodTypeGrandParentId;
    }

    public void setGoodTypeGrandParentId(Integer goodTypeGrandParentId) {
        this.goodTypeGrandParentId = goodTypeGrandParentId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}