package com.meitianhui.member.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

/**
 * 会员任务
 * 
 * @author Tiny
 *
 */
public interface MemberTaskService {

	/**
	 * 冻结服务费
	 */
	public void handleFreezeServiceFree(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 处理服务费
	 */
	public void handleProServiceFree(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 业务员助教申请时，门店锁定后如果三天内不做处理，系统自动改审核状态为驳回
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void handleAssistantServiceFree(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员每日自动获取成长值接口
	 */
	public void handleMemberServiceFree(Map<String, Object> paramsMap, ResultData result)
			throws Exception;

	/**
	 * 会员购买天数达标获得成长值
	 */
	void handleMemberShoppingServiceFree(Map<String, Object> paramsMap, ResultData result)
			throws Exception;
	
	/**
	 * 	计算会员的下级 数量，改变分销等级
	 */
	void  handledMemberDistribtion(Map<String, Object> paramsMap, ResultData result)
			throws Exception;
	
	/**
	 * 	计算会员的下级 数量，改变分销等级
	 */
	void  handledMemberDistribtion() throws Exception;
	/**
	 *  定时任务，把到期的体验会员改为普通会员
	 * @throws Exception
	 */
	public void consumerMemberEdit() throws Exception;
}
