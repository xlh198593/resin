package com.meitianhui.finance.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meitianhui.common.util.*;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.dao.TransactionDao;
import com.meitianhui.finance.street.dao.FdMemberAssetDAO;
import com.meitianhui.finance.street.dao.FdMemberShellLogDAO;
import com.meitianhui.finance.street.entity.FdMemberAsset;
import com.meitianhui.finance.street.entity.FdMemberShellLog;
import com.meitianhui.finance.street.handler.PayHandler;
import com.meitianhui.finance.util.HttpRequestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.finance.dao.FinanceDao;
import com.meitianhui.finance.entity.FDMemberAsset;
import com.meitianhui.finance.entity.FDMemberCashLog;
import com.meitianhui.finance.entity.FDMemberPointLog;
import com.meitianhui.finance.entity.FDMemberShellLog;
import com.meitianhui.finance.service.ShellConsumerService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShellConsumerServiceImpl implements ShellConsumerService {

	private static final Logger logger = Logger.getLogger(ShellConsumerServiceImpl.class);
	
	@Autowired
	private FinanceDao financeDao;

	@Autowired
	private FdMemberShellLogDAO fdMemberShellLogDAO;

	@Autowired
	private FdMemberAssetDAO fdMemberAssetDAO;

	@Override
	public void consumerFdMemberAssetFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] {"member_id"});
		logger.debug("开始查询消费者资产信息====>  member_id = "+paramsMap.get("member_id"));
		FDMemberAsset memberAsset = financeDao.selectFDMemberAsset(paramsMap);
//		if(memberAsset == null){
//			throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MEMBER_NO_EXIST);
//		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("member_id", memberAsset.getMember_id());
		resultMap.put("shell", memberAsset.getShell());
		resultMap.put("invite_balance", memberAsset.getInvite_balance());
		resultMap.put("cash_balance", memberAsset.getCash_balance().setScale(2, BigDecimal.ROUND_HALF_UP));
		resultMap.put("gift_certificates_368", memberAsset.getGift_certificates_368());
		resultMap.put("gift_certificates_188", memberAsset.getGift_certificates_188());
		resultMap.put("gift_certificates_68", memberAsset.getGift_certificates_68());
		result.setResultData(resultMap);
		logger.debug("结束查询消费者资产信息====>  member_id = "+paramsMap.get("member_id"));
	}

	@Override
	public void consumerFdMemberAssetShellLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		logger.debug("开始查询消费者贝壳资产信息====>  member_id = " + paramsMap.get("member_id") + "  member_type_key = "
				+ paramsMap.get("member_type_key"));
		List<FDMemberShellLog> list = financeDao.selectFDMemberShellLog(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (FDMemberShellLog fDMemberShellLog : list) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("amount", fDMemberShellLog.getAmount());
				tempMap.put("tracked_date",
						DateUtil.date2Str(fDMemberShellLog.getTracked_date(), DateUtil.fmt_yyyyMMddHHmmss));
				String remark = fDMemberShellLog.getRemark();
				Map<String, Object> jsonToMap = FastJsonUtil.jsonToMap(remark);
				String detail = (String) jsonToMap.get("detail");
				tempMap.put("detail", detail);
				resultList.add(tempMap);
			}
		}
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("list", resultList);
		result.setResultData(resultData);
		logger.debug("结束查询消费者贝壳资产信息====>  member_id = " + paramsMap.get("member_id"));
	}

	@Override
	public void consumerFdMemberAssetPointLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		logger.debug("开始查询消费者积分资产信息====>  member_id = " + paramsMap.get("member_id") + "  member_type_key = "
				+ paramsMap.get("member_type_key"));
		List<FDMemberPointLog> list = financeDao.selectFDMemberPointLog(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (FDMemberPointLog fDMemberShellLog : list) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("amount", fDMemberShellLog.getAmount());
				tempMap.put("tracked_date",
						DateUtil.date2Str(fDMemberShellLog.getTracked_date(), DateUtil.fmt_yyyyMMddHHmmss));
				String remark = fDMemberShellLog.getRemark();
				Map<String, Object> jsonToMap = FastJsonUtil.jsonToMap(remark);
				String detail = (String) jsonToMap.get("detail");
				tempMap.put("detail", detail);
				resultList.add(tempMap);
			}
		}
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("list", resultList);
		result.setResultData(resultData);
		logger.debug("结束查询消费者积分资产信息====>  member_id = " + paramsMap.get("member_id"));

	}

	@Override
	public void consumerFdMemberAssetCashLogFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		logger.debug("开始查询消费者现金资产信息====>  member_id = " + paramsMap.get("member_id") + "  member_type_key = "
				+ paramsMap.get("member_type_key"));
		List<FDMemberCashLog> list = financeDao.selectFDMemberCashLogNew(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (FDMemberCashLog fDMemberCashLog : list) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("amount", fDMemberCashLog.getAmount());
				tempMap.put("tracked_date",
						DateUtil.date2Str(fDMemberCashLog.getTracked_date(), DateUtil.fmt_yyyyMMddHHmmss));
				String remark = fDMemberCashLog.getRemark();
				Map<String, Object> jsonToMap = FastJsonUtil.jsonToMap(remark);
				String detail = (String) jsonToMap.get("detail");
				tempMap.put("detail", detail);
				resultList.add(tempMap);
			}
		}
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("list", resultList);
		result.setResultData(resultData);
		logger.debug("结束查询消费者现金资产信息====>  member_id = " + paramsMap.get("member_id"));

	}
	
	
	@Override
	public void memberRabateListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		logger.info("开始查询会员得到返佣明细====>  member_id = " + paramsMap.get("member_id"));
		List<Map<String,Object>> list = financeDao.selectMemberRateLog(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<>();
		if(null != list &&  list.size()>0) {
			for (Map<String,Object> memberMoneyMap : list) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("amount",String.valueOf(new BigDecimal(memberMoneyMap.get("amount").toString())));
				tempMap.put("tracked_date",
						DateUtil.date2Str((Date) memberMoneyMap.get("tracked_date"), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("mobile", StringUtil.formatStr(memberMoneyMap.get("mobile")));
				tempMap.put("inviteMobile", StringUtil.formatStr(memberMoneyMap.get("invite_mobile")));
				String remark = StringUtil.formatStr(memberMoneyMap.get("remark"));
				String category = StringUtil.formatStr(memberMoneyMap.get("category"));
				
				if(remark.indexOf("{")!=-1) {
					Map<String, Object> jsonToMap = FastJsonUtil.jsonToMap(remark);
					String detail = (String) jsonToMap.get("detail");
					tempMap.put("detail", detail);
				}else {
					if(category.equals("frozen")) {
						tempMap.put("detail", "提现");
					}
					if(category.equals("expenditure")) {
						tempMap.put("detail", "提现");
					}
					if(category.equals("activated")) {
						tempMap.put("detail", "提现失败");
					}
				}
				//查询提现的记录，放入到list传到前台
				resultList.add(tempMap);
			}
		}
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("list", resultList);
		result.setResultData(resultData);
		logger.info("结束查询会员得到返佣明细====>  member_id = " + paramsMap.get("member_id"));
	}

	@Override
	public void memberManagerRabateListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		logger.info("开始查询掌柜得到返佣明细====>  member_id = " + paramsMap.get("member_id"));
		paramsMap.put("type", "nextdirect");
		List<Map<String,Object>> list = financeDao.selectMemberMoneyLog(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<>();
		if(null != list &&  list.size()>0) {
			for (Map<String,Object> memberMoneyMap : list) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("amount",String.valueOf(new BigDecimal(memberMoneyMap.get("amount").toString())));
				tempMap.put("tracked_date",
						DateUtil.date2Str((Date) memberMoneyMap.get("tracked_date"), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("mobile", StringUtil.formatStr(memberMoneyMap.get("mobile")));
				tempMap.put("inviteMobile", StringUtil.formatStr(memberMoneyMap.get("invite_mobile")));
				String remark = StringUtil.formatStr(memberMoneyMap.get("remark"));
				String category = StringUtil.formatStr(memberMoneyMap.get("category"));
				
				if(remark.indexOf("{")!=-1) {
					Map<String, Object> jsonToMap = FastJsonUtil.jsonToMap(remark);
					String detail = (String) jsonToMap.get("detail");
					tempMap.put("detail", detail);
				}else {
					if(category.equals("frozen")) {
						tempMap.put("detail", "提现");
					}
					if(category.equals("expenditure")) {
						tempMap.put("detail", "提现");
					}
					if(category.equals("activated")) {
						tempMap.put("detail", "提现失败");
					}
				}
				//查询提现的记录，放入到list传到前台
				resultList.add(tempMap);
			}
		}
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("list", resultList);
		result.setResultData(resultData);
		logger.info("结束查询掌柜得到返佣明细====>  member_id = " + paramsMap.get("member_id"));
	}

	@Override
	public void memberManagerRabateCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		paramsMap.put("type", "nextdirect");
		BigDecimal rateCount =  financeDao.getMemberManagerRateCount(paramsMap);
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("rateCount", null ==rateCount?0:rateCount);
		result.setResultData(resultData);
	}

	/**
	 * 新用户或已注册未购买会员的用户赠送68贝壳
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void registerUserReceiveShell(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		String memberId = StringUtil.formatStr(paramsMap.get("member_id"));
		BigDecimal trade_amount = new BigDecimal("68");

		//查询用户基本信息
		Map<String, Object> map = HttpRequestUtils.findConsumerById(memberId);
		if(map==null){
			throw new BusinessException(RspCode.MEMBER_NO_EXIST,RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
		}

//		Integer type = Integer.parseInt(StringUtil.formatStr(map.get("type")));
		//不是新注册没付过钱的账号,直接返回不能领取
//		if(type!=3){
//			throw new BusinessException(RspCode.NOT_NEW_USER,RspCode.MSG.get(RspCode.NOT_NEW_USER));
//		}

		//判断是否已经领过
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", memberId);
		tempMap.put("amount",trade_amount.intValue());
		tempMap.put("remark", Constant.ORDER_TYPE_REGISTER);

		Map<String,Object> shellLog = fdMemberShellLogDAO.findShellLogByMemberIdAndRemark(tempMap);
		if(shellLog!=null){//已经领取过
			throw new BusinessException(RspCode.USER_RECEIVED,RspCode.MSG.get(RspCode.USER_RECEIVED));
		}

		//消费者增加贝壳
		tempMap.clear();
		tempMap.put("member_id", memberId);
		FDMemberAsset consumer = financeDao.selectFDMemberAsset(tempMap);
		FdMemberShellLog fdMemberShellLog = new FdMemberShellLog();
		fdMemberShellLog.setMemberTypeKey("consumer");
		fdMemberShellLog.setMemberId(consumer.getMember_id());
		fdMemberShellLog.setCategory(Constant.CATEGORY_INCOME);
		fdMemberShellLog.setPreBalance(consumer.getShell().intValue());
		fdMemberShellLog.setAmount(trade_amount.intValue());
		fdMemberShellLog.setBalance(consumer.getShell().intValue() +trade_amount.intValue());
		fdMemberShellLog.setTrackedDate(new Date());

		Map<String, Object> remarkMap = new HashMap<String, Object>();
		remarkMap.put("detail", "注册赠送68贝壳");
		remarkMap.put("transaction_member_type_key", "consumer");
		remarkMap.put("order_type_key", Constant.ORDER_TYPE_REGISTER);

		fdMemberShellLog.setRemark(FastJsonUtil.toJson(remarkMap));
		fdMemberShellLogDAO.insertSelective(fdMemberShellLog);

		FdMemberAsset updateConsumer = new FdMemberAsset();
		updateConsumer.setAssetId(consumer.getAsset_id());
		updateConsumer.setShell(trade_amount.longValue());
		updateConsumer.setModifiedDate(new Date());
		fdMemberAssetDAO.updateByPrimaryKeySelective(updateConsumer);

		// 公司减少贝壳
		FdMemberAsset company = fdMemberAssetDAO.selectByMemberInfo("company", PayHandler.SHELL_ALI_ASSET_ID);
		FdMemberShellLog companyFdMemberShellLog = new FdMemberShellLog();
		companyFdMemberShellLog.setMemberTypeKey("company");
		companyFdMemberShellLog.setMemberId(PayHandler.SHELL_ALI_ASSET_ID);
		companyFdMemberShellLog.setCategory(Constant.CATEGORY_EXPENDITURE);
		companyFdMemberShellLog.setPreBalance(company.getShell().intValue());
		companyFdMemberShellLog.setAmount(-trade_amount.intValue());
		companyFdMemberShellLog.setBalance(company.getShell().intValue() - trade_amount.intValue());
		companyFdMemberShellLog.setTrackedDate(new Date());

		remarkMap.clear();
		remarkMap.put("detail", "注册赠送68贝壳");
		remarkMap.put("transaction_member_type_key", "company");
		remarkMap.put("order_type_key", Constant.ORDER_TYPE_REGISTER);
		companyFdMemberShellLog.setRemark(FastJsonUtil.toJson(remarkMap));
		fdMemberShellLogDAO.insertSelective(companyFdMemberShellLog);

		FdMemberAsset updateCompany = new FdMemberAsset();
		updateCompany.setAssetId(company.getAssetId());
		updateCompany.setShell(-trade_amount.longValue());
		updateCompany.setModifiedDate(new Date());
		fdMemberAssetDAO.updateByPrimaryKeySelective(updateCompany);
	}

	/**
	 * 获取用户是否弹出新用户领取68贝壳的状态
	 */
	@Override
	public void getBulletBoxStatus(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		String memberId = StringUtil.formatStr(paramsMap.get("member_id"));
		BigDecimal trade_amount = new BigDecimal("68");
		int flage = 0;
		String msg = null;

		do{
			//查询用户基本信息
			Map<String, Object> map = HttpRequestUtils.findConsumerById(memberId);
			if(map==null){
				msg = "用户账号不存在";
				break;
			}

//			Integer type = Integer.parseInt(StringUtil.formatStr(map.get("type")));
			//不是新注册没付过钱的账号,直接返回不能领取
//			if(type!=3){
//				msg = "非新用户账号";
//				break;
//			}

			//判断是否已经领过
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("member_id", memberId);
			tempMap.put("amount",trade_amount.intValue());
			tempMap.put("remark", Constant.ORDER_TYPE_REGISTER);

			Map<String,Object> shellLog = fdMemberShellLogDAO.findShellLogByMemberIdAndRemark(tempMap);
			if(shellLog!=null){//已经领取过
				msg = "已领取";
				break;
			}

			flage = 1;
			msg = "可领取";
		}while (false);

		Map<String,Object> map = new HashMap<>();
		map.put("status",flage);
		map.put("msg",msg);
		result.setResultData(map);
	}

}
