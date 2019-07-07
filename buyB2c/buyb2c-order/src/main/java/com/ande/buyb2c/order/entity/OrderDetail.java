package com.ande.buyb2c.order.entity;

import java.math.BigDecimal;
import java.util.List;

public class OrderDetail {
    private Integer orderDetailId;

    private Integer orderId;

    private Integer goodsId;
    private String goodsNo;
	private Integer goodTypeId;

    private Integer goodTypeParentId;

    private Integer goodTypeGrandParentId;

    private String goodsName;

    private BigDecimal goodsPrice;

    private Byte goodsNum;

    private BigDecimal goodsTotalPrice;

    private String goodsImage;

    private String goodsAttribute;
    private String goodsState;
    
    public String getGoodsState() {
		return goodsState;
	}

	public void setGoodsState(String goodsState) {
		this.goodsState = goodsState;
	}

	private List<OrderAttribute> orderAttributeList;
	public List<OrderAttribute> getOrderAttributeList() {
		return orderAttributeList;
	}

	public void setOrderAttributeList(List<OrderAttribute> orderAttributeList) {
		this.orderAttributeList = orderAttributeList;
	}

	public String getGoodsNo() {
		return goodsNo;
	}

	public void setGoodsNo(String goodsNo) {
		this.goodsNo = goodsNo;
	}

	public Integer getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
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

	public BigDecimal getGoodsTotalPrice() {
        return goodsTotalPrice;
    }

    public void setGoodsTotalPrice(BigDecimal goodsTotalPrice) {
        this.goodsTotalPrice = goodsTotalPrice;
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
}