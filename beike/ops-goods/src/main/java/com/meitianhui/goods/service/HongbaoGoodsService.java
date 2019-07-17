package com.meitianhui.goods.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;

/**
 * 贝壳商城服务层
 */
public interface HongbaoGoodsService {

	/**
	 * 修改礼券专区商品库存
	 */
	void hongbaoGoodsSaleQtyUpdate(Map<String, Object> paramsMap, ResultData result) throws  Exception;

	/**
	 * 查找礼券专区商品
	 */
	void hongbaoGoodsFindForOrder(Map<String, Object> paramsMap, ResultData result) throws  Exception;


}
