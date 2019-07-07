package com.ande.buyb2c.order.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Order {
    private Integer orderId;

    private String orderNo;

    private Date createTime;

    private Date updateTime;

    private String payState;

    private String orderState;

    private String payType;

    private BigDecimal freight;

    private BigDecimal orderAmount;

    private BigDecimal orderTotalAmount;

    private Integer customerId;
    private Integer receiptAddressId;
    private String customerName;

    private String customerPhone;

    private String customerAddress;

    private Date payTime;

    private String customerFeedback;

    private Byte goodsPieces;

    private Byte goodsNum;

    private String delState;

    private Integer logisticsId;

    private String logisticsName;

    private String logisticsNo;
    
    private Integer sendLogisticsId;
    private String sendLogisticsName;
    private Date sendGoodsTime;
    private List<OrderDetail> orderDetailList;
    private String transactionId;
    private Date confirmTime;
    private String shopCartIds;//选择的购物车，下单时需要清理购物车
	public Integer getSendLogisticsId() {
		return sendLogisticsId;
	}

	public void setSendLogisticsId(Integer sendLogisticsId) {
		this.sendLogisticsId = sendLogisticsId;
	}

	public String getSendLogisticsName() {
		return sendLogisticsName;
	}

	public void setSendLogisticsName(String sendLogisticsName) {
		this.sendLogisticsName = sendLogisticsName;
	}

	public String getShopCartIds() {
		return shopCartIds;
	}

	public void setShopCartIds(String shopCartIds) {
		this.shopCartIds = shopCartIds;
	}

	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getReceiptAddressId() {
		return receiptAddressId;
	}

	public void setReceiptAddressId(Integer receiptAddressId) {
		this.receiptAddressId = receiptAddressId;
	}

	public Date getSendGoodsTime() {
		return sendGoodsTime;
	}

	public void setSendGoodsTime(Date sendGoodsTime) {
		this.sendGoodsTime = sendGoodsTime;
	}

	public List<OrderDetail> getOrderDetailList() {
		return orderDetailList;
	}

	public void setOrderDetailList(List<OrderDetail> orderDetailList) {
		this.orderDetailList = orderDetailList;
	}

	public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
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

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState == null ? null : payState.trim();
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState == null ? null : orderState.trim();
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(BigDecimal orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName == null ? null : customerName.trim();
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone == null ? null : customerPhone.trim();
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress == null ? null : customerAddress.trim();
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getCustomerFeedback() {
        return customerFeedback;
    }

    public void setCustomerFeedback(String customerFeedback) {
        this.customerFeedback = customerFeedback == null ? null : customerFeedback.trim();
    }

    public Byte getGoodsPieces() {
        return goodsPieces;
    }

    public void setGoodsPieces(Byte goodsPieces) {
        this.goodsPieces = goodsPieces;
    }

    public Byte getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Byte goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getDelState() {
        return delState;
    }

    public void setDelState(String delState) {
        this.delState = delState == null ? null : delState.trim();
    }

    public Integer getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Integer logisticsId) {
        this.logisticsId = logisticsId;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName == null ? null : logisticsName.trim();
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo == null ? null : logisticsNo.trim();
    }
}