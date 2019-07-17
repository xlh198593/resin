package com.meitianhui.finance.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.dao.FinanceDao;
import com.meitianhui.finance.dao.TransactionDao;
import com.meitianhui.finance.entity.FDMemberAsset;
import com.meitianhui.finance.entity.FDMemberCashLog;
import com.meitianhui.finance.entity.FDMemberPointLog;
import com.meitianhui.finance.entity.FDMemberShellLog;
import com.meitianhui.finance.entity.FDTransactionsResult;
import com.meitianhui.finance.service.NotifyService;
import com.meitianhui.finance.service.ReduceMoneyService;
@Service
public class ReduceMoneyServiceImpl implements ReduceMoneyService {

	private static final Logger logger = Logger.getLogger(ReduceMoneyServiceImpl.class);
	
	@Autowired
	public TransactionDao transactionDao;
	
	@Autowired
	public FinanceDao financeDao;
	
	@Autowired
	public NotifyService notifyService;
	
	//写入扣除用户资产记录
	@Override
	public void updateFdMemberAssetforReduceMoney(FDTransactionsResult fDTransactionsResult, String member_id)
			throws BusinessException, SystemException, Exception {
		logger.info("开始扣除用户现金资产====>"+FastJsonUtil.toJson(fDTransactionsResult)+"  out_member_id="+member_id);
		BigDecimal trade_amount = fDTransactionsResult.getAmount().negate();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", member_id);
		FDMemberAsset out_member = financeDao.selectFDMemberAsset(tempMap);
		BigDecimal pre_balance = out_member.getCash_balance();
		BigDecimal balance = MoneyUtil.moneyAdd(pre_balance, trade_amount);
		FDMemberCashLog operateLog = new FDMemberCashLog();
		operateLog.setMember_id(out_member.getMember_id());
		operateLog.setMember_type_key(out_member.getMember_type_key());
		operateLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		operateLog.setTracked_date(new Date());
		operateLog.setPre_balance(pre_balance);
		operateLog.setAmount(trade_amount);
		operateLog.setBalance(balance);
		operateLog.setTransaction_no(fDTransactionsResult.getTransaction_no());
		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("transaction_no", fDTransactionsResult.getTransaction_no());
		remarkMap.put("order_type_key", fDTransactionsResult.getOrder_type_key());
		remarkMap.put("payment_way_key", fDTransactionsResult.getPayment_way_key());
		remarkMap.put("detail", fDTransactionsResult.getDetail());
		remarkMap.put("transaction_member_id", fDTransactionsResult.getBuyer_id());
		remarkMap.put("transaction_member_type_key", fDTransactionsResult.getBuyer_member_type());
		remarkMap.put("transaction_member_name", fDTransactionsResult.getBuyer_name());
		remarkMap.put("transaction_member_contact", fDTransactionsResult.getBuyer_contact());
		operateLog.setRemark(FastJsonUtil.toJson(remarkMap));
		transactionDao.insertFDMemberCashLog(operateLog);

		// 发短信,非公司账号发送短信
//		if (!out_member.getMember_type_key().equals(Constant.MEMBER_TYPE_COMPANY)) {
//			String in_or_out = "支出";
//			if (trade_amount.compareTo(BigDecimal.ZERO) > 0) {
//				in_or_out = "入账";
//			}
//			String sms_info = "您的余额账户" + in_or_out + "了" + trade_amount.abs() + "元,时间:"
//					+ DateUtil.date2Str(fDTransactionsResult.getTransaction_date(), DateUtil.fmt_yyyyMMddHHmmss);
//			sendMsgNotify(out_member, sms_info);
//		}
		logger.info("结束扣除用户现金资产====>");
	}

	
	@Override
	public void updateMemberReduceMoney(FDTransactionsResult fDTransactionsResult, String out_member_id)
			throws BusinessException, SystemException, Exception {
		logger.info("开始扣除用户现金资产====>"+FastJsonUtil.toJson(fDTransactionsResult)+"  out_member_id="+out_member_id);
		BigDecimal trade_amount = fDTransactionsResult.getAmount().negate();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", out_member_id);
		FDMemberAsset out_member = financeDao.selectFDMemberAsset(tempMap);
		BigDecimal pre_balance = out_member.getCash_balance();
		BigDecimal balance = MoneyUtil.moneyAdd(pre_balance, trade_amount);
		FDMemberCashLog operateLog = new FDMemberCashLog();
		operateLog.setMember_id(out_member.getMember_id());
		operateLog.setMember_type_key(out_member.getMember_type_key());
		operateLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		operateLog.setTracked_date(new Date());
		operateLog.setPre_balance(pre_balance);
		operateLog.setAmount(trade_amount);
		operateLog.setBalance(balance);
		operateLog.setTransaction_no(fDTransactionsResult.getTransaction_no());
		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("transaction_no", fDTransactionsResult.getTransaction_no());
		remarkMap.put("order_type_key", fDTransactionsResult.getOrder_type_key());
		remarkMap.put("payment_way_key", fDTransactionsResult.getPayment_way_key());
		remarkMap.put("detail", fDTransactionsResult.getDetail());
		remarkMap.put("transaction_member_id", fDTransactionsResult.getBuyer_id());
		remarkMap.put("transaction_member_type_key", fDTransactionsResult.getBuyer_member_type());
		remarkMap.put("transaction_member_name", fDTransactionsResult.getBuyer_name());
		remarkMap.put("transaction_member_contact", fDTransactionsResult.getBuyer_contact());
		operateLog.setRemark(FastJsonUtil.toJson(remarkMap));
		transactionDao.insertFDMemberCashLog(operateLog);
		
	}
	
	
	
	@Override
	public void insertFDMemberCashLog(FDTransactionsResult fDTransactionsResult, String out_member_id)
			throws BusinessException, SystemException, Exception {
		logger.info("开始记录用户现金资产日志====>"+FastJsonUtil.toJson(fDTransactionsResult)+"  out_member_id="+out_member_id);
		BigDecimal trade_amount = fDTransactionsResult.getAmount().negate();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", out_member_id);
		FDMemberAsset out_member = financeDao.selectFDMemberAsset(tempMap);
		BigDecimal pre_balance = out_member.getCash_balance();
		BigDecimal balance = MoneyUtil.moneyAdd(pre_balance, trade_amount); 
		FDMemberCashLog operateLog = new FDMemberCashLog();
		operateLog.setMember_id(out_member.getMember_id());
		operateLog.setMember_type_key(out_member.getMember_type_key());
		operateLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		operateLog.setTracked_date(new Date());
		operateLog.setPre_balance(pre_balance);
		operateLog.setAmount(trade_amount);
		operateLog.setBalance(balance);
		operateLog.setTransaction_no(fDTransactionsResult.getTransaction_no());
		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("transaction_no", fDTransactionsResult.getTransaction_no());
		remarkMap.put("order_type_key", fDTransactionsResult.getOrder_type_key());
		remarkMap.put("payment_way_key", fDTransactionsResult.getPayment_way_key());
		remarkMap.put("detail", fDTransactionsResult.getDetail());
		remarkMap.put("transaction_member_id", fDTransactionsResult.getBuyer_id());
		remarkMap.put("transaction_member_type_key", fDTransactionsResult.getBuyer_member_type());
		remarkMap.put("transaction_member_name", fDTransactionsResult.getBuyer_name());
		remarkMap.put("transaction_member_contact", fDTransactionsResult.getBuyer_contact());
		operateLog.setRemark(FastJsonUtil.toJson(remarkMap));
		transactionDao.insertFDMemberCashLog(operateLog);
		logger.info("结束记录用户现金资产日志====>");
	}
	
	/**
	 * 信息通知
	 */
	private void sendMsgNotify(FDMemberAsset member, String sms_info) {
		// 资产信息变更通知
		notifyService.financeChangeSMSNotify(member.getMember_id(), member.getMember_type_key(), sms_info);
		notifyService.financeChangeAppNotify(member.getMember_id(), sms_info);
	}
	
	@Override
	public void updateFdMemberAssetforReducePoint(FDTransactionsResult fDTransactionsResult, String out_member_id)
			throws BusinessException, SystemException, Exception {
		logger.info("开始扣除用户积分资产====>"+FastJsonUtil.toJson(fDTransactionsResult)+"  out_member_id="+out_member_id);
		Map<String, Object> tempMap = new HashMap<>();
		Map<String, Object> temp = new HashMap<>();
		tempMap.put("member_id", out_member_id);
		FDMemberAsset out_member = financeDao.selectFDMemberAsset(tempMap);
		FDMemberPointLog operateLog = new FDMemberPointLog();
//		BigDecimal amount = fDTransactionsResult.getAmount();
//		Integer  pre_balance = 0;
//		String amout_season = PropertiesConfigUtil.getProperty("member_recharge_amout_season");
//		String amout_year = PropertiesConfigUtil.getProperty("member_recharge_amout_year");
//		if (amount.compareTo(new BigDecimal(amout_season)) == 0) {
//			temp.put("gift_certificates_188", -1);
//			pre_balance = out_member.getGift_certificates_188();
//			operateLog.setAmount(amount.negate());
//		} else if (amount.compareTo(new BigDecimal(amout_year)) == 0) {
//			temp.put("gift_certificates_368", -1);
//			pre_balance = out_member.getGift_certificates_368();
//			operateLog.setAmount(amount.negate());
//		} else{
//			throw new BusinessException(RspCode.TRADE_FAIL, "礼券金额错误");
//		}
//		Integer balance = pre_balance - 1;
		operateLog.setMember_id(out_member.getMember_id());
		operateLog.setMember_type_key(out_member.getMember_type_key());
		operateLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		operateLog.setTracked_date(new Date());
		operateLog.setPre_balance(new BigDecimal("0"));
		operateLog.setBalance(new BigDecimal("399"));
		operateLog.setTransaction_no(fDTransactionsResult.getTransaction_no());
		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("transaction_no", fDTransactionsResult.getTransaction_no());
		remarkMap.put("order_type_key", fDTransactionsResult.getOrder_type_key());
		remarkMap.put("payment_way_key", fDTransactionsResult.getPayment_way_key());
		remarkMap.put("detail", fDTransactionsResult.getDetail());
		remarkMap.put("transaction_member_id", fDTransactionsResult.getBuyer_id());
		remarkMap.put("transaction_member_type_key", fDTransactionsResult.getBuyer_member_type());
		remarkMap.put("transaction_member_name", fDTransactionsResult.getBuyer_name());
		remarkMap.put("transaction_member_contact", fDTransactionsResult.getBuyer_contact());
		operateLog.setRemark(FastJsonUtil.toJson(remarkMap));
		transactionDao.insertFDMemberPointLog(operateLog);
//		tempMap.clear();
//		temp.put("member_id", out_member.getMember_id());
//		Integer update_flag = financeDao.updateFDMemberAsset(temp);
//		if (update_flag == 0) {
//			throw new BusinessException(RspCode.TRADE_FAIL, "积分余额更新失败");
//		}
		logger.info("结束扣除用户积分资产====>");
	}

	//修改用户资产表贝壳数据
	@Override
	public void updateFdMemberAssetforReduceShell(FDTransactionsResult fDTransactionsResult, String out_member_id)
			throws BusinessException, SystemException, Exception {
		logger.info("开始扣除贝壳资产====>"+FastJsonUtil.toJson(fDTransactionsResult)+" ；out_member_id="+out_member_id);
		// 将amount  改为负数
		BigDecimal trade_amount = fDTransactionsResult.getAmount().negate();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", out_member_id);
		FDMemberAsset memberAsset = financeDao.selectFDMemberAsset(tempMap);
		BigDecimal pre_balance = memberAsset.getShell();
		BigDecimal balance = MoneyUtil.moneyAdd(pre_balance, trade_amount);
		FDMemberShellLog operateLog = new FDMemberShellLog();
		operateLog.setMember_id(memberAsset.getMember_id());
		operateLog.setMember_type_key(memberAsset.getMember_type_key());
		operateLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		operateLog.setTracked_date(new Date());
		operateLog.setPre_balance(pre_balance);
		operateLog.setAmount(trade_amount);
		operateLog.setBalance(balance);
		operateLog.setTransaction_no(fDTransactionsResult.getTransaction_no());
		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("transaction_no", fDTransactionsResult.getTransaction_no());
		remarkMap.put("order_type_key", fDTransactionsResult.getOrder_type_key());
		remarkMap.put("payment_way_key", fDTransactionsResult.getPayment_way_key());
		remarkMap.put("detail", fDTransactionsResult.getDetail());
		remarkMap.put("transaction_member_id", fDTransactionsResult.getBuyer_id());
		remarkMap.put("transaction_member_type_key", fDTransactionsResult.getBuyer_member_type());
		remarkMap.put("transaction_member_name", fDTransactionsResult.getBuyer_name());
		remarkMap.put("transaction_member_contact", fDTransactionsResult.getBuyer_contact());
		operateLog.setRemark(FastJsonUtil.toJson(remarkMap));
		transactionDao.insertFDMemberShellLog(operateLog);
		
		tempMap.clear();
		tempMap.put("member_id", memberAsset.getMember_id());
		tempMap.put("shell", trade_amount);
		Integer update_flag = financeDao.updateFDMemberAsset(tempMap);
		if (update_flag == 0) {
			throw new BusinessException(RspCode.TRADE_FAIL, "贝壳余额更新失败");
		}
		logger.info("结束扣除贝壳资产====>");
	}

	

}
