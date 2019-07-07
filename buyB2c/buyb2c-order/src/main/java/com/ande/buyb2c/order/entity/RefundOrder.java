package com.ande.buyb2c.order.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class RefundOrder {
    private Integer refundOrderId;
    private String refundOrderNo;
    private Integer orderId;
    private String orderNo;
    private Integer orderDetailId;

    private Date refundTime;

    private BigDecimal refundPrice;

    private BigDecimal refundFreightPrice;
    private BigDecimal reundGoodsPrice;
    private BigDecimal refundApplyPrice;
    private Date refundApplyTime;

    private Integer refundNum;

    private String refundReason;

    private String refundRemarks;

    private Integer goodsId;

    private String goodsNo;

    private String goodsName;

    private BigDecimal goodsPrice;

    private Byte goodNum;

    private String goodsImage;

    private String goodsAttribute;

    private String refundState;

    private Integer customerId;
    
    private String customerName;

    private String customerPhone;

    private String customerAddress;
    
    private String sendState;
    
    private BigDecimal totalPrice;//订单总金额 查询映射

    private String payType;
    
    private String image;
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getSendState() {
		return sendState;
	}

	public void setSendState(String sendState) {
		this.sendState = sendState;
	}

	public BigDecimal getRefundFreightPrice() {
		return refundFreightPrice;
	}

	public void setRefundFreightPrice(BigDecimal refundFreightPrice) {
		this.refundFreightPrice = refundFreightPrice;
	}

	public BigDecimal getReundGoodsPrice() {
		return reundGoodsPrice;
	}

	public void setReundGoodsPrice(BigDecimal reundGoodsPrice) {
		this.reundGoodsPrice = reundGoodsPrice;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getRefundOrderNo() {
		return refundOrderNo;
	}

	public void setRefundOrderNo(String refundOrderNo) {
		this.refundOrderNo = refundOrderNo;
	}

	public Integer getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(Integer refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public BigDecimal getRefundPrice() {
        return refundPrice;
    }

    public void setRefundPrice(BigDecimal refundPrice) {
        this.refundPrice = refundPrice;
    }

    public BigDecimal getRefundApplyPrice() {
        return refundApplyPrice;
    }

    public void setRefundApplyPrice(BigDecimal refundApplyPrice) {
        this.refundApplyPrice = refundApplyPrice;
    }

    public Date getRefundApplyTime() {
        return refundApplyTime;
    }

    public void setRefundApplyTime(Date refundApplyTime) {
        this.refundApplyTime = refundApplyTime;
    }

    public Integer getRefundNum() {
        return refundNum;
    }

    public void setRefundNum(Integer refundNum) {
        this.refundNum = refundNum;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason == null ? null : refundReason.trim();
    }

    public String getRefundRemarks() {
        return refundRemarks;
    }

    public void setRefundRemarks(String refundRemarks) {
        this.refundRemarks = refundRemarks == null ? null : refundRemarks.trim();
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo == null ? null : goodsNo.trim();
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

    public Byte getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Byte goodNum) {
        this.goodNum = goodNum;
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

    public String getRefundState() {
        return refundState;
    }

    public void setRefundState(String refundState) {
        this.refundState = refundState == null ? null : refundState.trim();
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}