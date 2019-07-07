package com.ande.buyb2c.order.entity;

import java.util.Date;

public class OrderAttribute {
    private Integer orderAttributeId;

    private Integer orderId;

    private Integer orderDetailId;

    private Integer goodsId;

    private Integer attributeId;

    private Integer attributeValId;

    private Date createTime;

    private Integer customerId;

    public Integer getOrderAttributeId() {
        return orderAttributeId;
    }

    public void setOrderAttributeId(Integer orderAttributeId) {
        this.orderAttributeId = orderAttributeId;
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

    public Integer getAttributeValId() {
        return attributeValId;
    }

    public void setAttributeValId(Integer attributeValId) {
        this.attributeValId = attributeValId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}