package com.ande.buyb2c.attribute.entity;

import java.util.Date;
import java.util.List;

public class AttributeType {
    private Integer attributeTypeId;

    private String attributeTypeName;

    private String attributeTypeLevel;

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
private List<AttributeType> childList;
private List<AttributeTypeAttribute> attributeTypeAttributeList;

    public List<AttributeTypeAttribute> getAttributeTypeAttributeList() {
	return attributeTypeAttributeList;
}

public void setAttributeTypeAttributeList(
		List<AttributeTypeAttribute> attributeTypeAttributeList) {
	this.attributeTypeAttributeList = attributeTypeAttributeList;
}

	public List<AttributeType> getChildList() {
	return childList;
}

public void setChildList(List<AttributeType> childList) {
	this.childList = childList;
}

	public Integer getAttributeTypeId() {
        return attributeTypeId;
    }

    public void setAttributeTypeId(Integer attributeTypeId) {
        this.attributeTypeId = attributeTypeId;
    }

    public String getAttributeTypeName() {
        return attributeTypeName;
    }

    public void setAttributeTypeName(String attributeTypeName) {
        this.attributeTypeName = attributeTypeName == null ? null : attributeTypeName.trim();
    }

    public String getAttributeTypeLevel() {
        return attributeTypeLevel;
    }

    public void setAttributeTypeLevel(String attributeTypeLevel) {
        this.attributeTypeLevel = attributeTypeLevel == null ? null : attributeTypeLevel.trim();
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