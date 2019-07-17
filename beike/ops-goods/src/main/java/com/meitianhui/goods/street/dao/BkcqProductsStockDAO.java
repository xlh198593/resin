package com.meitianhui.goods.street.dao;

import com.meitianhui.goods.street.entity.BkcqProductsStock;

/**
 * <pre> 街市商品库存数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 15:13
 */
public interface BkcqProductsStockDAO {

    /**
     * 根据主键查询
     *
     * @param goodsId 商品ID
     * @return 街市商品库存记录
     */
    BkcqProductsStock selectByPrimaryKey(Long goodsId);

    /**
     * 根据主键修改,库存为原库存+当前对象库存的值
     *
     * @param record 街市商品库存记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(BkcqProductsStock record);

}