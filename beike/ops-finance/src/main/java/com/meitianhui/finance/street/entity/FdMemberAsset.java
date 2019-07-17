package com.meitianhui.finance.street.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <pre> 会员资产对象 </pre>
 *
 * @author tortoise
 * @since 2019/4/1 11:05
 */
@Data
public class FdMemberAsset implements Serializable {
    /**
     * 会员资产标识
     */
    private String assetId;

    /**
     * 会员分类，可选值：consumer（消费者/用户）、stores（门店/商家）、supplier（供应商/企业）、company（公司/运营）、assistant（店东助手）
     */
    private String memberTypeKey;

    /**
     * 会员标识
     */
    private String memberId;

    /**
     * 现金余额
     */
    private BigDecimal cashBalance;

    /**
     * 冻结现金
     */
    private BigDecimal cashFroze;

    /**
     * 结算金额
     */
    private BigDecimal settledBalance;

    /**
     * 礼券余额(368礼券)
     */
    private Integer giftCertificates368;

    /**
     * 礼券余额(188)
     */
    private Integer giftCertificates188;

    /**
     * 礼券余额(68)
     */
    private Integer giftCertificates68;

    /**
     * 积分
     */
    private Long point;

    /**
     * 金币
     */
    private Integer gold;

    /**
     * 贝壳
     */
    private Long shell;

    /**
     * 贝壳支付金额在核销前，暂存字段
     */
    private Long settledShellBalance;

    /**
     * 优惠券
     */
    private BigDecimal cashCoupon;

    /**
     * 经验值
     */
    private Integer experience;

    /**
     * 创建时间
     */
    private Date createdDate;

    /**
     * 修改时间
     */
    private Date modifiedDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 次邀余额
     */
    private BigDecimal inviteBalance;

    private static final long serialVersionUID = 1L;

}