package com.meitianhui.order.dao;

import java.util.Map;

import com.meitianhui.order.entity.BeikeMallOrderTardeLog;
import com.meitianhui.order.entity.BeikeStreetOrderTardeLog;
import com.meitianhui.order.entity.HongBaoOrderTardeLog;

public interface OrderTradeLogDao {
	/**
	 * 插入贝壳商城订单日志
	 */
	Integer insertBeikeMallOrderTradeLog(BeikeMallOrderTardeLog orderTardeLog) throws Exception;

	/**
	 * 插入贝壳街市订单日志
	 */
	Integer insertBeikeStreetOrderTardeLog(BeikeStreetOrderTardeLog orderTardeLog) throws Exception;

	/**
	 * 插入红包兑订单日志
	 */
	Integer insertHongBaoOrderTardeLog(HongBaoOrderTardeLog orderTardeLog) throws Exception;

	/**
	 * 修改贝壳商城订单交易日志状态
	 */
	Integer beikeMallOrderTradeLogUpdate(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 修改贝壳兑订单交易日志状态
	 */
	Integer hongBaoOrderTradeLogUpdate(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 修改贝壳街市订单交易日志状态
	 */
	Integer beikeStreetOrderTradeLogUpdate(Map<String, Object> paramsMap) throws Exception;

}
