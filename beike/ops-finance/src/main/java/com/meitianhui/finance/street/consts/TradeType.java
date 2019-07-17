package com.meitianhui.finance.street.consts;

import java.util.Objects;

/**
 * 交易类型枚举类
 *
 * @author tortoise
 * @since 2019/3/31 10:40
 */
public enum TradeType {

    /**
     * 订单支付
     */
    JYLX_01("JYLX_01", "订单支付"),

    /**
     * 订单退款
     */
    JYLX_02("JYLX_02", "订单退款"),

    /**
     * 订单结算
     */
    JYLX_03("JYLX_03", "订单结算"),

    /**
     * 交易冲正
     */
    JYLX_04("JYLX_04", "交易冲正"),


    ;

    /**
     * 值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    TradeType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 解析枚举对象
     *
     * @param value 具体值
     * @return 枚举对象
     */
    public static TradeType parse(String value) {
        Objects.requireNonNull(value, "The matching value cannot be empty");

        for (TradeType object : values()) {
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
