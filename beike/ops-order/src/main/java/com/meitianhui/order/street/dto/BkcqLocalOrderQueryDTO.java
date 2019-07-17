package com.meitianhui.order.street.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <pre> 街市订单下单传输对象 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 15:05
 */
@Data
public class BkcqLocalOrderQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消费者标识
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String customerId;

    /**
     * 订单状态WAIT_BUYER_PAY(待付款);WAIT_BUYER_USE(待用户使用);TRADE_FINISHED(交易已完成);TRADE_CLOSED_BY_USER:(交易主动关闭);TRADE_CLOSED_BY_SYSTEM:(交易被系统关闭);REFUND_WAIT_SELLER_CONFIRM:(退款等待商家确认);REFUND_SUCC:(退款成功)；SELLER_REJECT_BUYER_REFUND(商家拒绝用户退款))
     */
    @Pattern(regexp = "\\s*|([a-zA-Z0-9_-]{1,50}(,[a-zA-Z0-9_-]{1,50})*)")
    private String status;

    /**
     * 当前页数
     */
    private int page_no = 1;

    /**
     * 每页记录数
     */
    private int page_size = 10;

}
