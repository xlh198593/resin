package com.meitianhui.goods.dao;

import java.util.List;
import java.util.Map;
import com.meitianhui.goods.entity.GdCategoryCat;
/**
 * 商品类别
 * @author Administrator
 * @date 2018-4-18
 */
public interface GdCategoryCatDao {
	

	/**
	 * 新增商品类别
	 * @param gdAppAdvert
	 * @throws Exception
	 */
	void insertGdCategoryCat(GdCategoryCat gdCategoryCat) throws Exception;

	/**
	 * 查询商品类别列表
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectGdCategoryCatList(Map<String, Object> map) throws Exception;

	/**
	 * 更新商品类别
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int updateGdCategoryCat(Map<String, Object> map) throws Exception;
	
	/**
	 * 删除商品类别
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int deleteGdCategoryCat(Map<String, Object> map) throws Exception;
	
	
	int updateCountByCatId(Map<String, Object> map);


}
