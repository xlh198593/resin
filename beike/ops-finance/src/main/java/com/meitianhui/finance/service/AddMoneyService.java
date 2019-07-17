package com.meitianhui.finance.service;

import java.math.BigDecimal;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.finance.entity.FDMemberAsset;
import com.meitianhui.finance.entity.FDTransactionsResult;

public interface AddMoneyService {

	/**
	 * 修改资产表加现金
	 */
	void updateFdMemberAssetforAddMoney(FDTransactionsResult fDTransactionsResult, String in_member_id)
			throws BusinessException, SystemException, Exception;

	/**
	 * 修改资产表加积分
	 */
	void updateFdMemberAssetforAddPoint(FDTransactionsResult fDTransactionsResult, String in_member_id)
			throws BusinessException, SystemException, Exception;

	/**
	 * 修改资产表加贝壳
	 */
	void updateFdMemberAssetforAddShell(FDTransactionsResult fDTransactionsResult, String in_member_id)
			throws BusinessException, SystemException, Exception;

	/**
	 * 记录用户现金资产日志
	 */
	void insertFDMemberCashLog(FDTransactionsResult fDTransactionsResult, String in_member_id)
			throws BusinessException, SystemException, Exception;

}
