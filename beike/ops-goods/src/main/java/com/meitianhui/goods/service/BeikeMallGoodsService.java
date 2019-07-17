package com.meitianhui.goods.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;

/**
 * 贝壳商城服务层
 */
public interface BeikeMallGoodsService {

	/**
	 * 首页商品展示
	 */
	void homeGoodsFind(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查询vip推荐商品
	 */
	void vipGoodsFind(Map<String, Object> paramsMap, ResultData result)throws Exception;

	/**
	 * 首页橱窗商品(新接口)
	 */
	void homeGoodsFind_V1(Map<String, Object> paramsMap, ResultData result)throws Exception;

	/**
	 * 首页推荐商品(分页)
	 */
	void findCommendGoods(Map<String, Object> paramsMap, ResultData result)throws Exception;

	/**
	 * 查找年货节商品(不分页)
	 */
	void newYearGoodsFind(Map<String, Object> paramsMap, ResultData result)throws Exception;

}
