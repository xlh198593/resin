package com.meitianhui.finance.street.dao;

import com.meitianhui.finance.street.entity.FdMemberShellLog;

import java.util.Map;

/**
 * <pre> 会员礼券日志数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/31 10:13
 */
public interface FdMemberShellLogDAO {

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 会员贝壳日志
     * @return 影响的行数
     */
    int insertSelective(FdMemberShellLog record);

    /**
     * 查询
     */
    Map<String,Object> findShellLogByMemberIdAndRemark(Map<String,Object> map)throws Exception;

}