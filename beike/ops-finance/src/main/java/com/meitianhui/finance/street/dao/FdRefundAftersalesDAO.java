package com.meitianhui.finance.street.dao;

import com.meitianhui.finance.street.entity.FdRefundAftersales;

/**
 * <pre> 退款记录数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/31 10:13
 */
public interface FdRefundAftersalesDAO {

    /**
     * 根据主键查询
     *
     * @param refundsNo 退款编号
     * @return 资产记录
     */
    FdRefundAftersales selectByRefundsNo(String refundsNo);

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 退款记录
     * @return 影响的行数
     */
    int insertSelective(FdRefundAftersales record);

    /**
     * 根据主键修改
     *
     * @param record 退款记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(FdRefundAftersales record);

}