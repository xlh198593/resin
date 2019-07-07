package com.ande.buyb2c.common.util;

import org.apache.ibatis.annotations.Param;

/**
 * 通用Service接口定义了新增、修改、删除、查询单个记录、查询记录列表、分页查询列表的方法
 * 
 * @author:chengzb
 * @param <T>
 */
public interface IBaseService<T> extends IBaseDao<T> {
	public PageResult<T> queryByPageFront(@Param("t") PageResult<T> t, @Param("entity") T entity);

	public PageResult<T> queryByPage(@Param("t") PageResult<T> t, @Param("entity") T entity);
}
