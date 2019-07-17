package com.meitianhui.order.street.exception;

import java.util.Objects;

/**
 * 错误代码枚举类
 *
 * @author Tortoise
 * @since 2019/3/27 12:40
 */
public enum ErrorCode {

    /* 系统错误 */
    SYSTEM_ERROR(-200, "System error");

    private Integer value;
    private String desc;

    ErrorCode(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 获取枚举对象
     *
     * @param value 具体值
     * @return 枚举对象
     */
    public static ErrorCode valueOf(Integer value) {
        Objects.requireNonNull(value, "The matching value cannot be empty");

        for (ErrorCode object : values()) {
            if (value.equals(object.getValue())) {
                return object;
            }
        }

        throw new IllegalArgumentException("No matching constant for [" + value + "]");
    }

    /**
     * 获取枚举对象的具体值
     *
     * @return 具体值
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取枚举对象的描述
     *
     * @return 具体值
     */
    public String getDesc() {
        return desc;
    }
}
