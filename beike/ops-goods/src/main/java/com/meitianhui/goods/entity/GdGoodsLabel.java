package com.meitianhui.goods.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品标签字典表
 *
 * @author Tiny
 *
 */

public class GdGoodsLabel implements Serializable {

    private static final long serialVersionUID = 1L;
    
	/** 标签标识 **/
	private Long label_id;
	/** 标签 **/
	private String label_promotion;
	/** 标签类型 **/
	private String label_type;
	/** 创建时间 **/
	private Date created_date;
	/** 状态，可选值：Y（正常）N（禁用） */
	private String status;
	/** 备注 **/
	private String remark;
	public Long getLabel_id() {
		return label_id;
	}
	public void setLabel_id(Long label_id) {
		this.label_id = label_id;
	}
	public String getLabel_promotion() {
		return label_promotion;
	}
	public void setLabel_promotion(String label_promotion) {
		this.label_promotion = label_promotion;
	}
	public String getLabel_type() {
		return label_type;
	}
	public void setLabel_type(String label_type) {
		this.label_type = label_type;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	
}
