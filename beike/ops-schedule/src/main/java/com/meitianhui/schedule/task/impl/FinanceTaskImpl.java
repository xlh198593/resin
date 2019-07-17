package com.meitianhui.schedule.task.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.schedule.task.FinanceTask;

@Component
public class FinanceTaskImpl implements FinanceTask {

	private static final Logger logger = Logger.getLogger(FinanceTaskImpl.class);

	//@Scheduled(cron = "0 */30 1-3 * * ?")
	@Override
	public void alipayBillImport() {
		try {
			logger.info("支付宝对账单导入");
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			// 更新商品库存
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "facePay.alipay.billImport");
			// 账单日期(下载两天前的对账单)
			String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			String bill_date = DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -2);
			bizParams.put("bill_date", bill_date);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			HttpClientUtil.post(finance_service_url, reqParams);
		} catch (BusinessException e) {
			logger.warn("支付宝对账单失败," + e.getMsg());
		} catch (SystemException e) {
			logger.error("支付宝对账单异常", e);
		} catch (Exception e) {
			logger.error("支付宝对账单异常", e);
		}
	}

	// 20分钟执行一次
	@Override
	//@Scheduled(cron = "0 */30 1-3 * * ?")
	public void wechatBillImport() {
		try {
			logger.info("微信对账单导入");
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			// 账单日期(下载两天前的对账单)
			String bill_date = DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -2);
			bizParams.put("bill_date", bill_date);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			// 消费者对账单导入
			reqParams.put("service", "consumer.wechat.billImport");
			HttpClientUtil.post(finance_service_url, reqParams);
			// 店东对账单导入
			reqParams.put("service", "stores.wechat.billImport");
			HttpClientUtil.post(finance_service_url, reqParams);
			// 惠点公众号对账单导入
			reqParams.put("service", "huidian.wechat.billImport");
			HttpClientUtil.post(finance_service_url, reqParams);
			// 熟么对账单导入
			reqParams.put("service", "shume.wechat.billImport");
			HttpClientUtil.post(finance_service_url, reqParams);
			// 惠点收银对账单导入
			reqParams.put("service", "cashier.wechat.billImport");
			HttpClientUtil.post(finance_service_url, reqParams);
			// 惠驿哥对账单导入
			reqParams.put("service", "hyg.wechat.billImport");
			HttpClientUtil.post(finance_service_url, reqParams);
		} catch (BusinessException e) {
			logger.warn("微信对账单失败," + e.getMsg());
		} catch (SystemException e) {
			logger.error("微信对账单异常", e);
		} catch (Exception e) {
			logger.error("微信对账单异常", e);
		}
	}

	// 20分钟执行一次
	@Override
	//@Scheduled(cron = "0 */20 4-5 * * ?")
	public void billCheck() {
		try {
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			String bill_date = DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -2);
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			bizParams.put("bill_date", bill_date);
			// 支付宝交易对账
			logger.info("支付宝交易对账");
			reqParams.put("service", "alipay.billCheck");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			HttpClientUtil.post(finance_service_url, reqParams);
			// 微信交易对账
			logger.info("微信交易对账");
			reqParams.put("service", "wechat.billCheck");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			HttpClientUtil.post(finance_service_url, reqParams);
		} catch (BusinessException e) {
			logger.warn("交易对账失败," + e.getMsg());
		} catch (SystemException e) {
			logger.error("交易对账异常", e);
		} catch (Exception e) {
			logger.error("交易对账异常", e);
		}
	}
	
	// 每月1号1点执行
	@Override
	@Scheduled(cron = "0 0 1 1 * ?")
	public void monthlyGiftCoupon() {
		try {
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<>();
			Map<String, Object> bizParams = new HashMap<>();
			//查找所有未到期的会员
			logger.info("每月赠送会员礼券");
			reqParams.put("service", "finance.consumer.monthlyGiftCoupon");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			HttpClientUtil.post(finance_service_url, reqParams);
		}catch (Exception e) {
			logger.error("每月赠送会员礼券异常", e);
		}
	}
	
	// 每天晚上1点执行
	@Override
	@Scheduled(cron = "0 0 1 * * ?")
	public void birthdayGiftCoupon() {
		try {
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<>();
			Map<String, Object> bizParams = new HashMap<>();
			//查找所有未到期的会员
			logger.info("每月赠送会员礼券");
			reqParams.put("service", "finance.consumer.birthdayGiftCoupon");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			HttpClientUtil.post(finance_service_url, reqParams);
		}catch (Exception e) {
			logger.error("每月赠送会员礼券异常", e);
		}
	}

	// 每月1号1点执行
	@Override
//	@Scheduled(cron = "0 0 1 1 * ?")
	//@Scheduled(cron = "0 */10 * * * ?")
	public void manageRateToBalance() {
		try {
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<>();
			Map<String, Object> bizParams = new HashMap<>();
			// 查找所有未到期的会员
			logger.info("掌柜每月1号定时将次邀金额转到红包金额里面");
			reqParams.put("service", "finance.consumer.manageRateToBalance");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			HttpClientUtil.post(finance_service_url, reqParams);
		} catch (Exception e) {
			logger.error("每月次邀金额转到红包金额异常", e);
		}
	}
	
}
