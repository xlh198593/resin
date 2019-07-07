package com.ande.buyb2c.attribute.entity;

public class AttributeVal {
    private Integer attributeValId;

    private String attributeVal;

    private Integer attributeId;

    private Integer adminId;

    public Integer getAttributeValId() {
        return attributeValId;
    }

    public void setAttributeValId(Integer attributeValId) {
        this.attributeValId = attributeValId;
    }

    public String getAttributeVal() {
        return attributeVal;
    }

    public void setAttributeVal(String attributeVal) {
        this.attributeVal = attributeVal == null ? null : attributeVal.trim();
    }

    public Integer getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}