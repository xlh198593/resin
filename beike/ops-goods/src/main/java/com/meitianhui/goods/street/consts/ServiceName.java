package com.meitianhui.goods.street.consts;

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
     * 根据主键查询一条商品记录
     */
    FIND_PRODUCT_BY_KEY("findProductByKey"),

    /**
     * 根据主键查询商品库存记录
     */
    FIND_PRODUCT_STOCK_BY_KEY("findProductStockByKey"),

    /**
     * 根据主键查询sku记录
     */
    FIND_SKU_BY_KEY("findSkuByKey"),

    /**
     * 根据主键查询sku库存记录
     */
    FIND_SKU_STOCK_BY_KEY("findSkuStockByKey"),

    /**
     * 冻结sku库存
     */
    FREEZE_SKU_STOCK("freezeSkuStock"),

    /**
     * 解冻sku库存
     */
    UNFREEZE_SKU_STOCK("unfreezeSkuStock"),

    /**
     * 扣减sku库存
     */
    DEDUCTION_SKU_STOCK("deductionSkuStock"),


    /**
     * 获取附近商家热销商品列表
     */
    FIND_NEARBY_STORES_PRODUCTS("good.street.findNearbyStoresProducts"),

    /**
     * 按城市搜索商品列表
     */
    FIND_PRODUCTS_BY_CITY("good.street.findProductsByCity"),

    /**
     * 贝壳街市首页
     */
    STREET_HOME_INIT("good.street.homeInit"),

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
