package com.ande.buyb2c.common.util;

import java.util.List;

import org.apache.ibatis.annotations.Param;






/**
 * 通用DAO接口定义了新增�?�修改�?�删除�?�查询单个记录�?�查询记录列表�?�分页查询列表的方法
 * 
 * @author: chengzb
 * @param <T>
 */
public interface IBaseDao<T> {

    /**
     * 插入数据
     * 
     * @param entity
     * @return
     * @throws Exception
     * @throws
     */
    public int insertSelective(T entity) throws Exception;

    /**
     * 更新数据
     * 
     * @param entity
     * @return
     * @throws Exception
     * @throws
     */
    public int updateByPrimaryKeySelective(T entity) throws Exception;

    /**
     * 根据id查询单个记录
     * 
     * @param id
     * @return
     * @throws Exception
     * @throws
     */
    public T selectByPrimaryKey(int id);
    
    /**
     * 根据id删除单个记录
     * 
     * @param id
     * @return
     * @throws Exception
     * @throws
     */
    public int deleteByPrimaryKey(int id)throws Exception ;

    /**
     * 获取记录 分页
     * 
     * @param entity
     * @return
     * @throws Exception
     * @throws
     */

   public List<T> getPage(T entity);

   public List<T> getPageFront(T entity);
    /**
     * 查询总数
     * 
     * @param entity
     * @return
     * @throws Exception
     * @throws
     */
    public int getCount(T entity);
   /* public List<T> getMyPageList(MyPage<T> page,T obj);*/
    public List<T> getAll();
    public List<T> getAllBySelect(T entity);
    
     
}

