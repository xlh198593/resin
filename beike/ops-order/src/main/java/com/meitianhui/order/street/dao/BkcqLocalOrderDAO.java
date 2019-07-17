package com.meitianhui.order.street.dao;

import com.meitianhui.order.street.entity.BkcqLocalOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <pre> 街市订单数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 15:13
 */
public interface BkcqLocalOrderDAO {

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 街市订单记录
     * @return 影响的行数
     */
    int insertSelective(BkcqLocalOrder record);

    /**
     * 根据主键查询
     *
     * @param orderId 订单主键
     * @return 街市订单记录
     */
    BkcqLocalOrder selectByPrimaryKey(Long orderId);

    /**
     * 根据订单编号查询
     *
     * @param orderNo 订单编号
     * @return 街市订单记录
     */
    BkcqLocalOrder selectByOrderNo(String orderNo);

    /**
     * 根据实体分页查询
     *
     * @param customerId 消费者编号
     * @param statuses   状态集合
     * @return 街市订单记录
     */
    List<BkcqLocalOrder> selectByParams(@Param("customerId") String customerId, @Param("statuses") List<String> statuses);

    /**
     * 查询过期订单
     *
     * @param expiredDate 过期时间
     * @param statuses    状态
     * @return 街市订单记录
     */
    List<BkcqLocalOrder> selectExpireOrder(@Param("expiredDate") Date expiredDate, @Param("statuses") List<String> statuses);

    /**
     * 根据主键修改
     *
     * @param record 街市订单记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(BkcqLocalOrder record);

}