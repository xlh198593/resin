package com.meitianhui.finance.entity;

import java.io.Serializable;
import java.util.Date;

public class FDMemberCouponsLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long oper_id;
    private Long coupons_id;
    private Integer lsn;
    private String member_id;
    private String oper_type;
    private String coupons_type;
    private String order_no;
    private Date created_time;

    public Long getOper_id() {
        return oper_id;
    }

    public void setOper_id(Long oper_id) {
        this.oper_id = oper_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getOper_type() {
        return oper_type;
    }

    public void setOper_type(String oper_type) {
        this.oper_type = oper_type;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public String getCoupons_type() {
        return coupons_type;
    }

    public void setCoupons_type(String coupons_type) {
        this.coupons_type = coupons_type;
    }

    public Long getCoupons_id() {
        return coupons_id;
    }

    public void setCoupons_id(Long coupons_id) {
        this.coupons_id = coupons_id;
    }

    public Integer getLsn() {
        return lsn;
    }

    public void setLsn(Integer lsn) {
        this.lsn = lsn;
    }
}
