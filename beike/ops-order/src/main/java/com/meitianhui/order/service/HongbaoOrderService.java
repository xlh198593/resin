package com.meitianhui.order.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

/**
 * 礼券专区服务层
 */
public interface HongbaoOrderService {

	/**
	 * 礼券专区创建订单
	 */
	void hongbaoOrderCreate(Map<String, Object> paramsMap, ResultData result) throws Exception;

	
	/**
	 * 礼券专区创建订单
	 */
	void h5OrderCreate(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 查找礼券专区订单
	 */
	void hongBaoOrderDetailFind(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 修改红包兑订单状态
	 */
	void hongBaoOrderUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 红包兑订单接口列表查询
	 */
	void hongBaoOrderListPageFind(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 红包兑确认订单接口
	 */
	void hongbaoOrderForConfirmReceipt(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 红包商城订单详情接口
	 */
	void hongbaoOrderDetail(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 查询礼券订单状态
	 */
	void findHongBaoOrderInfoByNo(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 礼包专区取消订单
	 */
	void hongbaoOrderAnnul(Map<String, Object> paramsMap, ResultData result) throws Exception;
}
