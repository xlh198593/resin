package com.meitianhui.finance.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.dao.FinanceDao;
import com.meitianhui.finance.entity.FDMemberAsset;
import com.meitianhui.finance.entity.FDTransactions;
import com.meitianhui.finance.entity.FDTransactionsResult;
import com.meitianhui.finance.service.BusinessAffirmService;
import com.meitianhui.finance.service.TradeService;
@Service
public class BusinessAffirmServiceImpl implements BusinessAffirmService {

	private static final Logger logger = Logger.getLogger(TradeServiceImpl.class);
	
	@Autowired
	public FinanceDao financeDao;

	@Autowired
	public TradeService tradeService;
	
	@Autowired
	public RedisUtil redisUtil;

	@Override
	public List<FDTransactionsResult> insertFdTransactionsResult(Map<String, Object> paramsMap,ResultData result)
			throws BusinessException, SystemException, Exception {
		
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "transaction_no" });
			List<FDTransactions> listFDTransactions = financeDao.selectListFDTransactions(paramsMap);
			if (listFDTransactions == null || listFDTransactions.size()==0) {
				throw new BusinessException(RspCode.TRADE_NO_EXIST, RspCode.MSG.get(RspCode.TRADE_NO_EXIST));
			}
			List<FDTransactionsResult> listFDTransactionsResult = new ArrayList<>();
			for (FDTransactions fdTransactions : listFDTransactions) {
				FDTransactionsResult fDTransactionsResult  = new FDTransactionsResult();
				BeanConvertUtil.copyProperties(fDTransactionsResult, fdTransactions);
				BeanConvertUtil.mapToBean(fDTransactionsResult, paramsMap);
				fDTransactionsResult.setCreated_date(new Date());
				// 完善交易信息
				completeTransactionData(fDTransactionsResult);
			}
			// 写入交易结果表
			financeDao.insertListFDTransactionsResult(listFDTransactionsResult);
			return listFDTransactionsResult;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 完善交易数据
	 * 
	 * @param fDTransactionsResult
	 */
	public void completeTransactionData(final FDTransactionsResult fDTransactionsResult) {
		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			/**
			 * 如果买家不存在,将买家替换成每天惠,同理,如果卖家不存在,将卖家替换成每天惠
			 */
			tempMap.put("member_id", fDTransactionsResult.getBuyer_id());
			FDMemberAsset buyer = financeDao.selectFDMemberAsset(tempMap);
			if (null == buyer) {
				throw new BusinessException(RspCode.BUYER_NO_EXIST, "买家" + fDTransactionsResult.getBuyer_id() + "不存在");
			} else {
				fDTransactionsResult.setBuyer_member_type(buyer.getMember_type_key());
			}
			Map<String, String> userInfo = userInfoFindByMember(buyer.getMember_id(), buyer.getMember_type_key());
			fDTransactionsResult.setBuyer_name(userInfo.get("name"));
			/**
			fDTransactionsResult.setBuyer_contact(userInfo.get("contact"));
			 * 卖家信息补充
			 */
			tempMap.clear();
			tempMap.put("member_id", fDTransactionsResult.getSeller_id());
			FDMemberAsset seller = financeDao.selectFDMemberAsset(tempMap);
			if (null == seller) {
				throw new BusinessException(RspCode.SELLER_NO_EXIST,
						"卖家" + fDTransactionsResult.getSeller_id() + "不存在");
			} else {
				fDTransactionsResult.setSeller_member_type(seller.getMember_type_key());
			}
			userInfo = userInfoFindByMember(seller.getMember_id(), seller.getMember_type_key());
			fDTransactionsResult.setSeller_name(userInfo.get("name"));
			fDTransactionsResult.setSeller_contact(userInfo.get("contact"));
			userInfo.clear();
			tempMap.clear();
			tempMap = null;
			userInfo = null;
		} catch (BusinessException e) {
			logger.info(e);
		} catch (SystemException e) {
			logger.error("完成交易数据异常", e);
		} catch (Exception e) {
			logger.error("完成交易数据异常", e);
		}
	}

	private Map<String, String> userInfoFindByMember(String member_id, String member_type_key) {
		Map<String, String> user = new HashMap<String, String>();
		user.put("name", "");
		user.put("contact", "");
		String cacheKey = "[userInfoFindByMember_new]_" + member_id + member_type_key;
		try {
			Object obj = redisUtil.getObj(cacheKey);
			if (null != obj) {
				user = (Map<String, String>) obj;
				return user;
			}
			// 如果是公司账号，就不需要查询会员信息
			if (member_type_key.equals(Constant.MEMBER_TYPE_COMPANY)) {
				if (member_id.equals(Constant.MEMBER_ID_BUSINESS)) {
					user.put("name", "每天惠商贸公司");
					user.put("contact", "每天惠商贸公司");
				} else {
					user.put("name", "每天惠");
					user.put("contact", "每天惠");
				}
			} else {
				String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
				Map<String, String> reqParams = new HashMap<String, String>();
				Map<String, String> paramMap = new HashMap<String, String>();
				String resultStr = null;
				reqParams.put("service", "member.memberInfoFindByMemberId");
				paramMap.put("member_id", member_id);
				paramMap.put("member_type_key", member_type_key);
				reqParams.put("params", FastJsonUtil.toJson(paramMap));
				resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
					if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
						user.put("name", StringUtil.formatStr(dateMap.get("nick_name")));
						user.put("contact", StringUtil.formatStr(dateMap.get("mobile")));
					} else if (member_type_key.equals(Constant.MEMBER_TYPE_STORES)) {
						user.put("name", StringUtil.formatStr(dateMap.get("stores_name")));
						String contact = StringUtil.formatStr(dateMap.get("contact_person")) + "|"
								+ StringUtil.formatStr(dateMap.get("contact_tel"));
						if (!contact.equals("|")) {
							user.put("contact", contact);
						}
					} else if (member_type_key.equals(Constant.MEMBER_TYPE_SUPPLIER)) {
						user.put("name", StringUtil.formatStr(dateMap.get("supplier_name")));
						String contact = StringUtil.formatStr(dateMap.get("contact_person")) + "|"
								+ StringUtil.formatStr(dateMap.get("contact_tel"));
						if (!contact.equals("|")) {
							user.put("contact", contact);
						}
					}
				}
			}
			// 缓存数据，有效时间为一个小时
			redisUtil.setObj(cacheKey, user, 3600);
		} catch (SystemException e) {
			logger.error("交易信息完善异常", e);
		} catch (Exception e) {
			logger.error("交易信息完善异常", e);
		}
		return user;
	}
}
