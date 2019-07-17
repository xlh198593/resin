package com.meitianhui.goods.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.goods.entity.GdGoodsSkuid;
/**
 * 会过Sku
 * @author Administrator
 *
 */
public interface GdGoodsSkuidDao {
	/**
	 * 新增sku
	 * @param record
	 * @return
	 */
    int insert(GdGoodsSkuid record);
    
    /**
     * 更新sku
     * @param record
     * @return
     */
    int updateGdGoodsSkuid(GdGoodsSkuid gdGoodsSkuid);
    
    /**
	 * 会过商品规格列表信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<GdGoodsSkuid> selectGdGoodsSkuidList(Map<String, Object> map) throws Exception;
	
	/**
	 * 查询单个商品规格信息
	 * @param map
	 * @return
	 */
	GdGoodsSkuid selectOneGdGoodsSkuid(Map<String, Object> map);
	
	
	/**
	 * 新自营商品规格表中库存恢复
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	Integer updatePsGoodsSkuidStockForOwnRestore(Map<String, Object> map) throws Exception;
	
	List<Map<String,Object>> queryGdGoodsSkuidBySkuId(Map<String, Object> map) throws Exception;

	/**
	 * 通过主属性查找所有的副属性
	 */
	List<GdGoodsSkuid> selectGdGoodsSkuidListForFvaule(Map<String, Object> skumap) throws Exception;

	/**
	 * 查找sku(新接口)
	 */
	List<GdGoodsSkuid> selectGdGoodsSkuidListNew(Map<String, Object> skumap);
  
}

