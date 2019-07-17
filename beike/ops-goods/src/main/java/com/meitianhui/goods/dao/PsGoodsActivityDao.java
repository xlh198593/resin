package com.meitianhui.goods.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.meitianhui.goods.entity.PsGoodsActivity;

public interface PsGoodsActivityDao {

	List<PsGoodsActivity> selectPsGoodsActivity(Map<String, Object> paramsMap) throws Exception;

	/**
	 *  查找 会员页面 活动商品
	 */
	List<Map<String, Object>> findPsGoodsActivity(Map<String, Object> paramsMap) throws Exception;

	List<Map<String, Object>> findAllGoodsBySupplierId(Map<String, Object> paramsMap) throws Exception;

	List<Map<String, Object>> findActivityGoodsBySupplierId(Map<String, Object> paramsMap);

	List<Map<String, Object>> findHDMS05GoodsBySupplierId(Map<String, Object> paramsMap);

	List<Map<String, Object>> findActivityGoodsBySupplierId02(Map<String, Object> paramsMap);

	List<Map<String, Object>> selectDetailGoods(Map<String, Object> paramsMap);

	Map<String,Object> selectBkcqProductsByGoodsId(Map<String, Object> oneMap);

	List<Map<String, Object>> selectBkcqActivityProductsList(Map<String, Object> oneMap);

    List<Map<String,Object>> selectGoodsSku(ArrayList<String> goodsIdList);




}

















