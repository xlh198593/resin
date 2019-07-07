package com.ande.buyb2c.order.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.ande.buyb2c.order.entity.Order;

/**
 * @author chengzb
 * @date 2018年1月31日下午2:29:35
 */
public class RequestOrderVo extends Order{
	@DateTimeFormat(pattern="yyyy-MM-dd")
private Date startTime;
	@DateTimeFormat(pattern="yyyy-MM-dd")
private Date endTime;
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
