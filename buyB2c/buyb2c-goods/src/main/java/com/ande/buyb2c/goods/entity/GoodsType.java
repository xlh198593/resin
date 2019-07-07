package com.ande.buyb2c.goods.entity;

import java.util.Date;
import java.util.List;

public class GoodsType {
    private Integer goodsTypeId;

    private String goodsType;

    private String goodsTypeLevel;

    private Date createTime;

    private Date updateTime;

    private String delState;

    private Integer adminId;

    private Integer parentId;

    private String logo;

    private Byte sort;

    private String parentName;

    private Integer grandParentId;

    private String grandParentName;

    private List<GoodsType> childList;
    
    public List<GoodsType> getChildList() {
		return childList;
	}

	public void setChildList(List<GoodsType> childList) {
		this.childList = childList;
	}

	public Integer getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Integer goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType == null ? null : goodsType.trim();
    }

    public String getGoodsTypeLevel() {
        return goodsTypeLevel;
    }

    public void setGoodsTypeLevel(String goodsTypeLevel) {
        this.goodsTypeLevel = goodsTypeLevel == null ? null : goodsTypeLevel.trim();
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

    public String getDelState() {
        return delState;
    }

    public void setDelState(String delState) {
        this.delState = delState == null ? null : delState.trim();
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo == null ? null : logo.trim();
    }

    public Byte getSort() {
		return sort;
	}

	public void setSort(Byte sort) {
		this.sort = sort;
	}

	public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName == null ? null : parentName.trim();
    }

    public Integer getGrandParentId() {
        return grandParentId;
    }

    public void setGrandParentId(Integer grandParentId) {
        this.grandParentId = grandParentId;
    }

    public String getGrandParentName() {
        return grandParentName;
    }

    public void setGrandParentName(String grandParentName) {
        this.grandParentName = grandParentName == null ? null : grandParentName.trim();
    }
}