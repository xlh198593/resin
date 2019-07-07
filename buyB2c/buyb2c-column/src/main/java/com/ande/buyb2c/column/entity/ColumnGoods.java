package com.ande.buyb2c.column.entity;

import java.util.Date;

public class ColumnGoods {
    private Integer columnGoodsId;

    private Integer columnId;

    private Integer goodsId;

    private String goodsNo;

    private String goodsName;

    private Date createTime;

    private Integer adminId;

    public Integer getColumnGoodsId() {
        return columnGoodsId;
    }

    public void setColumnGoodsId(Integer columnGoodsId) {
        this.columnGoodsId = columnGoodsId;
    }

    public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
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
		this.goodsNo = goodsNo;
	}

	public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}