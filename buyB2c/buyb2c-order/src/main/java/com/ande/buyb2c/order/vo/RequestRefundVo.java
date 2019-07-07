package com.ande.buyb2c.order.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.ande.buyb2c.order.entity.RefundOrder;

/**
 * @author chengzb
 * @date 2018年2月7日下午2:01:02
 */
public class RequestRefundVo extends RefundOrder{
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
