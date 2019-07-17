package com.meitianhui.finance.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;

public interface FdMemberBankService {

	/**
	 * 查找会员的银行卡绑定信息
	 */
	void findMemberBankBindingInfo(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 添加会员的银行卡绑定信息
	 */
	void addFdMemberBankInfo(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 申请提现
	 */
	void insertWithdrawal(Map<String, Object> paramsMap, ResultData result) throws Exception;

}
