package com.meitianhui.goods.street.consts;

import java.util.Objects;

/**
 * 订单类型枚举类
 *
 * @author tortoise
 * @since 2019/3/29 16:40
 */
public enum OrderType {

    /**
     * 贝壳街市
     */
    BKJS("BKJS", "贝壳街市"),

    /**
     * 贝壳商超
     */
    BKSC("BKSC", "贝壳商超"),

    /**
     * 贝壳部落
     */
    BKBL("BKBL", "贝壳部落"),

    /**
     * 贝壳部落
     */
    LBZQ("LBZQ", "礼包专区"),

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
