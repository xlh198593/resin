package com.meitianhui.finance.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.constant.ShellConstant;
import com.meitianhui.finance.constant.TradeIDUtil;
import com.meitianhui.finance.dao.FinanceDao;
import com.meitianhui.finance.entity.FDMemberAsset;
import com.meitianhui.finance.entity.FDTransactions;
import com.meitianhui.finance.service.AlipayPayService;
import com.meitianhui.finance.service.ShellOrderPayService;
import com.meitianhui.finance.service.TradeService;
import com.meitianhui.finance.service.WechatPayService;

import net.sf.json.JSONObject;

@SuppressWarnings("unchecked")
@Service
public class ShellOrderPayServiceImpl implements ShellOrderPayService {

	private static final Logger logger = Logger.getLogger(ShellOrderPayServiceImpl.class);

	@Autowired
	private FinanceDao financeDao;

	@Autowired
	private AlipayPayService alipayPayService;

	@Autowired
	private WechatPayService wechatPayService;

	@Autowired
	private TradeService tradeService;
	
	@Autowired
	public RedisUtil redisUtil;
	

	//添加话费充值订单
	public void addTradeOrder(Map<String, Object> paramsMap, ResultData result,FDMemberAsset member) throws BusinessException, SystemException, Exception{
		String data_source = (String) paramsMap.get("data_source");// 数据来源
		String payment_way_key = (String) paramsMap.get("payment_way_key");// 支付方式
		if (null == Constant.DATA_SOURCE_MAP.get(data_source)) {
			throw new BusinessException(RspCode.DATA_SOURCE_ERROR, RspCode.MSG.get(RspCode.DATA_SOURCE_ERROR));
		}
		//不用判断金额
		if ("支付宝支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))) {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_ZFB);
		} else if ("微信支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))) {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_WX);
		} else {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_MTH);
		}
		paramsMap.put("trade_type_key", ShellConstant.TRADE_TYPE_01);
		paramsMap.put("order_type_key", ShellConstant.ORDER_TYPE_06);//订单支付类型--充值订单 
		//添加订单操作类型
		
		String remark = Constant.DATA_SOURCE_MAP.get(paramsMap.get("data_source")) + "|"
				+ ShellConstant.TRADE_TYPE_MAP.get(paramsMap.get("trade_type_key")) + "|"
				+ ShellConstant.PAYMENT_WAY_MAP.get(paramsMap.get("payment_way_key")) + "|" + paramsMap.get("amount");
		paramsMap.put("remark", remark);
		addOrder(paramsMap, result, data_source, payment_way_key, remark, member);
	}

	@Override
	public void shellRecharge(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// type 会员类型， 0:SVIP会员  1：内测会员    2:vip会员  3，普通会员   4过期会员，5体验会员
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "payment_way_key", "amount", "member_id" ,"trade_type_key","currency_code"});
		String data_source = (String) paramsMap.get("data_source");// 数据来源
		String payment_way_key = (String) paramsMap.get("payment_way_key");// 支付方式
		String order_type_key = StringUtil.formatStr(paramsMap.get("order_type_key")); // 支付方式
		if (null == Constant.DATA_SOURCE_MAP.get(data_source)) {
			throw new BusinessException(RspCode.DATA_SOURCE_ERROR, RspCode.MSG.get(RspCode.DATA_SOURCE_ERROR));
		}
		paramsMap.put("buyer_id", paramsMap.get("member_id"));

		//微信和支付宝支付才会判断金额
		if ("支付宝支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))
				|| "微信支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))) {
			BigDecimal amount = new BigDecimal((String) paramsMap.get("amount"));
			// 如果金额是49.90 就是体验会员
			String experience_member_recharge_amout = PropertiesConfigUtil.getProperty("experience_member_recharge_amout");
			String amout_year_service = PropertiesConfigUtil.getProperty("member_recharge_amout_year");
			if (amount.compareTo(new BigDecimal(amout_year_service)) == 0) {
				paramsMap.put("recharge_type", "year");
			} else if(amount.setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(experience_member_recharge_amout))==1) {
				paramsMap.put("amount", experience_member_recharge_amout);
			} else if(amount.setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(experience_member_recharge_amout))==0) {
			}else{
				throw new BusinessException("金额错误", amount + "金额错误" + amout_year_service);
			}

		}

		if ("支付宝支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))) {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_ZFB);
		} else if ("微信支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))) {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_WX);
		} else {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_MTH);
		}
		if("H5支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))) {
			paramsMap.put("detail", "购买会员");
		}
		paramsMap.put("trade_type_key", ShellConstant.TRADE_TYPE_01);
		if(StringUtil.isNotBlank(order_type_key)) {
			paramsMap.put("order_type_key", order_type_key);
		}else {
			paramsMap.put("order_type_key", ShellConstant.ORDER_TYPE_06);
		}

		String remark = Constant.DATA_SOURCE_MAP.get(paramsMap.get("data_source")) + "|"
				+ ShellConstant.TRADE_TYPE_MAP.get(paramsMap.get("trade_type_key")) + "|"
				+ ShellConstant.PAYMENT_WAY_MAP.get(paramsMap.get("payment_way_key")) + "|" + paramsMap.get("amount");
		paramsMap.put("remark", remark);
		// 检测充值方是否存在
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("member_id", paramsMap.get("buyer_id"));
		FDMemberAsset buyer = financeDao.selectFDMemberAsset(queryMap);
		if (buyer == null) {
			throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
		}
		addOrder(paramsMap, result, data_source, payment_way_key, remark, buyer);
	}

	private void addOrder(Map<String, Object> paramsMap, ResultData result, String data_source, String payment_way_key,
			String remark, FDMemberAsset buyer) throws BusinessException, SystemException, Exception {
		// 入库
		transactionsCreateForRecharge(paramsMap, result);
		Map<String, Object> resultMap = (Map<String, Object>) result.getResultData();
		String transaction_no = (String) resultMap.get("transaction_no");

		// 进行充值订单创建 2018/7/12
		String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "order.rcOrderCreate");
		bizParams.put("transaction_no", transaction_no);
		bizParams.put("out_trade_no", paramsMap.get("out_trade_no"));
		bizParams.put("amount", paramsMap.get("amount"));
	//	bizParams.put("amount", "1.2");
		bizParams.put("payment_way_key", payment_way_key);
		bizParams.put("member_type_key", buyer.getMember_type_key());
		bizParams.put("member_id", buyer.getMember_id());
		bizParams.put("remark", remark);
		bizParams.put("data_source", data_source);
		bizParams.put("trade_type_key", paramsMap.get("trade_type_key"));
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(order_service_url, reqParams);
		logger.info("创建充值订单返回结果->" + resultStr);
		resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		if (payment_way_key.equals(Constant.PAYMENT_WAY_01)) {
			Map<String, String> alipayMap = new HashMap<String, String>();
			String total_fee = paramsMap.get("amount") + "";
			String subject = paramsMap.get("detail") + "";
			Map<String, Object> reqMap = new HashMap<String, Object>();
			reqMap.put("out_trade_no", transaction_no);
			reqMap.put("amount", total_fee);
			reqMap.put("subject", subject);
			if (data_source.equals(Constant.DATA_SOURCE_SJLY_01)) {
				alipayMap = alipayPayService.consumerAlipayInfoGet(reqMap);
			} else if (data_source.equals(Constant.DATA_SOURCE_SJLY_02)) {
				// alipayMap = alipayPayService.storeAlipayInfoGet(reqMap);
			} else if (data_source.equals(Constant.DATA_SOURCE_SJLY_12)) {
				// alipayMap = alipayPayService.shumeAlipayInfoGet(reqMap);
			} else if (data_source.equals(Constant.DATA_SOURCE_SJLY_14)) {
				// alipayMap = alipayPayService.cashierAlipayInfoGet(reqMap);
			} else {
				throw new BusinessException(RspCode.PAYMENT_WAY_ERROR, "支付宝支付暂未开通");
			}
			alipayMap.put("transaction_no", transaction_no);
			result.setResultData(alipayMap);
		} else if (payment_way_key.equals(Constant.PAYMENT_WAY_02)) {
			Map<String, Object> wechatInfoMap = new HashMap<String, Object>();
			Map<String, Object> reqMap = new HashMap<String, Object>();
			reqMap.put("out_trade_no", transaction_no);
			reqMap.put("amount", paramsMap.get("amount") + "");
			reqMap.put("body", paramsMap.get("detail") + "");
			reqMap.put("spbill_create_ip", "127.0.0.1");
			if (data_source.equals(Constant.DATA_SOURCE_SJLY_01)) {
				wechatInfoMap = wechatPayService.consumerWechatInfoGet(reqMap);
			} else if (data_source.equals(Constant.DATA_SOURCE_SJLY_02)) {
				// wechatInfoMap = wechatPayService.storeWechatInfoGet(reqMap);
			} else if (data_source.equals(Constant.DATA_SOURCE_SJLY_12)) {
				// wechatInfoMap = wechatPayService.shumeWechatInfoGet(reqMap);
			} else if (data_source.equals(Constant.DATA_SOURCE_SJLY_14)) {
				// wechatInfoMap = wechatPayService.cashierWechatInfoGet(reqMap);
			} else if (data_source.equals(Constant.DATA_SOURCE_SJLY_17)) {
				// reqMap.put("js_code", paramsMap.get("js_code"));
				// wechatInfoMap = wechatPayService.miniAppWechatInfoGet(reqMap);
			} else {
				throw new BusinessException(RspCode.PAYMENT_WAY_ERROR, "微信支付暂未开通");
			}
			wechatInfoMap.put("transaction_no", transaction_no);
			result.setResultData(wechatInfoMap);
		} else if (payment_way_key.equals(ShellConstant.PAYMENT_WAY_09)) {
			String order_type_key = (String) paramsMap.get("order_type_key");// 订单类型
			if (order_type_key.equals(ShellConstant.ORDER_TYPE_06)) {
				Map<String, Object> temp = new HashMap<>();
				temp.put("transaction_status", Constant.TRANSACTION_STATUS_CONFIRMED);
				temp.put("transaction_no", transaction_no);
				// 进行信息的补全
				tradeService.transactionConfirmed(temp, result);
			}
		} else if (payment_way_key.equals(ShellConstant.PAYMENT_WAY_07)) {
			String order_type_key = (String) paramsMap.get("order_type_key");// 订单类型
			logger.info("测试充值，贝壳支付参数："+order_type_key);
			if (order_type_key.equals(ShellConstant.ORDER_TYPE_06)) {
				Map<String, Object> map = new HashMap<>();
				map.put("transaction_status", Constant.TRANSACTION_STATUS_CONFIRMED);
				map.put("transaction_no", transaction_no);
				// 进行信息的补全
				tradeService.transactionConfirmed(map, result);
				
			}
		}	else if (payment_way_key.equals(ShellConstant.PAYMENT_WAY_10)) {
				//H5支付
				Map<String, Object> wechatInfoMap = new HashMap<String, Object>();
				Map<String, Object> reqMap = new HashMap<String, Object>();
				reqMap.put("out_trade_no", transaction_no);
				reqMap.put("amount", paramsMap.get("amount") + "");
				reqMap.put("body", paramsMap.get("detail") + "");
				String ip =  StringUtil.formatStr(paramsMap.get("ip"));
				logger.info("ShellOrderServiceImpl,ip:"+ip);
				if(StringUtils.isNotBlank(ip)) {
					reqMap.put("spbill_create_ip",ip);
				}else {
					reqMap.put("spbill_create_ip","113.118.234.255");
				}
				// 微信
				wechatInfoMap = wechatPayService.consumerWechatH5Pay(reqMap);
				wechatInfoMap.put("transaction_no", transaction_no);
				result.setResultData(wechatInfoMap);
		}
	}

	private void transactionsCreateForRecharge(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 此处可以用map,可以用javaBean
			Date date = new Date();
			FDTransactions fDTransactions = new FDTransactions();
			BeanConvertUtil.mapToBean(fDTransactions, paramsMap);
			String transactionNoUnion=TradeIDUtil.getTradeNo();
			paramsMap.put("transaction_no_union", transactionNoUnion);
			fDTransactions.setTransaction_no(transactionNoUnion);
			fDTransactions.setTransaction_date(date);
			fDTransactions.setCreated_date(date);
			financeDao.insertFDTransactions(fDTransactions);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("transaction_no", fDTransactions.getTransaction_no());
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
	public void shellOrderRefund(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {

		String transaction_no = "";
		try {
			// out_trade_no, 必传参数
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "out_member_id", "in_member_id", "amount", "detail" });
			if (null == Constant.DATA_SOURCE_MAP.get((String) paramsMap.get("data_source"))) {
				throw new BusinessException(RspCode.DATA_SOURCE_ERROR, RspCode.MSG.get(RspCode.DATA_SOURCE_ERROR));
			}
			
			String  out_trade_body  = (String) paramsMap.get("out_trade_body");
			JSONObject  jasonObject = JSONObject.fromObject(out_trade_body);
			Map map = (Map)jasonObject;
			String coupons_key = StringUtil.formatStr((String) map.get("coupons_key")) ;
			paramsMap.put("buyer_id", paramsMap.get("out_member_id"));
			paramsMap.put("seller_id", paramsMap.get("in_member_id"));
			// 支付方式转换
			String payment_way_key = paramsMap.get("payment_way_key") + "";
			paramsMap.put("payment_way_key", payment_way_key);
			if ("ZFFS_07".equals(payment_way_key)) {
				paramsMap.put("currency_code", ShellConstant.CURRENCY_BK);
			} else if ("ZFFS_09".equals(payment_way_key)) {
				paramsMap.put("currency_code", ShellConstant.CURRENCY_JF);
			} else {
				paramsMap.put("currency_code", ShellConstant.CURRENCY_CNY);
			}
			paramsMap.put("trade_type_key", ShellConstant.TRADE_TYPE_02);
			String remark = Constant.DATA_SOURCE_MAP.get(paramsMap.get("data_source")) + "|"
					+ ShellConstant.TRADE_TYPE_MAP.get(paramsMap.get("trade_type_key")) + "|"
					+ ShellConstant.PAYMENT_WAY_MAP.get(paramsMap.get("payment_way_key")) + "|" + paramsMap.get("amount");
			paramsMap.put("remark", remark);
			transactionsCreateForRecharge(paramsMap, result);
			Map<String, Object> resultMap = (Map<String, Object>) result.getResultData();
			transaction_no = resultMap.get("transaction_no") + "";
			Map<String, Object> tempMap = new HashMap<String, Object>();
			// 检查买家是否存在
			tempMap.clear();
			tempMap.put("member_id", paramsMap.get("buyer_id"));
			FDMemberAsset buyer = financeDao.selectFDMemberAsset(tempMap);
			if (buyer == null) {
				throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
			}
			tempMap.clear();
			tempMap.put("transaction_no", transaction_no);
			tempMap.put("transaction_status", Constant.TRANSACTION_STATUS_CONFIRMED);
			tempMap.put("amount", paramsMap.get("amount"));
			if(StringUtils.isNotBlank(coupons_key)) {
				tempMap.put("coupons_key", coupons_key);
			}
			result.setResultData(new ResultData());
			tradeService.transactionConfirmed(tempMap, result);
		} catch (BusinessException e) {
			tradeService.fDTransactionErrorLogCreate(transaction_no, "订单退款交易失败," + e.getMsg());
			throw e;
		} catch (SystemException e) {
			tradeService.fDTransactionErrorLogCreate(transaction_no, "订单退款交易异常," + e.getMsg());
			throw e;
		} catch (Exception e) {
			tradeService.fDTransactionErrorLogCreate(transaction_no, "订单退款交易异常," + e.getMessage());
			throw e;
		}
	}

	@Override
	public void beikeMallOrderPay(Map<String, Object> paramsMap, ResultData  result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_type_key", "payment_way_key",
				"member_id", "member_type_key", "goods_code", "order_no","currency_code" });
		String order_no = StringUtil.formatStr(paramsMap.get("order_no"));
		String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("data_source", paramsMap.get("data_source"));// 数据来源
		tempMap.put("order_type_key", paramsMap.get("order_type_key"));
		tempMap.put("payment_way_key", payment_way_key);// 支付方式
		tempMap.put("out_trade_no", order_no);
		Map<String, Object> resultMap = beikeMallOrderDetailFind(order_no);
		String qty = "";
		String sale_fee = "";
		String delivery_fee = "";
		String beike_credit = "";
		String vip_fee = "";
		if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
			qty = StringUtil.formatStr(dataMap.get("qty"));
			sale_fee = StringUtil.formatStr(dataMap.get("sale_fee"));
			delivery_fee = StringUtil.formatStr(dataMap.get("delivery_fee"));
			beike_credit = StringUtil.formatStr(dataMap.get("beike_credit"));
			vip_fee = StringUtil.formatStr(dataMap.get("vip_fee"));
		}
		String amount ="";
		if ("0".equals(beike_credit)) {
			amount = MoneyUtil.moneyMul(sale_fee, qty);
			amount = MoneyUtil.moneyAdd(amount, delivery_fee);
		} else {
			if ("ZFFS_07".equals(payment_way_key)) {
				amount = (Integer.valueOf(beike_credit) * Integer.valueOf(qty)) + "";
			} else if ("ZFFS_01".equals(payment_way_key) || "ZFFS_02".equals(payment_way_key)) {
				amount = MoneyUtil.moneyMul(vip_fee, qty);
				amount = MoneyUtil.moneyAdd(amount, delivery_fee);
			}
			tempMap.put("beike_credit", beike_credit);// 支付贝壳金额
		}
		logger.info("调用 beikeMallOrderPay支付,商品数量 qty=" + qty + "  计算出要付总金额：amount=" +amount+"   贝壳金额为  beike_credit=" +beike_credit);
		tempMap.put("amount", amount);// 支付金额
		tempMap.put("out_member_id", paramsMap.get("member_id"));// 会员ID
		if ("ZFFS_01".equals(payment_way_key)) {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_ZFB);
		} else if ("ZFFS_02".equals(payment_way_key)) {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_WX);
		} else {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_MTH);
		}
		tempMap.put("detail", ShellConstant.ORDER_TYPE_MAP.get(paramsMap.get("order_type_key")));
		tempMap.put("currency_code", paramsMap.get("currency_code"));
		Map<String, String> outTradeBodyMap = new HashMap<>();
		outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
		outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
		outTradeBodyMap.put("goods_code", paramsMap.get("goods_code") + "");
		outTradeBodyMap.put("order_id", paramsMap.get("order_id") + "");
		tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
		tradeService.orderPay(tempMap, result);
	}

	/**
	 * 根据order_id 查询 订单的 商品数量 qty
	 */
	private Map<String, Object> beikeMallOrderDetailFind(String order_no)
			throws SystemException, Exception {
		String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
		Map<String, Object> bizParams = new HashMap<>();
		bizParams.put("order_no",  order_no);
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "order.consumer.beikeMallOrderDetailFind");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(order_service_url, reqParams);
		logger.info("传入order_no:"+order_no+"查询订单结果:"+resultStr);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String)resultMap.get("error_code"), (String)resultMap.get("error_msg"));
		}
		return resultMap;
	}
	
	@Override
	public void hongBaoOrderPay(Map<String, Object> paramsMap, ResultData  result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_type_key", "payment_way_key",
				"member_id", "member_type_key", "order_no","currency_code" });
		String order_no = StringUtil.formatStr(paramsMap.get("order_no"));
		String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("data_source", paramsMap.get("data_source"));// 数据来源
		tempMap.put("order_type_key", paramsMap.get("order_type_key"));
		tempMap.put("payment_way_key", payment_way_key);// 支付方式
		tempMap.put("out_trade_no", order_no);// 
		Map<String, Object> resultMap = hongBaoOrderDetailFind(order_no);
		Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
		String qty = StringUtil.formatStr(dataMap.get("qty"));
		String sale_fee = StringUtil.formatStr(dataMap.get("sale_fee"));
		String amout_year = PropertiesConfigUtil.getProperty("member_recharge_amout_year");
		int sale = Integer.parseInt(sale_fee);
		int year = Integer.parseInt(amout_year);
		if (year != sale) {
			throw new BusinessException("金额错误", "礼券金额错误");
		}
		String amount = MoneyUtil.moneyMul(sale_fee, qty);
		logger.info("调用 beikeMallOrderPay支付,商品数量 qty=" + qty + "  计算出要付总金额：amount=" +amount);
		tempMap.put("amount", amount);// 支付金额
		tempMap.put("out_member_id", paramsMap.get("member_id"));// 会员ID
		if ("ZFFS_01".equals(payment_way_key)) {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_ZFB);
		} else if ("ZFFS_02".equals(payment_way_key)) {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_WX);
		} else {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_MTH);
		}
		tempMap.put("detail", ShellConstant.ORDER_TYPE_MAP.get(paramsMap.get("order_type_key")));
		tempMap.put("currency_code", paramsMap.get("currency_code"));
		Map<String, String> outTradeBodyMap = new HashMap<>();
		outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
		outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
		outTradeBodyMap.put("goods_id", paramsMap.get("goods_id") + "");
		outTradeBodyMap.put("order_id", paramsMap.get("order_id") + "");
		tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
		tradeService.orderPay(tempMap, result);
	}
	
	/**
	 * 根据order_id 查询 订单的 商品数量 qty
	 */
	private Map<String, Object> hongBaoOrderDetailFind(String order_no)
			throws SystemException, Exception {
		String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
		Map<String, Object> bizParams = new HashMap<>();
		bizParams.put("order_no",  order_no);
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "order.consumer.hongBaoOrderDetailFind");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(order_service_url, reqParams);
		logger.info("传入order_no:"+order_no+"查询订单结果:"+resultStr);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String)resultMap.get("error_code"), (String)resultMap.get("error_msg"));
		}
		return resultMap;
	}
	
	@Override
	public void beikeStreetOrderPay(Map<String, Object> paramsMap, ResultData  result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_type_key", "payment_way_key",
				"member_id", "member_type_key", "item_store_id", "order_no","currency_code" });
		String order_no = StringUtil.formatStr(paramsMap.get("order_no"));
		String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("data_source", paramsMap.get("data_source"));// 数据来源
		tempMap.put("order_type_key", paramsMap.get("order_type_key"));
		tempMap.put("payment_way_key", payment_way_key);// 支付方式
		tempMap.put("out_trade_no", order_no);
		Map<String, Object> resultMap = beikeStreetOrderDetailFind(order_no);
		String qty = "";
		String sale_fee = "";
		String beike_credit = "";
		String vip_fee = "";
		if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
			qty = StringUtil.formatStr(dataMap.get("qty"));
			sale_fee = StringUtil.formatStr(dataMap.get("sale_fee"));
			beike_credit = StringUtil.formatStr(dataMap.get("beike_credit"));
			vip_fee = StringUtil.formatStr(dataMap.get("vip_fee"));
		}
		String amount ="";
		if ("0".equals(beike_credit)) {
			amount = MoneyUtil.moneyMul(sale_fee, qty);
		} else {
			if ("ZFFS_07".equals(payment_way_key)) {
				amount = (Integer.valueOf(beike_credit) * Integer.valueOf(qty)) + "";
			} else if ("ZFFS_01".equals(payment_way_key) || "ZFFS_02".equals(payment_way_key)) {
				amount = MoneyUtil.moneyMul(vip_fee, qty);
			}
			tempMap.put("beike_credit", beike_credit);// 支付贝壳金额
		}
		logger.info("调用 beikeMallOrderPay支付,商品数量 qty=" + qty + "  计算出要付总金额：amount=" +amount+"   贝壳金额为  beike_credit=" +beike_credit);
		tempMap.put("amount", amount);// 支付金额
		tempMap.put("out_member_id", paramsMap.get("member_id"));// 会员ID
		if ("ZFFS_01".equals(payment_way_key)) {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_ZFB);
		} else if ("ZFFS_02".equals(payment_way_key)) {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_WX);
		} else {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_MTH);
		}
		tempMap.put("detail", ShellConstant.ORDER_TYPE_MAP.get(paramsMap.get("order_type_key")));
		tempMap.put("currency_code", paramsMap.get("currency_code"));
		Map<String, String> outTradeBodyMap = new HashMap<>();
		outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
		outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
		outTradeBodyMap.put("item_store_id", paramsMap.get("item_store_id") + "");
		outTradeBodyMap.put("order_no", paramsMap.get("order_no") + "");
		tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
		tradeService.orderPay(tempMap, result);
	}
	
	/**
	 * 根据order_id 查询 订单的 商品数量 qty
	 */
	private Map<String, Object> beikeStreetOrderDetailFind(String order_no)
			throws SystemException, Exception {
		String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
		Map<String, Object> bizParams = new HashMap<>();
		bizParams.put("order_no",  order_no);
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "order.consumer.beikeStreetOrderDetailFind");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(order_service_url, reqParams);
		logger.info("传入order_no:"+order_no+"查询订单结果:"+resultStr);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		return resultMap;
	}

	@Override
	public void handleBeikeMallOrderPay(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_type_key", "payment_way_key","amount",
				"member_id", "member_type_key", "order_no","currency_code" });
		String order_no = StringUtil.formatStr(paramsMap.get("order_no"));
		String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("data_source", paramsMap.get("data_source"));// 数据来源
		tempMap.put("order_type_key", paramsMap.get("order_type_key"));
		tempMap.put("payment_way_key", payment_way_key);// 支付方式
		tempMap.put("out_trade_no", order_no);
		String amount =(String) paramsMap.get("amount");
		/*if ("ZFFS_01".equals(payment_way_key) || "ZFFS_02".equals(payment_way_key) || "ZFFS_08".equals(payment_way_key)) {
			Map<String, Object>  orderAmountInfo =   this.beikeMallOrderFind(order_no);
			BigDecimal orderAmount =  (BigDecimal) orderAmountInfo.get("amount");
			Integer  itemNum =  (Integer) orderAmountInfo.get("item_num");
			BigDecimal orderMoney =MoneyUtil.moneyMul(orderAmount, new BigDecimal(String.valueOf(itemNum)));
			if(new BigDecimal(amount).compareTo(orderMoney) !=0) {
				throw new BusinessException(RspCode.ORDER_AMOUNT_ERROR, RspCode.MSG.get(RspCode.ORDER_AMOUNT_ERROR));
			}
		}*/
		if ("ZFFS_07".equals(payment_way_key)) {
			Map<String, Object>  orderAmountInfo =   this.beikeMallOrderFind(order_no);
			Integer shell =  (Integer) orderAmountInfo.get("shell");
			Integer  itemNum =  (Integer) orderAmountInfo.get("item_num");
			Integer  shellNum =shell * itemNum; 
			
			logger.info("amount:"+amount+";shellNum:"+shellNum);
			if(!StringUtil.equals(amount, String.valueOf(shellNum))) {
				throw new BusinessException(RspCode.SHELL_AMOUNT_ERROR, RspCode.MSG.get(RspCode.SHELL_AMOUNT_ERROR));
			}
		}
		//如果是组合支付
		String payType =   (String) paramsMap.get("payType");	
		if(StringUtils.equals(payType, "groupPay")) {
			logger.info("调用 组合支付, 计算出要付总金额：amount=" +amount+"=====payType:"+payType+"红包金额："+(String) paramsMap.get("redPacketAmount"));
			String  redPacketAmount =  (String) paramsMap.get("redPacketAmount");
			tempMap.put("payType", payType);
			tempMap.put("redPacketAmount", redPacketAmount);
		}
		
		tempMap.put("amount", String.valueOf(new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP)));// 支付金额
		tempMap.put("out_member_id", paramsMap.get("member_id"));// 会员ID
		if ("ZFFS_01".equals(payment_way_key)) {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_ZFB);
		} else if ("ZFFS_02".equals(payment_way_key)) {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_WX);
		} else {
			tempMap.put("in_member_id", ShellConstant.MEMBER_ID_MTH);
			if("ZFFS_09".equals(payment_way_key)) {
				String  member_coupons_id  =   (String) paramsMap.get("member_coupons_id");
				tempMap.put("member_coupons_id", member_coupons_id);
			}
			if("ZFFS_10".equals(payment_way_key)) {
				tempMap.put("ip", (String) paramsMap.get("ip"));
			}
		}
		if("ZFFS_10".equals(payment_way_key)) {
			tempMap.put("detail", "购买会员赠送");
		}
		if ("ZFFS_09".equals(payment_way_key)) {
			String order_type_key = StringUtil.formatStr(paramsMap.get("order_type_key"));
			if (StringUtil.isNotBlank(order_type_key)) {
				tempMap.put("detail", ShellConstant.ORDER_TYPE_MAP.get(order_type_key));
			} else {
				tempMap.put("detail", ShellConstant.ORDER_TYPE_MAP.get("DDLX_13"));
			}
		}else {
			tempMap.put("detail", ShellConstant.ORDER_TYPE_MAP.get(paramsMap.get("order_type_key")));
		}
		
		tempMap.put("currency_code", paramsMap.get("currency_code"));
		Map<String, String> outTradeBodyMap = new HashMap<>();
		outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
		outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
		outTradeBodyMap.put("order_no", paramsMap.get("order_no") + "");
		tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
		tradeService.orderPay(tempMap, result);
	}
	
	
	/**
	 * 根据order_id 查询订单金额
	 */
	private Map<String, Object> beikeMallOrderFind(String order_no)
			throws SystemException, Exception {
		String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
		Map<String, Object> bizParams = new HashMap<>();
		bizParams.put("order_no",  order_no);
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "order.consumer.findOrderInfoByNo");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(order_service_url, reqParams);
		logger.info("传入order_no:"+order_no+"查询订单结果:"+resultStr);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String)resultMap.get("error_code"), (String)resultMap.get("error_msg"));
		}
		Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
		logger.info("查询订单resultStr:"+resultStr);
		return data;
	}
	
	@Override
	public void telephoneRecharge(Map<String, Object> paramsMap, ResultData  result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] {"data_source", "payment_way_key","member_id", "amount", "shell","mobile"});
		
		String data_source = (String) paramsMap.get("data_source");// 数据来源 消费者终端
		String payment_way_key = (String) paramsMap.get("payment_way_key");// 支付方式
		String amount = StringUtil.formatStr(paramsMap.get("amount")); // 订单类型
		String shell = StringUtil.formatStr(paramsMap.get("shell")); // 订单类型
		String memberId = StringUtil.formatStr(paramsMap.get("member_id")); 
		String mobile = StringUtil.formatStr(paramsMap.get("mobile")); 
		logger.info("话费充值："+data_source+"======="+payment_way_key+";amount:"+amount);
		if (null == Constant.DATA_SOURCE_MAP.get(data_source)) {
			throw new BusinessException(RspCode.DATA_SOURCE_ERROR, RspCode.MSG.get(RspCode.DATA_SOURCE_ERROR));
		}
		BigDecimal tradeAmount =  new BigDecimal(amount);
		if(tradeAmount.compareTo(new BigDecimal("48")) !=0 ||  !StringUtil.equals(shell, "2")) {
			throw new BusinessException("话费充值", "金额错误");
		}
		Map<String,Object> tempMap =  new HashMap<String,Object>();
		//判断用户账号贝壳是否大于等于2
		tempMap.put("member_id", memberId);
		tempMap.put("shell", 2);
		FDMemberAsset member = financeDao.selectFDMemberAssetByMemberIdAndShell(tempMap);
		if(null==member) {
			throw new BusinessException(RspCode.SHELL_AMOUNT_ERROR, "您的贝壳不足，请续费会员");
		}
		//通过memberId，查询得到 mobile 然后进行比较
		
		if ("支付宝支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))) {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_ZFB);
		} else if ("微信支付".equals(ShellConstant.PAYMENT_WAY_MAP.get(payment_way_key))) {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_WX);
		} else {
			paramsMap.put("seller_id", ShellConstant.MEMBER_ID_MTH);
		}
		paramsMap.put("detail", "话费充值");
		paramsMap.put("buyer_id", paramsMap.get("member_id")); 
		paramsMap.put("trade_type_key", ShellConstant.TRADE_TYPE_01);
		paramsMap.put("order_type_key", ShellConstant.ORDER_TYPE_05);
	
		String remark = Constant.DATA_SOURCE_MAP.get(paramsMap.get("data_source")) + "|"
				+ ShellConstant.TRADE_TYPE_MAP.get(paramsMap.get("trade_type_key")) + "|"
				+ ShellConstant.PAYMENT_WAY_MAP.get(paramsMap.get("payment_way_key")) + "|" + paramsMap.get("amount");
		paramsMap.put("remark", remark);
		// 检测充值方是否存在
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("member_id", paramsMap.get("buyer_id"));
		FDMemberAsset buyer = financeDao.selectFDMemberAsset(queryMap);
		if (buyer == null) {
			throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
		}
		queryMap.put("order_type_key", "DDLX_05");
		// 查询单月是否购买过话费
		List<String>  transactionNoList =  financeDao.selectTelephoneOrderMonth(queryMap);
		//先查询出这个月的transaction_no   然后查询出状态得到状态是否相等
		if(null !=  transactionNoList &&  transactionNoList.size() >0) {
			JSONArray jsonArray=new JSONArray(transactionNoList);
			String transactionList  =  jsonArray.toString();
			//查询 orderStatus
			boolean  flag =	this.findTelephoneOrderStatus(transactionList);
			if(flag == true) {
				throw new BusinessException(RspCode.TELEPHONE_CHARGE_ERROR, RspCode.MSG.get(RspCode.TELEPHONE_CHARGE_ERROR));
			}
		}
		//把手机号码写入redis
		String key =  memberId + "telephoneCharge";
		logger.info("话费充值：key"+key);
		boolean   flag =redisUtil.exists(key);
		if(flag == false) {
			redisUtil.setStr(key, mobile,60);
		}else {
			redisUtil.del(key);
			redisUtil.setStr(key, mobile,60);
		}
		addOrder(paramsMap, result, data_source, payment_way_key, remark, buyer);
	}
	
	public  boolean findTelephoneOrderStatus(String transactionNoList) throws SystemException, Exception {
				String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
				Map<String, String> reqParams = new HashMap<>();
				Map<String, Object> bizParams = new HashMap<>();
				reqParams.put("service", "order.consumer.telephoneOrderStatus");
				bizParams.put("transactionNoList", transactionNoList);
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				String resultStr = HttpClientUtil.post(order_service_url, reqParams);
				logger.info("创建充值订单返回结果->" + resultStr);
				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
				}
				Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
				List<String> statusList =  (List<String>) dataMap.get("statusList");
				return	statusList.contains("paid");
	}
	
}
