package com.meitianhui.finance.street.dao;

import com.meitianhui.finance.street.entity.FdSysCallbackLog;

import java.util.List;
import java.util.Map;

/**
 * <pre> 接口回调记录数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/31 10:13
 */
public interface FdSysCallbackLogDAO {

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 接口回调记录
     * @return 影响的行数
     */
    int insertSelective(FdSysCallbackLog record);

    /**
     * 根据主键查询
     *
     * @param callbackId 回调ID
     * @return 资产记录
     */
    FdSysCallbackLog selectByPrimaryKey(Long callbackId);

    /**
     * 根据主键修改
     *
     * @param record 接口回调记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(FdSysCallbackLog record);

    /**
     * 根据Map查询
     *
     * @param map 查询条件
     * @return List<FdSysCallbackLog> 集合
     */
    List<FdSysCallbackLog> selectByMap(Map<String, Object> map);
}