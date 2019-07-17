package com.meitianhui.goods.street.dao;

import com.meitianhui.goods.street.entity.BkcqSkuStock;
import com.meitianhui.goods.street.entity.BkcqStockChangeLog;

/**
 * <pre> 街市商品库存变动日志数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 15:13
 */
public interface BkcqStockChangeLogDAO {

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 街市商品库存变动日志记录
     * @return 影响的行数
     */
    int insertSelective(BkcqStockChangeLog record);

    /**
     * 根据主键查询
     *
     * @param logId ID
     * @return 街市商品库存变动日志记录
     */
    BkcqStockChangeLog selectByPrimaryKey(Long logId);

    /**
     * 根据商品sku跟订单编号查询
     *
     * @param record ID
     * @return 街市商品库存变动日志记录
     */
    BkcqStockChangeLog selectBySkuIdAndOrderNo(BkcqStockChangeLog record);

    /**
     * 根据主键修改
     *
     * @param record 街市商品库存变动日志记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(BkcqStockChangeLog record);
}