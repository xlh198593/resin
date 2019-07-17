package com.meitianhui.goods.street.dao;

import com.meitianhui.goods.street.entity.BkcqSkuStock;

/**
 * <pre> 街市商品sku库存数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 15:13
 */
public interface BkcqSkuStockDAO {

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 街市商品sku库存记录
     * @return 影响的行数
     */
    int insertSelective(BkcqSkuStock record);

    /**
     * 根据主键查询
     *
     * @param skuId 商品skuID
     * @return 街市商品sku库存记录
     */
    BkcqSkuStock selectByPrimaryKey(Long skuId);

    /**
     * 根据主键修改
     *
     * @param record 街市商品sku库存记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(BkcqSkuStock record);

}