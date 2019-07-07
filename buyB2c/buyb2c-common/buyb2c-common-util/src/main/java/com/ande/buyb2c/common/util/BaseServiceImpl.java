package com.ande.buyb2c.common.util;

import java.util.List;


import com.github.pagehelper.PageHelper;


/**
 * 通用Service接口定义了新增、修改、删除、查询单个记录、查询记录列表、分页查询列表的方法
 * 
 * @author: chengzb
 */
public abstract class BaseServiceImpl<T> implements IBaseService<T> {
    protected abstract IBaseDao<T> getMapper();
    /**
     * 插入数据(选择性地)
     * 
     * @param entity
     * @return
     * @throws Exception
     * @throws
     */
    @Override
    public int insertSelective(T entity) throws Exception {
        	return getMapper().insertSelective(entity);
    }
    /**
     * 更新数据(选择性地)
     * 
     * @param entity
     * @return
     * @throws Exception
     * @throws
     */
    @Override
    public int updateByPrimaryKeySelective(T entity) throws Exception {
        return getMapper().updateByPrimaryKeySelective(entity);
    }
    
    @Override
    public int deleteByPrimaryKey(int id)throws Exception {
         return getMapper().deleteByPrimaryKey(id);
    }
    @Override
    public T selectByPrimaryKey(int id) {
        return getMapper().selectByPrimaryKey(id);
    }
    @Override
    public int getCount(T entity) {
        return getMapper().getCount(entity);
    }
    public List<T> getAll(){
         return getMapper().getAll();
    }
    public List<T> getAllBySelect(T entity){
         return getMapper().getAllBySelect(entity);
    }
  
    @Override
    public List<T> getPage(T obj){
        return getMapper().getPage(obj);
    }
    @Override
	public PageResult<T> queryByPage(PageResult<T> t,T entity) {
    	int pageNo=t.getPageNo();
    	int pageSize=t.getPageSize();
		pageNo = pageNo == 0?1:pageNo;
		pageSize = pageSize == 0?10:pageSize;
		PageHelper.startPage(pageNo,pageSize); 
		return PageResult.toPageResult(getPage(entity),t);
	}
    @Override
    public List<T> getPageFront(T obj){
        return getMapper().getPageFront(obj);
    }
    @Override
	public PageResult<T> queryByPageFront(PageResult<T> t,T entity) {
    	int pageNo=t.getPageNo();
    	int pageSize=t.getPageSize();
		pageNo = pageNo == 0?1:pageNo;
		pageSize = pageSize == 0?10:pageSize;
		PageHelper.startPage(pageNo,pageSize); 
		return PageResult.toPageResult(getPageFront(entity),t);
	}
   /* @Override
    public List<T> getMyPageList(MyPage<T> page,T obj){
        return getMapper().getMyPageList(page,obj);
    }
    @Override
	public PageResult<T> getMyPage(MyPage<T> page,T entity){
		int currentPage=page.getPageNo();
		int totalCount=getCount(entity);
		int totalPage=0;
		int size;
		if(page.getPageSize()==0){
			size=10;
		}else{
			size=page.getPageSize();
		}
		page.setPageSize(size);
		//计算总页数
		if(totalCount%size==0){
			totalPage=totalCount/size;
		}else{
			totalPage=totalCount/size+1;
		}
		page.setPages(totalPage);
		page.setTotal(totalCount);
		if(currentPage==0){
			currentPage=1;
			page.setStart(0);
		}else{
			if(currentPage<=totalPage){
				page.setStart((currentPage-1)*size);
			}else{
				page.setStart((totalPage-1)*size);
			}
		}
		if(currentPage<=totalPage){
		page.setPageNo(currentPage);
		}else{
		page.setPageNo(totalPage);
		}
		page.setEnd(size);
		page.setDataList(getMyPageList(page,entity));
		return page;
	}*/
}
