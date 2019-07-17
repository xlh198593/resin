package com.meitianhui.order.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

/**
 * 定时任务
* @ClassName: OrderTaskService  
* @author tiny 
* @date 2017年5月14日 下午12:21:52  
*
 */
public interface OrderTaskService {

	/**
	 * 领了么超时订单自动取消
	 */
	public void fgOrderAutoCancel(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 超时自营商品订单自动取消
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void fgOrderForOwnAutoCancel(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 处理团购预售订单
	 */
	public void proPsGroupOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批超时订单自动确认收货
	 */
	public void psOrderAutoReceived(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 精选特卖超时订单自动确认收货
	 */
	public void pcOrderAutoReceived(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 处理过期任务
	 */
	public void handleOdTaskTimeout(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	
	 * 伙拼团活动验证
	 */
	public void tsActivityCheck(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 伙拼团活动超时自动确认收货
	 */
	public void tsActivityAutoReceived(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 伙拼团订单超时自动确认收货
	 */
	public void tsOrderAutoReceived(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 自动转入佣金到店东零钱账户
	 * @param result 
	 * @param paramsMap 
	 */
	void transmatic(Map<String, Object> paramsMap, ResultData result) throws Exception;


	/**
	 * 超时贝壳商品订单自动取消 
	 */
	void beikeMallOrderForAutoCancel(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 超时红包兑商品订单自动取消 
	 */
	void hongBaoOrderForAutoCancel(Map<String, Object> paramsMap, ResultData result) throws Exception;

	
	
	
}
