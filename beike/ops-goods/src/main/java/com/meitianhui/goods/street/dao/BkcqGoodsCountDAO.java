package com.meitianhui.goods.street.dao;

import com.meitianhui.goods.street.entity.BkcqGoodsCount;

/**
 * <pre> 街市商品统计数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 15:13
 */
public interface BkcqGoodsCountDAO {

    /**
     * 根据主键修改，只修改数量，修改方式为累加
     *
     * @param record 街市商品统计记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(BkcqGoodsCount record);

}