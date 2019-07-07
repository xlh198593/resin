package com.ande.buyb2c.shopcart.entity;

import java.util.Date;

public class ShopCartAttribute {
    private Integer shopCartAttributeId;

    private Integer shopCartId;

    private Integer goodsAttributeId;

    private Integer goodsAttributeValId;

    private Integer goodsId;

    private Date createTime;

    public Integer getShopCartAttributeId() {
        return shopCartAttributeId;
    }

    public void setShopCartAttributeId(Integer shopCartAttributeId) {
        this.shopCartAttributeId = shopCartAttributeId;
    }

    public Integer getShopCartId() {
        return shopCartId;
    }

    public void setShopCartId(Integer shopCartId) {
        this.shopCartId = shopCartId;
    }
    public Integer getGoodsAttributeId() {
		return goodsAttributeId;
	}

	public void setGoodsAttributeId(Integer goodsAttributeId) {
		this.goodsAttributeId = goodsAttributeId;
	}

	public Integer getGoodsAttributeValId() {
		return goodsAttributeValId;
	}

	public void setGoodsAttributeValId(Integer goodsAttributeValId) {
		this.goodsAttributeValId = goodsAttributeValId;
	}

	public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}