package com.meitianhui.goods.street.consts;

import java.util.Objects;

/**
 * 商品状态枚举类
 *
 * @author tortoise
 * @since 2019/3/31 10:40
 */
public enum ProductStatus {

    /**
     * 正常
     */
    NORMAL("normal", "正常"),

    /**
     * 删除
     */
    DELETE("delete", "删除"),

    /**
     * 上架
     */
    ON_SHELF("on_shelf", "上架"),

    /**
     * 下架
     */
    OFF_SHELF("off_shelf", "下架"),

    /**
     * 违规
     */
    VIOLATION("violation", "违规"),

    /**
     * 待审核
     */
    SUSPEND("suspend", "待审核"),

    /**
     * 审核后直接上架
     */
    CHECKED_ONLINE("checked_online", "审核后直接上架"),


    ;

    /**
     * 值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    ProductStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 解析枚举对象
     *
     * @param value 具体值
     * @return 枚举对象
     */
    public static ProductStatus parse(String value) {
        Objects.requireNonNull(value, "The matching value cannot be empty");

        for (ProductStatus object : values()) {
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
