package com.ande.buyb2c.shopcart.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ShopCart {
    private Integer shopCartId;

    private Integer goodsId;
    private String goodsNo;

    private String goodsName;

    private BigDecimal goodsPrice;

    private Byte goodsNum;

    private String goodsImage;

    private String goodsAttribute;

    private Integer goodTypeId;

    private Integer goodTypeParentId;

    private Integer goodTypeGrandParentId;

    private Integer customerId;

    private Date createTime;

    private Date updateTime;
    private String goodsSaleState;
    private String goodsDelState;
    
    private String goodsAttributeValIds;
    
    private List<ShopCartAttribute> shopCartAttributeList;
    
	public String getGoodsNo() {
		return goodsNo;
	}

	public void setGoodsNo(String goodsNo) {
		this.goodsNo = goodsNo;
	}

	public String getGoodsAttributeValIds() {
		return goodsAttributeValIds;
	}

	public void setGoodsAttributeValIds(String goodsAttributeValIds) {
		this.goodsAttributeValIds = goodsAttributeValIds;
	}

	public List<ShopCartAttribute> getShopCartAttributeList() {
		return shopCartAttributeList;
	}

	public void setShopCartAttributeList(
			List<ShopCartAttribute> shopCartAttributeList) {
		this.shopCartAttributeList = shopCartAttributeList;
	}

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

	public Integer getShopCartId() {
        return shopCartId;
    }

    public void setShopCartId(Integer shopCartId) {
        this.shopCartId = shopCartId;
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

    public Byte getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Byte goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage == null ? null : goodsImage.trim();
    }

    public String getGoodsAttribute() {
        return goodsAttribute;
    }

    public void setGoodsAttribute(String goodsAttribute) {
        this.goodsAttribute = goodsAttribute == null ? null : goodsAttribute.trim();
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