package com.meitianhui.member.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisLock;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.member.constant.Constant;
import com.meitianhui.member.constant.RspCode;
import com.meitianhui.member.dao.AssistantApplicationDao;
import com.meitianhui.member.dao.ConsumerDao;
import com.meitianhui.member.dao.MemberDao;
import com.meitianhui.member.dao.MemberDistributionDao;
import com.meitianhui.member.dao.StoresDao;
import com.meitianhui.member.entity.MDConsumer;
import com.meitianhui.member.entity.MDMemberDistribution;
import com.meitianhui.member.entity.MDStores;
import com.meitianhui.member.entity.MDStoresServiceFee;
import com.meitianhui.member.service.MemberTaskService;

/**
 * 会员定时任务
 * 
 * @ClassName: MemberTaskServiceImpl
 * @author tiny
 * @date 2017年2月23日 下午3:18:39
 *
 */
@SuppressWarnings("unchecked")
@Service
public class MemberTaskServiceImpl implements MemberTaskService {

	private static final Logger logger = Logger.getLogger(MemberTaskServiceImpl.class);
	
	@Autowired
	public MemberDao memberDao;

	@Autowired
	public StoresDao storesDao;

	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private ConsumerDao consumerDao;
	
	@Autowired
	private AssistantApplicationDao AssistantApplicationDao;
	
	@Autowired
	private  MemberDistributionDao   memberDistributionDao;
	

	@Override
	public void handleAssistantServiceFree(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = "[task_assistantServiceFree]";
		try {
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();
			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				return;
			}
			redisUtil.setStr(lockKey, lockKey, 600);
			
			//1检索  1.1已经锁定  1.2业务员（助教）标识 为空    1.3时间是小于当前时间的三天后
			// 如果这三个条件都满足就证明 可以把门店对应的助教申请单改为驳回
			Map<String , Object> tempMap = new HashMap<>();
			tempMap.put("assistant_expired_date", new Date());
			tempMap.put("is_assistant_locked", "Y");
			List<MDStores> mdStoresList = storesDao.selectMDStores(tempMap);
			//2遍历所获得的门店ID对应的助教申请单 改为驳回
			for (int i = 0; i < mdStoresList.size(); i++) {
				tempMap.clear();
				tempMap.put("stores_id", mdStoresList.get(i));
				tempMap.put("audit_status", "reject");
				AssistantApplicationDao.updateAssistantApplication(tempMap);
			}
			
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}
	@Override
	public void handleFreezeServiceFree(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = "[task_freezeServiceFree]";
		try {
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();
			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				return;
			}
			redisUtil.setStr(lockKey, lockKey, 600);

			// 冻结联盟店
			paramsMap.put("stores_type_key", Constant.STORES_TYPE_02);
			List<String> businessStatusList = new ArrayList<String>();
			businessStatusList.add("TDJD_01");
			businessStatusList.add("TDJD_02");
			businessStatusList.add("TDJD_03");
			paramsMap.put("business_status_key_in", businessStatusList);
			List<MDStores> mDStoresList = storesDao.selectMDStoresForServiceFree(paramsMap);
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String tracked_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMM);
			Map<String, Object> tempMap = new HashMap<String, Object>();
			for (MDStores mDStores : mDStoresList) {
				tempMap.clear();
				tempMap.put("tracked_date", tracked_date);
				tempMap.put("stores_id", mDStores.getStores_id());
				tempMap.put("category", "freeze_service_free");
				Map<String, Object> logMap = memberDao.selectMDStoresScheduleLog(tempMap);
				if (null != logMap) {
					continue;
				}
				// 冻结金额
				resultMap.clear();
				reqParams.clear();
				bizParams.clear();
				reqParams.put("service", "finance.balanceFreeze");
				bizParams.put("data_source", Constant.DATA_SOURCE_SJLY_03);
				bizParams.put("member_id", mDStores.getStores_id());
				bizParams.put("member_type_key", Constant.MEMBER_TYPE_STORES);
				bizParams.put("amount", "98");
				bizParams.put("detail", "会员费冻结");
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				String resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
				resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"),
							(String) resultMap.get("error_msg"));
				}
				// 修改联盟店的服务费状态为1
				tempMap.clear();
				tempMap.put("stores_id", mDStores.getStores_id());
				tempMap.put("service_fee", "1");
				storesDao.updateMDStores(tempMap);
				// 记录日志
				tempMap.clear();
				tempMap.put("log_id", IDUtil.getUUID());
				tempMap.put("stores_id", mDStores.getStores_id());
				tempMap.put("category", "freeze_service_free");
				tempMap.put("tracked_date", new Date());
				tempMap.put("event", "冻结会员服务费");
				memberDao.insertMDStoresScheduleLog(tempMap);
				sendMsg(mDStores.getContact_tel(), "您的账户被冻结98元用作技术服务费，本月赚取2000金币可免除费用并自动解冻。");
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}

	@Override
	public void handleProServiceFree(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = "[task_proServiceFree]";
		try {
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();
			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				return;
			}
			redisUtil.setStr(lockKey, lockKey, 600);

			// 查询所有的门店
			List<String> businessStatusList = new ArrayList<String>();
			businessStatusList.add("TDJD_01");
			businessStatusList.add("TDJD_02");
			businessStatusList.add("TDJD_03");
			paramsMap.put("business_status_key_in", businessStatusList);
			List<MDStores> mDStoresList = storesDao.selectMDStoresForServiceFree(paramsMap);
			String tracked_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMM);
			Map<String, Object> tempMap = new HashMap<String, Object>();
			for (MDStores mDStores : mDStoresList) {
				// 检测是本月会员是否已经做了结算,如果已经完成了结算，则跳过结算
				tempMap.clear();
				tempMap.put("tracked_date", tracked_date);
				tempMap.put("stores_id", mDStores.getStores_id());
				tempMap.put("category", "settlement_service_free");
				Map<String, Object> logMap = memberDao.selectMDStoresScheduleLog(tempMap);
				if (null != logMap) {
					continue;
				}
				if (mDStores.getStores_type_key().equals(Constant.STORES_TYPE_03)) {
					/**
					 * 处理加盟店(检测金币余额是否大于等于2000,如果是,则进行扣除金币,然后送店东98元)
					 **/
					franchiseServiceFee(mDStores);
				} else {
					/**
					 * 处理联盟店逻辑 检测是否服务费状态是否为1,如果为1,检测金币是否大于等于2000,如果金币余额不够,则扣除金币,
					 * 解冻98元, 如果金币余额不够，检测可用余额是否大于0，如果是，则进行解冻98元,然后扣除98元,
					 * 如果不是,则设置会员服务非状态为2
					 **/
					alliedShopsServiceFee(mDStores);
				}
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}

	/**
	 * 加盟店会员费结算逻辑
	 * 
	 * @param mDStores
	 */
	public void franchiseServiceFee(MDStores mDStores) throws BusinessException, SystemException, Exception {
		try {
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			// 查询余额
			reqParams.put("service", "finance.memberAssetFind");
			bizParams.put("member_id", mDStores.getStores_id());
			bizParams.put("member_type_key", Constant.MEMBER_TYPE_STORES);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
			resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> assetMap = (Map<String, Object>) resultMap.get("data");
			String gold = assetMap.get("gold") + "";
			// 判断可用金币余额是否足够
			if (MoneyUtil.moneyComp(gold, "2000")) {

				MDStoresServiceFee serviceFee = new MDStoresServiceFee();
				serviceFee.setDate_id(DateUtil.getFormatDate("yyyyMMdd"));
				serviceFee.setCreated_date(new Date());
				serviceFee.setStores_id(mDStores.getStores_id());
				serviceFee.setCash(new BigDecimal("98"));
				serviceFee.setGold(0);
				serviceFee.setRemark("加盟店会员费结算成功");
				memberDao.insertMDStoresServiceFee(serviceFee);

				// 记录日志
				bizParams.clear();
				bizParams.put("log_id", IDUtil.getUUID());
				bizParams.put("stores_id", mDStores.getStores_id());
				bizParams.put("category", "settlement_service_free");
				bizParams.put("tracked_date", new Date());
				bizParams.put("event", "加盟店店会员费结算");
				memberDao.insertMDStoresScheduleLog(bizParams);
				// 扣金币
				resultMap.clear();
				reqParams.clear();
				bizParams.clear();
				reqParams.put("service", "finance.balancePay");
				bizParams.put("data_source", "SJLY_03");
				bizParams.put("payment_way_key", "ZFFS_08");
				bizParams.put("detail", "会员费");
				bizParams.put("amount", "2000");
				bizParams.put("buyer_id", mDStores.getStores_id());
				bizParams.put("seller_id", Constant.MEMBER_ID_MTH);
				bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
				resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"),
							(String) resultMap.get("error_msg"));
				}
				// 送现金
				reqParams.put("service", "finance.balancePay");
				bizParams.put("data_source", "SJLY_03");
				bizParams.put("payment_way_key", "ZFFS_05");
				bizParams.put("detail", "会员费");
				bizParams.put("amount", "98");
				bizParams.put("buyer_id", Constant.MEMBER_ID_MTH);
				bizParams.put("seller_id", mDStores.getStores_id());
				bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
				resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					logger.error("会员id【" + mDStores.getStores_id() + "】会员费结算异常");
				}
				// 发送短信
				sendMsg(mDStores.getContact_tel(), "恭喜您本月赚取超过2000金币，系统已充值98元以资奖励。");
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 联盟店会员费结算逻辑
	 * 
	 * @param mDStores
	 */
	public void alliedShopsServiceFee(MDStores mDStores) throws BusinessException, SystemException, Exception {
		try {
			/**
			 * 处理联盟店(检测是否服务费状态是否为1,如果为1,检测金币是否大于等于2000,是则扣除金币,解冻98元,
			 * 如果不是，检测可用余额是否大于0，如果是，则进行解冻98元，然后扣除98元，并设置会员服务状态为0)
			 **/
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			MDStoresServiceFee serviceFee = new MDStoresServiceFee();
			serviceFee.setDate_id(DateUtil.getFormatDate("yyyyMMdd"));
			serviceFee.setCreated_date(new Date());
			serviceFee.setStores_id(mDStores.getStores_id());
			serviceFee.setCash(new BigDecimal("0"));
			serviceFee.setGold(0);
			// 联盟店会员服务费的结算状态(默认为异常)
			Integer service_fee = 2;
			// 如果联盟店未冻结金额,则不进行结算
			if (mDStores.getService_fee() == 1) {
				// 查询余额
				reqParams.put("service", "finance.memberAssetFind");
				bizParams.put("member_id", mDStores.getStores_id());
				bizParams.put("member_type_key", Constant.MEMBER_TYPE_STORES);
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				String resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
				resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"),
							(String) resultMap.get("error_msg"));
				}
				Map<String, Object> assetMap = (Map<String, Object>) resultMap.get("data");
				String gold = assetMap.get("gold") + "";
				// 判断可用金币余额是否足够
				if (MoneyUtil.moneyComp(gold, "2000")) {
					// 扣金币
					resultMap.clear();
					reqParams.clear();
					bizParams.clear();
					reqParams.put("service", "finance.balancePay");
					bizParams.put("data_source", "SJLY_03");
					bizParams.put("payment_way_key", "ZFFS_08");
					bizParams.put("detail", "会员费");
					bizParams.put("amount", "2000");
					bizParams.put("buyer_id", mDStores.getStores_id());
					bizParams.put("seller_id", Constant.MEMBER_ID_MTH);
					bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
					reqParams.put("params", FastJsonUtil.toJson(bizParams));
					resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
					resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						throw new BusinessException((String) resultMap.get("error_code"),
								(String) resultMap.get("error_msg"));
					}
					// 解冻余额
					resultMap.clear();
					reqParams.clear();
					bizParams.clear();
					reqParams.put("service", "finance.balanceUnFreeze");
					bizParams.put("data_source", Constant.DATA_SOURCE_SJLY_03);
					bizParams.put("member_id", mDStores.getStores_id());
					bizParams.put("member_type_key", Constant.MEMBER_TYPE_STORES);
					bizParams.put("detail", "会员费解冻");
					bizParams.put("amount", "98");
					reqParams.put("params", FastJsonUtil.toJson(bizParams));
					resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
					resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						throw new BusinessException((String) resultMap.get("error_code"),
								(String) resultMap.get("error_msg"));
					}
					serviceFee.setGold(2000);
					// 发送短信
					sendMsg(mDStores.getContact_tel(), "恭喜您本月赚取超过2000金币，系统已返还98元技术服务费。");
					service_fee = 0;
				} else {
					BigDecimal cash_balance = new BigDecimal(assetMap.get("cash_balance") + "");
					BigDecimal cash_froze = new BigDecimal(assetMap.get("cash_froze") + "");
					BigDecimal usable_balance = MoneyUtil.moneySub(cash_balance, cash_froze);
					// 判断可用余额是否足够
					if (MoneyUtil.moneyComp(usable_balance, new BigDecimal("0.00"))) {
						// 解冻余额
						resultMap.clear();
						reqParams.clear();
						bizParams.clear();
						reqParams.put("service", "finance.serviceFeeUnFreeze");
						bizParams.put("data_source", Constant.DATA_SOURCE_SJLY_03);
						bizParams.put("member_id", mDStores.getStores_id());
						bizParams.put("member_type_key", Constant.MEMBER_TYPE_STORES);
						bizParams.put("detail", "会员费解冻");
						reqParams.put("params", FastJsonUtil.toJson(bizParams));
						resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
						resultMap = FastJsonUtil.jsonToMap(resultStr);
						if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
							throw new BusinessException((String) resultMap.get("error_code"),
									(String) resultMap.get("error_msg"));
						}
						// 扣除余额
						resultMap.clear();
						reqParams.clear();
						bizParams.clear();
						reqParams.put("service", "finance.balancePay");
						bizParams.put("data_source", "SJLY_03");
						bizParams.put("payment_way_key", "ZFFS_05");
						bizParams.put("detail", "会员费扣除");
						bizParams.put("amount", "98");
						bizParams.put("buyer_id", mDStores.getStores_id());
						bizParams.put("seller_id", Constant.MEMBER_ID_MTH);
						bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
						reqParams.put("params", FastJsonUtil.toJson(bizParams));
						resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
						resultMap = FastJsonUtil.jsonToMap(resultStr);
						if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
							throw new BusinessException((String) resultMap.get("error_code"),
									(String) resultMap.get("error_msg"));
						}
						serviceFee.setCash(new BigDecimal("98"));
						sendMsg(mDStores.getContact_tel(), "由于金币余额不足2000，系统已从您账户扣除98元用于技术服务费，谢谢合作。");
						service_fee = 0;
					}
				}
				// 更新会员费状态
				if (service_fee == 2) {
					bizParams.clear();
					bizParams.put("stores_id", mDStores.getStores_id());
					bizParams.put("service_fee", service_fee);
					storesDao.updateMDStores(bizParams);
				}
				// 会员费状态为0,表示结算整个,记录日志
				if (service_fee == 0) {
					serviceFee.setRemark("联盟店会员费结算成功");
					memberDao.insertMDStoresServiceFee(serviceFee);
				}

				// 记录日志
				bizParams.clear();
				bizParams.put("log_id", IDUtil.getUUID());
				bizParams.put("stores_id", mDStores.getStores_id());
				bizParams.put("category", "settlement_service_free");
				bizParams.put("tracked_date", new Date());
				bizParams.put("event", "联盟店会员费结算");
				memberDao.insertMDStoresScheduleLog(bizParams);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 发送短信
	 * 
	 * @param mobiles
	 * @param msg
	 */
	private void sendMsg(final String mobiles, final String msg) {
		try {
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			// 发送短信
			String notification_service_url = PropertiesConfigUtil.getProperty("notification_service_url");
			bizParams.put("sms_source", Constant.DATA_SOURCE_SJLY_03);
			bizParams.put("mobiles", mobiles);
			bizParams.put("msg", msg);
			reqParams.put("service", "notification.SMSSend");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			HttpClientUtil.post(notification_service_url, reqParams);
		} catch (Exception e) {
			logger.error("发送短信通知异常", e);
		}
	}

	@Override
	public void handleMemberServiceFree(Map<String, Object> paraMap, ResultData result) throws Exception {
		RedisLock lock = null;
		String lockKey = "[task_memberServiceFree]";
		try {
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();
			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				return;
			}
			redisUtil.setStr(lockKey, lockKey, 600);
			Map<String, Object> parasMap = new HashMap<>();
			parasMap.put("growth_value", 10);
			consumerDao.updateMDConsumerGrowthValue(parasMap);
		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}
	
	@Override
	public void handleMemberShoppingServiceFree(Map<String, Object> paraMap, ResultData result) throws Exception {
		RedisLock lock = null;
		String lockKey = "[task_memberShoppingServiceFree]";
		try {
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();
			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				return;
			}
			redisUtil.setStr(lockKey, lockKey, 600);
			Map<String, String> reqParams = new HashMap<>();
			Map<String, Object> bizParams = new HashMap<>();
			String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
			reqParams.put("service", "order.consumer.beikeMallOrderForAutoInterface");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.post(order_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			List<String> list =(List<String>) resultMap.get("list");
			paraMap.put("consumer_id_in", list);
			paraMap.put("growth_value", 10);
			consumerDao.updateMDConsumerGrowthValue(paraMap);
		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}
	
	//定时任务  ，查询所有二级分销的  会员id 然后把二级分销的id当做父级id, 进行查询   判断总数是否大于等于30   如果满足条件，得到ID，然后递归查询，得到总数,如果大于等于70 设置分销等级。
	@Override
	public void handledMemberDistribtion(Map<String, Object> paramsMap, ResultData result) throws Exception {
				Map<String,Object>  paramMap =  new HashMap<String,Object>();
				Map<String, Object>  tempMap =  new  HashMap<String, Object>();
				//先查询所有的注册为二级会员，且直邀数量 大于等于 30的  memberId
				List<String> memberIdList =   memberDistributionDao.getDistrMemberId();
				for(String memberId: memberIdList) {
					tempMap.clear();
					tempMap.put("member_id", memberId);
					List<String>  nextMemberIdList =	memberDistributionDao.getDistrNextMemberId(tempMap);
					this.memberDistrRelationEdit(new HashSet<>(nextMemberIdList), memberId);
//					//修改达标的会员的分销关系
					this.memberDistrbutionIdEdit(memberId);
				}
				
				
//				Set<String>  memberSet =  new HashSet<String>();
//				List<Map<String, Object>> memberIdList   =  memberDistributionDao.getSimpleMemberdistr(tempMap);
//				if(null !=  memberIdList &&   memberIdList.size() >0 ) {
//					for( Map<String, Object> map : memberIdList) {
//						String memberId =  StringUtil.formatStr(map.get("member_id")) ;
//						if(StringUtils.isNotBlank(memberId)) {
//							tempMap.clear();
//							tempMap.put("parent_id", memberId);
//							//查询所有已这个id为父级id的会员，查询子级
//							List<MDMemberDistribution> parentIdList   =  memberDistributionDao.getSimpleMemberdistrByParentId(tempMap);
//							int  dirCount =  parentIdList.size();
//							//这里直接判断这个会员是否是是内测会员。
//							tempMap.clear();
//							tempMap.put("member_id", memberId);
//							MDConsumer entity  = consumerDao.selectMDConsumerById(tempMap);
//							if(null !=  entity && null !=  entity.getType() && entity.getType() ==1) {
//								//查询下级用户是 type  等于 0  然后放入list  里面，然后得到这个size  如果这个size  大于等于3  就是走下一个流程 。
//								int lowerLevelCount = this.getLowerLevel(parentIdList);
//								if(lowerLevelCount >=30) {
//									//paramMap  里面的count  是总的下级 （直邀和间邀） 里面的memberId 是所有的下级
//									paramMap =this.getSimpleMemberdistrByParentId(parentIdList,memberSet);
//									if(!paramMap.isEmpty()) {
//										Set<String>  memberIdSet = (Set<String>) paramMap.get("memberIdSet");
//											//修改下级分销关系
//											this.memberDistrRelationEdit(memberIdSet, memberId);
//											//修改达标的会员的分销关系
//											this.memberDistrbutionIdEdit(memberId);
//									}
//								}	
//							}else {
//								if(dirCount >=30) {
//									//paramMap  里面的count  是总的下级 （直邀和间邀） 里面的memberId 是所有的下级
//									paramMap =this.getSimpleMemberdistrByParentId(parentIdList,memberSet);
//									if(!paramMap.isEmpty()) {
//										Set<String>  memberIdSet = (Set<String>) paramMap.get("memberIdSet");
//											//修改下级分销关系
//											this.memberDistrRelationEdit(memberIdSet, memberId);
//											//修改达标的会员的分销关系
//											this.memberDistrbutionIdEdit(memberId);
//									}
//								}
//							}
//								
//						}
//					}
//				}
	}
	
	private int getLowerLevel(List<MDMemberDistribution> parentIdList) throws Exception {
		List<String>  lowerList=  new ArrayList<String>();
		Map<String, Object>  tempMap =  new  HashMap<String, Object>();
		for(MDMemberDistribution  distrEntity:  parentIdList) {
			String memberId =  distrEntity.getMemberId();
			tempMap.clear();
			tempMap.put("member_id", memberId);
			MDConsumer entity  = consumerDao.selectMDConsumerById(tempMap);
			if(null != entity && entity.getType() ==0) {
				lowerList.add(memberId);
			}
		}
		return  lowerList.size();
		
	}
	//递归查询所有的下级会员
	public  Map<String,Object>  getSimpleMemberdistrByParentId(List<MDMemberDistribution> parentList,Set<String>  memberIdSet) {
		//把所有的下级查询出来  我只需要得到memberId，就行。把他的memberId 放入一个队列。
		Map<String,Object>  paramMap =  new HashMap<String,Object>();
		List<List<MDMemberDistribution>> listMemberPar =   new ArrayList<List<MDMemberDistribution>>();	
		int count = 0;
		Map<String,Object>  tempMap =  new HashMap<String,Object>();
			if(null != parentList &  parentList.size() >0) {
				for(MDMemberDistribution  memDistr  : parentList ) {
					memberIdSet.add(memDistr.getMemberId());
					tempMap.clear();
					tempMap.put("parent_id", memDistr.getMemberId());
					List<MDMemberDistribution> parentIdList   =  memberDistributionDao.getSimpleMemberdistrByParentId(tempMap);
					if(null != parentIdList&&  parentIdList.size() >0) {
						for(MDMemberDistribution  memberDistrution :parentIdList) {
							memberIdSet.add(memberDistrution.getMemberId());
						}
						listMemberPar.add(parentIdList);
						this.getSimpleMemberdistrByParentId(parentIdList,memberIdSet);
					}
				}
			}
				paramMap.put("count", memberIdSet.size());
				paramMap.put("memberIdSet", memberIdSet);
				System.out.println("测试分销递归 count："+memberIdSet.size());
		
		return paramMap;
	}
	
	
	//递归查询所有的下级会员
//		public  Map<String,Object>  getSimpleMemberdistrByParentId(List<String> parentList) {
//			//把所有的下级查询出来  我只需要得到memberId，就行。把他的memberId 放入一个队列。
//			Map<String,Object>  paramMap =  new HashMap<String,Object>();
//			Queue<String>  queue =  new LinkedList<String>();
//			Map<String,Object>  tempMap =  new HashMap<String,Object>();
//				if(null != parentList &  parentList.size() >0) {
//					for(String  memDistr  : parentList ) {
//						queue.add(memDistr);
//						tempMap.clear();
//						tempMap.put("parent_id", memDistr);
//						List<String> parentIdList   =  memberDistributionDao.getDistrMemberId(tempMap);
//						if(null != parentIdList&&  parentIdList.size() >0) {
//							for(String  distrMemberId :parentIdList) {
//								queue.add(distrMemberId);
//							}
//							this.getSimpleMemberdistrByParentId(parentIdList);
//						}
//					}
//				}
//					paramMap.put("count", queue.size());
//					paramMap.put("memberIdSet", queue);
//					System.out.println("测试分销递归 count："+queue.size());
//			
//			return paramMap;
//		}

	
	//修改下级会员关系
	public void memberDistrRelationEdit(Set<String>  memberIdSet,String distrbutionId) throws Exception {
		Iterator<String> memberIdStr = memberIdSet.iterator();
		Map<String, Object>  tempMap  =   new  HashMap<String,Object>();
		tempMap.clear();
		tempMap.put("member_id", distrbutionId);
		//达标的会员
		MDMemberDistribution   distribution   =  memberDistributionDao.getMemberDistributionInfo(tempMap);
		if(null != distribution) {
			if(distribution.getDistrLevel() ==2) {
				while (memberIdStr.hasNext()) {
					String memberId = memberIdStr.next();
					tempMap.clear();
					tempMap.put("member_id", memberId);
					//下级会员
					MDMemberDistribution   memberDistribution   =  memberDistributionDao.getMemberDistributionInfo(tempMap);
					if(null != memberDistribution) {
						this.memberDistrRelation2LevelUpdate(memberDistribution, distrbutionId);
					}
				} 
			}else if(distribution.getDistrLevel() ==3) {
				while (memberIdStr.hasNext()) {
					String memberId = memberIdStr.next();
					tempMap.clear();
					tempMap.put("member_id", memberId);
					//这个是下级
					MDMemberDistribution   memberDistribution   =  memberDistributionDao.getMemberDistributionInfo(tempMap);
					if(null != memberDistribution) {
						this.memberDistrRelation3LevelUpdate(memberDistribution, distribution, distrbutionId);
					}
				} 
			}
		}
	}
	
	/**
	 * 本身分销等级为2
	 * @param memberDistribution
	 * @param distrbutionId
	 * @throws Exception
	 */
	public  void  memberDistrRelation2LevelUpdate(MDMemberDistribution   memberDistribution ,String distrbutionId) throws Exception {
		if(memberDistribution.getDistrLevel() ==2) {
			if(!StringUtil.equals(distrbutionId, memberDistribution.getParentId()) && !StringUtil.equals(distrbutionId, memberDistribution.getGrandId())) {
				memberDistribution.setTopId(distrbutionId);
			}
		}else if(memberDistribution.getDistrLevel() ==3) {
			if(!StringUtil.equals(distrbutionId, memberDistribution.getParentId()) && !StringUtil.equals(distrbutionId, memberDistribution.getGrandId())) {
				if(StringUtil.isEmpty(memberDistribution.getTopId())) {
					memberDistribution.setTopId(distrbutionId);
				}else if(StringUtil.isEmpty(memberDistribution.getManagerId())) {
					memberDistribution.setManagerId(distrbutionId);
				} else if(StringUtil.isEmpty(memberDistribution.getGeneralId())) {
					memberDistribution.setGeneralId(distrbutionId);
				}
			}
		}
		memberDistribution.setDistrLevel(3);
		memberDistributionDao.update(memberDistribution);
	}
	
	/**
	 * 本身分销等级为3
	 * @param memberDistribution
	 * @param distrbutionId
	 * @throws Exception
	 */
	public  void  memberDistrRelation3LevelUpdate(MDMemberDistribution   memberDistribution ,MDMemberDistribution   distribution  ,String distrbutionId) throws Exception {
		//本身参数
		String generalId = StringUtil.formatStr(distribution.getGeneralId());
		String managerId = StringUtil.formatStr(distribution.getManagerId());
		String topId =  StringUtil.formatStr(distribution.getTopId());
		String grandId =  StringUtil.formatStr(distribution.getGrandId());
		String parentId = StringUtil.formatStr(distribution.getParentId());
		boolean  grandFlag = false;
		boolean  parentFlag = false;
		if(StringUtil.isNotBlank(grandId)) {
			  grandFlag =  this.selectMemberDistrRegist(grandId);
		}
		if(StringUtil.isNotBlank(parentId)) {
			  parentFlag =  this.selectMemberDistrRegist(parentId);
		}
		
		if(StringUtil.isEmpty(generalId) && StringUtil.isEmpty(managerId) &&  StringUtil.isEmpty(topId)) {
			//topId，managerId，generalId，都没有,就只有父级 和爷爷级
			if(StringUtil.isNotEmpty(memberDistribution.getGeneralId()) &&  StringUtil.isNotEmpty(memberDistribution.getManagerId()) 
					&&  StringUtil.isNotEmpty(memberDistribution.getTopId())) {
				//下级topId，managerId，generalId，都有
				if(StringUtil.equals(grandId, memberDistribution.getGeneralId())) {
					memberDistribution.setGeneralId(memberDistribution.getManagerId());
					memberDistribution.setManagerId(distrbutionId);
				}else if(StringUtil.equals(parentId, memberDistribution.getGeneralId())) {
					memberDistribution.setGeneralId(distrbutionId);
				}
			}else if(StringUtil.isEmpty(memberDistribution.getGeneralId()) &&  StringUtil.isNotEmpty(memberDistribution.getManagerId()) 
					&&  StringUtil.isNotEmpty(memberDistribution.getTopId())) {
				//下级有topId，managerId
				if(!StringUtil.equals(distrbutionId, memberDistribution.getParentId()) && !StringUtil.equals(distrbutionId, memberDistribution.getGrandId())) {
					if(StringUtil.equals(grandId, memberDistribution.getManagerId()) && StringUtil.equals(parentId, memberDistribution.getTopId())) {
						memberDistribution.setGeneralId(memberDistribution.getManagerId());
						memberDistribution.setManagerId(memberDistribution.getTopId());
						memberDistribution.setTopId(distrbutionId);
					}else if(StringUtil.equals(grandId, memberDistribution.getManagerId()) || StringUtil.equals(parentId, memberDistribution.getManagerId())){
						memberDistribution.setGeneralId(memberDistribution.getManagerId());
						memberDistribution.setManagerId(distrbutionId);
					}else  {
						memberDistribution.setGeneralId(distrbutionId);
					}
				}
			}else if(StringUtil.isEmpty(memberDistribution.getGeneralId()) &&  StringUtil.isEmpty(memberDistribution.getManagerId()) 
					&&  StringUtil.isNotEmpty(memberDistribution.getTopId())) {
				//下级只有topId，
					if(!StringUtil.equals(distrbutionId, memberDistribution.getParentId()) && !StringUtil.equals(distrbutionId, memberDistribution.getGrandId())) {
						if(StringUtil.equals(grandId, memberDistribution.getTopId()) || StringUtil.equals(parentId, memberDistribution.getTopId()) ) {
							memberDistribution.setManagerId(memberDistribution.getTopId());
							memberDistribution.setTopId(distrbutionId);
						}else {
							memberDistribution.setManagerId(distrbutionId);
						}
						
					}
			}
		}else if(StringUtil.isEmpty(generalId) && StringUtil.isEmpty(managerId) &&  StringUtil.isNotBlank(topId)) {
			//只有topId的情况
			if(StringUtil.isEmpty(memberDistribution.getGeneralId()) &&  StringUtil.isEmpty(memberDistribution.getManagerId()) 
					&&  StringUtil.isNotEmpty(memberDistribution.getTopId())) {
				if(StringUtil.equals(topId, memberDistribution.getTopId())) {
					if(!StringUtil.equals(distrbutionId, memberDistribution.getParentId()) &&  !StringUtil.equals(distrbutionId, memberDistribution.getGrandId()) ) {
						memberDistribution.setManagerId(topId);
						memberDistribution.setTopId(distrbutionId);
					}
				}
			}else if(StringUtil.isEmpty(memberDistribution.getGeneralId()) &&  StringUtil.isNotEmpty(memberDistribution.getManagerId()) 
					&&  StringUtil.isNotEmpty(memberDistribution.getTopId())) {
				if(StringUtil.equals(topId, memberDistribution.getManagerId())) {
					if(!StringUtil.equals(distrbutionId, memberDistribution.getParentId()) &&  !StringUtil.equals(distrbutionId, memberDistribution.getGrandId()) ) {
						memberDistribution.setGeneralId(memberDistribution.getManagerId());
						memberDistribution.setManagerId(distrbutionId);
					}
				}
			}else if(StringUtil.isNotEmpty(memberDistribution.getGeneralId()) &&  StringUtil.isNotEmpty(memberDistribution.getManagerId()) 
					&&  StringUtil.isNotEmpty(memberDistribution.getTopId())) {
				if(!StringUtil.equals(distrbutionId, memberDistribution.getParentId()) &&  !StringUtil.equals(distrbutionId, memberDistribution.getGrandId()) ) {
					if(StringUtil.equals(topId, memberDistribution.getGeneralId()) && (grandFlag == true || parentFlag == true)) {
						memberDistribution.setGeneralId(memberDistribution.getManagerId());
						memberDistribution.setManagerId(memberDistribution.getTopId());
						memberDistribution.setTopId(distrbutionId);
					}else if(StringUtil.equals(topId, memberDistribution.getGeneralId()) &&  grandFlag == false && parentFlag == false ) {
						memberDistribution.setGeneralId(distrbutionId);
					}else if(StringUtil.equals(parentId, memberDistribution.getGeneralId()) ||  StringUtil.equals(grandId, memberDistribution.getGeneralId())) {
						memberDistribution.setGeneralId(distrbutionId);
					}
				}
			}
		}else if(StringUtil.isEmpty(generalId) && StringUtil.isNotEmpty(managerId) &&  StringUtil.isNotBlank(topId)) {
			//有 topId，managerId的情况
			if(StringUtil.isEmpty(memberDistribution.getGeneralId()) &&  StringUtil.isNotEmpty(memberDistribution.getManagerId()) 
					&&  StringUtil.isNotEmpty(memberDistribution.getTopId())) {
				if(!StringUtil.equals(memberDistribution.getGrandId(), distrbutionId) && !StringUtil.equals(memberDistribution.getParentId(), distrbutionId)) {
					memberDistribution.setGeneralId(memberDistribution.getManagerId());
					memberDistribution.setManagerId(memberDistribution.getTopId());
					memberDistribution.setTopId(distrbutionId);
				}
			}else if(StringUtil.isNotEmpty(memberDistribution.getGeneralId()) &&  StringUtil.isNotEmpty(memberDistribution.getManagerId()) 
					&&  StringUtil.isNotEmpty(memberDistribution.getTopId())) {
				if(!StringUtil.equals(memberDistribution.getGrandId(), distrbutionId) && !StringUtil.equals(memberDistribution.getParentId(), distrbutionId)) {
					if(StringUtil.equals(managerId, memberDistribution.getGeneralId())) {
						memberDistribution.setGeneralId(memberDistribution.getManagerId());
						if(StringUtil.equals(topId, memberDistribution.getManagerId())) {
							memberDistribution.setManagerId(memberDistribution.getTopId());
							memberDistribution.setTopId(distrbutionId);
						}else {
							memberDistribution.setManagerId(distrbutionId);
						}
					}else if(StringUtil.equals(topId, memberDistribution.getGeneralId())) {
						memberDistribution.setGeneralId(memberDistribution.getManagerId());
						memberDistribution.setManagerId(memberDistribution.getTopId());
						memberDistribution.setTopId(distrbutionId);
					}
				}
			}
		}else if(StringUtil.isNotEmpty(generalId) && StringUtil.isNotEmpty(managerId) &&  StringUtil.isNotBlank(topId)) {
			//有 topId，managerId，generalId的情况下
			if(StringUtil.equals(generalId, memberDistribution.getGeneralId())) {
				if(!StringUtil.equals(memberDistribution.getGrandId(), distrbutionId) && !StringUtil.equals(memberDistribution.getParentId(), distrbutionId)) {
					memberDistribution.setGeneralId(memberDistribution.getManagerId());
					memberDistribution.setManagerId(memberDistribution.getTopId());
					memberDistribution.setTopId(distrbutionId);
				}
			}else if(StringUtil.equals(managerId, memberDistribution.getGeneralId())) {
				if(!StringUtil.equals(memberDistribution.getGrandId(), distrbutionId) && !StringUtil.equals(memberDistribution.getParentId(), distrbutionId)) {
					memberDistribution.setGeneralId(memberDistribution.getManagerId());
					memberDistribution.setManagerId(distrbutionId);
				}
			}else if(StringUtil.equals(topId, memberDistribution.getGeneralId())) {
				if(!StringUtil.equals(memberDistribution.getGrandId(), distrbutionId) && !StringUtil.equals(memberDistribution.getParentId(), distrbutionId)) {
					memberDistribution.setGeneralId(distrbutionId);
				}
			}
		}
		memberDistributionDao.update(memberDistribution);
	}
	
	//修改达标会员关系
	public void memberDistrbutionIdEdit(String distrbutionId) throws Exception {
		Map<String, Object>  tempMap  =   new  HashMap<String,Object>();
		tempMap.clear();
		tempMap.put("member_id", distrbutionId);
		MDMemberDistribution   memberDistribution   =  memberDistributionDao.getMemberDistributionInfo(tempMap);
		if(null != memberDistribution) {
			memberDistribution.setDistrLevel(3);
			memberDistribution.setRegistLevel(3);
			memberDistribution.setUpdateTime(new Date());
			memberDistributionDao.update(memberDistribution);				
		}
	}
	
	
	public boolean  selectMemberDistrRegist(String memberId) {
		logger.info("测试成为掌柜,查找注册等级："+memberId);
		Map<String, Object>  tempMap  =   new  HashMap<String,Object>();
		tempMap.clear();
		tempMap.put("member_id", memberId);
		MDMemberDistribution   memberDistribution   =  memberDistributionDao.getMemberDistributionInfo(tempMap);
		if(null != memberDistribution.getRegistLevel() &&  memberDistribution.getRegistLevel() ==3) {
			return true;
		}else {
			return false;
		}
	}
	
	
	
	@Override
	public void handledMemberDistribtion() throws Exception {
		logger.info("MemberTaskServiceImpl"+"测试是否进入定时任务");
		Map<String, Object>  tempMap =  new  HashMap<String, Object>();
		//先查询所有的注册为二级会员，且直邀数量 大于等于 30的  memberId
		List<String> memberIdList =   memberDistributionDao.getDistrMemberId();
		for(String memberId: memberIdList) {
			tempMap.clear();
			tempMap.put("member_id", memberId);
			List<String>  nextMemberIdList =	memberDistributionDao.getDistrNextMemberId(tempMap);
			this.memberDistrRelationEdit(new HashSet<>(nextMemberIdList), memberId);
//			//修改达标的会员的分销关系
			this.memberDistrbutionIdEdit(memberId);
		}
//		Map<String,Object>  paramMap =  new HashMap<String,Object>();
//		Map<String, Object>  tempMap =  new  HashMap<String, Object>();
//		tempMap.put("regist_level", 2);
//		//先查询所有的注册为二级会员的id
//		Set<String>  memberSet =  new HashSet<String>();
//		List<Map<String, Object>> memberIdList   =  memberDistributionDao.getSimpleMemberdistr(tempMap);
//		if(null !=  memberIdList &&   memberIdList.size() >0 ) {
//			for( Map<String, Object> map : memberIdList) {
//				String memberId =  StringUtil.formatStr(map.get("member_id")) ;
//				if(StringUtils.isNotBlank(memberId)) {
//					tempMap.clear();
//					tempMap.put("parent_id", memberId);
//					//查询所有已这个id为父级id的会员，查询子级
//					List<MDMemberDistribution> parentIdList   =  memberDistributionDao.getSimpleMemberdistrByParentId(tempMap);
//					int  dirCount =  parentIdList.size();
//					//这里直接判断这个会员是否是是内测会员。
//					tempMap.clear();
//					tempMap.put("member_id", memberId);
//					MDConsumer entity  = consumerDao.selectMDConsumerById(tempMap);
//					if(null !=  entity && null !=  entity.getType() && entity.getType() ==1) {
//						//查询下级用户是 type  等于 0  然后放入list  里面，然后得到这个size  如果这个size  大于等于3  就是走下一个流程 。
//						int lowerLevelCount = this.getLowerLevel(parentIdList);
//						if(lowerLevelCount >=10) {
//							//paramMap  里面的count  是总的下级 （直邀和间邀） 里面的memberId 是所有的下级
//							paramMap =this.getSimpleMemberdistrByParentId(parentIdList,memberSet);
//							if(!paramMap.isEmpty()) {
//								Set<String>  memberIdSet = (Set<String>) paramMap.get("memberIdSet");
//									//修改下级分销关系
//									this.memberDistrRelationEdit(memberIdSet, memberId);
//									//修改达标的会员的分销关系
//									this.memberDistrbutionIdEdit(memberId);
//							}
//						}	
//					}else {
//						if(dirCount >=10) {
//							//paramMap  里面的count  是总的下级 （直邀和间邀） 里面的memberId 是所有的下级
//							paramMap =this.getSimpleMemberdistrByParentId(parentIdList,memberSet);
//							if(!paramMap.isEmpty()) {
//								Set<String>  memberIdSet = (Set<String>) paramMap.get("memberIdSet");
//									//修改下级分销关系
//									this.memberDistrRelationEdit(memberIdSet, memberId);
//									//修改达标的会员的分销关系
//									this.memberDistrbutionIdEdit(memberId);
//							}
//						}
//					}
//						
//				}
//			}
//		}
		
	}

	@Override
	public void consumerMemberEdit() throws Exception {
		logger.info("MemberTaskServiceImpl"+"进入定时任务---consumerMemberEdit");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("type", 5);
		map.put("vip_end_time", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
		List<MDConsumer> consumerList = consumerDao.queryMDConsumerList(map);
		if(consumerList!=null && consumerList.size()>0){
			Date now = new Date();
			Map<String, Object> paramMap = new HashMap<String,Object>();
			// 过期改为普通会员
			paramMap.put("type", 4);
			paramMap.put("is_vip_expired", 0);
			for (MDConsumer mdConsumer: consumerList) {
				// 判断过期体验会员
				if(mdConsumer.getVip_end_time().getTime() < now.getTime()){
					paramMap.put("consumer_id", mdConsumer.getConsumer_id());
					consumerDao.updateMDConsumer(paramMap);
					logger.info("MemberTaskServiceImpl----进入定时任务---consumerMemberEdit----data："+mdConsumer.toString());
				}
			}
		}
	}
}
