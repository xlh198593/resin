package com.meitianhui.finance.street.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <pre> 会员资产日志对象 </pre>
 *
 * @author tortoise
 * @since 2019/4/1 11:05
 */
@Data
public class FdMemberAssetLog implements Serializable {
    /**
     * 日志标识
     */
    private String logId;

    /**
     * 会员资产标识
     */
    private String assetId;

    /**
     * 会员分类，可选值：consumer（消费者）、stores（加盟商）、supplier（供应商）、partner（联盟商）
     */
    private String memberTypeKey;

    /**
     * 会员标识
     */
    private String memberId;

    /**
     * 资产分类，cash（现金）、voucher（礼券）、gold（金币）、bonus（积分）、experience（经验值）
     */
    private String category;

    /**
     * 发生前余额
     */
    private BigDecimal preBalance;

    /**
     * 发生金额
     */
    private BigDecimal amount;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 发生时间
     */
    private Date trackedDate;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;

}