package com.meitianhui.order.street.dao;

import com.meitianhui.order.street.entity.BkcqLocalOrderItem;

import java.util.List;

/**
 * <pre> 街市订单明细数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 17:13
 */
public interface BkcqLocalOrderItemDAO {

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 街市订单明细记录
     * @return 影响的行数
     */
    int insertSelective(BkcqLocalOrderItem record);

    /**
     * 根据主键查询
     *
     * @param orderItemId 明细编号
     * @return 街市订单明细记录
     */
    BkcqLocalOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单编号查询
     *
     * @param orderId 订单编号
     * @return 街市订单明细列表
     */
    List<BkcqLocalOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据主键修改
     *
     * @param record 街市订单明细记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(BkcqLocalOrderItem record);

}