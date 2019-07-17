package com.meitianhui.goods.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.goods.entity.GdCategoryProps;


public interface GdCategoryPropsDao {
	

	/**
	 * 新增商品规格属性
	 * @param gdAppAdvert
	 * @throws Exception
	 */
	void insertGdCategoryProps(GdCategoryProps gdCategoryProps) throws Exception;

	/**
	 * 查询商品规格属性列表
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectGdCategoryPropsList(Map<String, Object> map) throws Exception;

	/**
	 * 更新商品规格属性
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int updateGdCategoryProps(Map<String, Object> map) throws Exception;
	
	/**
	 * 删除商品规格属性
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int deleteGdCategoryProps(Map<String, Object> map) throws Exception;
	
	/**
	 * 查询单个商品规格属性
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectGdCategoryProps(Map<String, Object> map) throws Exception;


}
