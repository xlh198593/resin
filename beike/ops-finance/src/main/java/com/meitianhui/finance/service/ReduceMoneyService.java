package com.meitianhui.finance.service;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.finance.entity.FDTransactionsResult;

public interface ReduceMoneyService {
	/**
	 * 扣除用户现金资产
	 */
	public void updateFdMemberAssetforReduceMoney(FDTransactionsResult fDTransactionsResult, String out_member_id)
			throws BusinessException, SystemException, Exception;
	
	/**
	 *  记录支付日志
	 */
	public void updateMemberReduceMoney(FDTransactionsResult fDTransactionsResult, String out_member_id)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 扣除用户积分资产
	 */
	public void updateFdMemberAssetforReducePoint(FDTransactionsResult fDTransactionsResult, String out_member_id)
					throws BusinessException, SystemException, Exception;
	/**
	 * 扣除用户贝壳资产
	 */
	public void updateFdMemberAssetforReduceShell(FDTransactionsResult fDTransactionsResult, String out_member_id)
					throws BusinessException, SystemException, Exception;
	/**
	 * 记录用户现金资产日志
	 */
	void insertFDMemberCashLog(FDTransactionsResult fDTransactionsResult, String out_member_id)
			throws BusinessException, SystemException, Exception;
}
