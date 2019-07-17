package com.meitianhui.goods.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface GdCategoryCatService {
	
	
	/**
	 * 商品分类列表查询
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void gdCategoryCatListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 商品规格属性列表查询
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void gdCategoryPropsFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 商品规格属性详细信息查询
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void gdCategoryPropsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 新增商品规格属性
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void gdCategoryPropsCreate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
    /**
     * 编辑商品规格属性
     * @param paramsMap
     * @param result
     * @throws BusinessException
     * @throws SystemException
     * @throws Exception
     */
	void gdCategoryPropsEdit(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	

	/**
	 * 新增商品类别
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void gdCategoryCatCreate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	

    /**
     * 编辑商品分类
     * @param paramsMap
     * @param result
     * @throws BusinessException
     * @throws SystemException
     * @throws Exception
     */
	void gdCategoryCatEdit(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	

	
	void getCategoryCatDetail(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
}
