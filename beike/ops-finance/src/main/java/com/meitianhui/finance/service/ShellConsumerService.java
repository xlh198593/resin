package com.meitianhui.finance.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface ShellConsumerService {

	/**
	 * 查询消费者资产信息
	 */
	void consumerFdMemberAssetFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询消费者贝壳资产日志
	 */
	void consumerFdMemberAssetShellLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询消费者积分资产日志
	 */
	void consumerFdMemberAssetPointLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询消费者现金资产日志
	 */
	void consumerFdMemberAssetCashLogFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 	查找会员返佣明细
	 */
	void memberRabateListFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 	查找掌柜次邀返佣明细
	 */
	void memberManagerRabateListFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 	查找掌柜累计的返佣
	 */
	void memberManagerRabateCount(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;

	/**
	 * 新用户或已注册未购买会员的用户赠送68贝壳
	 */
	void registerUserReceiveShell(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;

	/**
	 * 获取用户是否弹出新用户领取68贝壳的状态
	 */
	void getBulletBoxStatus(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
}
