package com.meitianhui.finance.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

/**
 * 订单支付
 */

public interface OrderPaymentService {
	/**
	 * 余额充值接口
	 */
	public void balanceRecharge(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 积分订单支付接口
	 */
	public void pointOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
}
