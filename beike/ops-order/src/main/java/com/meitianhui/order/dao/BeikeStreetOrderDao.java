package com.meitianhui.order.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.order.entity.BeikeStreetOrder;

public interface BeikeStreetOrderDao {

	/**
	 * 插入贝壳街市订单表
	 */
	void insertBeikeStreetOrder(BeikeStreetOrder order) throws Exception;

	/**
	 * 查询贝壳街市订单列表
	 */
	List<Map<String, Object>> selectBeikeStreetOrderList(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 修改贝壳街市订单状态
	 */
	Integer beikeStreetOrderUpdate(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 贝壳街市列表查询
	 */
	List<Map<String, Object>> selectBeikeStreetOrdeList(Map<String, Object> paramsMap);

}
