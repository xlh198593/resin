package com.ande.buyb2c.comment.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.ande.buyb2c.comment.entity.Comment;

/**
 * @author chengzb
 * @date 2018年2月1日下午3:04:14
 */
public class RequestCommentVo extends Comment{
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
