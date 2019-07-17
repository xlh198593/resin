package com.meitianhui.finance.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//import javax.transaction.Transactional;

import com.meitianhui.common.util.*;
import com.meitianhui.finance.dao.TransactionDao;
import com.meitianhui.finance.entity.*;
import com.meitianhui.finance.street.dao.FdMemberCouponsLogDAO;
import com.meitianhui.finance.util.AESTool;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.meitianhui.common.constant.CommonConstant;
import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.constant.PageParam;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.constant.ShellConstant;
import com.meitianhui.finance.constant.TradeIDUtil;
import com.meitianhui.finance.dao.FinanceDao;
import com.meitianhui.finance.dao.MemberCashCouponDao;
import com.meitianhui.finance.service.FinanceService;
import com.meitianhui.finance.service.FinanceSyncService;
import com.meitianhui.finance.service.NotifyService;
import com.meitianhui.finance.service.TradeService;
import org.springframework.util.CollectionUtils;

/**
 * 金融服务的服务层
 * 
 * @author Tiny
 *
 */
@SuppressWarnings("unchecked")
@Service
public class FinanceServiceImpl implements FinanceService {

	@Autowired
	public FinanceDao financeDao;
	@Autowired
	private TradeService tradeService;
	@Autowired
	public NotifyService notifyService;
	@Autowired
	private FinanceSyncService financeSyncService;
	@Autowired
	private MemberCashCouponDao memberCashCouponDao;
	@Autowired
	public TransactionDao transactionDao;

	@Autowired
	private DocUtil docUtil;
	@Autowired
	private FdMemberCouponsLogDAO memberCouponsLogDAO;
	
	private static final Logger logger = Logger.getLogger(FinanceServiceImpl.class);

	@Override
	public void handleInitMemberAsset(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
			Integer  type = (Integer) paramsMap.get("type");
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("member_id", paramsMap.get("member_id"));
			queryMap.put("member_type_key", paramsMap.get("member_type_key"));
			FDMemberAsset fDMemberAsset = financeDao.selectFDMemberAsset(queryMap);
			if (fDMemberAsset == null) {
				fDMemberAsset = new FDMemberAsset();
				BeanConvertUtil.mapToBean(fDMemberAsset, paramsMap);
				fDMemberAsset.setAsset_id(IDUtil.getUUID());
				fDMemberAsset.setCreated_date(new Date());
				fDMemberAsset.setModified_date(new Date());
				if (null != type && type == 1) {
					fDMemberAsset.setShell(new BigDecimal("1314"));
				} else {
					fDMemberAsset.setShell(new BigDecimal("0"));
				}
				financeDao.insertFDMemberAsset(fDMemberAsset);
			} else {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("member_id", fDMemberAsset.getMember_id());
				if (null != type && type == 1) {
					tempMap.put("shell", new BigDecimal("1314"));
				} else {
					tempMap.put("shell", new BigDecimal("0"));
				}
				financeDao.updateMemberShell(tempMap);
			}
			//写入会员注册的逻辑
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void storeCashierPromotion(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "payment_way_key", "buyer_id", "seller_id", "rebate_cash",
							"pay_amount", "reward_gold", "out_trade_no", "out_trade_body", "detail" });
			String payment_way_key = paramsMap.get("payment_way_key") + "";

			// 检测公司是否入账
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("buyer_id", paramsMap.get("buyer_id"));
			tempMap.put("seller_id", Constant.MEMBER_ID_MTH);
			tempMap.put("out_trade_no", paramsMap.get("out_trade_no"));
			tempMap.put("business_type_key", Constant.BUSINESS_TYPE_BALANCEPAY);
			tempMap.put("payment_way_key", Constant.PAYMENT_WAY_05);
			List<FDTransactionsResult> fDTransactionsResultList = financeDao.selectFDTransactionsResult(tempMap);
			// 验证消费者是否支付金额到平台账号上去
			if (fDTransactionsResultList.size() <= 0) {
				return;
			}

			// 如果支付方式不为零钱，则不进行返现操作
			if (!payment_way_key.equals(Constant.PAYMENT_WAY_05)) {
				return;
			}
			BigDecimal pay_amount = new BigDecimal(paramsMap.get("pay_amount") + "");
			BigDecimal rebate_cash = new BigDecimal(paramsMap.get("rebate_cash") + "");
			// 店东应得的钱
			BigDecimal stores_amount = MoneyUtil.moneySub(pay_amount, rebate_cash);
			// 支付店东应该得到的钱
			if (stores_amount.compareTo(BigDecimal.ZERO) > 0) {
				Map<String, Object> balanceParams = new HashMap<String, Object>();
				balanceParams.put("data_source", paramsMap.get("data_source"));
				balanceParams.put("out_member_id", Constant.MEMBER_ID_MTH);
				balanceParams.put("in_member_id", paramsMap.get("seller_id"));
				balanceParams.put("payment_way_key", "ZFFS_05");
				balanceParams.put("amount", stores_amount + "");
				balanceParams.put("out_trade_no", paramsMap.get("out_trade_no"));
				balanceParams.put("out_trade_body", paramsMap.get("out_trade_body"));
				balanceParams.put("detail", paramsMap.get("detail"));
				tradeService.balancePay(balanceParams, result);
			}

			// 对消费者进行返现,支付金额要大于返现金额且返现金额不小于0
			if (pay_amount.compareTo(rebate_cash) > 0 && rebate_cash.compareTo(BigDecimal.ZERO) > 0) {
				Map<String, Object> balanceParams = new HashMap<String, Object>();
				balanceParams.put("data_source", paramsMap.get("data_source"));
				balanceParams.put("out_member_id", Constant.MEMBER_ID_MTH);
				balanceParams.put("in_member_id", paramsMap.get("buyer_id"));
				balanceParams.put("payment_way_key", "ZFFS_05");
				balanceParams.put("amount", rebate_cash + "");
				balanceParams.put("out_trade_no", paramsMap.get("out_trade_no"));
				balanceParams.put("out_trade_body", paramsMap.get("out_trade_body"));
				balanceParams.put("detail", "商家返利");
				tradeService.balancePay(balanceParams, result);
			}

			// 对消费者进行赠送金币
			BigDecimal reward_gold = new BigDecimal(paramsMap.get("reward_gold") + "");
			if (reward_gold.compareTo(BigDecimal.ZERO) > 0) {
				Map<String, Object> balanceParams = new HashMap<String, Object>();
				balanceParams.put("data_source", paramsMap.get("data_source"));
				balanceParams.put("out_member_id", Constant.MEMBER_ID_MTH);
				balanceParams.put("in_member_id", paramsMap.get("buyer_id"));
				balanceParams.put("payment_way_key", "ZFFS_08");
				balanceParams.put("amount", reward_gold + "");
				balanceParams.put("out_trade_no", paramsMap.get("out_trade_no"));
				balanceParams.put("out_trade_body", paramsMap.get("out_trade_body"));
				balanceParams.put("detail", "平台返利");
				tradeService.balancePay(balanceParams, result);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberAssetFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
			String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
			List<String> list = StringUtil.str2List(member_id, ",");
			if (list.size() > 1) {
				paramsMap.remove("member_id");
				paramsMap.put("member_id_in", list);
			}
			FDMemberAsset fDMemberAsset = financeDao.selectFDMemberAsset(paramsMap);
			if (fDMemberAsset == null) {
				throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
			}
			Map<String, Object> resultData = new HashMap<String, Object>();
			resultData.put("member_id", fDMemberAsset.getMember_id());
			resultData.put("cash_balance", fDMemberAsset.getCash_balance() + "");
			resultData.put("cash_froze", fDMemberAsset.getCash_froze() + "");
			// 消费者计算实际的礼券余额
			//BigDecimal voucher_balance = fDMemberAsset.getVoucher_balance();
			if (fDMemberAsset.getMember_type_key().equals(Constant.MEMBER_TYPE_CONSUMER)) {
				//voucher_balance = new BigDecimal("0");
				/*
				 * String member_service_url =
				 * PropertiesConfigUtil.getProperty("member_service_url");
				 * Map<String, String> reqParams = new HashMap<String,
				 * String>(); Map<String, Object> bizParams = new
				 * HashMap<String, Object>(); reqParams.put("service",
				 * "member.memberInfoFindByMemberId");
				 * bizParams.put("member_id", member_id);
				 * bizParams.put("member_type_key", "consumer");
				 * reqParams.put("params", FastJsonUtil.toJson(bizParams));
				 * String resultStr = HttpClientUtil.post(member_service_url,
				 * reqParams); Map<String, Object> resultMap =
				 * FastJsonUtil.jsonToMap(resultStr); if (((String)
				 * resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				 * Map<String, Object> dateMap = (Map<String, Object>)
				 * resultMap.get("data"); String mobile =
				 * (StringUtil.formatStr(dateMap.get("mobile"))); if
				 * (StringUtils.isNotEmpty(mobile)) { voucher_balance = new
				 * BigDecimal(financeSyncService.giftRechargeBalanceFind(mobile)
				 * ); } } else { voucher_balance = new BigDecimal("0"); }
				 */

			} else if (fDMemberAsset.getMember_type_key().equals(Constant.MEMBER_TYPE_STORES)) {
				// 因为只有门店类型的会员才会有佣金 所以另外写这个逻辑。
//				Map<String, Object> tempMap = new HashMap<String, Object>();
//				tempMap.put("store_id", member_id);
//				Map<String, Object> tempCashCommissionMap = financeDao.selectStoresCashCommission(tempMap);
//				if (null == tempCashCommissionMap) {
//					resultData.put("commission_cash", "0");// 佣金金额
//				} else {
//					resultData.put("commission_cash", tempCashCommissionMap.get("commission_cash"));// 佣金金额
//				}
				 
				//查询店东总佣金
				Map<String, String> reqParams = new HashMap<>();
				Map<String, Object> bizParams = new HashMap<>();
				String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
				reqParams.put("service", "order.fgOrderCommissionToAmountMemberTotal");
				bizParams.put("stores_id", member_id);
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				String resultStr = HttpClientUtil.postShort(order_service_url, reqParams);
				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
				}
				Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
				if(data.get("totalAmount") != null){
					String totalAmount = data.get("totalAmount").toString();
					resultData.put("totalAmount", totalAmount);
				}else{
					resultData.put("totalAmount", "0");
				}
			}
			// modify by dingshuo 2016-10-11 添加信用余额，数据库暂时没有值
			resultData.put("credit_balance", "0");
			//resultData.put("voucher_balance", voucher_balance.intValue() + "");
			//resultData.put("bonus", fDMemberAsset.getBonus() + "");
			resultData.put("gold", fDMemberAsset.getGold() + "");
			resultData.put("experience", fDMemberAsset.getExperience() + "");
			result.setResultData(resultData);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberAssetListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
			String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
			List<String> list = StringUtil.str2List(member_id, ",");
			if (list.size() > 1) {
				paramsMap.remove("member_id");
				paramsMap.put("member_id_in", list);
			}
			List<FDMemberAsset> fDMemberAssetList = financeDao.selectFDMemberAssetList(paramsMap);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (FDMemberAsset fDMemberAsset : fDMemberAssetList) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("member_id", fDMemberAsset.getMember_id());
				tempMap.put("cash_balance", fDMemberAsset.getCash_balance() + "");
				tempMap.put("gold", fDMemberAsset.getGold() + "");
				resultList.add(tempMap);
			}
			Map<String, Object> resultData = new HashMap<String, Object>();
			resultData.put("list", resultList);
			result.setResultData(resultData);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberUsableCashBalanceFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
			FDMemberAsset fDMemberAsset = financeDao.selectFDMemberAsset(paramsMap);
			if (fDMemberAsset == null) {
				throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
			}
			Map<String, Object> resultData = new HashMap<String, Object>();
			BigDecimal cash_balance = fDMemberAsset.getCash_balance();
			BigDecimal cash_froze = fDMemberAsset.getCash_froze();
			resultData.put("usable_cash_balance", MoneyUtil.moneySub(cash_balance, cash_froze) + "");
			result.setResultData(resultData);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberCashLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
			List<Map<String, Object>> list = financeDao.selectFDMemberCashLog(paramsMap);
			if (list == null || list.size() == 0) {
				throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
			}
			List<Map<String, String>> resultList = new ArrayList<>();
			for (Map<String, Object> map : list) {
				Map<String, String> tempMap = new HashMap<>();
				Object commission_cash = map.get("commission_cash");
				Object amount = map.get("amount");
				if (commission_cash != null && StringUtil.isNotBlank(commission_cash.toString())) {
					tempMap.put("detail", "佣金转零钱");
					tempMap.put("amount", map.get("commission_cash").toString());
					tempMap.put("category", "income");
				} else if(amount != null && StringUtil.isNotBlank(amount.toString())){
					if (map.get("remark") != null && StringUtil.isNotBlank(map.get("remark").toString())) {
						tempMap.put("category", map.get("category") + "");
						tempMap.put("amount", map.get("amount") + "");
						Map<String, Object> remarkMap = FastJsonUtil.jsonToMap(map.get("remark").toString());
						tempMap.put("transaction_no", StringUtil.formatStr(remarkMap.get("transaction_no")));
						tempMap.put("detail", StringUtil.formatStr(remarkMap.get("detail")));
						tempMap.put("payment_way_key", StringUtil.formatStr(remarkMap.get("payment_way_key")));
						tempMap.put("business_type_key", StringUtil.formatStr(remarkMap.get("business_type_key")));
						tempMap.put("transaction_member_name",
								StringUtil.formatStr(remarkMap.get("transaction_member_name")));
						tempMap.put("transaction_member_contact",
								StringUtil.formatStr(remarkMap.get("transaction_member_contact")));
					} else {
						tempMap.put("transaction_no", "");
						tempMap.put("detail", "");
						tempMap.put("payment_way_key", "");
						tempMap.put("business_type_key", "");
						tempMap.put("transaction_member_name", "");
						tempMap.put("transaction_member_contact", "");
					}
				}
				tempMap.put("tracked_date",
						DateUtil.date2Str((Date) map.get("tracked_date"), DateUtil.fmt_yyyyMMddHHmmss));
				resultList.add(tempMap);
			}
			Map<String, Object> resultDateMap = new HashMap<>();
			resultDateMap.put("list", resultList);
			result.setResultData(resultDateMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void memberCashLogListFindNew(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
			List<FDMemberCashLog> list = financeDao.selectFDMemberCashLogNew(paramsMap);
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			for (FDMemberCashLog log : list) {
				Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put("category", log.getCategory());
				tempMap.put("tracked_date", DateUtil.date2Str(log.getTracked_date(), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("amount", log.getAmount() + "");
				// 解析备注信息
				String remark = log.getRemark();
				if (!StringUtils.isEmpty(remark)) {
					Map<String, Object> remarkMap = FastJsonUtil.jsonToMap(remark);
					tempMap.put("transaction_no", StringUtil.formatStr(remarkMap.get("transaction_no")));
					tempMap.put("detail", StringUtil.formatStr(remarkMap.get("detail")));
					tempMap.put("payment_way_key", StringUtil.formatStr(remarkMap.get("payment_way_key")));
					tempMap.put("business_type_key", StringUtil.formatStr(remarkMap.get("business_type_key")));
					tempMap.put("transaction_member_name",
							StringUtil.formatStr(remarkMap.get("transaction_member_name")));
					tempMap.put("transaction_member_contact",
							StringUtil.formatStr(remarkMap.get("transaction_member_contact")));
				} else {
					tempMap.put("transaction_no", "");
					tempMap.put("detail", "");
					tempMap.put("payment_way_key", "");
					tempMap.put("business_type_key", "");
					tempMap.put("transaction_member_name", "");
					tempMap.put("transaction_member_contact", "");
				}
				resultList.add(tempMap);
			}
			Map<String, Object> resultDateMap = new HashMap<String, Object>();
			resultDateMap.put("list", resultList);
			result.setResultData(resultDateMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}


	@Override
	public void memberPointLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
			List<FDMemberPointLog> list = financeDao.selectFDMemberPointLog(paramsMap);
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			for (FDMemberPointLog log : list) {
				Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put("category", log.getCategory());
				tempMap.put("tracked_date", DateUtil.date2Str(log.getTracked_date(), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("amount", log.getAmount() + "");
				// 解析备注信息
				String remark = log.getRemark();
				if (!StringUtils.isEmpty(remark)) {
					Map<String, Object> remarkMap = FastJsonUtil.jsonToMap(remark);
					tempMap.put("transaction_no", StringUtil.formatStr(remarkMap.get("transaction_no")));
					tempMap.put("detail", StringUtil.formatStr(remarkMap.get("detail")));
					tempMap.put("payment_way_key", StringUtil.formatStr(remarkMap.get("payment_way_key")));
					tempMap.put("business_type_key", StringUtil.formatStr(remarkMap.get("business_type_key")));
					tempMap.put("transaction_member_name",
							StringUtil.formatStr(remarkMap.get("transaction_member_name")));
					tempMap.put("transaction_member_contact",
							StringUtil.formatStr(remarkMap.get("transaction_member_contact")));
				} else {
					tempMap.put("transaction_no", "");
					tempMap.put("detail", "");
					tempMap.put("payment_way_key", "");
					tempMap.put("business_type_key", "");
					tempMap.put("transaction_member_name", "");
					tempMap.put("transaction_member_contact", "");
				}
				resultList.add(tempMap);
			}
			Map<String, Object> resultDateMap = new HashMap<String, Object>();
			resultDateMap.put("list", resultList);
			result.setResultData(resultDateMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	@Override
	public void memberGoldLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
			List<FDMemberGoldLog> list = financeDao.selectFDMemberGoldLog(paramsMap);
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			for (FDMemberGoldLog log : list) {
				Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put("category", log.getCategory());
				tempMap.put("tracked_date", DateUtil.date2Str(log.getTracked_date(), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("amount", log.getAmount() + "");
				// 解析备注信息
				String remark = log.getRemark();
				if (!StringUtils.isEmpty(remark)) {
					Map<String, Object> remarkMap = FastJsonUtil.jsonToMap(remark);
					tempMap.put("transaction_no", StringUtil.formatStr(remarkMap.get("transaction_no")));
					tempMap.put("detail", StringUtil.formatStr(remarkMap.get("detail")));
					tempMap.put("payment_way_key", StringUtil.formatStr(remarkMap.get("payment_way_key")));
					tempMap.put("business_type_key", StringUtil.formatStr(remarkMap.get("business_type_key")));
					tempMap.put("transaction_member_name",
							StringUtil.formatStr(remarkMap.get("transaction_member_name")));
					tempMap.put("transaction_member_contact",
							StringUtil.formatStr(remarkMap.get("transaction_member_contact")));
				} else {
					tempMap.put("transaction_no", "");
					tempMap.put("detail", "");
					tempMap.put("payment_way_key", "");
					tempMap.put("business_type_key", "");
					tempMap.put("transaction_member_name", "");
					tempMap.put("transaction_member_contact", "");
				}
				resultList.add(tempMap);
			}
			Map<String, Object> resultDateMap = new HashMap<String, Object>();
			resultDateMap.put("list", resultList);
			result.setResultData(resultDateMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberVoucherLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
			List<FDMemberVoucherLog> list = financeDao.selectFDMemberVoucherLog(paramsMap);
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			for (FDMemberVoucherLog log : list) {
				Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put("category", log.getCategory());
				tempMap.put("tracked_date", DateUtil.date2Str(log.getTracked_date(), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("amount", log.getAmount() + "");
				// 解析备注信息
				String remark = log.getRemark();
				if (!StringUtils.isEmpty(remark)) {
					Map<String, Object> remarkMap = FastJsonUtil.jsonToMap(remark);
					tempMap.put("transaction_no", StringUtil.formatStr(remarkMap.get("transaction_no")));
					tempMap.put("detail", StringUtil.formatStr(remarkMap.get("detail")));
					tempMap.put("payment_way_key", StringUtil.formatStr(remarkMap.get("payment_way_key")));
					tempMap.put("business_type_key", StringUtil.formatStr(remarkMap.get("business_type_key")));
					tempMap.put("transaction_member_name",
							StringUtil.formatStr(remarkMap.get("transaction_member_name")));
					tempMap.put("transaction_member_contact",
							StringUtil.formatStr(remarkMap.get("transaction_member_contact")));
				} else {
					tempMap.put("transaction_no", "");
					tempMap.put("detail", "");
					tempMap.put("payment_way_key", "");
					tempMap.put("business_type_key", "");
					tempMap.put("transaction_member_name", "");
					tempMap.put("transaction_member_contact", "");
				}
				resultList.add(tempMap);
			}
			Map<String, Object> resultDateMap = new HashMap<String, Object>();
			resultDateMap.put("list", resultList);
			result.setResultData(resultDateMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberPointEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 如果存在则加5分 如果不存在则在其基础上加5分
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id","member_type_key", "point_values", "booking_mark" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			String member_id = paramsMap.get("member_id") + "";
			int point_values = Integer.parseInt(paramsMap.get("point_values").toString());

			// 向资产表加积分
			tempMap.put("member_id", member_id);
			tempMap.put("trade_bonus", point_values);
			financeDao.updateFDMemberAsset(tempMap);
			
			// 把记录添加到一条记录
			tempMap.clear();
			tempMap.put("log_id", IDUtil.getUUID());
			tempMap.put("member_type_key", paramsMap.get("member_type_key") + "");
			tempMap.put("member_id", member_id);
			tempMap.put("category", paramsMap.get("booking_mark") + "");
			
			tempMap.put("pre_balance", "0");
			tempMap.put("amount", point_values);
			tempMap.put("balance", "0");
			
			tempMap.put("tracked_date", new Date());
			tempMap.put("remark", paramsMap.get("remark") + "");
			financeDao.insertFdMemberPointLog(tempMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberPointFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		FDMemberAsset memberPoint = financeDao.selectFDMemberAsset(paramsMap);
		if (memberPoint != null) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			//tempMap.put("point_values", memberPoint.getBonus());
			result.setResultData(tempMap);
		}
	}

	@Override
	public void storeCashCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "transaction_date" });
			Map<String, Object> resultMap = financeDao.selectStoreCashierCount(paramsMap);
			resultMap.put("in_sum", resultMap.get("in_sum") == null ? "0.00" : resultMap.get("in_sum") + "");
			resultMap.put("in_case_sum",
					resultMap.get("in_case_sum") == null ? "0.00" : resultMap.get("in_case_sum") + "");
			resultMap.put("num_count", resultMap.get("num_count") + "");
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void storeVoucherBillCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });

			String business_type_key = StringUtil.formatStr(paramsMap.get("business_type_key"));
			if (!"".equals(business_type_key)) {
				List<String> list = StringUtil.str2List(business_type_key, ",");
				if (list.size() > 1) {
					paramsMap.remove("business_type_key");
					paramsMap.put("business_type_in", list);
				}
			}
			Map<String, Object> countsMap = financeDao.selectFDVoucherDailyAccountStoreCount(paramsMap);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("count_num", "0");
			if (null != countsMap) {
				resultMap.put("count_num", countsMap.get("count_num") + "");
			}
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void storeVoucherRewardAccountCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
			paramsMap.put("transaction_date_start", DateUtil.getFormatDate(DateUtil.fmt_yyyyMM) + "-01");
			paramsMap.put("transaction_date_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
			Map<String, Object> countsMap = financeDao.selectFDStoreVoucherRewardAccountCount(paramsMap);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("count_num", "0");
			if (null != countsMap) {
				resultMap.put("count_num", countsMap.get("count_num") + "");
			}
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void storesCashierCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "flow_no", "stores_id", "amount", "discount_amount",
					"pay_amount", "payment_way_key", "cashier_id" });
			FDStoresCashier fDStoreCashier = new FDStoresCashier();
			BeanConvertUtil.mapToBean(fDStoreCashier, paramsMap);
			fDStoreCashier.setFlow_id(IDUtil.getUUID());
			fDStoreCashier.setCreated_date(new Date());
			if (paramsMap.get("reduce_amount") == null) {
				fDStoreCashier.setReduce_amount(new BigDecimal("0.00"));
			}
			if (paramsMap.get("rebate_cash") == null) {
				fDStoreCashier.setRebate_cash(new BigDecimal("0.00"));
			}
			if (paramsMap.get("reward_gold") == null) {
				fDStoreCashier.setReward_gold(0);
			}
			financeDao.insertFDStoresCashier(fDStoreCashier);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void storesCashierFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
			List<FDStoresCashier> storeCashierList = financeDao.selectFDStoresCashier(paramsMap);
			List<Map<String, Object>> resultlist = new LinkedList<Map<String, Object>>();
			for (FDStoresCashier storeCashier : storeCashierList) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("flow_no", storeCashier.getFlow_no());
				resultMap.put("amount", storeCashier.getAmount() + "");
				resultMap.put("discount_amount", storeCashier.getDiscount_amount() + "");
				resultMap.put("reduce_amount", storeCashier.getReduce_amount() + "");
				resultMap.put("reward_voucher", "0.00");
				resultMap.put("rebate_cash", storeCashier.getRebate_cash() + "");
				resultMap.put("reward_gold", storeCashier.getReward_gold() + "");
				resultMap.put("pay_amount", storeCashier.getPay_amount() + "");
				resultMap.put("payment_way_key", storeCashier.getPayment_way_key());
				resultMap.put("json_data", StringUtil.formatStr(storeCashier.getJson_data()));
				resultMap.put("cashier_id", storeCashier.getCashier_id());
				resultMap.put("created_date",
						DateUtil.date2Str(storeCashier.getCreated_date(), DateUtil.fmt_yyyyMMddHHmmss));
				resultlist.add(resultMap);
			}
			result.setResultData(resultlist);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberCouponCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key", "sku_id", "item_id",
					"sku_code", "title", "expired_date", "status" });
			FdMemberAssetCoupon fdMemberAssetCoupon = new FdMemberAssetCoupon();
			BeanConvertUtil.mapToBean(fdMemberAssetCoupon, paramsMap);
			fdMemberAssetCoupon.setAsset_id(IDUtil.getUUID());
			fdMemberAssetCoupon.setCreated_date(new Date());
			financeDao.insertFdMemberAssetCoupon(fdMemberAssetCoupon);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberCouponStatusEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "sku_code", "status" });
			financeDao.updateFdMemberAssetCoupon(paramsMap);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberCouponFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			List<FdMemberAssetCoupon> fdMemberAssetCouponList = financeDao.selectFdMemberAssetCoupon(paramsMap);
			List<String> doc_ids = new ArrayList<String>();
			for (FdMemberAssetCoupon fdMemberAssetCoupon : fdMemberAssetCouponList) {
				Map<String, Object> temp = new HashMap<String, Object>();
				temp.put("item_id", fdMemberAssetCoupon.getItem_id());
				temp.put("sku_id", fdMemberAssetCoupon.getSku_id());
				temp.put("sku_code", fdMemberAssetCoupon.getSku_code());
				temp.put("title", fdMemberAssetCoupon.getTitle());
				temp.put("status", fdMemberAssetCoupon.getStatus());
				temp.put("expired_date",
						DateUtil.date2Str(fdMemberAssetCoupon.getExpired_date(), DateUtil.fmt_yyyyMMddHHmmss));
				temp.put("pic_path", fdMemberAssetCoupon.getPic_path());
				temp.put("created_date",
						DateUtil.date2Str(fdMemberAssetCoupon.getCreated_date(), DateUtil.fmt_yyyyMMddHHmmss));
				temp.put("remark", StringUtil.formatStr(fdMemberAssetCoupon.getRemark()));
				// 查询商品新
				String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
				Map<String, String> reqParams = new HashMap<String, String>();
				Map<String, Object> paramMap = new HashMap<String, Object>();
				reqParams.put("service", "goods.couponDetailFind");
				paramMap.put("sku_id", fdMemberAssetCoupon.getSku_id());
				reqParams.put("params", FastJsonUtil.toJson(paramMap));
				String resultStr = HttpClientUtil.post(goods_service_url, reqParams);
				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"),
							(String) resultMap.get("error_msg"));
				}
				Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
				temp.put("category_id", StringUtil.formatStr(data.get("category_id")));
				temp.put("coupon_prop", StringUtil.formatStr(data.get("coupon_prop")));
				temp.put("stores_name", StringUtil.formatStr(data.get("stores_name")));
				temp.put("logo_pic_path", StringUtil.formatStr(data.get("logo_pic_path")));
				temp.put("limit_amount", StringUtil.formatStr(data.get("limit_amount")));
				resultList.add(temp);
				doc_ids.addAll(StringUtil.str2List(StringUtil.formatStr(data.get("logo_pic_path")), ","));
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("doc_url", docUtil.imageUrlFind(doc_ids));
			map.put("list", resultList);
			result.setResultData(map);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberIdBySkuCodeFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "sku_code" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("sku_code", paramsMap.get("sku_code"));
			List<FdMemberAssetCoupon> fdMemberAssetCouponList = financeDao.selectFdMemberAssetCoupon(tempMap);
			if (fdMemberAssetCouponList.size() == 0) {
				throw new BusinessException(RspCode.COUPON_NO_EXIST, RspCode.MSG.get(RspCode.COUPON_NO_EXIST));
			}
			tempMap.clear();
			FdMemberAssetCoupon fdMemberAssetCoupon = fdMemberAssetCouponList.get(0);
			tempMap.put("member_id", fdMemberAssetCoupon.getMember_id());
			result.setResultData(tempMap);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberCouponCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "status" });
			Map<String, Object> countsMap = financeDao.selectFdMemberAssetCouponCount(paramsMap);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("count_num", "0");
			if (null != countsMap) {
				resultMap.put("count_num", countsMap.get("count_num") + "");
			}
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void disabledCouponStatusUpdate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
					DateUtil.fmt_yyyyMMddHHmmss, 3, -1));
			tempMap.put("lt_date", new Date());
			financeDao.updateDisabledCouponAssetStatus(tempMap);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void tradeConsumerListForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
			Map<String, Object> tempConmuserMap = new HashMap<String, Object>();
			paramsMap.put("transaction_date_start", DateUtil.getFormatDate(DateUtil.fmt_yyyyMM) + "-01");
			paramsMap.put("transaction_date_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
			List<Map<String, Object>> tradeConsumerList = financeDao.selectTradeConsumerListForStores(paramsMap);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> tm : tradeConsumerList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("member_id", tm.get("member_id"));
				map.put("name", StringUtil.formatStr(tm.get("name")));
				map.put("contact", StringUtil.formatStr(tm.get("contact")));
				map.put("date", DateUtil.date2Str((Date) tm.get("date"), DateUtil.fmt_yyyyMMddHHmmss));
				map.put("type", "收银转帐");
				map.put("amount", StringUtil.formatStr(tm.get("amount")));
				resultList.add(map);
				tempConmuserMap.put(tm.get("member_id") + "", map);
			}
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("list", resultList);
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void tradeConsumerListForMemberList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
			Map<String, Object> resultMap = new HashMap<String, Object>();
			Map<String, Object> tempConmuserMap = new HashMap<String, Object>();
			List<Map<String, Object>> tradeConsumerList = financeDao.selectTradeConsumerListForStores(paramsMap);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> tm : tradeConsumerList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("member_id", tm.get("member_id"));
				map.put("name", StringUtil.formatStr(tm.get("name")));
				map.put("contact", StringUtil.formatStr(tm.get("contact")));
				map.put("date", DateUtil.date2Str((Date) tm.get("date"), DateUtil.fmt_yyyyMMddHHmmss));
				map.put("type", "收银转帐");
				map.put("amount", StringUtil.formatStr(tm.get("amount")));
				resultList.add(map);
				tempConmuserMap.put(tm.get("member_id") + "", map);
			}
			resultMap.clear();
			resultMap.put("list", resultList);
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void consumerVoucherBill(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = new PageParam();
			pageParam.setPage_size(100);
			financeSyncService.giftRechargeLogFind(paramsMap, pageParam, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void storeVoucherBill(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
			List<FDVoucherDailyAccountMember> list = financeDao.selectFDVoucherDailyAccountStore(paramsMap);
			List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
			for (FDVoucherDailyAccountMember daily : list) {
				Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put("transaction_no", daily.getTransaction_no());
				tempMap.put("transaction_date",
						DateUtil.date2Str(daily.getTransaction_date(), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("detail", daily.getDetail());
				tempMap.put("amount", daily.getAmount().toString());
				tempMap.put("booking_mark", daily.getBooking_mark());
				tempMap.put("payment_way_key", daily.getPayment_way_key());
				tempMap.put("business_type_key", daily.getBusiness_type_key());
				resultList.add(tempMap);
			}
			result.setResultData(resultList);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void handleStoresUnfreezeBalanceForConsumer(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "stores_id", "stores_name", "consumer_id" });
			// 查询消费者余额
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("member_id", paramsMap.get("consumer_id"));
			tempMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
			FDMemberAsset consumer_memberAsset = financeDao.selectFDMemberAsset(tempMap);
			BigDecimal consumer_cash_froze = consumer_memberAsset.getCash_froze();
			if (consumer_cash_froze.compareTo(BigDecimal.ZERO) > 0) {
				// 检测店东可用余额是否足够
				tempMap.clear();
				tempMap.put("member_id", paramsMap.get("stores_id"));
				tempMap.put("member_type_key", Constant.MEMBER_TYPE_STORES);
				FDMemberAsset stores_memberAsset = financeDao.selectFDMemberAsset(tempMap);
				BigDecimal stores_cash_froze = stores_memberAsset.getCash_froze();
				BigDecimal stores_cash_balance = stores_memberAsset.getCash_balance();
				// 店东可用余额
				BigDecimal usable_cash_balance = MoneyUtil.moneySub(stores_cash_balance, stores_cash_froze);
				if (MoneyUtil.moneySub(usable_cash_balance, consumer_cash_froze).compareTo(BigDecimal.ZERO) < 0) {
					throw new BusinessException(RspCode.TRADE_AMOUNT_ERROR, "您的可用余额不足以解冻消费者的冻结金额");
				}

				// 冻结店东的余额
				tempMap.clear();
				tempMap.put("data_source", paramsMap.get("data_source"));
				tempMap.put("member_id", paramsMap.get("stores_id"));
				tempMap.put("member_type_key", Constant.MEMBER_TYPE_STORES);
				tempMap.put("amount", consumer_cash_froze + "");
				tempMap.put("detail", "余额冻结");
				tradeService.balanceFreeze(tempMap, result);

				// 解冻消费者冻结余额
				tempMap.clear();
				tempMap.put("data_source", paramsMap.get("data_source"));
				tempMap.put("member_id", paramsMap.get("consumer_id"));
				tempMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
				tempMap.put("amount", consumer_cash_froze + "");
				tempMap.put("detail", "余额解冻,操作门店【" + paramsMap.get("stores_name") + "】");
				tempMap.put("operator", paramsMap.get("stores_id"));
				tempMap.put("operator_type", "stores");
				tradeService.balanceUnFreeze(tempMap, result);

				// 推送消息
				Map<String, String> extrasMap = new HashMap<String, String>();
				extrasMap.put("type", Constant.PUSH_MESSAGE_TYPE_02);
				extrasMap.put("consumer_id", paramsMap.get("consumer_id") + "");
				String msg = "您已成功解冻" + consumer_cash_froze + "元\n操作门店【" + paramsMap.get("stores_name") + "】";
				String member_id = paramsMap.get("consumer_id") + "";
				notifyService.pushAppMessage(member_id, msg, extrasMap);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void billCheckLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "bill_type", "bill_date" });
		List<Map<String, Object>> list = financeDao.selectBillCheckLog(paramsMap);
		for (Map<String, Object> log : list) {
			log.put("transaction_date",
					DateUtil.date2Str((Date) log.get("transaction_date"), DateUtil.fmt_yyyyMMddHHmmss));
			log.put("buyer_member_type", StringUtil.formatStr(log.get("buyer_member_type")));
			log.put("buyer_name", StringUtil.formatStr(log.get("buyer_name")));
			log.put("buyer_contact", StringUtil.formatStr(log.get("buyer_contact")));

			log.put("seller_member_type", StringUtil.formatStr(log.get("seller_member_type")));
			log.put("seller_name", StringUtil.formatStr(log.get("seller_name")));
			log.put("seller_contact", StringUtil.formatStr(log.get("seller_contact")));
		}
		Map<String, Object> resultDateMap = new HashMap<String, Object>();
		resultDateMap.put("list", list);
		result.setResultData(resultDateMap);
	}

	@Override
	public void storesCashCommissionLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("store_id", paramsMap.get("member_id"));
		// List<Map<String, Object>> list =
		// financeDao.selectStoresCashCommissionLogList(paramsMap);
		// for (Map<String, Object> log : list) {
		// log.put("created_date", DateUtil.date2Str((Date)
		// log.get("created_date"), DateUtil.fmt_yyyyMMddHHmmss));
		// log.put("commission_cash",
		// StringUtil.formatStr(log.get("commission_cash")));
		// log.put("consumer_mobile",
		// StringUtil.formatStr(log.get("consumer_moblie")));
		// }
		Map<String, Object> resultDateMap = new HashMap<String, Object>();
		// resultDateMap.put("list", list);
		result.setResultData(resultDateMap);
	}

	
	
	/**
	 *  分页查询用户积分列表查询
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	@Override
	public void menberBonusListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		
		List<Map<String, Object>> list = financeDao.selectFDMemberAssetPageList(paramsMap);
		Map<String, Object> resultDateMap = new HashMap<String, Object>();
		resultDateMap.put("list", list);
		result.setResultData(resultDateMap);
	}
	
	@Override
	public void storesCashCommissionCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "store_id", "consumer_mobile", "consumer_id" });

			// String store_id = paramsMap.get("store_id") + "";
			// String remark = paramsMap.get("remark") + "";
			// String consumer_id = paramsMap.get("consumer_id") + "";
			// String consumer_mobile = paramsMap.get("consumer_mobile") + "";
			// BigDecimal commission_cash = new
			// BigDecimal(paramsMap.get("commission_cash").toString());
			// // 1.验证门店是否存在
			// Map<String, Object> tempMap = new HashMap<String, Object>();
			// tempMap.put("store_id", store_id);
			// Map<String, Object> tempSalesman =
			// financeDao.selectStoresCashCommission(tempMap);
			/*
			 * 这里有两种情况：一种是当门店佣金表中没有对应的门店佣金记录就进行新增操作 一种是当门店佣金表中有了对应的门店佣金记录就进行修改操作
			 * 新增操作：创建一条门店佣金记录，并且把当前传递过来的佣金加入到冻结金额中（这里只会出现正数的佣金），再新增一条日志
			 * 修改操作：根据门店ID来对门店佣金记录进行修改，把传递过来的佣金直接加入到冻结金额中（这里只会出现正数的佣金），再新增一条日志
			 */
			// if (null == tempSalesman) {
			// System.out.println("门店佣金信息不存在");
			// // 2.添加门店佣金记录
			// FDCashCommissionStore fdCashCommissionStore = new
			// FDCashCommissionStore();
			// BeanConvertUtil.mapToBean(fdCashCommissionStore, paramsMap);
			// fdCashCommissionStore.setStore_id(store_id);
			// //fdCashCommissionStore.setUsable_cash(new BigDecimal(0));//可提金额
			// //fdCashCommissionStore.setFroze_cash(commission_cash);//冻结金额
			// fdCashCommissionStore.setUpdated_date(new Date());
			// fdCashCommissionStore.setRemark(remark);
			// financeDao.insertStoresCashCommission(fdCashCommissionStore);
			// // 3.添加门店佣金记录日记
			// Map<String, Object> logMap = new HashMap<String, Object>();
			// logMap.put("log_id", IDUtil.getUUID());
			// logMap.put("store_id", store_id);
			// logMap.put("consumer_id", consumer_id);
			// logMap.put("consumer_mobile", consumer_mobile);
			// logMap.put("commission_cash", commission_cash);
			// logMap.put("created_date", new Date());
			// logMap.put("remark",
			// "remark:新增门店佣金记录,store_id:"+store_id+",consumer_mobile:"+consumer_mobile+",commission_cash:"+commission_cash);
			// financeDao.insertStoresCashCommissionLog(logMap);
			// } else {
			// ValidateUtil.validateParams(paramsMap, new String[] {
			// "commission_cash" });
			// System.out.println("门店佣金信息已存在");
			// // 2.修改门店佣金记录
			// Map<String, Object> fdCashCommissionStore = new HashMap<String,
			// Object>();
			// fdCashCommissionStore.put("store_id", store_id);
			// fdCashCommissionStore.put("commission_cash", commission_cash);
			// financeDao.updateStoresCashCommission(fdCashCommissionStore);
			// // 3.添加门店佣金记录日记
			// Map<String, Object> logMap = new HashMap<String, Object>();
			// logMap.put("log_id", IDUtil.getUUID());
			// logMap.put("store_id", store_id);
			// logMap.put("consumer_id", consumer_id);
			// logMap.put("consumer_mobile", consumer_mobile);
			// logMap.put("commission_cash", commission_cash);
			// logMap.put("created_date", new Date());
			// logMap.put("remark",
			// "remark:修改门店佣金记录,store_id:"+store_id+",consumer_mobile:"+consumer_mobile+",commission_cash:"+commission_cash);
			// financeDao.insertStoresCashCommissionLog(logMap);
			// }

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void storesCashCommissionEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "store_id", "consumer_mobile", "consumer_id", "commission_cash" });
			String store_id = paramsMap.get("store_id") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String consumer_mobile = paramsMap.get("consumer_mobile") + "";
			BigDecimal commission_cash = new BigDecimal(paramsMap.get("commission_cash").toString());
			/*
			 * 因为是撤销返款 所以这里传递进来的佣金是负数的 这个一定要注意
			 */
			// 1.验证门店是否存在
			// Map<String, Object> tempMap = new HashMap<String, Object>();
			// tempMap.put("store_id", store_id);
			// Map<String, Object> tempSalesman =
			// financeDao.selectStoresCashCommission(tempMap);
			// if (null == tempSalesman) {
			// throw new BusinessException(RspCode.ALIPAY_ERROR, "门店佣金不存在");
			// }
			// // 2.修改门店佣金记录
			// Map<String, Object> fdCashCommissionStore = new HashMap<String,
			// Object>();
			// fdCashCommissionStore.put("store_id", store_id);
			// fdCashCommissionStore.put("commission_cash", commission_cash);
			// financeDao.updateStoresCashCommission(fdCashCommissionStore);
			// // 3.添加门店佣金记录日记
			// Map<String, Object> logMap = new HashMap<String, Object>();
			// logMap.put("log_id", IDUtil.getUUID());
			// logMap.put("store_id", store_id);
			// logMap.put("consumer_id", consumer_id);
			// logMap.put("consumer_mobile", consumer_mobile);
			// logMap.put("commission_cash", commission_cash);
			// logMap.put("created_date", new Date());
			// logMap.put("remark",
			// "remark:修改门店佣金记录,store_id:"+store_id+",consumer_mobile:"+consumer_mobile+",commission_cash:"+commission_cash);
			// financeDao.insertStoresCashCommissionLog(logMap);
			//
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void transactionsnoByOuttradnoFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "order_id" });
			String order_id = paramsMap.get("order_id") + "";
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", order_id);
			
			Map<String, Object> params = new HashMap<String, Object>();
			Calendar cal_start = Calendar.getInstance();  
			cal_start.set(Calendar.HOUR_OF_DAY, 0);  
			cal_start.set(Calendar.SECOND, 0);  
			cal_start.set(Calendar.MINUTE, 0);  
			cal_start.set(Calendar.MILLISECOND, 0); 
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			params.put("transaction_date", format.format(cal_start.getTime()));
			
			Map<String, Object> tempSalesman = new HashMap<String, Object>();
			tempSalesman = financeDao.selectTransactionsnoByOuttradno(tempMap);
			tempMap.clear();
			if(tempSalesman != null){
				tempMap.put("transaction_no", StringUtil.formatStr(tempSalesman.get("transaction_no")));
				tempMap.put("payment_way_key", StringUtil.formatStr(tempSalesman.get("payment_way_key")));
				
			}else{
				tempMap.put("transaction_no", "");
				tempMap.put("payment_way_key", "");
			}
			
			result.setResultData(tempMap);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public void transmatic (Map<String, Object> paramsMap, ResultData result) throws Exception {
		String resultStr = (String)paramsMap.get("data");
		List<Map<String, Object>> resultList = FastJsonUtil.jsonToList(resultStr);
		Map<String, Object> mapParams = new HashMap<>();
		List<Map<String, Object>> succList = new ArrayList<>();
		for (Map<String, Object> map : resultList) {
			mapParams.put("member_id", map.get("stores_id"));
			mapParams.put("trade_cash_balance", map.get("sum_amount"));
			Integer updateFlag = financeDao.updateFDMemberAsset(mapParams);
			if (updateFlag > 0) {
				succList.add(map);
				mapParams.clear();
				mapParams.put("store_id", map.get("stores_id"));
				mapParams.put("log_id", IDUtil.getUUID());
				mapParams.put("booking_mark", "transfer");//佣金转入
				mapParams.put("created_date", new Date());
				mapParams.put("commission_cash", map.get("sum_amount"));
				mapParams.put("remark", "自动转零钱成功");
				financeDao.insertStoresCashCommissionLog(mapParams);
				mapParams.clear();
			} else {
				logger.info("佣金转入失败的店东="+map.get("stores_id")+",    转入的佣金金额="+map.get("sum_amount"));
			}
		}
		Map<String, Object> resultMap = new HashMap<>();
		if (succList != null && succList.size() != 0) {
			resultMap.put("succData", succList);
		}
		result.setResultData(resultMap);
	}

	@Override
	public void memberAssetsInformation(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
		List<String> list = StringUtil.str2List(member_id, ",");
		if (list.size() > 1) {
			paramsMap.remove("member_id");
			paramsMap.put("member_id_in", list);
		}
		List<Map<String,Object>> fdMemberAssetList = financeDao.selectFDMemberAssetPageList(paramsMap);
		result.setResultData(fdMemberAssetList);
	}

	/**
	 * 和包用户领取现金券
	 */
	@Override
	public void handleHebaoGetCashCoupon(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try{
			ValidateUtil.validateParams(paramsMap, new String[] {"member_id", "cashCouponCode", "cashcouponValue"});
			//查询是否领取过该优惠券
			Map<String, Object> tempMap=new HashMap<String, Object>();
			tempMap.put("member_id", paramsMap.get("member_id"));
			tempMap.put("amount", paramsMap.get("cashcouponValue"));
			List<FDMemberCashCoupon> list=memberCashCouponDao.selectFDMemberCashCouponBy(tempMap);
			
			if(list!=null && list.size()>0){
				throw new BusinessException(RspCode.TRADE_EXIST, "重复领取");
			}
			
			Map<String, Object> memberAssetMap=new HashMap<String, Object>();
			memberAssetMap.put("member_id", paramsMap.get("member_id"));
			FDMemberAsset fdMemberAsset=financeDao.selectFDMemberAsset(memberAssetMap);
			if(fdMemberAsset==null){
				throw new BusinessException(RspCode.TRADE_EXIST, "用户不存在!");
			}
			
			//保存现金券
			memberCashCouponDao.insertFDMemberCashCoupon(genareteCashCoupon(paramsMap));
			
			//保存现金券操作日志
			memberCashCouponDao.insertFDMemberCashCouponLog(doFDMemberCashCouponLog(paramsMap, fdMemberAsset));
			
			//同步增加总账户现金券余额
			Map<String, Object>  paramsMap2=new HashMap<String, Object>();
			paramsMap2.put("member_id", paramsMap.get("member_id"));
			if(fdMemberAsset.getCash_coupon()==null){
				paramsMap2.put("cash_coupon", paramsMap.get("cashcouponValue"));
			}else{
				paramsMap2.put("cash_coupon", fdMemberAsset.getCash_coupon().add(new BigDecimal(paramsMap.get("cashcouponValue").toString())));
			}
			financeDao.updateFDMemberAsset(paramsMap2);
			
			result.setResultData("领取现金券成功");
		}catch(Exception e){
			throw e;
		}
	}
	
	
	/**
	 * 获取和包用户已领现金券列表
	 */
	@Override
	public void hebaoCashCouponListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try{
			ValidateUtil.validateParams(paramsMap, new String[] {"member_id"});
			List<FDMemberCashCoupon> cashCouponList=memberCashCouponDao.selectFDMemberCashCouponBy(paramsMap);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			
			for (FDMemberCashCoupon cashCoupon : cashCouponList) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("coupon_no", cashCoupon.getCoupon_no());
				tempMap.put("member_id", cashCoupon.getMember_id());
				tempMap.put("amount", cashCoupon.getAmount());
				tempMap.put("status", cashCoupon.getStatus());
				tempMap.put("created_time", DateUtil.date2Str(cashCoupon.getCreated_time(), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("modified_time", DateUtil.date2Str(cashCoupon.getModified_time(), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("remark", cashCoupon.getRemark());
				resultList.add(tempMap);
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", resultList);
			result.setResultData(map);
			
		}catch(Exception e){
			throw e;
		}
	}
	/**
	 * 和包用户核销现金券
	 */
	@Override
	public void hebaoUseCashCoupon(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] {"seller_id", "cashCouponCode"});
			
			//校验现金券
			Map<String, Object> couponMap=new HashMap<String, Object>();
			couponMap.put("coupon_no", paramsMap.get("cashCouponCode"));
			List<FDMemberCashCoupon> list=memberCashCouponDao.selectFDMemberCashCouponBy(couponMap);
			if(list==null ||list.size()==0 || list.get(0)==null){
				throw new BusinessException(CommonRspCode.USER_NOT_EXIST, "对不起，不存在该现金券码!"); 
			}
			
			if(StringUtil.isEmpty(list.get(0).getMember_id())){
				throw new BusinessException(CommonRspCode.USER_NOT_EXIST, "该现金券存在异常!"); 
			}
			
			if(list.get(0).getStatus()==1){
				throw new BusinessException(CommonRspCode.USER_NOT_EXIST, "该券已使用，不可重复使用!"); 
			}
			
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", CommonConstant.DATA_SOURCE_SJLY_02);
			tempMap.put("order_type_key", Constant.ORDER_TYPE_CASH_COUPON);
			tempMap.put("payment_way_key", Constant.PAYMENT_WAY_50);
			tempMap.put("business_type_key", Constant.BUSINESS_TYPE_CASH_COUPON);
			
			tempMap.put("amount", list.get(0).getAmount()+"");
			tempMap.put("out_trade_no", TradeIDUtil.getTradeNo());
			tempMap.put("out_member_id", list.get(0).getMember_id());
			tempMap.put("in_member_id", paramsMap.get("seller_id"));
			tempMap.put("detail", "用户核销现金券");
			tempMap.put("cashCouponCode", paramsMap.get("cashCouponCode"));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	

	private FDMemberCashCoupon genareteCashCoupon(Map<String, Object> paramsMap){
		FDMemberCashCoupon cashCoupon=new FDMemberCashCoupon();
		cashCoupon.setCoupon_no((String)paramsMap.get("cashCouponCode"));
		cashCoupon.setMember_id((String)paramsMap.get("member_id"));
		cashCoupon.setAmount(new Byte(paramsMap.get("cashcouponValue").toString()));
		Date currentTime=new Date();
		cashCoupon.setCreated_time(currentTime);
		cashCoupon.setModified_time(currentTime);
		cashCoupon.setStatus(new Byte("0"));
		cashCoupon.setRemark("和包用户领取了现金券");
		return cashCoupon;
	}
	
	private FDMemberCashCouponLog doFDMemberCashCouponLog(Map<String, Object> paramsMap,FDMemberAsset fdMemberAsset) throws Exception{
		FDMemberCashCouponLog fdMemberCashCouponLog=new FDMemberCashCouponLog();
		
		BigDecimal preCashCoupon=fdMemberAsset.getCash_coupon();
		
		BigDecimal cashcouponValue=new BigDecimal(paramsMap.get("cashcouponValue").toString());
		if(preCashCoupon==null || preCashCoupon.floatValue()<=0){
			fdMemberCashCouponLog.setPre_balance(new BigDecimal("0"));
			fdMemberCashCouponLog.setBalance(cashcouponValue);
		}else{
			fdMemberCashCouponLog.setPre_balance(preCashCoupon);
			fdMemberCashCouponLog.setBalance(preCashCoupon.add(cashcouponValue));
		}
		
		
		fdMemberCashCouponLog.setLog_id(StringUtil.genarateUUID());
		fdMemberCashCouponLog.setAmount(cashcouponValue);
		fdMemberCashCouponLog.setCategory("income");
		fdMemberCashCouponLog.setMember_type_key("consumer");
		fdMemberCashCouponLog.setMember_id((String)paramsMap.get("member_id"));
		fdMemberCashCouponLog.setRemark("和包用户领取现金券");
		fdMemberCashCouponLog.setTracked_date(new Date());
		fdMemberCashCouponLog.setTransaction_no(TradeIDUtil.getTradeNo());
		return fdMemberCashCouponLog;
	}

	@Override
	public void monthlyGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception {
		// 查询未过期会员的会员等级
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "consumer.consumerVipLevel");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		List<Map<String, Object>> data = (List<Map<String, Object>>) resultMap.get("data");
		List<FDMemberCoupons> list = givingGiftCertificates(data);
		if(list.size() != 0){
			financeDao.insertFdMemberCoupons(list);   
		}
	}

	private List<FDMemberCoupons> givingGiftCertificates(List<Map<String, Object>> data) throws Exception {
		List<FDMemberCoupons> list = new ArrayList<>();
		Date date = new Date();
		Date parseToDate = DateUtil.parseToDate(DateUtil.addDate(DateUtil.date2Str(date, DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 2, 1));
		for (Map<String, Object> map : data) {
			String level = (String) map.get("level");
			String consumer_id =(String) map.get("consumer_id");
			if ("svip_2".equals(level) || "svip_3".equals(level)) {
				FDMemberCoupons fDMemberCoupons = new FDMemberCoupons(); 
				fDMemberCoupons.setCoupons_validity(parseToDate);
				fDMemberCoupons.setCreated_date(date);
				fDMemberCoupons.setMember_id(consumer_id);
				fDMemberCoupons.setCoupons_key("lq_02");
				fDMemberCoupons.setCoupons_mun(1);
				fDMemberCoupons.setIs_activate("Y");
				list.add(fDMemberCoupons);
				FDMemberCoupons fDMemberCoupons2 = new FDMemberCoupons();
				BeanConvertUtil.copyProperties(fDMemberCoupons2, fDMemberCoupons);
				fDMemberCoupons2.setCoupons_key("lq_03");
				list.add(fDMemberCoupons2);
				if("svip_3".equals(level)){
					FDMemberCoupons fDMemberCoupons3 = new FDMemberCoupons();
					BeanConvertUtil.copyProperties(fDMemberCoupons3, fDMemberCoupons);
					fDMemberCoupons3.setCoupons_key("my_01");
					list.add(fDMemberCoupons3);
				}
			} else if ("svip_4".equals(level) || "svip_5".equals(level)) {
				FDMemberCoupons fDMemberCoupons = new FDMemberCoupons();
				fDMemberCoupons.setCoupons_validity(parseToDate);
				fDMemberCoupons.setCreated_date(date);
				fDMemberCoupons.setMember_id(consumer_id);
				fDMemberCoupons.setCoupons_key("lq_05");
				fDMemberCoupons.setCoupons_mun(1);
				fDMemberCoupons.setIs_activate("Y");
				list.add(fDMemberCoupons);
				FDMemberCoupons fDMemberCoupons2 = new FDMemberCoupons();
				BeanConvertUtil.copyProperties(fDMemberCoupons2, fDMemberCoupons);
				fDMemberCoupons2.setCoupons_key("lq_04");
				list.add(fDMemberCoupons2);
				FDMemberCoupons fDMemberCoupons3 = new FDMemberCoupons();
				BeanConvertUtil.copyProperties(fDMemberCoupons3, fDMemberCoupons);
				fDMemberCoupons3.setCoupons_key("my_01");
				fDMemberCoupons3.setCoupons_mun(2);
				if ("svip_5".equals(level)) {
					fDMemberCoupons3.setCoupons_mun(3);
				}
				list.add(fDMemberCoupons3);
			} else if ("svip_6".equals(level)||"svip_7".equals(level)) {
				FDMemberCoupons fDMemberCoupons = new FDMemberCoupons();
				fDMemberCoupons.setCoupons_validity(parseToDate);
				fDMemberCoupons.setCreated_date(date);
				fDMemberCoupons.setMember_id(consumer_id);
				fDMemberCoupons.setCoupons_key("lq_04");
				fDMemberCoupons.setCoupons_mun(1);
				fDMemberCoupons.setIs_activate("Y");
				list.add(fDMemberCoupons);
				FDMemberCoupons fDMemberCoupons2 = new FDMemberCoupons();
				BeanConvertUtil.copyProperties(fDMemberCoupons2, fDMemberCoupons);
				fDMemberCoupons2.setCoupons_key("lq_06");
				list.add(fDMemberCoupons2);
				FDMemberCoupons fDMemberCoupons3 = new FDMemberCoupons();
				BeanConvertUtil.copyProperties(fDMemberCoupons3, fDMemberCoupons);
				fDMemberCoupons3.setCoupons_key("my_01");
				fDMemberCoupons3.setCoupons_mun(3);
				list.add(fDMemberCoupons3);
			}
		}
		return list;
	}
	
	@Override
	public void birthdayGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception {
		// 查询未过期会员的会员等级
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "consumer.consumerVipLevel");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		List<Map<String, Object>> data = (List<Map<String, Object>>) resultMap.get("data");
		List<FDMemberCoupons> list = new ArrayList<>();
		List<FDMemberAsset> memberList = new ArrayList<>();
		Date date = new Date();
		Date parseToDate = DateUtil.parseToDate(DateUtil.addDate(DateUtil.date2Str(date, DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 2, 1));
		for (Map<String, Object> map : data) {
			String level = (String) map.get("level");
			String consumer_id = (String) map.get("consumer_id");
			if ("svip_3".equals(level) || "svip_4".equals(level)) {
				FDMemberCoupons fDMemberCoupons = new FDMemberCoupons();
				fDMemberCoupons.setCoupons_validity(parseToDate);
				fDMemberCoupons.setCreated_date(date);
				fDMemberCoupons.setMember_id(consumer_id);
				fDMemberCoupons.setCoupons_key("lq_07");
				fDMemberCoupons.setCoupons_mun(1);
				fDMemberCoupons.setIs_activate("Y");
				list.add(fDMemberCoupons);
				FDMemberAsset fdMemberAsset = new FDMemberAsset();
				fdMemberAsset.setMember_id(consumer_id);
				fdMemberAsset.setShell(new BigDecimal("66"));
				memberList.add(fdMemberAsset);
			} else if ("svip_5".equals(level)) {
				FDMemberCoupons fDMemberCoupons = new FDMemberCoupons();
				fDMemberCoupons.setCoupons_validity(parseToDate);
				fDMemberCoupons.setCreated_date(date);
				fDMemberCoupons.setMember_id(consumer_id);
				fDMemberCoupons.setCoupons_key("lq_07");
				fDMemberCoupons.setCoupons_mun(2);
				fDMemberCoupons.setIs_activate("Y");
				list.add(fDMemberCoupons);
				FDMemberAsset fdMemberAsset = new FDMemberAsset();
				fdMemberAsset.setMember_id(consumer_id);
				fdMemberAsset.setShell(new BigDecimal("66"));
				memberList.add(fdMemberAsset);
			} else if ("svip_6".equals(level)) {
				FDMemberCoupons fDMemberCoupons = new FDMemberCoupons();
				fDMemberCoupons.setCoupons_validity(parseToDate);
				fDMemberCoupons.setCreated_date(date);
				fDMemberCoupons.setMember_id(consumer_id);
				fDMemberCoupons.setCoupons_key("lq_08");
				fDMemberCoupons.setCoupons_mun(2);
				fDMemberCoupons.setIs_activate("Y");
				list.add(fDMemberCoupons);
				FDMemberAsset fdMemberAsset = new FDMemberAsset();
				fdMemberAsset.setMember_id(consumer_id);
				fdMemberAsset.setShell(new BigDecimal("88"));
				memberList.add(fdMemberAsset);
			} else if ("svip_7".equals(level)) {
				FDMemberCoupons fDMemberCoupons = new FDMemberCoupons();
				fDMemberCoupons.setCoupons_validity(parseToDate);
				fDMemberCoupons.setCreated_date(date);
				fDMemberCoupons.setMember_id(consumer_id);
				fDMemberCoupons.setCoupons_key("lq_08");
				fDMemberCoupons.setCoupons_mun(1);
				fDMemberCoupons.setIs_activate("Y");
				list.add(fDMemberCoupons);
				FDMemberCoupons fDMemberCoupons2 = new FDMemberCoupons();
				BeanConvertUtil.copyProperties(fDMemberCoupons2, fDMemberCoupons);
				fDMemberCoupons2.setCoupons_key("lq_06");
				list.add(fDMemberCoupons2);
				FDMemberAsset fdMemberAsset = new FDMemberAsset();
				fdMemberAsset.setMember_id(consumer_id);
				fdMemberAsset.setShell(new BigDecimal("188"));
				memberList.add(fdMemberAsset);
			}
		}
		if(list.size() != 0){
			financeDao.insertFdMemberCoupons(list);
			for (FDMemberAsset fdMemberAsset : memberList) {
				Map<String,Object> temp = new HashMap<>();
				temp.put("data_source", CommonConstant.DATA_SOURCE_SJLY_01);
				temp.put("payment_way_key", ShellConstant.PAYMENT_WAY_07);
				temp.put("amount", fdMemberAsset.getShell());
				temp.put("out_member_id", ShellConstant.MEMBER_ID_MTH);
				temp.put("in_member_id", fdMemberAsset.getMember_id());
				temp.put("detail", "礼包赠送");
				temp.put("order_type_key", ShellConstant.ORDER_TYPE_13);
				temp.put("currency_code", "贝壳");
				temp.put("out_trade_body", FastJsonUtil.toJson(temp));
				tradeService.orderPay(temp, result);
			}
		}
	}

	@Override
	public void findGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		paramsMap.put("coupons_validity", DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd)));
		List<FDMemberCoupons> list = financeDao.selectFdMemberCoupons(paramsMap);
		logger.info("查询礼券日志开始======="+FastJsonUtil.toJson(list));
		int i = 0;
		int m = 0;
		for (FDMemberCoupons fDMemberCoupons : list) {
			String coupons_key = fDMemberCoupons.getCoupons_key();
			if("my_01".equals(coupons_key)){
				i = i + fDMemberCoupons.getCoupons_mun();
			}else{
				m = m + fDMemberCoupons.getCoupons_mun();
			}
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("mianyou", i);
		resultMap.put("youhui", m);
		logger.info("查询礼券日志结束======="+FastJsonUtil.toJson(resultMap));
		result.setResultData(resultMap);
	}

	@Override
	public void handleMemberReceiveMoney(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "receiveMoney" });
		// 写入金额， 写入日志
		String member_id = (String) paramsMap.get("member_id");
		String receiveMoney = (String) paramsMap.get("receiveMoney");
		String mobile = (String) paramsMap.get("mobile");
		String inviteMobile = (String) paramsMap.get("inviteMobile");
		String type = (String) paramsMap.get("type");
		String remark = (String) paramsMap.get("remark");
		String receiveType = null;
		if (StringUtil.equals("直邀奖励", remark)) {
			receiveType = "direct";
		} else if (StringUtil.equals("间邀奖励", remark)) {
			receiveType = "indirect";
		} else if (StringUtil.equals("次邀奖励", remark)) {
//			receiveType = "nextdirect";
			return;
		} else if (StringUtil.equals("活动奖励", remark)) {
			receiveType = "lottery";
		}
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", member_id);
		FDMemberAsset memberAssetEntity = financeDao.selectFDMemberAsset(tempMap);
		BigDecimal pre_balance = memberAssetEntity.getCash_balance();
		BigDecimal balance = memberAssetEntity.getCash_balance()
				.add(new BigDecimal(receiveMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
		tempMap.clear();
		tempMap.put("member_id", member_id);
//		if (StringUtil.equals("次邀奖励", remark)) {
//			tempMap.put("invite_balance", new BigDecimal(receiveMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
//		} else {
			tempMap.put("trade_cash_balance", new BigDecimal(receiveMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
//		}
		Integer flag = financeDao.updateFDMemberAsset(tempMap);
		if (flag == 1) {
			// 写入记录
			FDMemberRebateLog memberRebateLog = new FDMemberRebateLog();
			memberRebateLog.setMember_id(member_id);
			memberRebateLog.setPre_balance(pre_balance);
			memberRebateLog.setBalance(balance);
			memberRebateLog.setCash_money(new BigDecimal(receiveMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
			memberRebateLog.setCreate_time(new Date());
			memberRebateLog.setMobile(mobile);
			memberRebateLog.setInviteMobile(inviteMobile);
			memberRebateLog.setCategory("income");
			Map<String, Object> remarkMap = new HashMap<String, Object>();
			remarkMap.put("mobile", mobile);
			if (StringUtils.equals(type, "order")) {
				remarkMap.put("detail", "购物");
			}
			if (StringUtils.equals(type, "recharge")) {
				remarkMap.put("detail", remark);
			}
			if (StringUtils.equals(type, "lottery")) {
				remarkMap.put("detail", "活动奖励");
			}
			remarkMap.put("receiveMoney", receiveMoney);
			String itemJSONObj = JSON.toJSONString(remarkMap);

			memberRebateLog.setType(receiveType);
			memberRebateLog.setRemark(itemJSONObj);
			financeDao.insertFdMemberRebateLog(memberRebateLog);
		} else {
			throw new BusinessException("更新会员返佣出错  ", "插入会员返佣出错 ");
		}

		if (StringUtil.equals("直邀奖励", remark)) {
			Map<String,Object> param = new HashMap<>();
			param.put("message_type", "finance");
			param.put("message_category", "direct_inform");
			param.put("member_id", member_id);
			sendMessage(param);
		} else if (StringUtil.equals("间邀奖励", remark)) {
			Map<String,Object> param = new HashMap<>();
			param.put("message_type", "finance");
			param.put("message_category", "indirect_inform");
			param.put("member_id", member_id);
			sendMessage(param);
		}
	}

	private void sendMessage(Map<String, Object> paramsMap) throws Exception, SystemException, BusinessException {
		// 推送消息
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "member.sendMessage");
		reqParams.put("params", FastJsonUtil.toJson(paramsMap));
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
	}
	
	@Override
	public void findGiftCouponList(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		paramsMap.put("coupons_validity", DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd)));
		List<Map<String,Object>> list = financeDao.selectMemberCouponsList(paramsMap);
		result.setResultData(list);
	}
	
	
	@Override
	public void findGiftCouponList_v2(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
		String coupons_key = StringUtil.formatStr(paramsMap.get("coupons_key"));
		String overdue = StringUtil.formatStr(paramsMap.get("overdue"));
		if (StringUtil.equals(coupons_key, "n_lj")) {
			paramsMap.put("coupons_key", "lj");
			paramsMap.put("is_activate", "N");
		} else if (StringUtil.equals(coupons_key, "y_lj")) {
			paramsMap.put("coupons_key", "lj");
			paramsMap.put("is_activate", "Y");
		}
		// 查询有效期的礼券
//		paramsMap.put("coupons_validity", DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd)));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String is_use = StringUtil.formatStr(paramsMap.get("is_use"));
		if (StringUtil.equals(is_use, "Y")) {
			// 未使用的查询有效期内未使用的礼券
			paramsMap.put("coupons_mun", 1);
			paramsMap.put("coupons_validity",
					DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd)));
			list = financeDao.selectMemberCouponsList(paramsMap);
			Date nowDate = DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
			for (Map<String, Object> map : list) {
				String is_activate = StringUtil.formatStr(map.get("is_activate"));
				if ("N".equals(is_activate)) {
					map.put("name", "未激活礼券");
				}
				String coupons_validity = (String) map.get("coupons_validity");
				Date couponsValidity = DateUtil.parseToDate(coupons_validity);
				if (nowDate.after(couponsValidity)) {
					map.put("overdue", "Y");
				} else {
					map.put("overdue", "N");
				}
			}

		} else if (StringUtil.equals(is_use, "N")) {
			List<Map<String, Object>> mapList = this.findOrderCouponsId(paramsMap);
			List<Integer> couponsIdList = new ArrayList<Integer>();
			Map<String, Object> useMap = new HashMap<String, Object>();
			for (Map<String, Object> couponMap : mapList) {
				Integer couponsId = Integer.valueOf(StringUtil.formatStr(couponMap.get("coupons_id")));
				Integer count = (Integer) couponMap.get("count");
				couponsIdList.add(couponsId);
				useMap.put(couponsId.toString(), count);
			}
			if (null != couponsIdList && couponsIdList.size() > 0) {
				paramsMap.put("coupons_id_in", couponsIdList);
				list = financeDao.selectMemberCouponsList(paramsMap);
				if (null != list && list.size() > 0 && null != useMap) {
					for (Map<String, Object> map : list) {
						String member_coupons_id = StringUtil.formatStr(map.get("member_coupons_id"));
						for (String key : useMap.keySet()) {
							if (StringUtil.equals(member_coupons_id, key)) {
								map.put("useQty", useMap.get(key));
							}
						}
					}
				}
			}
		} else {
			List<Map<String, Object>> listMap = financeDao.selectMemberCouponsList(paramsMap);
			List<Map<String, Object>> mapList = this.findOrderCouponsId(paramsMap);
			Date nowDate = DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
			if (null != listMap && listMap.size() > 0) {
				if (StringUtil.isNotBlank(overdue)) {
					for (int i = 0; i < listMap.size(); i++) {
						String coupons_validity = (String) listMap.get(i).get("coupons_validity");
						String is_activate = (String) listMap.get(i).get("is_activate");
						if ("N".equals(is_activate)) {
							listMap.get(i).put("name", "未激活礼券");
						}
						Date couponsValidity = DateUtil.parseToDate(coupons_validity);
						Integer memberCouponId = Integer
								.valueOf(StringUtil.formatStr(listMap.get(i).get("member_coupons_id")));
						if (StringUtil.equals(overdue, "Y")) {
							if (nowDate.after(couponsValidity)) {
								listMap.get(i).put("overdue", "Y");
								if (null != mapList && mapList.size() > 0) {
									for (Map<String, Object> couponMap : mapList) {
										Integer couponsId = Integer
												.valueOf(StringUtil.formatStr(couponMap.get("coupons_id")));
										Integer count = (Integer) couponMap.get("count");
										if (memberCouponId.intValue() == couponsId.intValue()) {
											listMap.get(i).put("useQty", count);
										}
									}
								}
								list.add(listMap.get(i));
							}
						} else if (StringUtil.equals(overdue, "N")) {
							if (nowDate.before(couponsValidity)) {
								listMap.get(i).put("overdue", "N");
								if (null != mapList && mapList.size() > 0) {
									for (Map<String, Object> couponMap : mapList) {
										Integer couponsId = Integer
												.valueOf(StringUtil.formatStr(couponMap.get("coupons_id")));
										Integer count = (Integer) couponMap.get("count");
										if (memberCouponId.intValue() == couponsId.intValue()) {
											listMap.get(i).put("useQty", count);
										}
									}
								}
								list.add(listMap.get(i));
							}
						}

					}
				} else {
					for (Map<String, Object> map : list) {
						String coupons_validity = (String) map.get("coupons_validity");
						Date couponsValidity = DateUtil.parseToDate(coupons_validity);
						Integer memberCouponId = Integer.valueOf(StringUtil.formatStr(map.get("member_coupons_id")));

						if (nowDate.after(couponsValidity)) {
							map.put("overdue", "Y");
						} else {
							map.put("overdue", "N");
						}
						if (null != mapList && mapList.size() > 0) {
							for (Map<String, Object> couponMap : mapList) {
								Integer couponsId = Integer.valueOf(StringUtil.formatStr(couponMap.get("coupons_id")));
								Integer count = (Integer) couponMap.get("count");
								if (memberCouponId.intValue() == couponsId.intValue()) {
									map.put("useQty", count);
								}
							}
						}
						list.add(map);
					}
				}
			}
		}
		result.setResultData(list);
	}
	
	
	public  List<Map<String, Object>>  findOrderCouponsId(Map<String, Object> paramsMap) {
		try {
			String url = PropertiesConfigUtil.getProperty("order_service_url");
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.selectMemberCouponsId");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", StringUtil.formatStr(paramsMap.get("member_id")));
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) data.get("mapList");
			return  mapList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	public void findGiftCouponList_v1(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		paramsMap.put("coupons_validity", DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd)));
		List<Map<String,Object>> list = financeDao.selectMemberCouponsList(paramsMap);
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("list", list);
		result.setResultData(resultMap);
	}
	@Override
	public void updateGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "coupons_id" });
		String coupons_id = StringUtil.formatStr(paramsMap.get("coupons_id"));
		List<String> coupons_id_in = StringUtil.str2List(coupons_id, ",");
		Map<String,Object> tempMap = new HashMap<>();
		tempMap.put("coupons_id_in", coupons_id_in);
		tempMap.put("member_id", paramsMap.get("member_id"));
		tempMap.put("coupons_validity", DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd)));
		tempMap.put("coupons_mun", paramsMap.get("coupons_mun"));
		List<Map<String,Object>> list = financeDao.selectMemberCouponsList(tempMap);
		if(list.size() != coupons_id_in.size()){
			throw new BusinessException("礼券已失效", "礼券已失效,请重新核对");
		}
		Integer integer = financeDao.updateMemberCoupons(tempMap);
		if(list.size() != integer){
			throw new BusinessException("礼券扣除失败", "礼券扣除失败,请稍后重试");
		}
		result.setResultData(list);
	}

	@Override
	public void editMemberCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		String member_id =  (String) paramsMap.get("member_id");
		String is_New = StringUtil.formatStr(paramsMap.get("is_New"));
		String type = StringUtil.formatStr(paramsMap.get("type"));
		List<FDMemberCoupons> list =  new ArrayList<>();
		int num = 1;
		boolean isAdd = false;
		// 17:54 2019/5/7 欧少辉
		if("Y".equals(is_New)){
			if("5".equals(type)){
				// 体验会员只有一张精品礼券
				// num = 1;
			}else {
				// 如果是新用户,我们发12张礼券
//				num = 12;
				num = 1;
			}
			isAdd = true;
		}
		Date now = new Date();
		for (int i = 0; i < num; i++) {
			FDMemberCoupons memberCoupons = new FDMemberCoupons();
			memberCoupons.setCoupons_key("lj_399");
			memberCoupons.setMember_id(member_id);
			memberCoupons.setCreated_date(now);
			memberCoupons.setCoupons_mun(1);
			if("5".equals(type)) {
				memberCoupons.setIs_activate("N");
				memberCoupons.setIs_first("1");
				// 起始时间
				String startStr = DateUtil.date2Str(now,DateUtil.fmt_yyyyMMdd);
				Date startDate = DateUtil.str2Date(startStr, DateUtil.fmt_yyyyMMdd);
				// 结束时间
				String endStr = DateUtil.addDate(startStr,DateUtil.fmt_yyyyMMdd,3,9);
				Date endDate = DateUtil.str2Date(endStr, DateUtil.fmt_yyyyMMdd);
				memberCoupons.setCoupons_validity(endDate);
				memberCoupons.setCoupons_validity_start(startDate);
				memberCoupons.setCoupons_validity_end(endDate);
			}else {
				String endStr = DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 2, i+1);
				endStr = DateUtil.addDate(endStr, DateUtil.fmt_yyyyMMdd, 3, -1);
				Date endDate = DateUtil.str2Date(endStr, DateUtil.fmt_yyyyMMdd);

				String startStr = DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd),DateUtil.fmt_yyyyMMdd, 2, i);
				Date startDate = DateUtil.str2Date(startStr, DateUtil.fmt_yyyyMMdd);

				memberCoupons.setCoupons_validity(endDate);
				memberCoupons.setCoupons_validity_end(endDate);
				memberCoupons.setCoupons_validity_start(startDate);
				// 14:07 2019/6/16 第一张礼券是需要激活，并且时间有效期为3年
				if (i == 0) {
					memberCoupons.setCoupons_validity(DateUtil.str2Date(DateUtil.addDate(startStr,DateUtil.fmt_yyyyMMdd,1,3),DateUtil.fmt_yyyyMMdd));
					memberCoupons.setIs_activate("Y");
					memberCoupons.setIs_first("1");
				} else {
					memberCoupons.setIs_activate("N");
					memberCoupons.setIs_first("0");
				}
			}
			list.add(memberCoupons);
		}
		financeDao.insertFdMemberCoupons(list);
		logger.info("editMemberCoupon--mun=12 isAdd: "+isAdd);
		if(isAdd) {
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, String> reqParams = new HashMap<>();
			Map<String, Object> bizParams = new HashMap<>();
			if(StringUtils.isNotBlank(member_id)){
				bizParams.put("member_id", member_id);
				reqParams.put("service", "consumer.consumer.memberDistributionInfoById");
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				String resultStr = HttpClientUtil.post(member_service_url, reqParams);
				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					Map<String, Object> resMap = FastJsonUtil.jsonToMap(resultMap.get("data").toString());
					String parent_id = (String)resMap.get("parent_id");
					logger.info("editMemberCoupon--邀请者parent_id: "+parent_id);
					if(StringUtils.isNotBlank(parent_id)){
						Map<String, Object> dbParams = new HashMap<>();
						dbParams.put("member_id", parent_id);
						dbParams.put("coupons_key", "lj_399");
						dbParams.put("actiDate", DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
						List<FDMemberCoupons> couponsList = financeDao.selectFdMemberCoupons(dbParams);
						logger.info("editMemberCoupon--查询邀请者: "+couponsList);
						if(couponsList!=null && couponsList.size()==1){
							FDMemberCoupons coupons = couponsList.get(0);
							if("5".equals(type)) {
								// 如果邀请的用户是购买体验会员只有一次补签
								coupons.setRepair_count(parseInt(coupons.getRepair_count()) + 1);
								coupons.setRepair_count_all(parseInt(coupons.getRepair_count_all()) + 1);
							}else{
								coupons.setRepair_count(parseInt(coupons.getRepair_count()) + 5);
								coupons.setRepair_count_all(parseInt(coupons.getRepair_count_all()) + 5);
							}
							financeDao.updateFdMemberCouponsRepairCount02(coupons);
							logger.info("editMemberCoupon--给邀请者加5次机会parent_id: "+parent_id);
						}else{
							logger.info("editMemberCoupon--查询用户礼券失败--parent_id: "+parent_id);
						}
					}
				}else{
					logger.info("editMemberCoupon--请求consumer.consumer.memberDistributionInfoById服务失败--" + resultStr);
				}
			}
		}

		//如果是免费会员重置贝壳
		if(StringUtil.isNotEmpty(type) && Integer.valueOf(type)==2) {
			editMemberAssetShell(member_id);
		}
	}
	
	//重置贝壳
	public  void editMemberAssetShell(String memberId) throws Exception {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("member_id", memberId);
		FDMemberAsset fDMemberAsset = financeDao.selectFDMemberAsset(queryMap);
		if (null != fDMemberAsset) {
			queryMap.put("shell", new BigDecimal("1314"));
			financeDao.updateMemberShell(queryMap);
		}else {
			throw new BusinessException("免费会员续费", "免费会员续费，重置贝壳，找不到资产表");
		}
	}
	
	@Override
	public void updateMemberGift(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		String coupons_key =  (String) paramsMap.get("coupons_key");
		String member_id =  (String) paramsMap.get("member_id");
		String member_coupons_id = (String) paramsMap.get("member_coupons_id");
		Map<String, Object>  tempMap =  new HashMap<String, Object>();
		tempMap.put("member_id", member_id);
		tempMap.put("coupons_key", coupons_key);
		tempMap.put("member_coupons_id", Long.valueOf(member_coupons_id));
		List<Map<String,Object>> listGift  =  financeDao.selectMemberCoupons(paramsMap);
		if(null != listGift &&  listGift.size()>0) {
			Map<String,Object>  giftMap =  listGift.get(0);
			Integer  group_num =  (Integer) giftMap.get("coupons_mun");
			group_num = group_num +1;
			tempMap.clear();
			tempMap.put("member_id", member_id);
			tempMap.put("coupons_key", coupons_key);
			tempMap.put("coupons_mun", group_num);
			tempMap.put("member_coupons_id", member_coupons_id);
			financeDao.updateMemberCouByMemberId(tempMap);
		}
	}

	public static String addDay(String current){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(DateUtil.str2Date(current, DateUtil.fmt_yyyyMMdd));
        calendar.add(calendar.DAY_OF_MONTH, 1);
        return DateUtil.date2Str(calendar.getTime(), DateUtil.fmt_yyyyMMdd);
    }


	/**
	 * 贝壳交易
	 */
	@Override
	public void buyByShell(Map<String, Object> paramsMap, ResultData result) throws Exception{
//		ValidateUtil.validateParams(paramsMap, new String[]{"member_id","shell_num","shop_member_id"});
		ValidateUtil.validateParams(paramsMap, new String[]{"member_id", "shell_num", "code"});

		String code = AESTool.decrypt(paramsMap.get("code") + "");
//		Map<String, Object> stringObjectMap = FastJsonUtil.jsonToMap(code);
//		String stores_id = stringObjectMap.get("stores_id") + "";
		String stores_id = code;
		logger.info("解密的店铺stores_id是："+stores_id);


		String stores_name = "";
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		bizParams.put("stores_id", stores_id);
		reqParams.put("service", "stores.stores.storesBaseInfoFind");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMapStores = FastJsonUtil.jsonToMap(resultStr);
		if (((String) resultMapStores.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> ddd = FastJsonUtil.jsonToMap(resultMapStores.get("data").toString());
			stores_name = (String) ddd.get("stores_name");
		}


		String shell_num = paramsMap.get("shell_num") + "";
		BigDecimal shellBigDecimal = new BigDecimal(shell_num);
		if (shellBigDecimal.intValue()<=0) {
			throw new BusinessException("1001", "请输入正确的贝壳数");
		}

		Map<String, Object> shellMap = new HashMap<>();
		shellMap.put("member_id", paramsMap.get("member_id"));
		FDMemberAsset memberAsset = financeDao.selectFDMemberAsset(shellMap);
		if (memberAsset==null) {
			throw new BusinessException("1000", "没有查到用户");
		}

		BigDecimal shell = memberAsset.getShell();
		BigDecimal moneySub = MoneyUtil.moneySub(shell, shellBigDecimal);
		if (moneySub.compareTo(new BigDecimal("0")) < 0) {
			throw new BusinessException("3000", "您的账户贝壳数不足");
		}


		Map<String, Object> assetShellMap = new HashMap<>();
		assetShellMap.put("member_id", paramsMap.get("member_id"));
		assetShellMap.put("shell", new BigDecimal(shell_num).negate());
		Integer update_flag = financeDao.updateFDMemberAsset(assetShellMap);
		if (update_flag==null || update_flag == 0) {
			throw new BusinessException(RspCode.TRADE_FAIL, "用户贝壳余额更新失败");
		}


		//写入贝壳日志
		BigDecimal pre_balance = memberAsset.getShell();
		BigDecimal balanceSub = MoneyUtil.moneySub(pre_balance, shellBigDecimal);
		FDMemberShellLog operateLog = new FDMemberShellLog();
		operateLog.setMember_id(memberAsset.getMember_id());
		operateLog.setMember_type_key(memberAsset.getMember_type_key());
		operateLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		operateLog.setTracked_date(new Date());
		operateLog.setPre_balance(pre_balance);
		operateLog.setAmount(shellBigDecimal.negate());
		operateLog.setBalance(balanceSub);
		String detail_id = TradeIDUtil.getTradeNo();
		operateLog.setTransaction_no(detail_id);

		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("detail", "向"+stores_name+"商户付款");
		operateLog.setRemark(FastJsonUtil.toJson(remarkMap));

		transactionDao.insertFDMemberShellLog(operateLog);


		//
		Map<String, Object> shellMap_shop = new HashMap<>();
		shellMap_shop.put("member_id", stores_id);
		shellMap_shop.put("member_type_key", "stores");
		FDMemberAsset memberAsset_shop = financeDao.selectFDMemberAsset(shellMap_shop);
		if (memberAsset_shop==null) {
			throw new BusinessException("1000", "没有查到商户");
		}

		Map<String, Object> assetShellMap_shop = new HashMap<>();
		assetShellMap_shop.put("member_id", stores_id);
		assetShellMap_shop.put("shell", new BigDecimal(shell_num));
		Integer update_flag_shop = financeDao.updateFDMemberAsset(assetShellMap_shop);
		if (update_flag_shop==null || update_flag_shop == 0) {
			throw new BusinessException(RspCode.TRADE_FAIL, "商户贝壳余额更新失败");
		}

		BigDecimal pre_balance_shop = memberAsset_shop.getShell();
		BigDecimal balanceAdd_shop = MoneyUtil.moneyAdd(pre_balance_shop, shellBigDecimal);
		FDMemberShellLog operateLog_shop = new FDMemberShellLog();
		operateLog_shop.setMember_id(memberAsset_shop.getMember_id());
		operateLog_shop.setMember_type_key(memberAsset_shop.getMember_type_key());
		operateLog_shop.setCategory(Constant.CATEGORY_INCOME);
		operateLog_shop.setTracked_date(new Date());
		operateLog_shop.setPre_balance(pre_balance_shop);
		operateLog_shop.setAmount(shellBigDecimal);
		operateLog_shop.setBalance(balanceAdd_shop);
		operateLog_shop.setTransaction_no(detail_id);
		Map<String, Object> remarkMap_shop = new HashMap<String, Object>();
		remarkMap_shop.put("detail", stores_name+"收款");
		operateLog_shop.setRemark(FastJsonUtil.toJson(remarkMap_shop));
		transactionDao.insertFDMemberShellLog(operateLog_shop);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("msg", "ok");
		result.setResultData(resultMap);
	}


	/**
	 * 贝壳换补签次数
	 * finance.consumer.shellSwitchToCount
	 */
	@Override
	public void shellSwitchToCount(Map<String, Object> paramsMap, ResultData result) throws Exception{
		ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
		Map<String, Object> shellMap = new HashMap<>();
//		shellMap.put("member_type_key", "consumer");
		shellMap.put("member_id", paramsMap.get("member_id"));
		FDMemberAsset memberAsset = financeDao.selectFDMemberAsset(shellMap);
		BigDecimal shell = memberAsset.getShell();
		BigDecimal moneySub = MoneyUtil.moneySub(shell, new BigDecimal(16));
		if (moneySub.compareTo(new BigDecimal("0")) < 0) {
			throw new BusinessException("支付异常", "当前贝壳数量不够");
		}

		//
		paramsMap.put("member_id", paramsMap.get("member_id"));
		paramsMap.put("coupons_key", "lj_399");
		paramsMap.put("shell_count", "1");
		//paramsMap.put("is_activate", "N");
		paramsMap.put("actiDate", DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
		List<FDMemberCoupons> memberCouponsCurr = financeDao.selectFdMemberCoupons(paramsMap);
		if(memberCouponsCurr==null||memberCouponsCurr.size()<=0){
			throw new BusinessException("查询礼券", "不是lj_399卷或不是未激活或没查询到当月礼券" + paramsMap.get("member_id"));
		}

		FDMemberCoupons couponsCurr = memberCouponsCurr.get(0);
		if(parseInt(couponsCurr.getShell_count())>0){
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("shell_switch_to_count", "1");
			result.setResultData(resultMap);
			return;
		}


		//
		BigDecimal shellBigDecimal = new BigDecimal("-16");
		//写入贝壳日志
		Map<String, Object> tempMap = new HashMap<String, Object>();
		BigDecimal pre_balance = memberAsset.getShell();
		BigDecimal balance = MoneyUtil.moneyAdd(pre_balance, shellBigDecimal);
		FDMemberShellLog operateLog = new FDMemberShellLog();
		operateLog.setMember_id(memberAsset.getMember_id());
		operateLog.setMember_type_key(memberAsset.getMember_type_key());
		operateLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		operateLog.setTracked_date(new Date());
		operateLog.setPre_balance(pre_balance);
		operateLog.setAmount(shellBigDecimal);
		operateLog.setBalance(balance);
		String detail_id = TradeIDUtil.getTradeNo();
		operateLog.setTransaction_no(detail_id);
		Map<String, Object> remarkMap = new HashMap<String, Object>();
//		remarkMap.put("transaction_no", fDTransactionsResult.getTransaction_no());
//		remarkMap.put("order_type_key", fDTransactionsResult.getOrder_type_key());
//		remarkMap.put("payment_way_key", fDTransactionsResult.getPayment_way_key());
//		remarkMap.put("detail", fDTransactionsResult.getDetail());
//		remarkMap.put("transaction_member_id", fDTransactionsResult.getBuyer_id());
//		remarkMap.put("transaction_member_type_key", fDTransactionsResult.getBuyer_member_type());
//		remarkMap.put("transaction_member_name", fDTransactionsResult.getBuyer_name());
//		remarkMap.put("transaction_member_contact", fDTransactionsResult.getBuyer_contact());
		remarkMap.put("detail", "获得1次补签");
		operateLog.setRemark(FastJsonUtil.toJson(remarkMap));
		transactionDao.insertFDMemberShellLog(operateLog);



		Map<String, Object> assetShellMap = new HashMap<>();
		assetShellMap.put("member_id", paramsMap.get("member_id"));
		assetShellMap.put("shell", shellBigDecimal);
		Integer update_flag = financeDao.updateFDMemberAsset(assetShellMap);
		if (update_flag==null || update_flag == 0) {
			throw new BusinessException(RspCode.TRADE_FAIL, "贝壳余额更新失败");
		}
		logger.info("结束扣除贝壳资产====>");


		//
//		paramsMap.put("member_id", paramsMap.get("member_id"));
//		paramsMap.put("actiDate", DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
//		List<FDMemberCoupons> memberCouponsCurr = financeDao.selectFdMemberCoupons(paramsMap);
//		if(memberCouponsCurr==null||memberCouponsCurr.size()<=0){
//			throw new BusinessException("查询礼券", "没查询到当月礼券" + paramsMap.get("member_id"));
//		}
//
//		FDMemberCoupons couponsCurr = memberCouponsCurr.get(0);
//		if(parseInt(couponsCurr.getShell_count())>0){
//			Map<String, Object> resultMap = new HashMap<>();
//			resultMap.put("shell_switch_to_count", "1");
//			result.setResultData(resultMap);
//			return;
//		}
		Map<String, Object> financeShellMap = new HashMap<>();
		financeShellMap.put("member_coupons_id", couponsCurr.getMember_coupons_id());
		financeShellMap.put("shell_count", parseInt(couponsCurr.getShell_count())+1);
		financeShellMap.put("repair_count", parseInt(couponsCurr.getRepair_count())+1);
		financeShellMap.put("repair_count_all", parseInt(couponsCurr.getRepair_count_all())+1);
		Integer shellInteger = financeDao.updateMemberCouByMemberId(financeShellMap);
		if (shellInteger==null || shellInteger == 0) {
			throw new BusinessException("贝壳购买", "贝壳换补签次数失败");
		}
		paramsMap.put("member_id", paramsMap.get("member_id"));
		paramsMap.put("coupons_key", "lj_399");
		paramsMap.put("shell_count", "1");
//		paramsMap.put("is_activate", "N");
		paramsMap.put("actiDate", DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
//		List<FDMemberCoupons> memberCouponsCurr02 = financeDao.selectFdMemberCoupons(paramsMap);
		List<FDMemberCoupons> memberCouponsCurr02 = memberCouponsCurr;
//		if(memberCouponsCurr02==null||memberCouponsCurr02.size()<=0){
//			throw new BusinessException("查询礼券", "没查询到当月礼券" + paramsMap.get("member_id"));
//		}
		FDMemberCoupons couponsCurr02 = memberCouponsCurr02.get(0);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("shell", balance);
		resultMap.put("shell_count", 1);
		resultMap.put("repair_count", couponsCurr02.getRepair_count());
		resultMap.put("repair_count_all", couponsCurr02.getRepair_count_all());
		resultMap.put("member_coupons_id", couponsCurr02.getMember_coupons_id());
		resultMap.put("member_id", couponsCurr02.getMember_id());
		resultMap.put("shell_switch_to_count", "0");
		result.setResultData(resultMap);
		logger.info("结束增加签到次数====>");
	}

	private static int parseInt(Integer number){
		if(number==null){
			number = 0;
		}
		return number;
	}



	public void repairGiftCouponNoVip(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
		paramsMap.put("coupons_key", "lj_399");
		//
		paramsMap.put("actiDate", DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
		List<FDMemberCoupons> memberCouponsCurr = financeDao.selectFdMemberCoupons(paramsMap);
		if(memberCouponsCurr==null||memberCouponsCurr.size()<=0){
			throw new BusinessException("查询礼券", "没查询到当月礼券" + paramsMap.get("member_id"));
		}
		FDMemberCoupons couponsCurr = memberCouponsCurr.get(0);
		FDMemberCoupons couponsNext = couponsCurr;
		Integer repairCountCurr = couponsCurr.getRepair_count();
		if(repairCountCurr==null||repairCountCurr<=0){
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("state", "0");
			resultMap.put("signStr", "补签次数机会0次");
			resultMap.put("newSignList", new ArrayList<>());
			result.setResultData(resultMap);
			return;
		}

		String sign_data = couponsNext.getSign_data();
		if(StringUtils.isBlank(sign_data)){
			sign_data = null;
		}
		List<String> toList = FastJsonUtil.jsonToList(sign_data);
		if(toList==null){
			toList = new ArrayList<>();
		}


		Date coupons_validity_start = couponsCurr.getCoupons_validity_start();
		String coupons_validity_start_str = DateUtil.date2Str(coupons_validity_start, DateUtil.fmt_yyyyMMdd);
		Date coupons_validity = couponsCurr.getCoupons_validity();
		String coupons_validity_str = DateUtil.date2Str(coupons_validity, DateUtil.fmt_yyyyMMdd);

		String nowDate = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
		Date nowDateStr = DateUtil.str2Date(nowDate, DateUtil.fmt_yyyyMMdd);
		String distanceDays = DateUtil.getDistanceDays(coupons_validity_start_str, nowDate);
		int parseInt = Integer.parseInt(distanceDays);


		if(toList.size()>=0){
			boolean currentIsSign = false;
			//判断当天是否已经签到
			if (StringUtil.isNotBlank(sign_data)) {
				String lastDay = toList.get(toList.size() - 1);
				if (lastDay.equals(nowDate)) {
					currentIsSign = true;
				}
			}
			if(parseInt==toList.size()&&!currentIsSign){//这个时候是当前签到的天数等于这张卷已经签到的天数,此时只有一天没签到
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("state", "1");
				resultMap.put("signStr", "当天不能补签");
				resultMap.put("newSignList", new ArrayList<>());
				result.setResultData(resultMap);
				return;
			}
		}

//
//		if(toList.size()==0){
//
//		}
//		if(parseInt==toList.size()&&!toList.contains(nowDate)){
//
//		}


		String sign_data_repair_next = couponsNext.getSign_data_repair();
		if(StringUtils.isBlank(sign_data_repair_next)){
			sign_data_repair_next = null;
		}
		List<String> sign_data_repair_next_list = FastJsonUtil.jsonToList(sign_data_repair_next);
		if(sign_data_repair_next_list==null){
			sign_data_repair_next_list = new ArrayList<>();
		}

		List<String> sSignList = new ArrayList<>();
		List<String> newSignList = new ArrayList<>();
		List<String> oldSignList = new ArrayList<>();
		List<String> sign_data_repair = new ArrayList<>();
		for (long i = 0; i < parseInt+1; i++) {

			if(toList.contains(coupons_validity_start_str)){
				oldSignList.add(coupons_validity_start_str);
				sSignList.add(coupons_validity_start_str);
				if(sign_data_repair_next_list.contains(coupons_validity_start_str)){
					sign_data_repair.add(coupons_validity_start_str);
				}
			}else{
				if (coupons_validity_start_str.equals(nowDate)) {//当天不需要签到
					break;
				}
				if(repairCountCurr>0 && !coupons_validity_start_str.equals(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd))){
					sign_data_repair.add(coupons_validity_start_str);
					newSignList.add(coupons_validity_start_str);
					sSignList.add(coupons_validity_start_str);
					--repairCountCurr;
				}
			}
			coupons_validity_start_str = addDay(coupons_validity_start_str);

		}

		Map<String, Object> tempMap = new HashMap<>();
		String is_activate = "N";
		if(sSignList.size() >= ShellConstant.SIGN_IN_DAY){
			tempMap.put("is_activate", "Y");
			is_activate = "Y";


			//加日期
			Map<String, Object> resCouponsMap = new HashMap<>();
			resCouponsMap.put("coupons_validity", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 1, 3));
			resCouponsMap.put("member_coupons_id", couponsCurr.getMember_coupons_id());
			financeDao.updateFdMemberCoupons(resCouponsMap);


			//
			int lsn = -1;
			Map<String, Object> hashMap = new HashMap<>();
			hashMap.put("member_id", couponsNext.getMember_id());
			List<Map<String, Object>> maps = financeDao.selectCouponsLSN(hashMap);
			logger.info("2输出member_id: "+couponsNext.getMember_id()+"的礼卷"+maps.size()+"张: ");
			for (int i = 0; i < maps.size(); i++) {
				Map<String, Object> objectMap = maps.get(i);
				if (couponsNext.getMember_coupons_id().equals(objectMap.get("member_coupons_id")+"")) {
					lsn = i + 1;
					Map<String, Object> sqlMap = new HashMap<>();
					sqlMap.put("coupons_id", couponsNext.getMember_coupons_id());
					sqlMap.put("member_id", couponsNext.getMember_id());
					sqlMap.put("oper_type", "2");
					sqlMap.put("coupons_type", "lj_399");
					sqlMap.put("order_no", null);
					sqlMap.put("lsn", lsn);
					sqlMap.put("created_time", new Date());
					financeDao.insertFDMemberCouponsLog(sqlMap);
					break;
				}
			}
			logger.error("2输出member_id: "+couponsNext.getMember_id()+"的礼卷, 输出第几张礼卷: "+lsn);

		}

		tempMap.put("member_coupons_id", couponsNext.getMember_coupons_id());
		tempMap.put("sign_data_repair", FastJsonUtil.toJson(sign_data_repair));
		tempMap.put("sign_data", FastJsonUtil.toJson(sSignList));
		Integer integer = financeDao.updateMemberCouByMemberId(tempMap);
		Map<String, Object> currMap = new HashMap<>();
		currMap.put("member_coupons_id", couponsCurr.getMember_coupons_id());
		currMap.put("repair_count", repairCountCurr);
		Integer integer2 = financeDao.updateMemberCouByMemberId(currMap);
		if (integer == 0 || integer2==0) {
			throw new BusinessException("补签失败", "补签失败,请稍后再试");
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("is_activate", is_activate);
		resultMap.put("sSignList", sSignList);
		resultMap.put("newSignList", newSignList);
		resultMap.put("oldSignList", oldSignList);
		resultMap.put("signDataRepair", sign_data_repair);
		resultMap.put("signStr", "正常签到");
		resultMap.put("shell_count", couponsCurr.getShell_count());
		if(newSignList!=null&&newSignList.size()<=0){
			resultMap.put("signStr", "不需要补签");
		}
		resultMap.put("is_activate", "N");
		if(sSignList.size() >= ShellConstant.SIGN_IN_DAY){
			resultMap.put("is_activate", "Y");
		}
		result.setResultData(resultMap);

	}


	@Override
	public void repairGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception {
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		bizParams.put("consumer_id", paramsMap.get("member_id")+"");
		reqParams.put("service", "consumer.findConsumerById");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		logger.info("resultStr--> "+resultStr);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
		int type = (int)dataMap.get("type");


		if("5".equals(type+"")) {
			repairGiftCouponNoVip(paramsMap, result);
		} else {
			repairGiftCouponVip(paramsMap, result);
		}

//		ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
//		repairGiftCouponVip(paramsMap, result);
	}




	/**
	 * 补签
	 */
    public void repairGiftCouponVip(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
		String dateStr = DateUtil.addDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 2, 1);
		paramsMap.put("actiDate", dateStr);
		paramsMap.put("coupons_key", "lj_399");
		List<FDMemberCoupons> memberCouponsNext = financeDao.selectFdMemberCoupons(paramsMap);
		if(memberCouponsNext==null||memberCouponsNext.size()<=0){
			throw new BusinessException("查询礼券", "没查询到下张礼券" + paramsMap.get("member_id"));
		}
		//
		paramsMap.put("actiDate", DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
		List<FDMemberCoupons> memberCouponsCurr = financeDao.selectFdMemberCoupons(paramsMap);
		if(memberCouponsCurr==null||memberCouponsCurr.size()<=0){
			throw new BusinessException("查询礼券", "没查询到当月礼券" + paramsMap.get("member_id"));
		}
		FDMemberCoupons couponsCurr = memberCouponsCurr.get(0);
		FDMemberCoupons couponsNext = memberCouponsNext.get(0);
		Integer repairCountCurr = couponsCurr.getRepair_count();
		if(repairCountCurr==null||repairCountCurr<=0){
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("state", "0");
			resultMap.put("signStr", "补签次数机会0次");
			resultMap.put("newSignList", new ArrayList<>());
			result.setResultData(resultMap);
			return;
		}

		String sign_data = couponsNext.getSign_data();
		if(StringUtils.isBlank(sign_data)){
			sign_data = null;
		}
		List<String> toList = FastJsonUtil.jsonToList(sign_data);
		if(toList==null){
			toList = new ArrayList<>();
		}


		Date coupons_validity_start = couponsCurr.getCoupons_validity_start();
		String coupons_validity_start_str = DateUtil.date2Str(coupons_validity_start, DateUtil.fmt_yyyyMMdd);
		Date coupons_validity = couponsCurr.getCoupons_validity();
		String coupons_validity_str = DateUtil.date2Str(coupons_validity, DateUtil.fmt_yyyyMMdd);

		String nowDate = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
		Date nowDateStr = DateUtil.str2Date(nowDate, DateUtil.fmt_yyyyMMdd);
		String distanceDays = DateUtil.getDistanceDays(coupons_validity_start_str, nowDate);
		int parseInt = Integer.parseInt(distanceDays);


		if(toList.size()>=0){
			boolean currentIsSign = false;
			//判断当天是否已经签到
			if (StringUtil.isNotBlank(sign_data)) {
				String lastDay = toList.get(toList.size() - 1);
				if (lastDay.equals(nowDate)) {
					currentIsSign = true;
				}
			}
			if(parseInt==toList.size()&&!currentIsSign){//这个时候是当前签到的天数等于这张卷已经签到的天数,此时只有一天没签到
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("state", "1");
				resultMap.put("signStr", "当天不能补签");
				resultMap.put("newSignList", new ArrayList<>());
				result.setResultData(resultMap);
				return;
			}
		}

//
//		if(toList.size()==0){
//
//		}
//		if(parseInt==toList.size()&&!toList.contains(nowDate)){
//
//		}


		String sign_data_repair_next = couponsNext.getSign_data_repair();
		if(StringUtils.isBlank(sign_data_repair_next)){
			sign_data_repair_next = null;
		}
		List<String> sign_data_repair_next_list = FastJsonUtil.jsonToList(sign_data_repair_next);
		if(sign_data_repair_next_list==null){
			sign_data_repair_next_list = new ArrayList<>();
		}

		List<String> sSignList = new ArrayList<>();
		List<String> newSignList = new ArrayList<>();
		List<String> oldSignList = new ArrayList<>();
		List<String> sign_data_repair = new ArrayList<>();
		for (long i = 0; i < parseInt+1; i++) {

			if(toList.contains(coupons_validity_start_str)){
				oldSignList.add(coupons_validity_start_str);
				sSignList.add(coupons_validity_start_str);
				if(sign_data_repair_next_list.contains(coupons_validity_start_str)){
					sign_data_repair.add(coupons_validity_start_str);
				}
			}else{
				if (coupons_validity_start_str.equals(nowDate)) {//当天不需要签到
					break;
				}
				if(repairCountCurr>0 && !coupons_validity_start_str.equals(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd))){
					sign_data_repair.add(coupons_validity_start_str);
					newSignList.add(coupons_validity_start_str);
					sSignList.add(coupons_validity_start_str);
					--repairCountCurr;
				}
			}
			coupons_validity_start_str = addDay(coupons_validity_start_str);

		}

		Map<String, Object> tempMap = new HashMap<>();
		String is_activate = "N";
		if(sSignList.size() >= ShellConstant.SIGN_IN_DAY){
			tempMap.put("is_activate", "Y");
			is_activate = "Y";


			//
			int lsn = -1;
			Map<String, Object> hashMap = new ConcurrentHashMap<>();
			hashMap.put("member_id", couponsNext.getMember_id());
			List<Map<String, Object>> maps = financeDao.selectCouponsLSN(hashMap);
			logger.info("2输出member_id: "+couponsNext.getMember_id()+"的礼卷"+maps.size()+"张: ");
			for (int i = 0; i < maps.size(); i++) {
				Map<String, Object> objectMap = maps.get(i);
				if (couponsNext.getMember_coupons_id().equals(objectMap.get("member_coupons_id")+"")) {
					lsn = i + 1;
					Map<String, Object> sqlMap = new HashMap<>();
					sqlMap.put("coupons_id", couponsNext.getMember_coupons_id());
					sqlMap.put("member_id", couponsNext.getMember_id());
					sqlMap.put("oper_type", "2");
					sqlMap.put("coupons_type", "lj_399");
					sqlMap.put("order_no", null);
					sqlMap.put("lsn", lsn);
					sqlMap.put("created_time", new Date());
					financeDao.insertFDMemberCouponsLog(sqlMap);
					break;
				}
			}
			logger.error("2输出member_id: "+couponsNext.getMember_id()+"的礼卷, 输出第几张礼卷: "+lsn);

		}

		tempMap.put("member_coupons_id", couponsNext.getMember_coupons_id());
		tempMap.put("sign_data_repair", FastJsonUtil.toJson(sign_data_repair));
		tempMap.put("sign_data", FastJsonUtil.toJson(sSignList));
		Integer integer = financeDao.updateMemberCouByMemberId(tempMap);
		Map<String, Object> currMap = new HashMap<>();
		currMap.put("member_coupons_id", couponsCurr.getMember_coupons_id());
		currMap.put("repair_count", repairCountCurr);
		Integer integer2 = financeDao.updateMemberCouByMemberId(currMap);
		if (integer == 0 || integer2==0) {
			throw new BusinessException("补签失败", "补签失败,请稍后再试");
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("is_activate", is_activate);
		resultMap.put("sSignList", sSignList);
		resultMap.put("newSignList", newSignList);
		resultMap.put("oldSignList", oldSignList);
		resultMap.put("signDataRepair", sign_data_repair);
		resultMap.put("signStr", "正常签到");
		resultMap.put("shell_count", couponsCurr.getShell_count());
		if(newSignList!=null&&newSignList.size()<=0){
			resultMap.put("signStr", "不需要补签");
		}
		resultMap.put("is_activate", "N");
		if(sSignList.size() >= ShellConstant.SIGN_IN_DAY){
			resultMap.put("is_activate", "Y");
		}
		result.setResultData(resultMap);
	}

	/**
	 * 签到
	 */
	@Override
	public void activationGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_coupons_id"});
		List<FDMemberCoupons> memberCoupons = financeDao.selectFdMemberCoupons(paramsMap);
		FDMemberCoupons coupons = memberCoupons.get(0);
		String sign_data = coupons.getSign_data();
		String nowDate = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
		Date now = new Date();
		// 签到逻辑
		List<String> toList = new ArrayList<>();
		if (StringUtil.isNotBlank(sign_data)) {
			toList = FastJsonUtil.jsonToList(sign_data);
			String lastDay = toList.get(toList.size() - 1);
			if (lastDay.equals(nowDate)) {
				throw new BusinessException("重复签到", "您当日已签到,一天可签到一次");
			}
		}
		toList.add(nowDate);
		Map<String, Object> tempMap = new HashMap<>();
		// 会员类型， 0:SVIP会员  1：内测会员    2:vip会员  3，普通会员   4过期会员，5体验会员
		//查询会员信息
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		reqParams.put("service", "consumer.findConsumerById");
		paramMap.put("consumer_id",coupons.getMember_id());
		reqParams.put("params", FastJsonUtil.toJson(paramMap));
		String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
		Map<String, Object> resMap = FastJsonUtil.jsonToMap(resultStr);
		// 是否体验会员
		String type = null;
		if (((String) resMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> dataMap = (Map<String, Object>) resMap.get("data");
			if(dataMap==null){
				throw new BusinessException(RspCode.MEMBER_NO_EXIST,RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
			}
			type = dataMap.get("type")+"";
		}else{
			throw new BusinessException((String) resMap.get("error_code"), (String) resMap.get("error_msg"));
		}
		boolean isType = false;
		if("5".equals(type)){
			if(toList.size() >= ShellConstant.SIGN_IN_DAY_EXPERIENCE){
				isType = true;
			}
		}else{
			if(toList.size() >= ShellConstant.SIGN_IN_DAY){
				isType = true;
			}
		}
		// 签到次数达到设定数，激活一张礼券
		if(isType){
			// 是否激活
			tempMap.put("is_activate", "Y");
			// 礼券激活后需要设置过期时间（有效期为3年）
			String coupons_validity_date = DateUtil.date2Str(now, DateUtil.fmt_yyyyMMdd);
			// 结束时间
			tempMap.put("coupons_validity",DateUtil.addDate(coupons_validity_date,DateUtil.fmt_yyyyMMdd,1,3));
			int lsn = -1;
			Map<String, Object> hashMap = new ConcurrentHashMap<>();
			hashMap.put("member_id", coupons.getMember_id());
			List<Map<String, Object>> maps = financeDao.selectCouponsLSN(hashMap);
			for (int i = 0; i < maps.size(); i++) {
				Map<String, Object> objectMap = maps.get(i);
				if (coupons.getMember_coupons_id().equals(objectMap.get("member_coupons_id")+"")) {
					lsn = i + 1;
					insertFDMemberCouponsLog(lsn,coupons.getMember_coupons_id(),coupons.getMember_id(),"2","lj_399","",now);
					break;
				}
			}
			logger.error("输出member_id: "+coupons.getMember_id()+"激活的礼卷, 输出第几张礼卷: "+lsn);
		}
		int lsn = -1;
		Map<String, Object> hashMap = new ConcurrentHashMap<>();
		hashMap.put("member_id", coupons.getMember_id());
		List<Map<String, Object>> maps = financeDao.selectCouponsLSN(hashMap);
		logger.info("输出member_id: "+coupons.getMember_id()+"的礼卷"+maps.size()+"张: ");
		for (int i = 0; i < maps.size(); i++) {
			Map<String, Object> objectMap = maps.get(i);
			if (coupons.getMember_coupons_id().equals(objectMap.get("member_coupons_id")+"")) {
				lsn = i + 1;
				insertFDMemberCouponsLog(lsn,coupons.getMember_coupons_id(),coupons.getMember_id(),"1","lj_399","",now);
				break;
			}
		}
		logger.error("输出member_id: "+coupons.getMember_id()+"的礼卷, 输出第几张礼卷: "+lsn);
		tempMap.put("member_coupons_id", coupons.getMember_coupons_id());
		tempMap.put("sign_data", FastJsonUtil.toJson(toList));
		Integer integer = financeDao.updateMemberCouByMemberId(tempMap);
		if (integer == 0) {
			throw new BusinessException("签到失败", "签到失败,请稍后再试");
		}
		Map<String, Object> resultMap = new HashMap<>();
		//TODO 签到成功后,随机海报图(后期再实现,先写死)
//		String[] arrSignInSuccAd = new String[] {"https://app-static.huigujia.cn/communityDocs/ads/xlpromotion.png",
//				"https://app-static.huigujia.cn/communityDocs/bkcqapp/sign2.png",
//				"https://app-static.huigujia.cn/communityDocs/bkcqapp/sign3.png",
//				"https://app-static.huigujia.cn/communityDocs/bkcqapp/sign4.png"};
//		int r=(int) (Math.random()*arrSignInSuccAd.length);
//      resultMap.put("url", arrSignInSuccAd[r]);
		resultMap.put("url", "https://app-static.huigujia.cn/communityDocs/ads/xlpromotion.png");
		resultMap.put("signInDay", toList.size());
		result.setResultData(resultMap);
	}

	public void insertFDMemberCouponsLog(int lsn, String couponsId, String memberId, String operType ,String couponsType,String orderNo,Date date){
		Map<String, Object> sqlDataMap = new HashMap<>();
		sqlDataMap.put("coupons_id", couponsId);
		sqlDataMap.put("member_id", memberId);
		sqlDataMap.put("oper_type", operType);
		sqlDataMap.put("coupons_type",couponsType);
		sqlDataMap.put("order_no", orderNo);
		sqlDataMap.put("lsn", lsn);
		sqlDataMap.put("created_time",date);
		financeDao.insertFDMemberCouponsLog(sqlDataMap);
	}

	public static void main(String[] args) {
		Date now = new Date();
		String startStr = DateUtil.date2Str(now,DateUtil.fmt_yyyyMMdd);
		System.out.println(startStr);
		System.out.println(DateUtil.date2Str(DateUtil.str2Date(DateUtil.addDate(startStr,DateUtil.fmt_yyyyMMdd,1,3),DateUtil.fmt_yyyyMMdd),DateUtil.fmt_yyyyMMdd));
	}
	/**
	 * 查找当月礼券签到的信息
	 */
	@Override
	public void findGiftCouponSignInfo(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id"});
		// 9:36 2019/5/15 欧少辉
		Map<String, Object> resultMap = new HashMap<>();
		List<FDMemberCoupons> memberCouponsCurr = null;
		List<FDMemberCoupons> memberCouponsNext = null;
		//查询会员信息
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		reqParams.put("service", "consumer.findConsumerById");
		paramMap.put("consumer_id", paramsMap.get("member_id").toString());
		reqParams.put("params", FastJsonUtil.toJson(paramMap));
		String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
		Map<String, Object> resMap = FastJsonUtil.jsonToMap(resultStr);
		// 是否体验会员
		String type = null;
		if (((String) resMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> dataMap = (Map<String, Object>) resMap.get("data");
			if(dataMap==null){
				throw new BusinessException(RspCode.MEMBER_NO_EXIST,RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
			}
			type = dataMap.get("type").toString();
		}else{
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		boolean flag = "5".equals(type)== true ? true : false;
		paramsMap.put("coupons_key", "lj_399");
		Date actiDate = DateUtil.subMonthOne();
		// 体验会员只有一张礼券
		if(flag){
			// 查找当月可用礼券的激活时间区间
			paramsMap.put("actiDate", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
			paramsMap.put("is_activate", "N");
			memberCouponsCurr = financeDao.selectFdMemberCoupons(paramsMap);
			memberCouponsNext = memberCouponsCurr;
		}else {
			String dateStr = DateUtil.date2Str(actiDate, DateUtil.fmt_yyyyMMdd);
			paramsMap.put("actiDate", dateStr);
			memberCouponsNext = financeDao.selectFdMemberCoupons(paramsMap);
			// 查找当月可用礼券的激活时间区间
			paramsMap.put("actiDate", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
			memberCouponsCurr = financeDao.selectFdMemberCoupons(paramsMap);
		}
		if (memberCouponsCurr == null || memberCouponsCurr.size() == 0 || memberCouponsNext == null || memberCouponsNext.size() == 0) {
			resultMap.put("have_next", "N");// 没有下一张礼券
			result.setResultData(resultMap);
			return;
		}

		FDMemberCoupons couponsNext = memberCouponsNext.get(0);
		FDMemberCoupons couponsCurr = memberCouponsCurr.get(0);
		String sign_data_nt = couponsNext.getSign_data();
		String sign_data_repair = couponsNext.getSign_data_repair();
		List<String> toListNt = FastJsonUtil.jsonToList(sign_data_nt);
		List<String> toList_repair_Nt = FastJsonUtil.jsonToList(sign_data_repair);
		if(toListNt==null){
			toListNt = new ArrayList<>();
		}
		if(toList_repair_Nt==null){
			toList_repair_Nt = new ArrayList<>();
		}
		String nowDate = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
		// 查询用户签到的信息
		if (toListNt!=null&&toListNt.size()>0) {
			int size = toListNt.size();
			String lastDay = toListNt.get(size - 1);
			if (lastDay.equals(nowDate)) {
				resultMap.put("is_signIn", "Y");
			} else {
				resultMap.put("is_signIn", "N");
			}
			int temp = 0;
			if(flag){
				temp = ShellConstant.SIGN_IN_DAY_EXPERIENCE - size;
			}else {
				temp = ShellConstant.SIGN_IN_DAY - size;
			}
			if (temp > 0) {
				resultMap.put("need_signIn", temp);
			} else {
				resultMap.put("need_signIn", 0);
			}
		} else {
			resultMap.put("is_signIn", "N");
			if(flag){
				resultMap.put("need_signIn", ShellConstant.SIGN_IN_DAY_EXPERIENCE);
			}else {
				resultMap.put("need_signIn", ShellConstant.SIGN_IN_DAY);
			}
			resultMap.put("sign_data", new ArrayList<>());
		}
		resultMap.put("coupons_validity_start", DateUtil.date2Str(couponsCurr.getCoupons_validity_start(), DateUtil.fmt_yyyyMMdd));
		resultMap.put("coupons_validity", DateUtil.date2Str(couponsCurr.getCoupons_validity_end(), DateUtil.fmt_yyyyMMdd));
		resultMap.put("repair_count", couponsCurr.getRepair_count());
		resultMap.put("repair_count_all", couponsCurr.getRepair_count_all());
		resultMap.put("shell_count", couponsCurr.getShell_count());
		resultMap.put("member_coupons_id", couponsNext.getMember_coupons_id());
		resultMap.put("is_activate", couponsNext.getIs_activate());
		if(flag){
			resultMap.put("signIn_day", ShellConstant.SIGN_IN_DAY_EXPERIENCE);
		}else {
			resultMap.put("signIn_day", ShellConstant.SIGN_IN_DAY);// 文本内容 需要签到25天
		}
		resultMap.put("sign_data", toListNt);
		resultMap.put("sign_data_repair", toList_repair_Nt);
		result.setResultData(resultMap);
	}

	@Override
	public void findGiftCouponTransform(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "coupons_key" });
		paramsMap.put("is_activate", "N");
		paramsMap.put("is_activate_to", "Y");
		paramsMap.put("coupons_mun", "0");
		paramsMap.put("to_couponsKey", "jh_399");
		paramsMap.put("coupons_validity_start", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
		paramsMap.put("coupons_validity_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
		String addDate = DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 2, 1);
		paramsMap.put("coupons_validity", DateUtil.addDate(addDate, DateUtil.fmt_yyyyMMdd, 3, -1));//减去一天,正好是一个月的时间
		paramsMap.put("coupons_validity", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 1, 3));
		
		
		Map<String, Object> resMap = new HashMap<>();
		try{
			resMap = financeDao.selectFdMemberCouponsCouponsKey(paramsMap);
		}catch (Exception e){
			logger.error(e);
		}

		int i = financeDao.updateFdMemberCouponsCouponsKey(paramsMap);
		if (i == 0) {
			throw new BusinessException("转换失败", "礼券转换失败,请稍后再试");
		}

		//加日期
		//Map<String, Object> resCouponsMap = new HashMap<>();
		//resCouponsMap.put("coupons_validity", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 1, 3));
		//resCouponsMap.put("member_coupons_id", resMap.get("member_coupons_id"));
		//financeDao.updateFdMemberCoupons(resCouponsMap);
		
		
		//
		try{
			logger.error("4输出coupons："+resMap);
//			if(resMap!=null) {
				String member_coupons_id =resMap.get("member_coupons_id")+"";
//				if (StringUtils.isNotBlank(member_coupons_id)) {
					int lsn = -1;
					Map<String, Object> hashMap = new ConcurrentHashMap<>();
					hashMap.put("member_id", paramsMap.get("member_id"));
					List<Map<String, Object>> maps = financeDao.selectCouponsLSN(hashMap);
					logger.error("4输出member_id："+paramsMap.get("member_id")+"的礼卷"+maps.size()+"张: ");
					for (int y = 0; y < maps.size(); y++) {
						Map<String, Object> objectMap = maps.get(y);
						if (member_coupons_id.equals(objectMap.get("member_coupons_id")+"")) {
							lsn = y + 1;
							Map<String, Object> sqlDataMap = new HashMap<>();
							sqlDataMap.put("coupons_id", member_coupons_id);
							sqlDataMap.put("member_id", paramsMap.get("member_id"));
							sqlDataMap.put("oper_type", "4");
							sqlDataMap.put("coupons_type", "jh_399");
							sqlDataMap.put("order_no", null);
							sqlDataMap.put("lsn", lsn);
							sqlDataMap.put("created_time", new Date());
							financeDao.insertFDMemberCouponsLog(sqlDataMap);
							break;
						}
					}
					logger.error("4输出member_id: "+member_coupons_id+"的礼卷, 输出第几张礼卷: "+lsn);
//
//				}
//			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void updateGiftCouponMun(Map<String, Object> paramsMap, ResultData result) throws Exception {
		String coupons_ids = StringUtil.formatStr(paramsMap.get("coupons_ids"));
		if(StringUtil.isBlank(coupons_ids)) {
			//如果没有礼券id,直接结束方法
			return;
		}
		List<String> coupons_id_in = StringUtil.str2List(StringUtil.formatStr(paramsMap.get("coupons_ids")), ",");
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("coupons_id_in", coupons_id_in);
		tempMap.put("coupons_key", "jh_399");
		tempMap.put("coupons_mun", "1");
		int i = financeDao.updateGiftCouponMun(tempMap);
		if(i == 0) {
			throw new BusinessException("修改礼券失败", "修改礼券失败");
		}
	}

	@Override
	public void findGiftCouponType(Map<String, Object> paramsMap, ResultData result) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String, Object>>  list =   new ArrayList<Map<String, Object>>();
		resultMap.put("id", "my");
		resultMap.put("name", "免邮券");
		list.add(resultMap);
		Map<String, Object> resultMap1 = new HashMap<>();
		resultMap1.put("id", "lq");
		resultMap1.put("name", "优惠券");
		list.add(resultMap1);
		Map<String, Object> resultMap2 = new HashMap<>();
		resultMap2.put("id", "y_lj");
		resultMap2.put("name", "精品礼券");
		list.add(resultMap2);
		Map<String, Object> resultMap3 = new HashMap<>();
		resultMap3.put("id", "n_lj");
		resultMap3.put("name", "未激活礼券");
		list.add(resultMap3);
		Map<String, Object> resultMap4 = new HashMap<>();
		resultMap4.put("id", "jh");
		resultMap4.put("name", "尖货礼券");
		list.add(resultMap4);
		result.setResultData(list);
	}

	@Override
	public void manageRateToBalance(Map<String, Object> paramsMap, ResultData result) throws Exception {
		//查询所有掌柜的ID
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "consumer.memberDistributionManagerId");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
		List<String>  managerList     =    (List<String>) dataMap.get("managerList");
		if(null != managerList &&  managerList.size() >0 ) {
			for(String memberId:managerList) {
				this.managerRateBalance(memberId);
			}
		}
	}
	
	public void managerRateBalance(String memberId) throws Exception {
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("member_id", memberId);
		BigDecimal rateAmount = financeDao.getMemberManagerRateMonCount(tempMap);
		//如果金额小于或等于0 直接跳过该方法;
		if (rateAmount.compareTo(new BigDecimal("0")) != 1) {
			return;
		}
		tempMap.put("trade_cash_balance", rateAmount);
		tempMap.put("invite_balance", rateAmount.negate());
		financeDao.updateFDMemberAsset(tempMap);
		// 写入红包记录
		tempMap.clear();
		tempMap.put("member_id", memberId);
		FDMemberAsset memberAsset = financeDao.selectFDMemberAsset(tempMap);
		BigDecimal balance = memberAsset.getCash_balance();
		BigDecimal trade_amount = rateAmount;
		BigDecimal cash_balance = MoneyUtil.moneySub(balance, trade_amount);
		FDMemberRebateLog memberRebateLog = new FDMemberRebateLog();
		memberRebateLog.setMember_id(memberId);
		memberRebateLog.setCategory("expenditure");
		String tradeMount = "-" + rateAmount;
		memberRebateLog.setCash_money(new BigDecimal(tradeMount).setScale(2, BigDecimal.ROUND_HALF_UP));
		memberRebateLog.setCreate_time(new Date());
		memberRebateLog.setType("nextdirect");
		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("detail", "掌柜返佣转入红包");
		remarkMap.put("交易金额", trade_amount);
		String itemJSONObj = JSON.toJSONString(remarkMap);
		// 增加日志记录
		memberRebateLog.setPre_balance(balance);
		memberRebateLog.setBalance(cash_balance);
		memberRebateLog.setRemark(itemJSONObj);
		financeDao.insertFdMemberRebateLog(memberRebateLog);
	}

	@Override
	public void testManageRateToBalance(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id"});
		String memberId =  StringUtil.formatStr(paramsMap.get("member_id"));
		this.managerRateBalance(memberId);
	}

	@Override
	public void activationFormerOneGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id"});
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("member_id", paramsMap.get("member_id"));
		tempMap.put("is_activate", "N");
		tempMap.put("coupons_key", "lj_399");
		tempMap.put("is_activate_to", "Y");
		financeDao.updateFdMemberCouponsIsActivate(tempMap);
	}

	/**
	 * 查询上个月的签到信息
	 */
	@Override
	public void findGiftCouponSignInfoForLastMonth(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		Map<String, Object> resultMap = new HashMap<>();

		String dateStr = DateUtil.addDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 2, -1);
		paramsMap.put("actiDate", dateStr);
		paramsMap.put("coupons_key", "lj_399");
//		paramsMap.put("is_first", "0");
		List<FDMemberCoupons> memberCouponsLast = financeDao.selectFdMemberCoupons(paramsMap);
		if(memberCouponsLast==null||memberCouponsLast.size()<=0){
			throw new BusinessException("查询上个月签到", "没有上个月签到信息");
		}
		paramsMap.put("actiDate", DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
		paramsMap.put("coupons_key", "lj_399");
		List<FDMemberCoupons> memberCouponsNext = financeDao.selectFdMemberCoupons(paramsMap);
		if(memberCouponsNext==null||memberCouponsNext.size()<=0){
			throw new BusinessException("查询当月签到", "没有当月签到信息");
		}


		FDMemberCoupons couponsLast = memberCouponsLast.get(0);
		FDMemberCoupons couponsNext = memberCouponsNext.get(0);
		String sign_data_next = couponsNext.getSign_data();
		String sign_data_repair_next = couponsNext.getSign_data_repair();
		Integer repair_count = couponsLast.getRepair_count();
		Integer repair_count_all = couponsLast.getRepair_count_all();
		logger.info("findGiftCouponSignInfoForLastMonth--sign_data_next: "+sign_data_next);
		logger.info("findGiftCouponSignInfoForLastMonth--sign_data_repair_next："+sign_data_repair_next);
		if(StringUtils.isBlank(sign_data_next)){
			sign_data_next = null;
		}
		if(StringUtils.isBlank(sign_data_repair_next)){
			sign_data_repair_next = null;
		}
		List<String> toListNext = FastJsonUtil.jsonToList(sign_data_next);
		List<String> toListRepairNext = FastJsonUtil.jsonToList(sign_data_repair_next);
		if(toListNext==null){
			toListNext = new ArrayList<>();
		}
		if(toListRepairNext==null){
			toListRepairNext = new ArrayList<>();
		}
		// 查询用户签到的信息
		if (toListNext!=null&&toListNext.size()>0) { //StringUtil.isNotBlank(sign_data)
			int size = toListNext.size();
			int i = ShellConstant.SIGN_IN_DAY - size;
			if (i > 0) {
				resultMap.put("need_signIn", i);
			} else {
				resultMap.put("need_signIn", 0);
			}
		} else {
			resultMap.put("is_signIn", "N");
			resultMap.put("need_signIn", ShellConstant.SIGN_IN_DAY);
			resultMap.put("sign_data", new ArrayList<>());
		}
		resultMap.put("coupons_validity_start",DateUtil.date2Str(couponsLast.getCoupons_validity_start(), DateUtil.fmt_yyyyMMdd));
		resultMap.put("coupons_validity", DateUtil.date2Str(couponsLast.getCoupons_validity_end(), DateUtil.fmt_yyyyMMdd));
		resultMap.put("member_coupons_id", couponsLast.getMember_coupons_id());
		resultMap.put("shell_count", couponsLast.getShell_count());
		resultMap.put("is_activate", couponsNext.getIs_activate());
		resultMap.put("signIn_day", ShellConstant.SIGN_IN_DAY);// 文本内容 需要签到25天
		resultMap.put("sign_data", toListNext);
		resultMap.put("sign_data_repair", toListRepairNext);
		resultMap.put("repair_count", repair_count);
		resultMap.put("repair_count_all", repair_count_all);
		result.setResultData(resultMap);
	}

	/**
	 * 更具手机号获取会员资产
	 * @param paramsMap
	 * @param result
	 */
	@Override
	public void findMemberAssetByMobile(Map<String, Object> paramsMap, ResultData result) throws Exception{
		ValidateUtil.validateParams(paramsMap, new String[] { "mobile" });

		//根据会员手机号查询会员member_id
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		String resultStr = null;
		reqParams.put("service", "member.memberInfoFindByMobile");
		paramMap.put("mobile", paramsMap.get("mobile")+"");
		paramMap.put("member_type_key",Constant.MEMBER_TYPE_CONSUMER);
		reqParams.put("params", FastJsonUtil.toJson(paramMap));
		resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
		if(StringUtils.isEmpty(resultStr)){
			logger.info("finance.consumer.findMemberAssetByMobile请求service:member.memberInfoFindByMobile返回空");
			throw new BusinessException(RspCode.DATA_SOURCE_ERROR,"根据手机号查询用户ID失败" );
		}
		
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		String member_id = null;
		if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			if(dateMap==null){
				throw new BusinessException(RspCode.DATA_SOURCE_ERROR,"根据手机号查询用户ID返回值data缺失" );
			}
			member_id = dateMap.get("consumer_id")+"";
		}else{
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}

		//根据member_id查询会员资产
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", member_id);
		FDMemberAsset memberAsset = financeDao.selectFDMemberAsset(tempMap);
		if(memberAsset==null){
			throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
		}

		Map<String,Object> assertMap = new HashMap<>();

		//获取用户优惠券信息
		List<FDMemberCoupons> memberCouponsInoList = financeDao.selectFdMemberCoupons(tempMap);
		List<Map<String,Object>> memberCouponsList = new ArrayList<>();
		Map<String,Object> couponMap = null;
		if(!CollectionUtils.isEmpty(memberCouponsInoList)){
			for (FDMemberCoupons coupon:memberCouponsInoList) {
				couponMap = new HashMap<>();
				couponMap.put("coupons_mun",coupon.getCoupons_mun());
				couponMap.put("coupons_validity_start",coupon.getCoupons_validity_start());
				couponMap.put("coupons_validity",coupon.getCoupons_validity());
				couponMap.put("coupons_key",coupon.getCoupons_key());
				couponMap.put("is_activate",coupon.getIs_activate());
				couponMap.put("sign_data",coupon.getSign_data()==null?"":coupon.getSign_data());
				memberCouponsList.add(couponMap);
			}
		}
		assertMap.put("memberCouponsList",memberCouponsList);
		assertMap.put("mobile",paramsMap.get("mobile"));
		assertMap.put("cash_balance",memberAsset.getCash_balance());
		assertMap.put("shell",memberAsset.getShell());
		assertMap.put("cash_coupon",memberAsset.getCash_coupon());
		result.setResultData(assertMap);
	}

}
