package com.ande.buyb2c.attribute.entity;

import java.util.Date;

public class AttributeTypeAttribute {
    private Integer attributeTypeAttributeId;

    private Integer attributeTypeId;

    private Integer attributeId;

    private String attributeName;

    private Integer attributeTypeParentId;

    private Integer attributeTypeGrandParentId;

    private Date createTime;

    private Integer adminId;

    private String remarks;//属性名备注
    public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getAttributeTypeAttributeId() {
        return attributeTypeAttributeId;
    }

    public void setAttributeTypeAttributeId(Integer attributeTypeAttributeId) {
        this.attributeTypeAttributeId = attributeTypeAttributeId;
    }

    public Integer getAttributeTypeId() {
        return attributeTypeId;
    }

    public void setAttributeTypeId(Integer attributeTypeId) {
        this.attributeTypeId = attributeTypeId;
    }

    public Integer getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName == null ? null : attributeName.trim();
    }

    public Integer getAttributeTypeParentId() {
        return attributeTypeParentId;
    }

    public void setAttributeTypeParentId(Integer attributeTypeParentId) {
        this.attributeTypeParentId = attributeTypeParentId;
    }

    public Integer getAttributeTypeGrandParentId() {
        return attributeTypeGrandParentId;
    }

    public void setAttributeTypeGrandParentId(Integer attributeTypeGrandParentId) {
        this.attributeTypeGrandParentId = attributeTypeGrandParentId;
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