package com.meitianhui.goods.street.dao;

import com.meitianhui.goods.street.entity.BkcqSku;

/**
 * <pre> 街市商品sku数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 15:13
 */
public interface BkcqSkuDAO {

    /**
     * 根据主键查询
     *
     * @param skuId 订单号
     * @return 街市商品记录
     */
    BkcqSku selectByPrimaryKey(Long skuId);

}