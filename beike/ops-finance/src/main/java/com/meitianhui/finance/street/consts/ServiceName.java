package com.meitianhui.finance.street.consts;

import java.util.Objects;

/**
 * 业务名称枚举类
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings("Duplicates")
public enum ServiceName {

    /**
     * 订单支付
     */
    ORDER_PAY("orderPay"),

    /**
     * 根据订单编号查询交易记录
     */
    FIND_TRANSACTION_BY_ORDER_NO("findTransactionByOrderNo"),

    /**
     * 贝壳退款
     */
    BEIKE_REFUND("beikeRefund"),

    ;

    private String value;

    ServiceName(String value) {
        this.value = value;
    }

    /**
     * 解析枚举对象
     *
     * @param value 具体值
     * @return 枚举对象
     */
    public static ServiceName parse(String value) {
        Objects.requireNonNull(value, "The matching value cannot be empty");

        for (ServiceName object : values()) {
            if (value.equals(object.getValue())) {
                return object;
            }
        }

        throw new IllegalArgumentException("No matching constant for [" + value + "]");
    }

    public String getValue() {
        return value;
    }

}
