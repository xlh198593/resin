package com.meitianhui.finance.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface ShellOrderPayService {

	/**
	 * 贝壳传奇充值接口
	 */
	void shellRecharge(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳传奇退款接口
	 */
	void shellOrderRefund(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳商城订单支付
	 */
	void beikeMallOrderPay(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 红包兑订单支付
	 */
	void hongBaoOrderPay(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳街市订单支付
	 */
	void beikeStreetOrderPay(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 *    订单支付（新）
	 */
	void  handleBeikeMallOrderPay(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 *    话费充值
	 */
	void telephoneRecharge(Map<String, Object> paramsMap, ResultData  result) throws Exception;
}
