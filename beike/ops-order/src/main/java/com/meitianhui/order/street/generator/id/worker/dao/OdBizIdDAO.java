package com.meitianhui.order.street.generator.id.worker.dao;

import com.meitianhui.order.street.generator.id.worker.entity.OdBizId;

/**
 * <pre> 数据库编号生成器数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 10:13
 */
public interface OdBizIdDAO {

    /**
     * 新增，插入非空数据
     *
     * @param odBizId OdBizId实体
     * @return 影响的行数
     */
    int insertSelective(OdBizId odBizId);

    /**
     * 根据主键修改
     *
     * @param odBizId OdBizId实体
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(OdBizId odBizId);

    /**
     * 根据主键查询，该操作会加行锁
     *
     * @param bizTag 业务标志
     * @return OdBizId实体
     */
    OdBizId selectByPrimaryKey(String bizTag);

}
