package com.meitianhui.order.street.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre> 街市订单实体 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 15:05
 */
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BkcqLocalOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 订单标识
     */
    private Long orderId;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 订单日期
     */
    private Date orderDate;
    /**
     * 提货码
     */
    private String loadedCode;
    /**
     * 便利店会员id
     */
    private String storesId;
    /**
     * 门店名称
     */
    private String storesName;
    /**
     * 消费者会员id
     */
    private String consumerId;
    /**
     * 订单描述
     */
    private String desc1;
    /**
     * 订单商品数量
     */
    private Integer itemNum;
    /**
     * 支付方式
     */
    private String paymentWayKey;
    /**
     * 普通会员金额
     */
    private BigDecimal saleFee;
    /**
     * VIP会员价格
     */
    private BigDecimal vipFee;
    /**
     * 结算价
     */
    private BigDecimal settledPrice;
    /**
     * 贝壳金额
     */
    private Long beikeCredit;
    /**
     * 配送地址
     */
    private String deliveryAddress;
    /**
     * 联系人
     */
    private String contactPerson;
    /**
     * 联系人电话
     */
    private String contactTel;
    /**
     * 订单状态 可选值:WAIT_BUYER_PAY(待付款);WAIT_BUYER_USE(待用户使用);TRADE_FINISHED(交易已完成);TRADE_CLOSED_BY_USER:(交易主动关闭);TRADE_CLOSED_BY_SYSTEM:(交易被系统关闭);REFUND_WAIT_SELLER_CONFIRM:(退款等待商家确认);REFUND_PROCESSING (退款处理中);REFUND_SUCC:(退款成功)；SELLER_REJECT_BUYER_REFUND(商家拒绝用户退款))
     */
    private String status;
    /**
     * 退款过程状态记录例：[{"status":“REFUND_PROCESSING"",record_time":"2019-04-07 12:34:33"}]
     */
    private String refundProcessRecord;
    /**
     * 创建时间
     */
    private Date createdDate;
    /**
     * 备注
     */
    private String remark;
    /**
     * 过期时间
     */
    private Date expiredDate;
    /**
     * 修改时间
     */
    private Date modifiedDate;

    /**
     * 退款说明
     */
    private String refundReason;

    /**
     * 旧状态
     */
    private String oldStatus;

    /**
     * 街市订单明细列表
     */
    private List<BkcqLocalOrderItem> items;

    /**
     * 核销码
     */
    private List<BkcqLoadCode> codes;

    /**
     * 加密后的核销码
     */
    private String codeStr;

    /**
     * 店铺信息
     */
    private Map<String, Object> stores;

    /**
     * 交易信息
     */
    private List<Map<String, Object>> transactions;

    /**
     * 用户手机号
     */
    private String memberMobile;


}