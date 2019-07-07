package com.ande.buyb2c.advert.entity;

import java.util.Date;
import java.util.List;

public class AdvertPosition {
    private Integer advertPositionId;

    private String advertName;

    private Date createTime;

    private String isShow;

    private Integer adminId;

    private List<AdvertPositionDetail> list;
    
    public List<AdvertPositionDetail> getList() {
		return list;
	}

	public void setList(List<AdvertPositionDetail> list) {
		this.list = list;
	}

	public Integer getAdvertPositionId() {
        return advertPositionId;
    }

    public void setAdvertPositionId(Integer advertPositionId) {
        this.advertPositionId = advertPositionId;
    }

    public String getAdvertName() {
        return advertName;
    }

    public void setAdvertName(String advertName) {
        this.advertName = advertName == null ? null : advertName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow == null ? null : isShow.trim();
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}