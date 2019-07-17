package com.meitianhui.finance.street.consts;

import java.util.Objects;

/**
 * 支付方式枚举类
 *
 * @author tortoise
 * @since 2019/3/31 10:40
 */
public enum PayWay {

    /**
     * 支付宝
     */
    ZFFS_01("ZFFS_01", "支付宝"),

    /**
     * 微信
     */
    ZFFS_02("ZFFS_02", "微信"),

    /**
     * 贝壳
     */
    ZFFS_07("ZFFS_07", "贝壳"),

    /**
     * 红包
     */
    ZFFS_08("ZFFS_08", "红包"),

    ;

    /**
     * 值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    PayWay(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 解析枚举对象
     *
     * @param value 具体值
     * @return 枚举对象
     */
    public static PayWay parse(String value) {
        Objects.requireNonNull(value, "The matching value cannot be empty");

        for (PayWay object : values()) {
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
