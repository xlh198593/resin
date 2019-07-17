package com.meitianhui.finance.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.finance.entity.FdMemberBank;
import com.meitianhui.finance.entity.FdWithdraw;

public interface FdMemberBankDao {

	/**
	 * 查找会员的银行卡绑定信息
	 */
	List<Map<String, Object>> findMemberBankBindingInfo(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 添加会员的银行卡绑定信息
	 */
	int addFdMemberBankInfo(FdMemberBank bank) throws Exception;

	/**
	 * 查找会员的银行卡信息
	 */
	FdMemberBank findFdMemberBankInfo(Map<String, Object> temp) throws Exception;

	/**
	 * 添加提现管理日志
	 */
	int addFdWithdraw(FdWithdraw fdWithdraw) throws Exception;

	/**
	 * 查询会员所有提现管理日志
	 */
	List<FdWithdraw> findFdWithdraw(Map<String, Object> temp) throws Exception;

}
