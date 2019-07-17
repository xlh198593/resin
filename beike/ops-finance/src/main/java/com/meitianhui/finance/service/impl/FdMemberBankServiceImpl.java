package com.meitianhui.finance.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.constant.ShellConstant;
import com.meitianhui.finance.dao.FdMemberBankDao;
import com.meitianhui.finance.dao.FinanceDao;
import com.meitianhui.finance.dao.TransactionDao;
import com.meitianhui.finance.entity.FDMemberAsset;
import com.meitianhui.finance.entity.FDMemberCashLog;
import com.meitianhui.finance.entity.FDMemberRebateLog;
import com.meitianhui.finance.entity.FdMemberBank;
import com.meitianhui.finance.entity.FdWithdraw;
import com.meitianhui.finance.service.FdMemberBankService;

//@SuppressWarnings("unchecked")
@Service
public class FdMemberBankServiceImpl implements FdMemberBankService {

	@Autowired
	public FdMemberBankDao fdMemberBankDao;
	@Autowired
	public FinanceDao financeDao;
	@Autowired
	public TransactionDao transactionDao;

	@Override
	public void findMemberBankBindingInfo(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		List<Map<String, Object>> memberBankBindingInfo = fdMemberBankDao.findMemberBankBindingInfo(paramsMap);
		if (memberBankBindingInfo.isEmpty() || memberBankBindingInfo.size() == 0) {
			throw new BusinessException("会员不存在", "会员不存在,请稍后重试");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("memberInfo", memberBankBindingInfo.get(0));
		map.put("is_true", "Y");
		map.put("annunciateInfo", ShellConstant.ANNUNCIATE);
		result.setResultData(map);
	}

	@Override
	public void addFdMemberBankInfo(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap,
				new String[] { "member_id", "mobile", "member_type_key", "proposer", "bank_code", "bank_account" });
		String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
		String mobile = StringUtil.formatStr(paramsMap.get("mobile"));
		String member_type_key = StringUtil.formatStr(paramsMap.get("member_type_key"));
		String proposer = StringUtil.formatStr(paramsMap.get("proposer"));
		String bank_code = StringUtil.formatStr(paramsMap.get("bank_code"));
		String bank_account = StringUtil.formatStr(paramsMap.get("bank_account"));
		FdMemberBank bank = new FdMemberBank();
		bank.setId(IDUtil.getUUID());
		bank.setMember_type_key(member_type_key);
		bank.setMember_id(member_id);
		bank.setMobile(mobile);
		bank.setProposer(proposer);
		bank.setBank_code(bank_code);
		bank.setBank_account(bank_account);
		bank.setCreated_date(new Date());
		int i = fdMemberBankDao.addFdMemberBankInfo(bank);
		if (i == 0) {
			throw new BusinessException("绑定失败", "绑定失败,请稍后重试");
		}
	}

	@Override
	public void insertWithdrawal(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "bank_id", "amount" });
		String bank_id = StringUtil.formatStr(paramsMap.get("bank_id"));
		String amountStr = StringUtil.formatStr(paramsMap.get("amount"));
		Date date = new Date();
		Map<String, Object> temp = new HashMap<>();
		temp.put("bank_id", bank_id);
		FdMemberBank fdMemberBank = fdMemberBankDao.findFdMemberBankInfo(temp);
		String member_id = fdMemberBank.getMember_id();
		temp.clear();
		temp.put("member_id", member_id);
		Map<String, Object> consumerInfo = consumerFind(temp);
		String is_vip = StringUtil.formatStr(consumerInfo.get("is_vip"));
		if(!"Y".equalsIgnoreCase(is_vip)){
			throw new BusinessException("会员已过期", "您的会员已过期，红包余额不可提现，如需提现，请续费超级会员。");
		}
		FDMemberAsset in_member = financeDao.selectFDMemberAsset(temp);
		BigDecimal pre_balance = in_member.getCash_balance();
		BigDecimal trade_amount = new BigDecimal(amountStr);
		int to = trade_amount.compareTo(new BigDecimal("10000"));
		int to2 = trade_amount.compareTo(new BigDecimal("100"));
		if (to2 == -1 || to == 1){
			throw new BusinessException("金额不正确", "可提现金额不正确,金额不能小于100或大于10000");
		}
		int compareTo = pre_balance.compareTo(trade_amount);
		if (compareTo == -1) {
			throw new BusinessException("金额不足", "可提现金额不足,请稍后重试");
		}
		temp.put("status_in", new Integer[]{0,2});
		temp.put("month", new Date());
		List<FdWithdraw> list =  fdMemberBankDao.findFdWithdraw(temp);
		int size = list.size();
		if(size >= 3){
			throw new BusinessException("提现次数不足", "抱歉,您当月提现次数已达上限(3次)");
		}
		// 记录提现管理日志
		FdWithdraw fdWithdraw = new FdWithdraw();
		BeanConvertUtil.copyProperties(fdWithdraw, fdMemberBank);
		fdWithdraw.setAdd_time(date);
		fdWithdraw.setId(IDUtil.getUUID());
		fdWithdraw.setOpt_time(date);
		fdWithdraw.setAmount(trade_amount);
		fdWithdraw.setPoundage(new BigDecimal("0"));// 暂时没有手续费
		fdWithdraw.setStatus(0);
		int add_flag = fdMemberBankDao.addFdWithdraw(fdWithdraw);
		// 记录现金冻结日志
		BigDecimal balance = MoneyUtil.moneySub(pre_balance, trade_amount);
		FDMemberRebateLog rebateLog = new FDMemberRebateLog();
		rebateLog.setMember_id(in_member.getMember_id());
		rebateLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		rebateLog.setCreate_time(date);
		rebateLog.setPre_balance(pre_balance);
		rebateLog.setCash_money(trade_amount.negate());
		rebateLog.setBalance(balance);
		rebateLog.setMobile(fdMemberBank.getMobile());
		Map<String, Object> remarkeMap = new HashMap<>();
		remarkeMap.put("detail", "用户提现");
		remarkeMap.put("receiveMoney", trade_amount.negate());
		remarkeMap.put("cashMobile", fdMemberBank.getMobile());
		rebateLog.setRemark(FastJsonUtil.toJson(remarkeMap));
		financeDao.insertFdMemberRebateLog(rebateLog);
		temp.clear();
		temp.put("member_id", in_member.getMember_id()); 
		temp.put("trade_cash_froze", trade_amount);
		temp.put("trade_cash_balance", trade_amount.negate());
		Integer update_flag = financeDao.updateFDMemberAsset(temp);
		if (update_flag == 0 || add_flag == 0) {
			throw new BusinessException(RspCode.TRADE_FAIL, "现金余额更新失败");
		}
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("msg", "（本月还可提现"+(2-size)+"次)");
		result.setResultData(resultMap);
	}

	private Map<String, Object> consumerFind(Map<String, Object> temp) throws SystemException, Exception, BusinessException {
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "member.consumerFind");
		reqParams.put("params", FastJsonUtil.toJson(temp));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultM = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultM.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultM.get("error_code"), (String) resultM.get("error_msg"));
		}
		Map<String, Object> consumerInfo = (Map<String, Object>)resultM.get("data");
		return consumerInfo;
	}
}
