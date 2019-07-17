package com.meitianhui.goods.street.consts;

import java.util.Objects;

/**
 * 库存改变状态枚举类
 *
 * @author tortoise
 * @since 2019/3/29 16:40
 */
public enum StockChangeStatus {

    /**
     * 未知
     */
    UNKNOW("unknow", "未知"),

    /**
     * 成功
     */
    SUCCESS("success", "成功"),

    /**
     * 失败
     */
    FAILED("failed", "失败"),

    ;

    /**
     * 值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    StockChangeStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 解析枚举对象
     *
     * @param value 具体值
     * @return 枚举对象
     */
    public static StockChangeStatus parse(String value) {
        Objects.requireNonNull(value, "The matching value cannot be empty");

        for (StockChangeStatus object : values()) {
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
