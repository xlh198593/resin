package com.ande.buyb2c.column.entity;

import java.util.Date;
import java.util.List;

public class Column {
    private Integer columnId;

    private String columnName;

    private Date createTime;

    private Date updateTime;

    private Byte sort;

    private Integer adminId;

    private String goodsSort;

    private Byte showGoodsNum;

    private String columnLogo;

    private String isRecommendIndex;

    private String delState;
    private List<ColumnGoods> list;

    public List<ColumnGoods> getList() {
		return list;
	}

	public void setList(List<ColumnGoods> list) {
		this.list = list;
	}

	public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName == null ? null : columnName.trim();
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

    public Byte getSort() {
        return sort;
    }

    public void setSort(Byte sort) {
        this.sort = sort;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getGoodsSort() {
        return goodsSort;
    }

    public void setGoodsSort(String goodsSort) {
        this.goodsSort = goodsSort == null ? null : goodsSort.trim();
    }

    public Byte getShowGoodsNum() {
        return showGoodsNum;
    }

    public void setShowGoodsNum(Byte showGoodsNum) {
        this.showGoodsNum = showGoodsNum;
    }

    public String getColumnLogo() {
        return columnLogo;
    }

    public void setColumnLogo(String columnLogo) {
        this.columnLogo = columnLogo == null ? null : columnLogo.trim();
    }

    public String getIsRecommendIndex() {
        return isRecommendIndex;
    }

    public void setIsRecommendIndex(String isRecommendIndex) {
        this.isRecommendIndex = isRecommendIndex == null ? null : isRecommendIndex.trim();
    }

    public String getDelState() {
        return delState;
    }

    public void setDelState(String delState) {
        this.delState = delState == null ? null : delState.trim();
    }
}