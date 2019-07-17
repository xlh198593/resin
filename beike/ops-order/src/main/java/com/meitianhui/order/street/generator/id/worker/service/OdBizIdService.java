package com.meitianhui.order.street.generator.id.worker.service;

import com.meitianhui.order.street.generator.id.worker.entity.OdBizId;

/**
 * <pre> 数据库编号生成器业务操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 11:13
 */
public interface OdBizIdService {

    /**
     * 根据主键获取OdBizId实体，并更新max_id
     *
     * @param bizTag 业务标志
     * @return OdBizId实体
     */
    OdBizId obtainOdBizId(String bizTag);

}
