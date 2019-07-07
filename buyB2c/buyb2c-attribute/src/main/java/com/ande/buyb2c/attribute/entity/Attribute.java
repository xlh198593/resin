package com.ande.buyb2c.attribute.entity;

import java.util.Date;
import java.util.List;

public class Attribute {
    private Integer attributeId;

    private String attributeName;

    private String attributeType;

    private Integer adminId;

    private Date createTime;

    private Date updateTime;
    private List<AttributeVal> attributeValList;
    //查询映射
    private String attributeValStr;
    private Integer attributeValId;
    
    private String remarks;
    public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getAttributeValId() {
		return attributeValId;
	}

	public void setAttributeValId(Integer attributeValId) {
		this.attributeValId = attributeValId;
	}

	public String getAttributeValStr() {
		return attributeValStr;
	}

	public void setAttributeValStr(String attributeValStr) {
		this.attributeValStr = attributeValStr;
	}

	public List<AttributeVal> getAttributeValList() {
		return attributeValList;
	}

	public void setAttributeValList(List<AttributeVal> attributeValList) {
		this.attributeValList = attributeValList;
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

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType == null ? null : attributeType.trim();
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
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