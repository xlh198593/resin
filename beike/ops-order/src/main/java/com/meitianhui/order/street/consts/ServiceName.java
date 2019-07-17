package com.meitianhui.order.street.consts;

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
     * 创建街市订单
     */
    CREATE_STREET_ORDER("createStreetOrder"),

    /**
     * 根据订单编号查询街市订单
     */
    FIND_STREET_ORDER_BY_ORDER_NO("findStreetOrderByOrderNo"),

    /**
     * 分页查询街市订单
     */
    QUERY_STREET_ORDER("queryStreetOrder"),

    /**
     * 街市订单支付成功
     */
    STREET_ORDER_PAY_SUCCESS("streetOrderPaySuccess"),

    /**
     * 取消街市订单
     */
    CANCEL_STREET_ORDER("cancelStreetOrder"),

    /**
     * 申请退款街市订单
     */
    APPLY_REFUND_STREET_ORDER("applyRefundStreetOrder"),

    /**
     * 查询街市订单核销码
     */
    FIND_STREET_ORDER_CODE("findStreetOrderCode"),

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
