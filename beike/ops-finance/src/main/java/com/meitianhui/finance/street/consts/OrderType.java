package com.meitianhui.finance.street.consts;

import java.util.Objects;

/**
 * 订单类型枚举类
 *
 * @author tortoise
 * @since 2019/3/31 10:40
 */
public enum OrderType {

    /**
     * 贝壳街市
     */
    DDLX_13("DDLX_13", "贝壳街市"),


    ;

    /**
     * 值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    OrderType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 解析枚举对象
     *
     * @param value 具体值
     * @return 枚举对象
     */
    public static OrderType parse(String value) {
        Objects.requireNonNull(value, "The matching value cannot be empty");

        for (OrderType object : values()) {
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
