package com.meitianhui.finance.street.dao;

import com.meitianhui.finance.street.entity.FdMemberCashLog;

/**
 * <pre> 会员现金日志数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/31 10:13
 */
public interface FdMemberCashLogDAO {

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 会员现金日志
     * @return 影响的行数
     */
    int insertSelective(FdMemberCashLog record);

}