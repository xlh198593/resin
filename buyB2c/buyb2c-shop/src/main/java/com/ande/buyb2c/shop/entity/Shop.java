package com.ande.buyb2c.shop.entity;

import java.util.Date;

public class Shop {
    private Integer shopId;

    private String logo;

    private String shopName;

    private String shopDomain;

    private String copyright;

    private String customerPhone;

    private Integer adminId;

    private Date createTime;

    private Date updateTime;

    private String delState;
    private Integer freeShipping;

    private Integer freeShippingPrompt;
    
    
    public Integer getFreeShipping() {
		return freeShipping;
	}

	public void setFreeShipping(Integer freeShipping) {
		this.freeShipping = freeShipping;
	}

	public Integer getFreeShippingPrompt() {
		return freeShippingPrompt;
	}

	public void setFreeShippingPrompt(Integer freeShippingPrompt) {
		this.freeShippingPrompt = freeShippingPrompt;
	}

	public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo == null ? null : logo.trim();
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName == null ? null : shopName.trim();
    }

    public String getShopDomain() {
        return shopDomain;
    }

    public void setShopDomain(String shopDomain) {
        this.shopDomain = shopDomain == null ? null : shopDomain.trim();
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright == null ? null : copyright.trim();
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone == null ? null : customerPhone.trim();
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

    public String getDelState() {
        return delState;
    }

    public void setDelState(String delState) {
        this.delState = delState == null ? null : delState.trim();
    }
}