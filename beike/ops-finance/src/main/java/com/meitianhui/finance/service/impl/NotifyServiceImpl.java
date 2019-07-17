package com.meitianhui.finance.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.meitianhui.finance.constant.ConsumerConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meitianhui.common.constant.CommonConstant;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.MD5Util;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.constant.ShellConstant;
import com.meitianhui.finance.controller.FinanceController;
import com.meitianhui.finance.dao.FinanceDao;
import com.meitianhui.finance.dao.TelephoneChargeLogDao;
import com.meitianhui.finance.dao.TransactionDao;
import com.meitianhui.finance.entity.FDMemberAsset;
import com.meitianhui.finance.entity.FDMemberRebateLog;
import com.meitianhui.finance.entity.FDMemberShellLog;
import com.meitianhui.finance.entity.FDTelephoneChargeLog;
import com.meitianhui.finance.entity.FDTransactionsResult;
import com.meitianhui.finance.service.AddMoneyService;
import com.meitianhui.finance.service.NotifyService;
import com.meitianhui.finance.service.ReduceMoneyService;
import com.meitianhui.finance.service.TradeService;
import com.meitianhui.finance.util.DESUtils;

import net.sf.json.JSONObject;

@SuppressWarnings("unchecked")
@Service
public class NotifyServiceImpl implements NotifyService {
	private static final Logger logger = Logger.getLogger(NotifyServiceImpl.class);
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private TradeService tradeService;
	@Autowired
	public TransactionDao transactionDao;
	@Autowired
	public  FinanceDao  financeDao;
	@Autowired
	public  TelephoneChargeLogDao  telephoneChargeLogDao;
	@Autowired
	private AddMoneyService addMoneyService;
	@Autowired
	private ReduceMoneyService reduceMoneyService;
	

	@Override
	public void financeChangeSMSNotify(final String member_id, final String member_type_key, final String msg) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (member_id.equals(Constant.MEMBER_ID_MTH) || member_id.equals(Constant.MEMBER_ID_HYD)
							|| member_id.equals(Constant.MEMBER_ID_ANONYMITY)
							|| member_id.equals(Constant.MEMBER_ID_BUSINESS)
							|| member_id.equals(Constant.MEMBER_ID_PLATFORM)) {
						return;
					}
					// 获取会员手机号
					String mobile = null;

					// 增加缓存处理
					String cache_key = "[financeChangeSMSNotify]_" + member_id + member_type_key;
					String obj_str = redisUtil.getStr(cache_key);
					if (StringUtil.isNotEmpty(obj_str)) {
						mobile = obj_str;
					} else {
						String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
						Map<String, String> reqParams = new LinkedHashMap<String, String>();
						Map<String, String> paramMap = new LinkedHashMap<String, String>();
						reqParams.put("service", "member.memberInfoFindByMemberId");
						paramMap.put("member_id", member_id);
						paramMap.put("member_type_key", member_type_key);
						reqParams.put("params", FastJsonUtil.toJson(paramMap));
						String resultStr = HttpClientUtil.post(member_service_url, reqParams);
						Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
						String rsp_code = (String) resultMap.get("rsp_code");
						if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
							throw new BusinessException((String) resultMap.get("error_code"),
									(String) resultMap.get("error_msg"));
						}
						Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
						if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
							mobile = dateMap.get("mobile") + "";
						} else if (member_type_key.equals(Constant.MEMBER_TYPE_STORES)) {
							mobile = dateMap.get("contact_tel") + "";
						} else if (member_type_key.equals(Constant.MEMBER_TYPE_SUPPLIER)) {
							mobile = dateMap.get("contact_tel") + "";
						}
						redisUtil.setStr(cache_key, mobile, 1800);
					}

					if (StringUtil.isNotEmpty(mobile)) {
						// 发送短信
						String notification_service_url = PropertiesConfigUtil.getProperty("notification_service_url");
						Map<String, String> reqParams = new HashMap<String, String>();
						Map<String, String> bizParams = new HashMap<String, String>();
						reqParams.put("service", "notification.SMSSend");
						bizParams.put("sms_source", Constant.DATA_SOURCE_SJLY_03);
						bizParams.put("mobiles", mobile);
						bizParams.put("msg", msg);
						reqParams.put("params", FastJsonUtil.toJson(bizParams));
						HttpClientUtil.postShort(notification_service_url, reqParams);
					}
				} catch (BusinessException e) {
					logger.error(e.getMsg());
				} catch (SystemException e) {
					logger.error(e.getMsg());
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		});
	}

	@Override
	public void financeChangeAppNotify(final String member_id, final String msg) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "member.appMsgNotify");
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("message", msg);
					paramMap.put("receiver", member_id);
					reqParams.put("params", FastJsonUtil.toJson(paramMap));
					HttpClientUtil.postShort(member_service_url, reqParams);
				} catch (BusinessException e) {
					logger.error(e.getMsg());
				} catch (SystemException e) {
					logger.error(e.getMsg());
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
	}

	@Override
	public void pushAppNotify(final String data_source, final String member_id, final String msg) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String notification_service_url = PropertiesConfigUtil.getProperty("notification_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "notification.pushNotification");
					Map<String, String> paramMap = new HashMap<String, String>();
					Map<String, String> extrasparam = new HashMap<String, String>();
					paramMap.put("title", "交易通知");
					paramMap.put("alert", msg);
					paramMap.put("member_id", member_id);
					paramMap.put("data_source", data_source);
					extrasparam.put("type", "1");
					paramMap.put("extrasparam", FastJsonUtil.toJson(extrasparam));
					reqParams.put("params", FastJsonUtil.toJson(paramMap));
					HttpClientUtil.postShort(notification_service_url, reqParams);
				} catch (BusinessException e) {
					logger.error(e.getMsg());
				} catch (SystemException e) {
					logger.error(e.getMsg());
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
	}

	@Override
	public void pushAppMessage(final String member_id, final String msg, final Map<String, String> extrasMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String notification_service_url = PropertiesConfigUtil.getProperty("notification_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "notification.pushMessage");
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("msg_title", "交易通知");
					paramMap.put("msg_content", msg);
					paramMap.put("member_id", member_id);
					paramMap.put("extrasparam", FastJsonUtil.toJson(extrasMap));
					reqParams.put("params", FastJsonUtil.toJson(paramMap));
					HttpClientUtil.postShort(notification_service_url, reqParams);
				} catch (BusinessException e) {
					logger.error(e.getMsg());
				} catch (SystemException e) {
					logger.error(e.getMessage());
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
	}

	/**
	 * 交易注册(付款码)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void tradeNotify(FDTransactionsResult fDTransactionsResult) {
		try {
			String transaction_no = StringUtil.formatStr(fDTransactionsResult.getTransaction_no());
			String order_type_key = StringUtil.formatStr(fDTransactionsResult.getOrder_type_key());
			String body =  StringUtil.formatStr(fDTransactionsResult.getOut_trade_body());
			Map<String, Object> orderMap = new HashMap<String, Object>();
			if(StringUtil.isNotBlank(body)) {
				 orderMap = FastJsonUtil.jsonToMap(fDTransactionsResult.getOut_trade_body());
			}
			// 设置订单类型
			logger.info("notify回调:order_type_key:"+order_type_key+"getPayment_way_key:"+fDTransactionsResult.getPayment_way_key()+";--body:"+fDTransactionsResult.getOut_trade_body());
			orderMap.put("order_type_key", order_type_key);
			orderMap.put("payment_way_key", fDTransactionsResult.getPayment_way_key());
			if (order_type_key.equals(Constant.ORDER_TYPE_WYP)) {
				wypOrderPayNotity(transaction_no, fDTransactionsResult.getAmount(), orderMap);
			} else if (order_type_key.equals(Constant.ORDER_TYPE_COUPON)) {
				couponCreateNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);
			} else if (order_type_key.equals(Constant.ORDER_TYPE_LUCK_DRAW)) {
				String activity_type = StringUtil.formatStr(orderMap.get("activity_type"));
				if (activity_type.equals("YYY")) {
					yyyActivityOrderCreateNotify(transaction_no, orderMap);
				} else if (activity_type.equals("GGL")) {
					gglActivityOrderCreateNotify(transaction_no, orderMap);
				} else {
					dskActivityOrderCreateNotify(transaction_no, orderMap);
				}
			} else if (order_type_key.equals(Constant.ORDER_TYPE_LOCAL_SALE)) {
				pcOrderPayCreateNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);
			} else if (order_type_key.equals(Constant.ORDER_TYPE_CASHBACK)) {
				orderMap.put("amount", fDTransactionsResult.getAmount() + "");
				orderMap.put("member_id", fDTransactionsResult.getBuyer_id());
				if (fDTransactionsResult.getData_source().equals("SJLY_17")) {
					orderMap.put("data_source", fDTransactionsResult.getData_source());
					fgOrderPayCreateNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);
				} else if (fDTransactionsResult.getData_source().equals("SJLY_01")){
					ownOrderPayNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);	
				}
			}  else if (order_type_key.equals(Constant.ORDER_TYPE_PRESELL)) {
				psGroupPayCreateNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);
			}  else if (order_type_key.equals(Constant.ORDER_TYPE_TS)) {
				orderMap.put("amount", fDTransactionsResult.getAmount() + "");
				tsActivityPayCreateNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);
			} else if (order_type_key.equals(Constant.ORDER_TYPE_GROUP)) {
				orderMap.put("amount", fDTransactionsResult.getAmount() + "");
				tsOrderPayCreateNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);
			} else if (order_type_key.equals(Constant.ORDER_TYPE_POINT)) {
				orderMap.put("amount", fDTransactionsResult.getAmount() + "");
				peOrderPayCreateNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);
			} else if (order_type_key.equals(Constant.ORDER_TYPE_HUIGUO)) {
				orderMap.put("amount", fDTransactionsResult.getAmount() + "");
				huiguoOrderPayNotify(transaction_no, fDTransactionsResult.getAmount(), orderMap);
			} else if (order_type_key.equals(ShellConstant.ORDER_TYPE_05)) {
				String payment_way_key = fDTransactionsResult.getPayment_way_key();
				logger.info("测试话费充值，order_type_key:+"+order_type_key);
				String buyer_id = fDTransactionsResult.getBuyer_id();
				String out_trade_no = fDTransactionsResult.getOut_trade_no();
				telePhoneChargeOrderNotify(transaction_no,payment_way_key,buyer_id,out_trade_no);		
				//抵扣贝壳数量
				fDTransactionsResult.setAmount(new BigDecimal("2"));
				addMoneyService.updateFdMemberAssetforAddShell(fDTransactionsResult, fDTransactionsResult.getSeller_id());
				reduceMoneyService.updateFdMemberAssetforReduceShell(fDTransactionsResult, fDTransactionsResult.getBuyer_id());
			} else if (order_type_key.equals(ShellConstant.ORDER_TYPE_06)) {
				String payment_way_key = fDTransactionsResult.getPayment_way_key();
				logger.info("notify回调 查询支付方式 :order_type_key:"+order_type_key+"payment_way_key:"+payment_way_key);
				if(payment_way_key.equals(ShellConstant.PAYMENT_WAY_01)|| payment_way_key.equals(ShellConstant.PAYMENT_WAY_02) ||
						payment_way_key.equals(ShellConstant.PAYMENT_WAY_10)){
					logger.info("测试是否进入回调 查询支付方式   :order_type_key:"+order_type_key+"payment_way_key:"+payment_way_key);
					String buyer_id = fDTransactionsResult.getBuyer_id();
					String out_trade_no = fDTransactionsResult.getOut_trade_no();
					// 16:40 2019/5/7
					// type = 5 为体验会员
					String type = "0";
					if(ConsumerConstant.isConsumerType(fDTransactionsResult.getTrade_type_key(),fDTransactionsResult.getOrder_type_key(),fDTransactionsResult.getAmount())){
						type = "5";
					}
					rcOrderPayNotify(transaction_no,payment_way_key,buyer_id,out_trade_no,type);
				}
			} else if (order_type_key.equals(ShellConstant.ORDER_TYPE_11)){
				String payment_way_key = fDTransactionsResult.getPayment_way_key();
				String out_trade_no = fDTransactionsResult.getOut_trade_no();
				if(payment_way_key.equals(ShellConstant.PAYMENT_WAY_01)||payment_way_key.equals(ShellConstant.PAYMENT_WAY_02)){
					beikeMallOrderPayNotify(fDTransactionsResult);
				}else if(payment_way_key.equals(ShellConstant.PAYMENT_WAY_08)){
					//红包支付
					beikeMallOrderPayRedPacket(fDTransactionsResult);
				}else if(payment_way_key.equals(ShellConstant.PAYMENT_WAY_09)){
					//礼卷支付
					beikeMallOrderPayGift(fDTransactionsResult);
				}
			} else if(order_type_key.equals(ShellConstant.ORDER_TYPE_10)){
				String payment_way_key = fDTransactionsResult.getPayment_way_key();
				if(payment_way_key.equals(ShellConstant.PAYMENT_WAY_01)||payment_way_key.equals(ShellConstant.PAYMENT_WAY_02)){
					hongBaoOrderPayNotifyToRMB(fDTransactionsResult);
				} else {
					hongBaoOrderPayNotify(fDTransactionsResult);
				}
			} else if (order_type_key.equals(ShellConstant.ORDER_TYPE_12)){
				String payment_way_key = fDTransactionsResult.getPayment_way_key();
				String out_trade_no = fDTransactionsResult.getOut_trade_no();
				if(payment_way_key.equals(ShellConstant.PAYMENT_WAY_01)||payment_way_key.equals(ShellConstant.PAYMENT_WAY_02)){
					beikeStreetOrderPayNotify(transaction_no,out_trade_no);
				}
			} 
		} catch (SystemException e) {
			logger.error("订单通知异常,订单类型：" + fDTransactionsResult.getOrder_type_key(), e);
		} catch (Exception e) {
			logger.error("订单通知异常,订单类型：" + fDTransactionsResult.getOrder_type_key(), e);
		}
	}

	private void hongBaoOrderPayNotifyToRMB(final FDTransactionsResult fDTransactionsResult) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("礼包专区订单通知->" + fDTransactionsResult.getOut_trade_no());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.hongBaoOrderUpdate");
					paramsMap.put("order_no", fDTransactionsResult.getOut_trade_no());
					String payment_way_key = fDTransactionsResult.getPayment_way_key();
					if (ShellConstant.PAYMENT_WAY_01.equals(payment_way_key)) {
						paramsMap.put("payment_way_key", ShellConstant.PAYMENT_WAY_23);
					} else if (ShellConstant.PAYMENT_WAY_01.equals(payment_way_key)) {
						paramsMap.put("payment_way_key", ShellConstant.PAYMENT_WAY_22);
					}
					paramsMap.put("pay_date", DateUtil.date2Str(new Date(), "yyyy-MM-dd HH:mm:ss"));
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					}else{


						String memberId = fDTransactionsResult.getBuyer_id();
						memberId = StringUtil.formatStr(memberId);
						String member_coupons_id = StringUtil.formatStr(redisUtil.getStr(memberId+"couponsId"));
						member_coupons_id = StringUtil.formatStr(member_coupons_id);
						logger.error("6输出member_id: "+memberId);
						logger.error("6输出member_coupons_id: "+member_coupons_id);
						int lsn = -1;
						Map<String, Object> hashMap = new HashMap<>();
						hashMap.put("member_id", memberId);
						List<Map<String, Object>> maps = financeDao.selectCouponsLSN(hashMap);
						logger.error("6输出member_id: "+memberId+"的礼卷"+maps.size()+"张: ");
						for (int i = 0; i < maps.size(); i++) {
							Map<String, Object> objectMap = maps.get(i);
							String coupons_key = objectMap.get("coupons_key")+"";
							if (member_coupons_id.equals(objectMap.get("member_coupons_id")+"") && "jh_399".equals(coupons_key) ) {
								lsn = i + 1;
								Map<String, Object> sqlDataMap = new HashMap<>();
								sqlDataMap.put("coupons_id", member_coupons_id);
								sqlDataMap.put("member_id", memberId);
								sqlDataMap.put("oper_type", "6");
								sqlDataMap.put("coupons_type", "jh_399");
								sqlDataMap.put("order_no", null);
								sqlDataMap.put("lsn", lsn);
								sqlDataMap.put("created_time", new Date());
								financeDao.insertFDMemberCouponsLog(sqlDataMap);
								break;
							}
						}
						logger.error("6输出member_id: "+memberId+"的礼卷, 输出第几张礼卷: "+lsn);

					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							logger.info(error_msg);
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	
		
	}

	/**
	 * 话费充值通知  
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void telePhoneChargeOrderNotify(final String transaction_no,final String payment_way_key,final String member_id ,final String out_trade_no) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("话费充值->" + transaction_no);
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.telephoneCharge");
					paramsMap.put("transaction_no", transaction_no);
					paramsMap.put("payment_way_key", payment_way_key);
					paramsMap.put("member_id", member_id);
					paramsMap.put("out_trade_no", out_trade_no);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					}else {
						//调用充值
						this.tradeRecharge(member_id,transaction_no,out_trade_no);
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					if (StringUtils.isNotBlank(error_msg)) {
						logger.info(error_msg);
					}					
				}
			}
			
			/*
			 * 调用第三方话费充值
			 * reqStreamId 流水号
			 * chargeAddr  充值号码	
			 * chargeMoney 充值金额（元
			 */

			private void tradeRecharge(String memberId, String transaction_no, String out_trade_no) throws Exception {
				//6、话费充值
				Map<String, Object> params = new HashMap<String, Object>();
				String reqStreamId = DateUtil.date2Str(new Date(), "yyMMddHHmmssSSS");
				String key =  memberId + "telephoneCharge";
				String mobile = redisUtil.getStr(key);
				params.put("reqStreamId", reqStreamId);//流水号		
				params.put("chargeAddr",mobile);//充值号码				
				params.put("chargeMoney","50");//充值金额（元
				
				String chargeType = PropertiesConfigUtil.getProperty("trade.charge.chargeType");
				params.put("chargeType",chargeType);//充值类型
				String agtPhone = PropertiesConfigUtil.getProperty("trade.charge.agtPhone");
				params.put("agtPhone",agtPhone);//代理号码
				String tradePwd = PropertiesConfigUtil.getProperty("trade.charge.tradePwd");
				tradePwd = MD5Util.MD5Encode(tradePwd);
				params.put("tradePwd","48764404bcee081ba9377e41f5f4ece3");//交易密码 --32位md5小写		
				String notifyUrl = PropertiesConfigUtil.getProperty("trade.charge.notifyUrl");
				params.put("notifyUrl",notifyUrl);//回调地址
				//结果返回
				JSONObject result = requestUrl("charge", "chat.getIMUserAccount", params);		
				String status  = result.get("status")+"";
				logger.info("话费充值返回结果::==========================="+status);
				Map<String,Object> maps = JSON.parseObject(result.getString("data")); 
				//String reqStreamId=maps.get("reqStreamId")+"";//流水号
				String orderNo=maps.get("orderNo")+"";//平台订单号
				Double balance=Double.parseDouble(maps.get("balance")+"");	//充值后代理商保证金余额
				String applyTime=maps.get("applyTime")+"";	//交易时间
				Double chargeNumBalance=Double.parseDouble(maps.get("chargeNumBalance")+"");//充值后客户号码余额
				//执行成功的统一处理方法
				FDTelephoneChargeLog  telephoneChargeLog =  new  FDTelephoneChargeLog();
				telephoneChargeLog.setMemberId(memberId);
				telephoneChargeLog.setMobile(mobile);
				telephoneChargeLog.setReqStreamId(reqStreamId);
				telephoneChargeLog.setOrderNo(orderNo);
				telephoneChargeLog.setTransactionNo(transaction_no);
				telephoneChargeLog.setOutTradeNo(out_trade_no);
				telephoneChargeLog.setAmount(new BigDecimal("50"));
				telephoneChargeLog.setCompanyBalance(new BigDecimal(balance));
				telephoneChargeLog.setTradeStatus(status);
				telephoneChargeLog.setTradeTime(DateUtil.str2Date(applyTime, "yyyy-MM-dd HH:mm:ss"));
				telephoneChargeLog.setCreateTime(new Date());
				if("1011".equals(status)) {
					telephoneChargeLog.setRemark("订单正在处理中");
//					this.sendSMSNotice(mobile);
				}else {
					telephoneChargeLog.setRemark("充话费失败");
				}	
				telephoneChargeLogDao.insert(telephoneChargeLog);
			}

//			private void sendSMSNotice(String mobile) throws SystemException, Exception {
//				// TODO Auto-generated method stub
//				String url =  PropertiesConfigUtil.getProperty("notification_service_url");
//				Map<String, String> requestData = new HashMap<String, String>();
//				requestData.put("service", "notification.SMSSend");
//				Map<String, Object> params = new LinkedHashMap<String, Object>();
//				params.put("mobiles", mobile);
//				params.put("sms_source", "SJLY_03");
//				params.put("type", "到账通知");
//				params.put("msg", "充值话费");
//				requestData.put("params", FastJsonUtil.toJson(params));
//				String result = HttpClientUtil.post(url, requestData);
//			}
		});
	}

	//6、订单查询		
	public void getOrderInfo() {
		//{"data":{"orderNo":"A154512762314672148","reqStreamId":"2018121811350005698457"},"status":1011,"order":null,"msg":"订单正在处理中"}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reqStreamId", "2018121811350005698457");//流水号
		params.put("agtPhone","18664940068");//代理号码
		
		JSONObject result = requestUrl("getOrderInfo", "chat.getIMUserAccount", params);
		if(null!=result) {
			Map maps = JSON.parseObject(result.getString("data")); 
			String status  = result.get("status")+"";
			String msg = result.get("msg")+"";
			if("1000".equals(status)) {
				//执行成功的统一处理方法
				msg="充值成功";
			}else {
				msg="充值失败";
			}
		}		
	}

		
	private static JSONObject requestUrl(String url, String service, Map<String, Object> params){
		try{						
			String base_url = PropertiesConfigUtil.getProperty("trade.charge.base_url");
			String AppId = PropertiesConfigUtil.getProperty("trade.charge.AppId");
			String AppKey = PropertiesConfigUtil.getProperty("trade.charge.AppKey");
			Map<String, String> reqParams = new HashMap<String, String>();
			String paramStr=JSONObject.fromObject(params).toString();
			reqParams.put("appId", AppId);
			reqParams.put("param", DESUtils.encrypt(paramStr, AppKey));
			System.out.println(JSONObject.fromObject(reqParams));
			return JSONObject.fromObject(HttpClientUtil.postCharge(base_url + url, JSONObject.fromObject(reqParams)));			 						
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 充值订单通知
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void rcOrderPayNotify(final String transaction_no,final String payment_way_key,final String member_id ,final String out_trade_no, final String type) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("充值订单->" + transaction_no);
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.rcOrderPayNotify");
					paramsMap.put("transaction_no", transaction_no);
					paramsMap.put("payment_way_key", payment_way_key);
					paramsMap.put("member_id", member_id);
					paramsMap.put("out_trade_no", out_trade_no);
					// 17:32 2019/5/7 欧少辉 体验会员类型
					paramsMap.put("type", type);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					}
					
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {/*
							// 订单退款
							Thread.sleep(1000);
							logger.error("会过订单创建异常->" + error_msg);
							String payment_way_key = paramsMap.get("payment_way_key") + "";
							Map<String, Object> bizParams = new HashMap<>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "会过交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", payment_way_key);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
							tradeService.orderRefund(bizParams, new ResultData());
						*/}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 自营订单通知  APP端
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void ownOrderPayNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("APP端自营订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "order.consumer.ownOrderPayNotify");
					paramsMap.put("transaction_no", transaction_no);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("transaction_no", transaction_no);
						paramMap.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(paramMap);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							// 订单退款
							Thread.sleep(1000);
							logger.error("APP端自营订单创建异常->" + error_msg);
							String payment_way_key = paramsMap.get("payment_way_key") + "";
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "APP端自营订单交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", payment_way_key);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}
	
	
	/**
	 * 会过订单通知
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void huiguoOrderPayNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("会过订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "order.consumer.huiguoOrderPayNotify");
					paramsMap.put("transaction_no", transaction_no);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("transaction_no", transaction_no);
						paramMap.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(paramMap);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
					
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							// 订单退款
							Thread.sleep(1000);
							logger.error("会过订单创建异常->" + error_msg);
							String payment_way_key = paramsMap.get("payment_way_key") + "";
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "会过交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", payment_way_key);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}
	
	/**
	 * 我要批订单通知
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void wypOrderPayNotity(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("我要批订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "order.wypOrderPayNotity");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					String rsp_code = (String) resultMap.get("rsp_code");
					if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							// 订单退款
							Thread.sleep(1000);
							logger.error("我要批订单创建异常->" + error_msg);
							String payment_way_key = paramsMap.get("payment_way_key") + "";
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "我要批交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", payment_way_key);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 优惠券订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void couponCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("优惠券订单->" + paramsMap.toString());
					String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "goods.couponCreateNotify");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(goods_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 交易冲正
							logger.error("优惠券交易退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "优惠券退款");
							bizParams.put("order_type_key", Constant.ORDER_TYPE_COUPON);
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 话费充值
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void phoneBillOrderCreateNotity(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("话费充值订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "csOrder.app.phoneBillOrderCreateNotity");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> bizParams = new HashMap<String, Object>();
						bizParams.put("transaction_no", transaction_no);
						bizParams.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(bizParams);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 交易冲正
							logger.error("话费充值交易退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "话费充值交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("话费充值交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 一元抽奖订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void dskActivityOrderCreateNotify(final String transaction_no, final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("一元抽奖订单->" + paramsMap.toString());
					String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "goods.dskActivityProcessCreateNotify");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(goods_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 交易冲正
							logger.error("定时开交易退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "一元抽奖交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("consumer_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", paramsMap.get("qty") + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("一元抽交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 摇一摇订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void yyyActivityOrderCreateNotify(final String transaction_no, final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("摇一摇订单->" + paramsMap.toString());
					String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "goods.yyyActivityProcessCreateNotify");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(goods_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							// 交易冲正
							Thread.sleep(1000);
							logger.error("摇一摇交易退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "摇一摇交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("consumer_id"));
							bizParams.put("out_member_id", paramsMap.get("stores_id"));
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", "1.00");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("摇一摇交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 刮刮乐订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void gglActivityOrderCreateNotify(final String transaction_no, final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("刮刮乐订单->" + paramsMap.toString());
					String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "goods.gglActivityProcessCreateNotify");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(goods_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 交易冲正
							logger.error("刮刮乐交易退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "刮刮乐交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("consumer_id"));
							bizParams.put("out_member_id", paramsMap.get("stores_id"));
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", "1.00");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 便利精选特卖
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void pcOrderPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("精选特卖订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "order.pcOrderPayCreateNotify");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> bizParams = new HashMap<String, Object>();
						bizParams.put("transaction_no", transaction_no);
						bizParams.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(bizParams);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 交易冲正
							logger.error("精选特卖交易退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "精选特卖交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("consumer_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("精选特卖交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 金币兑订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void geOrderPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("名品汇订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "order.geOrderPayCreateNotify");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("transaction_no", transaction_no);
						paramMap.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(paramMap);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 交易冲正
							logger.error("名品汇交易退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "品牌领交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("名品汇交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 积分兑订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void peOrderPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {

				String error_msg = null;
				try {
					logger.info("会员权益订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "gdActivity.consumer.gdActivityDeliveryCreate");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("transaction_no", transaction_no);
						paramMap.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(paramMap);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 交易冲正
							logger.error("会员权益退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "会员权益交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_09);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("名品汇交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 领了么订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void fgOrderPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("领了么订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "order.fgOrderCreateNotity");
					paramsMap.put("transaction_no", transaction_no);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("transaction_no", transaction_no);
						paramMap.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(paramMap);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							// 交易冲正
							Map<String, Object> bizParams = new HashMap<String, Object>();
							if (paramsMap.get("data_source") + "" == "SJLY_17") {
								bizParams.put("data_source", "SJLY_17");
								bizParams.put("detail", "自营商品交易退款");
							} else {
								bizParams.put("data_source", "SJLY_03");
								bizParams.put("detail", "淘淘领交易退款");
							}
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							logger.error("领了么交易退款->" + error_msg);
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("领了么交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 惠易定订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void hyd2OrderPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("惠易定2.0订单->" + paramsMap.toString());
					String hyd_service_url = PropertiesConfigUtil.getProperty("hyd_service_url") + "payment/notify";
					String hyd_sign_key = PropertiesConfigUtil.getProperty("hyd_sign_key");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("payment_id", paramsMap.get("payment_id") + "");
					reqParams.put("status", "1");
					reqParams.put("transaction_no", transaction_no);
					reqParams.put("transaction_no", transaction_no);
					reqParams.put("payment_way_key", paramsMap.get("payment_way_key") + "");
					reqParams.put("sign", MD5Util.sign(createLinkString(reqParams), hyd_sign_key, "utf-8"));
					logger.info("惠易定2.0支付结果通知->request:" + reqParams.toString());
					String resultStr = HttpClientUtil.post(hyd_service_url, reqParams);
					logger.info("惠易定2.0支付结果通知->response:" + resultStr);
					if (resultStr.equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> bizParams = new HashMap<String, Object>();
						bizParams.put("transaction_no", transaction_no);
						bizParams.put("out_trade_no", paramsMap.get("payment_id"));
						transactionDao.updateFDTransactionsResult(bizParams);
					} else {
						error_msg = resultStr;
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							logger.error("惠易定2.0交易退款->error_msg:" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "惠易定2.0交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", paramsMap.get("payment_id"));
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("惠易定惠易定2.0交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 团购预售订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void psGroupPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("团购预售订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "order.psGroupSubOrderCreateNotify");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("transaction_no", transaction_no);
						paramMap.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(paramMap);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 订单退款
							logger.error("团购预售订单退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", Constant.DATA_SOURCE_SJLY_03);
							bizParams.put("detail", "团购预售订单退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 惠易定3.0订单
	 * 
	 * @param transaction_no
	 * @param paramsMap
	 */
	private void hyd3OrderPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("惠易定3.0订单->" + paramsMap.toString());
					String hyd3_service_url = PropertiesConfigUtil.getProperty("hyd3_service_url")
							+ "javaApis/hydAccount/payCallback";
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("payment_id", paramsMap.get("payment_id") + "");
					reqParams.put("payment_way_key", paramsMap.get("payment_way_key") + "");
					reqParams.put("transaction_no", transaction_no);
					reqParams.put("price", paramsMap.get("price") + "");
					reqParams.put("result", "succ");
					logger.info("惠易定3.0支付请求->request:" + reqParams.toString());
					String resultStr = HttpClientUtil.post(hyd3_service_url, reqParams);
					logger.info("惠易定3.0支付结果通知->response:" + resultStr);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					Integer status = (Integer) resultMap.get("status");
					if (status != 200) {
						error_msg = "通知失败,status->" + status;
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							logger.error("惠易定交易退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "惠易定3.0交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", paramsMap.get("payment_id"));
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("惠易定交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 伙拼团活动创建
	 * 
	 * @Title: tsOrderPayCreateNotify
	 * @param transaction_no
	 * @param paramsMap
	 * @author tiny
	 */
	private void tsActivityPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("拼团领创建首笔订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "tsActivity.consumer.ladderTsActivityCreate");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("transaction_no", transaction_no);
						paramMap.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(paramMap);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 订单退款
							logger.error("拼团领创建首笔订单退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", Constant.DATA_SOURCE_SJLY_03);
							bizParams.put("detail", "拼团领订单退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}

	/**
	 * 伙拼团活动创建
	 * 
	 * @Title: tsOrderPayCreateNotify
	 * @param transaction_no
	 * @param paramsMap
	 * @author tiny
	 */
	private void tsOrderPayCreateNotify(final String transaction_no, final BigDecimal amount,
			final Map<String, Object> paramsMap) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("拼团领订单->" + paramsMap.toString());
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					reqParams.put("service", "tsOrder.consumer.ladderTsOrderCreate");
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
						String order_no = (String) dataMap.get("order_no");
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("transaction_no", transaction_no);
						paramMap.put("out_trade_no", order_no);
						transactionDao.updateFDTransactionsResult(paramMap);
					} else {
						error_msg = resultMap.get("error_msg") + "";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {
							Thread.sleep(1000);
							// 订单退款
							logger.error("拼团领订单退款->" + error_msg);
							Map<String, Object> bizParams = new HashMap<String, Object>();
							bizParams.put("data_source", Constant.DATA_SOURCE_SJLY_03);
							bizParams.put("detail", "拼团领订单退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", Constant.PAYMENT_WAY_05);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
							tradeService.orderRefund(bizParams, new ResultData());
						}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}

	public String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = (String) params.get(key);
			// 拼接时，不包括最后一个&字符
			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	/**
	 * 贝壳商城订单通知
	 */
	private void beikeMallOrderPayNotify(final FDTransactionsResult fDTransactionsResult) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				
				try {
					logger.info("贝壳商城订单通知->" + fDTransactionsResult.getOut_trade_no()+"订单金额:"+String.valueOf(fDTransactionsResult.getAmount()));
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.beikeMallOrderUpdate");
					paramsMap.put("order_no", fDTransactionsResult.getOut_trade_no());
					paramsMap.put("payment_way_key", fDTransactionsResult.getPayment_way_key());
					paramsMap.put("trade_fee", String.valueOf(fDTransactionsResult.getAmount()));
					paramsMap.put("pay_date", DateUtil.date2Str(new Date(), "yyyy-MM-dd HH:mm:ss"));
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					} 
					Map<String, Object> orderInfoMap = (Map<String, Object>) resultMap.get("data");
					String memberId =  (String) orderInfoMap.get("memberId");
					Integer  beikeCredit = (Integer) orderInfoMap.get("beikeCredit");
					Map<String, Object> memberMap =  new HashMap<String, Object>();
					memberMap.put("member_id", memberId);
					FDMemberAsset   fdMemberAsset = financeDao.selectFDMemberAsset(memberMap);
					BigDecimal  beikeQty  =  fdMemberAsset.getShell();
					if(beikeQty.compareTo(BigDecimal.valueOf(beikeCredit))==-1) {
						throw new BusinessException("会员贝壳数量不够", "贝壳商超支付后，会员贝壳数量不够");
					}
					//减去贝壳
					BigDecimal   shell   = beikeQty.subtract(BigDecimal.valueOf(beikeCredit));
					Map<String, Object> assetMap =  new HashMap<String, Object>();
					assetMap.put("sell", shell);
					assetMap.put("member_id", memberId);
					int r  = financeDao.updateMemberShell(assetMap);
					if(r ==1) {
						//写入贝壳日志
						FDMemberShellLog    fdMemberShellLog =  new FDMemberShellLog();
						fdMemberShellLog.setMember_id(memberId);
						fdMemberShellLog.setMember_type_key(CommonConstant.MEMBER_TYPE_CONSUMER);
						fdMemberShellLog.setCategory("expenditure");
						fdMemberShellLog.setPre_balance(beikeQty);
						fdMemberShellLog.setAmount(BigDecimal.valueOf(beikeCredit));
						fdMemberShellLog.setBalance(shell);
						fdMemberShellLog.setTransaction_no(fDTransactionsResult.getTransaction_no());
						fdMemberShellLog.setTracked_date(new Date());
						
						Map<String, Object> remarkMap = new HashMap<>();
						remarkMap.put("detail", ShellConstant.PAYMENT_WAY_MAP.get("PAYMENT_WAY_07"));
						remarkMap.put("payment_way_key", ShellConstant.PAYMENT_WAY_07);
						remarkMap.put("transaction_member_id", memberId);
						remarkMap.put("transaction_member_name", "");
						remarkMap.put("transaction_member_type_key", CommonConstant.MEMBER_TYPE_CONSUMER);
						remarkMap.put("transaction_no", fDTransactionsResult.getTransaction_no());
						fdMemberShellLog.setRemark(FastJsonUtil.toJson(remarkMap));
						transactionDao.insertFDMemberShellLog(fdMemberShellLog);
					}
					
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {/*
							// 订单退款
							Thread.sleep(1000);
							logger.error("会过订单创建异常->" + error_msg);
							String payment_way_key = paramsMap.get("payment_way_key") + "";
							Map<String, Object> bizParams = new HashMap<>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "会过交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", payment_way_key);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
							tradeService.orderRefund(bizParams, new ResultData());
						*/}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}
	
	
	/**
	 * 贝壳商城红包支付
	 */
	private void beikeMallOrderPayRedPacket(final FDTransactionsResult fDTransactionsResult) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("贝壳商城订单通知->" + fDTransactionsResult.getOut_trade_no()+";会员id:"+fDTransactionsResult.getBuyer_id());
					// 得到订单ID，返回状态，判断字符串
					String   orderNo = fDTransactionsResult.getOut_trade_no();
					String  status =  this.findOrderInfoByNo(orderNo);
					if(StringUtils.equals("wait_buyer_pay", status)) {
						Map<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("member_id", fDTransactionsResult.getBuyer_id());
						FDMemberAsset memberAsset = financeDao.selectFDMemberAsset(tempMap);
						BigDecimal   balance =  memberAsset.getCash_balance();
						BigDecimal  trade_amount = fDTransactionsResult.getAmount();
						BigDecimal cash_balance = MoneyUtil.moneySub(balance, trade_amount);
						tempMap.clear();
						tempMap.put("member_id", fDTransactionsResult.getBuyer_id());
						tempMap.put("cash_balance", cash_balance);
						Integer flag =  financeDao.updateMemberShell(tempMap);
						if(flag ==1) {
							String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
							Map<String, String> reqParams = new HashMap<>();
							Map<String, String> paramsMap = new HashMap<>();
							reqParams.put("service", "order.consumer.beikeMallOrderUpdate");
							paramsMap.put("order_no", fDTransactionsResult.getOut_trade_no());
							paramsMap.put("payment_way_key", ShellConstant.PAYMENT_WAY_08);
							paramsMap.put("redpacket_fee", String.valueOf(trade_amount));
							paramsMap.put("pay_date", DateUtil.date2Str(new Date(), "yyyy-MM-dd HH:mm:ss"));
							reqParams.put("params", FastJsonUtil.toJson(paramsMap));
							String resultStr = HttpClientUtil.post(order_service_url, reqParams);
							Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
							if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
								error_msg = resultMap.get("error_msg") + "";
							} 
							//写入记录
							FDMemberRebateLog   memberRebateLog  =  new  FDMemberRebateLog();
							memberRebateLog.setMember_id(fDTransactionsResult.getBuyer_id());
							memberRebateLog.setCategory("expenditure");
							String tradeMount = "-"+trade_amount;
							memberRebateLog.setCash_money(new BigDecimal(tradeMount).setScale(2, BigDecimal.ROUND_HALF_UP));
							memberRebateLog.setCreate_time(new Date());
							Map<String,Object>  remarkMap =  new  HashMap<String,Object>();
							remarkMap.put("detail", "购物");
							remarkMap.put("交易金额", trade_amount);
							String itemJSONObj = JSON.toJSONString(remarkMap);
							//增加日志记录
							memberRebateLog.setPre_balance(balance);
							memberRebateLog.setBalance(cash_balance);
							memberRebateLog.setRemark(itemJSONObj);
							financeDao.insertFdMemberRebateLog(memberRebateLog);

						}else {
							error_msg="更新会员余额失败";
						}
					}else {
						error_msg="订单不是未支付状态";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				}finally {
					if(StringUtils.isNotEmpty(error_msg)) {
						logger.info(error_msg);
					}
				}
			}

			private String findOrderInfoByNo(String orderNo) {
				try {
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.findOrderInfoByNo");
					paramsMap.put("order_no", orderNo);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						throw new BusinessException((String) resultMap.get("error_code"),
								(String) resultMap.get("error_msg"));
					}
					Map<String, Object> orderMap = (Map<String, Object>) resultMap.get("data");
					String status = (String) orderMap.get("status");
					return status;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				
			}
		});
	}

	
	/**
	 * 贝壳商城礼券支付
	 */
	private void beikeMallOrderPayGift(final FDTransactionsResult fDTransactionsResult) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("贝壳商城订单通知->" + fDTransactionsResult.getOut_trade_no()+";会员id:"+fDTransactionsResult.getBuyer_id());
					// 得到订单ID，返回状态，判断字符串
					String   orderNo = fDTransactionsResult.getOut_trade_no();
					String  status =  this.findHongBaoOrderInfoByNo(orderNo);
					if(StringUtils.equals("wait_buyer_pay", status)) {
						//  得到 礼券的id。
						String  memberId =  fDTransactionsResult.getBuyer_id();
						String  member_coupons_id  =  StringUtil.formatStr(redisUtil.getStr(memberId+"couponsId"));
						if(StringUtils.isEmpty(member_coupons_id)) {
							throw new BusinessException("未传入会员礼券ID", "礼券支付时,会员礼券ID为空！");
						}
						Map<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("member_coupons_id", member_coupons_id);
						List<Map<String,Object>> listCoupons = financeDao.selectMemberCoupons(tempMap);
						if(null != listCoupons &&  listCoupons.size()>0) {
							Map<String,Object> couponsMap =  listCoupons.get(0);
							Integer  coupons_mun  =  (Integer) couponsMap.get("coupons_mun");
							if(null !=coupons_mun &&  coupons_mun >0 ) {
								couponsMap.put("coupons_mun", coupons_mun -1);
								Integer  flag =	financeDao.updateMemberCouByMemberId(couponsMap);
								if(flag ==1) {
									String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
									Map<String, String> reqParams = new HashMap<>();
									Map<String, String> paramsMap = new HashMap<>();
									reqParams.put("service", "order.consumer.hongBaoOrderUpdate");
									paramsMap.put("order_no", fDTransactionsResult.getOut_trade_no());
									paramsMap.put("payment_way_key", ShellConstant.PAYMENT_WAY_09);
									paramsMap.put("pay_date", DateUtil.date2Str(new Date(), "yyyy-MM-dd HH:mm:ss"));
									reqParams.put("params", FastJsonUtil.toJson(paramsMap));
									String resultStr = HttpClientUtil.post(order_service_url, reqParams);
									Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
									if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
										error_msg = resultMap.get("error_msg") + "";
									} else {
//
									}
								}else {
									error_msg="礼券支付时，扣除礼券失败";
								}
							}
							
						}
					}else {
						error_msg="订单不是未支付状态";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				}finally {
					if(StringUtils.isNotEmpty(error_msg)) {
						logger.info(error_msg);
					}
				}
			}

			public String findHongBaoOrderInfoByNo(String orderNo) {
				try {
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.findHongBaoOrderInfoByNo");
					paramsMap.put("order_no", orderNo);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						throw new BusinessException((String) resultMap.get("error_code"),
								(String) resultMap.get("error_msg"));
					}
					Map<String, Object> orderMap = (Map<String, Object>) resultMap.get("data");
					String status = (String) orderMap.get("status");
					return status;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				
			}
		});
	}
	
	/**
	 * 贝壳商城礼券支付
	 */
	private void beikeMallOrderPayH5(final FDTransactionsResult fDTransactionsResult) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("贝壳商城订单通知->" + fDTransactionsResult.getOut_trade_no()+";会员id:"+fDTransactionsResult.getBuyer_id());
					// 得到订单ID，返回状态，判断字符串
					String   orderNo = fDTransactionsResult.getOut_trade_no();
					String  status =  this.findHongBaoOrderInfoByNo(orderNo);
					if(StringUtils.equals("wait_buyer_pay", status)) {
						String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
						Map<String, String> reqParams = new HashMap<>();
						Map<String, String> paramsMap = new HashMap<>();
						reqParams.put("service", "order.consumer.hongBaoOrderUpdate");
						paramsMap.put("order_no", fDTransactionsResult.getOut_trade_no());
						paramsMap.put("payment_way_key", ShellConstant.PAYMENT_WAY_10);
						paramsMap.put("pay_date", DateUtil.date2Str(new Date(), "yyyy-MM-dd HH:mm:ss"));
						reqParams.put("params", FastJsonUtil.toJson(paramsMap));
						String resultStr = HttpClientUtil.post(order_service_url, reqParams);
						Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
						if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
							error_msg = resultMap.get("error_msg") + "";
						} 													
					}else {
						error_msg="订单不是未支付状态";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				}finally {
					if(StringUtils.isNotEmpty(error_msg)) {
						logger.info(error_msg);
					}
				}
			}

			private String findHongBaoOrderInfoByNo(String orderNo) {
				try {
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.findHongBaoOrderInfoByNo");
					paramsMap.put("order_no", orderNo);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						throw new BusinessException((String) resultMap.get("error_code"),
								(String) resultMap.get("error_msg"));
					}
					Map<String, Object> orderMap = (Map<String, Object>) resultMap.get("data");
					String status = (String) orderMap.get("status");
					return status;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		});
	}
	
	
	/**
	 * 贝壳街市订单通知
	 */
	private void beikeStreetOrderPayNotify(final String transaction_no,final String out_trade_no) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				
				try {
					logger.info("贝壳街市订单通知->" + transaction_no);
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.beikeStreetOrderUpdate");
					paramsMap.put("order_no", out_trade_no);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						error_msg = resultMap.get("error_msg") + "";
					} 
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				} finally {
					try {
						if (StringUtils.isNotBlank(error_msg)) {/*
							// 订单退款
							Thread.sleep(1000);
							logger.error("会过订单创建异常->" + error_msg);
							String payment_way_key = paramsMap.get("payment_way_key") + "";
							Map<String, Object> bizParams = new HashMap<>();
							bizParams.put("data_source", "SJLY_03");
							bizParams.put("detail", "会过交易退款");
							bizParams.put("order_type_key", paramsMap.get("order_type_key"));
							bizParams.put("out_trade_no", transaction_no);
							bizParams.put("in_member_id", paramsMap.get("member_id"));
							bizParams.put("out_member_id", Constant.MEMBER_ID_MTH);
							bizParams.put("payment_way_key", payment_way_key);
							bizParams.put("amount", amount + "");
							bizParams.put("out_trade_body", FastJsonUtil.toJson(bizParams));
							tradeService.orderRefund(bizParams, new ResultData());
						*/}
					} catch (Exception e2) {
						logger.error("交易退款异常", e2);
					}
				}
			}
		});
	}
	
	/**
	 * 红包支付订单通知
	 */
	private void hongBaoOrderPayNotify(final FDTransactionsResult fDTransactionsResult) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				String error_msg = null;
				try {
					logger.info("礼包专区订单通知->" + fDTransactionsResult.getOut_trade_no() + ";会员id:" + fDTransactionsResult.getBuyer_id());
					// 得到订单ID，返回状态，判断字符串
					String orderNo = fDTransactionsResult.getOut_trade_no();
					String status = this.findHongBaoOrderInfoByNo(orderNo);
					if (StringUtils.equals("wait_buyer_pay", status)) {
						// 得到 礼券的id。
						String memberId = fDTransactionsResult.getBuyer_id();
						String member_coupons_id = StringUtil.formatStr(redisUtil.getStr(memberId + "couponsId"));
						if (StringUtils.isEmpty(member_coupons_id)) {
							throw new BusinessException("未传入会员礼券ID", "礼券支付时,会员礼券ID为空！");
						}
						Map<String, Object> tempMap = new HashMap<>();
						int size = 1;
						if (member_coupons_id.contains(",")) {
							List<String> coupons_id_in = StringUtil.str2List(member_coupons_id, ",");
							size = coupons_id_in.size();
							tempMap.put("coupons_id_in", coupons_id_in);
						} else {
							tempMap.put("member_coupons_id", member_coupons_id);
						}
						tempMap.put("coupons_mun", 0);
						List<Map<String, Object>> listCoupons = financeDao.selectMemberCoupons(tempMap);
						if (size != listCoupons.size()) {
							throw new BusinessException("礼券错误", "礼券支付时,应抵扣礼券数量错误");
						}
						Map<String, Object> couponsMap = listCoupons.get(0);
						Integer coupons_mun = (Integer) couponsMap.get("coupons_mun");
						if (null != coupons_mun && coupons_mun > 0) {
							tempMap.put("coupons_mun", coupons_mun - 1);
							Integer flag = financeDao.updateMemberCouByMemberId(tempMap);
							if (flag != size) {
								error_msg = "礼券支付时，扣除礼券失败";
							}
						}
					} else {
						error_msg = "订单不是未支付状态";
					}
				} catch (BusinessException e) {
					error_msg = e.getMsg();
				} catch (SystemException e) {
					error_msg = e.getMsg();
				} catch (Exception e) {
					error_msg = e.getMessage();
				}finally {
					if(StringUtils.isNotEmpty(error_msg)) {
						logger.info(error_msg);
					}
				}
			}

			public String findHongBaoOrderInfoByNo(String orderNo) {
				try {
					String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
					Map<String, String> reqParams = new HashMap<>();
					Map<String, String> paramsMap = new HashMap<>();
					reqParams.put("service", "order.consumer.findHongBaoOrderInfoByNo");
					paramsMap.put("order_no", orderNo);
					reqParams.put("params", FastJsonUtil.toJson(paramsMap));
					String resultStr = HttpClientUtil.post(order_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						throw new BusinessException((String) resultMap.get("error_code"),
								(String) resultMap.get("error_msg"));
					}
					Map<String, Object> orderMap = (Map<String, Object>) resultMap.get("data");
					String status = (String) orderMap.get("status");
					return status;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				
			}
		});
	}
}
