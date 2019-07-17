package com.meitianhui.finance.street.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre> 会员贝壳日志对象 </pre>
 *
 * @author tortoise
 * @since 2019/4/1 11:05
 */
@Data
public class FdMemberShellLog implements Serializable {
    /**
     * 日志标识
     */
    private Long logId;

    /**
     * 会员分类，可选值：consumer（消费者/用户）、stores（门店/商家）、supplier（供应商/企业）、company（公司 100001/运营,100002/支付宝,100003/微信）、assistant（店东助手）
     */
    private String memberTypeKey;

    /**
     * 会员标识
     */
    private String memberId;

    /**
     * 操作，income（入账）、expenditure（出账）、frozen（冻结）、activated（解冻）
     */
    private String category;

    /**
     * 发生前余额
     */
    private Integer preBalance;

    /**
     * 发生金额
     */
    private Integer amount;

    /**
     * 交易号
     */
    private String transactionNo;

    /**
     * 余额
     */
    private Integer balance;

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