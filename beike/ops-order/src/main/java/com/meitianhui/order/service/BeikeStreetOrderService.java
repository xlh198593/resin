package com.meitianhui.order.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;

/**
 * 贝壳街市订单接口
 *
 */
public interface BeikeStreetOrderService {

	/**
	 * 创建订单
	 */
	void beikeStreetOrderCreate (Map<String, Object> paramsMap, ResultData result) throws Exception;

}
