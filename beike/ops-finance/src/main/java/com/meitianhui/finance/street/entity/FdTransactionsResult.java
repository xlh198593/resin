package com.meitianhui.finance.street.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <pre> 交易结果对象 </pre>
 *
 * @author tortoise
 * @since 2019/4/1 11:05
 */
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FdTransactionsResult implements Serializable {
    /**
     * 交易标识
     */
    private Long transactionId;

    /**
     * 交易流水号
     */
    private String transactionNo;

    /**
     * 数据来源，引用字典分类（SJLY）
     */
    private String dataSource;

    /**
     * 交易类型，订单支付，订单退款，订单结算，交易冲正
     */
    private String tradeTypeKey;

    /**
     * 业务类型（交易的业务场景，如充值，兑款）
     */
    private String orderTypeKey;

    /**
     * 支付方式，引用字典分类（ZFFS）
     */
    private String paymentWayKey;

    /**
     * 交易日期
     */
    private Date transactionDate;

    /**
     * 交易说明（摘要）
     */
    private String detail;

    /**
     * 交易金额（默认为本币）
     */
    private BigDecimal amount;

    /**
     * 原始流水号（商家订单号）
     */
    private String outTradeNo;

    /**
     * 原始凭证内容
     */
    private String outTradeBody;

    /**
     * 交易凭证内容
     */
    private String transactionBody;

    /**
     * 买家会员类型，	consumer（消费者）、stores（加盟店）、supplier（供应商）
     */
    private String buyerMemberType;

    /**
     * 买家标识
     */
    private String buyerId;

    /**
     * 买家
     */
    private String buyerName;

    /**
     * 买家联系方式
     */
    private String buyerContact;

    /**
     * 买方资金账号
     */
    private String buyerAccountNo;

    /**
     * 买方账号名称
     */
    private String buyerAccountName;

    /**
     * 卖家会员类型，consumer（消费者）、stores（加盟店）、supplier（供应商）
     */
    private String sellerMemberType;

    /**
     * 卖家标识
     */
    private String sellerId;

    /**
     * 卖家信息
     */
    private String sellerName;

    /**
     * 卖家联系方式
     */
    private String sellerContact;

    /**
     * 卖家资金账号
     */
    private String sellerAccountNo;

    /**
     * 卖家账号名称
     */
    private String sellerAccountName;

    /**
     * 交易状态，可选值：pending（交易暂停）underway（发起请求进行中）refund_reject（退款被拒绝）completed（交易完成）
     */
    private String transactionStatus;

    /**
     * 创建日期
     */
    private Date createdDate;

    /**
     * 修改日期
     */
    private Date modifiedDate;

    /**
     * 交易关闭日期
     */
    private Date closedDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 支付宝、微信支付流水号
     */
    private String externalNumber;

    /**
     * 交易状态，可选值：pending（交易暂停）underway（发起请求进行中）refund_reject（退款被拒绝）completed（交易完成）
     */
    private String oldStatus;

    private static final long serialVersionUID = 1L;

}