package com.meitianhui.finance.street.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <pre> 交易退款记录对象 </pre>
 *
 * @author tortoise
 * @since 2019/4/1 11:05
 */
@Data
public class FdRefundAftersales implements Serializable {
    private Integer id;

    /**
     * 退款申请单号
     */
    private String refundsNo;

    /**
     * 交易单号
     */
    private String transactionNo;

    /**
     * 退款类型1，支付宝，2，微信，3红包
     */
    private String refundsType;

    /**
     * 状态，0，succ，1,failed
     */
    private String status;

    /**
     * 退款原因
     */
    private String refundsReason;

    /**
     * 退款金额
     */
    private BigDecimal totalPrice;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 修改时间
     */
    private Date modifiedTime;

    /**
     * 会员id
     */
    private String memberId;

    /**
     * 店铺id
     */
    private String shopId;

    /**
     * 该退款单的主订单号
     */
    private String tid;

    /**
     * 该退款单的订单号
     */
    private String oid;

    private static final long serialVersionUID = 1L;

}