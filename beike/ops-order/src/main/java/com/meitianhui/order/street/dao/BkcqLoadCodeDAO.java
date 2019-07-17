package com.meitianhui.order.street.dao;

import com.meitianhui.order.street.entity.BkcqLoadCode;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <pre> 街市订单核销码数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 15:13
 */
public interface BkcqLoadCodeDAO {

    /**
     * 根据主键查询核销码
     *
     * @param id 主键
     * @return BkcqLoadCode
     */
    BkcqLoadCode selectByPk(Long id);

    /**
     * 查询过期订单核销码
     *
     * @param expiredDate 过期时间
     * @param statuses    状态
     * @return 核销码集合
     */
    List<BkcqLoadCode> selectExpireCode(@Param("expiredDate") Date expiredDate, @Param("statuses") List<String> statuses);

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 街市订单核销码记录
     * @return 影响的行数
     */
    int insertSelective(BkcqLoadCode record);

    /**
     * 根据订单编号查询核销码
     *
     * @param orderNo 订单编号
     * @return List<BkcqLoadCode> 核销码集合
     */
    List<BkcqLoadCode> selectByOrderNo(String orderNo);

    /**
     * 根据主键修改
     *
     * @param record 核销码记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(BkcqLoadCode record);

}