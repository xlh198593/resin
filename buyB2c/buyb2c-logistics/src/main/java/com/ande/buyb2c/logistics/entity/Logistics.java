package com.ande.buyb2c.logistics.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Logistics {
    private Integer logisticsId;

    private String logisticsName;

    private BigDecimal logisticsCost;
    private String status;

    private Date createTime;

    private Date updateTime;
    private String delState;
private Integer adminId;

	public Integer getAdminId() {
	return adminId;
}

public void setAdminId(Integer adminId) {
	this.adminId = adminId;
}

	public String getDelState() {
		return delState;
	}

	public void setDelState(String delState) {
		this.delState = delState;
	}

	public Integer getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Integer logisticsId) {
        this.logisticsId = logisticsId;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName == null ? null : logisticsName.trim();
    }
    public BigDecimal getLogisticsCost() {
		return logisticsCost;
	}

	public void setLogisticsCost(BigDecimal logisticsCost) {
		this.logisticsCost = logisticsCost;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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