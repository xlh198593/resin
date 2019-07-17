package com.meitianhui.order.street.consts;

import java.util.Objects;

/**
 * 库存改变状态枚举类
 *
 * @author tortoise
 * @since 2019/3/29 16:40
 */
public enum OrderStatus {

    /**
     * 待付款
     */
    WAIT_BUYER_PAY("WAIT_BUYER_PAY", "待付款"),

    /**
     * 待用户使用
     */
    WAIT_BUYER_USE("WAIT_BUYER_USE", "待用户使用"),

    /**
     * 交易已完成
     */
    TRADE_FINISHED("TRADE_FINISHED", "交易已完成"),

    /**
     * 交易主动关闭
     */
    TRADE_CLOSED_BY_USER("TRADE_CLOSED_BY_USER", "交易主动关闭"),

    /**
     * 交易被系统关闭
     */
    TRADE_CLOSED_BY_SYSTEM("TRADE_CLOSED_BY_SYSTEM", "交易被系统关闭"),

    /**
     * 退款等待商家确认
     */
    REFUND_WAIT_SELLER_CONFIRM("REFUND_WAIT_SELLER_CONFIRM", "退款等待商家确认"),

    /**
     * 退款成功
     */
    REFUND_SUCC("REFUND_SUCC", "退款成功"),

    /**
     * 商家拒绝用户退款
     */
    SELLER_REJECT_BUYER_REFUND("SELLER_REJECT_BUYER_REFUND", "商家拒绝用户退款"),

    ;

    /**
     * 值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    OrderStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 解析枚举对象
     *
     * @param value 具体值
     * @return 枚举对象
     */
    public static OrderStatus parse(String value) {
        Objects.requireNonNull(value, "The matching value cannot be empty");

        for (OrderStatus object : values()) {
            if (value.equals(object.getValue())) {
                return object;
            }
        }

        throw new IllegalArgumentException("No matching constant for [" + value + "]");
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
