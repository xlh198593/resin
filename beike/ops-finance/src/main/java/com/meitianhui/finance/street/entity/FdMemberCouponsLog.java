package com.meitianhui.finance.street.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre> 会员礼券日志对象 </pre>
 *
 * @author tortoise
 * @since 2019/4/1 11:05
 */
@Data
public class FdMemberCouponsLog implements Serializable {
    /**
     * 礼券操作id
     */
    private Long operId;

    /**
     * 用户id
     */
    private String memberId;

    /**
     * 操作类型（1,礼券签到 2,礼券激活 3,礼券使用 4,转尖货礼券 5,礼券退还－缺货情况）
     */
    private String operType;

    /**
     * 礼券类型精品礼券标识:lj_399,尖货礼券标识:jh_399
     */
    private String couponsType;

    /**
     * 订单号，对应类型为３的操作，礼券使用时产生的订单号
     */
    private String orderNo;

    /**
     * 操作时间
     */
    private Date createdTime;

    private static final long serialVersionUID = 1L;

}