package com.meitianhui.finance.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
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
import com.meitianhui.finance.service.OrderPaymentService;
import com.meitianhui.finance.service.WechatPayService;
@Service
public class OrderPaymentServiceImpl implements OrderPaymentService {

	private static final Logger logger = Logger.getLogger(TradeServiceImpl.class);

	@Autowired
	private FinanceDao financeDao;
	
	@Autowired
	private AlipayPayService alipayPayService;
	
	@Autowired
	private WechatPayService wechatPayService;
	
	@Override
	public void balanceRecharge(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// out_trade_no, 必传参数
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "payment_way_key", "amount", "member_id", "detail" });
			if (null == Constant.DATA_SOURCE_MAP.get((String) paramsMap.get("data_source"))) {
				throw new BusinessException(RspCode.DATA_SOURCE_ERROR, RspCode.MSG.get(RspCode.DATA_SOURCE_ERROR));
			}
			if (!("订单充值").equals((String) paramsMap.get("order_type_key"))) {
				throw new BusinessException("订单类型错误", "订单类型不是充值订单");
			}
			
			paramsMap.put("buyer_id", Constant.MEMBER_ID_MTH);
			paramsMap.put("seller_id", paramsMap.get("member_id"));
			// 余额充值
			paramsMap.put("currency_code", Constant.CURRENCY_CNY);
			//paramsMap.put("trade_type_key", ShellConstant.TRADE_TYPE_ORDERPAY);
			paramsMap.put("order_type_key", "订单充值");

			String remark = Constant.DATA_SOURCE_MAP.get(paramsMap.get("data_source")) + "|"
					+ /*Constant.TRADE_TYPE_MAP.get(paramsMap.get("trade_type_key"))*/"" + "|"
					+ Constant.PAYMENT_WAY_MAP.get(paramsMap.get("payment_way_key")) + "|" 
					
					+ paramsMap.get("amount");
			paramsMap.put("remark", remark);
			if (MoneyUtil.moneyComp("0", (String) paramsMap.get("amount"))) {
				throw new BusinessException(RspCode.TRADE_AMOUNT_ERROR, RspCode.MSG.get(RspCode.TRADE_AMOUNT_ERROR));
			}
			// 检测充值方是否存在
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("member_id", paramsMap.get("seller_id"));
			FDMemberAsset buyer = financeDao.selectFDMemberAsset(queryMap);
			if (buyer == null) {
				throw new BusinessException(RspCode.MEMBER_NO_EXIST, RspCode.MSG.get(RspCode.MEMBER_NO_EXIST));
			}
			Map<String, Object> resultMap = (Map<String, Object>) result.getResultData();
			String transaction_no = TradeIDUtil.getTradeNo();
			String payment_way_key = paramsMap.get("payment_way_key") + "";
			String data_source = paramsMap.get("data_source") + "";
			
			//TODO 进行充值订单创建     2018/7/12
			String member_service_url = PropertiesConfigUtil.getProperty("order_service_url");
			Map<String, String> reqParams = new HashMap<>();
			Map<String, Object> bizParams = new HashMap<>();
			reqParams.put("service", "order.rcOrderCreate");
			bizParams.put("transaction_no", transaction_no);
			bizParams.put("amount", paramsMap.get("amount"));
			bizParams.put("payment_way_key", payment_way_key);
			bizParams.put("member_type_key", buyer.getMember_type_key());
			bizParams.put("member_id", buyer.getMember_id());
			bizParams.put("remark", remark);
			bizParams.put("data_source", data_source);
			bizParams.put("trade_type_key", "订单充值");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			logger.info("创建充值订单返回结果->" + resultStr);
			
			// 入库
			transactionsCreateForRecharge(paramsMap, result);
			
			if (payment_way_key.equals(Constant.PAYMENT_WAY_01)) {
				Map<String, String> alipayMap = new HashMap<>();
				String total_fee = paramsMap.get("amount") + "";
				String subject = paramsMap.get("detail") + "";
				Map<String, Object> reqMap = new HashMap<>();
				reqMap.put("out_trade_no", transaction_no);
				reqMap.put("amount", total_fee);
				reqMap.put("subject", subject);
				if (data_source.equals(Constant.DATA_SOURCE_SJLY_01)) {
					alipayMap = alipayPayService.consumerAlipayInfoGet(reqMap);
				} else {
					throw new BusinessException(RspCode.PAYMENT_WAY_ERROR, "支付宝支付暂未开通");
				}
				alipayMap.put("transaction_no", transaction_no);
				result.setResultData(alipayMap);
			} else if (payment_way_key.equals(Constant.PAYMENT_WAY_02)) {
				Map<String, Object> wechatInfoMap = new HashMap<>();
				Map<String, Object> reqMap = new HashMap<>();
				reqMap.put("out_trade_no", transaction_no);
				reqMap.put("amount", paramsMap.get("amount") + "");
				reqMap.put("body", paramsMap.get("detail") + "");
				reqMap.put("spbill_create_ip", "127.0.0.1");
				if (data_source.equals(Constant.DATA_SOURCE_SJLY_01)) {
					wechatInfoMap = wechatPayService.consumerWechatInfoGet(reqMap);
				} else {
					throw new BusinessException(RspCode.PAYMENT_WAY_ERROR, "微信支付暂未开通");
				}
				wechatInfoMap.put("transaction_no", transaction_no);
				result.setResultData(wechatInfoMap);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
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
			fDTransactions.setPayment_way_key("积分支付");
			BigDecimal amount = fDTransactions.getAmount().setScale(0, BigDecimal.ROUND_DOWN);//去除小数
			fDTransactions.setAmount(amount);
			financeDao.insertFDTransactions(fDTransactions);
			fDTransactions.setPayment_way_key("贝壳支付");
			fDTransactions.setAmount(amount.multiply(new BigDecimal("2")));
			financeDao.insertFDTransactions(fDTransactions);
			Map<String, Object> resultMap = new HashMap<>();
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
	public void pointOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap,
				new String[] { "data_source", "buyer_id", "seller_id", "amount","detail","out_trade_body"});
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("data_source", paramsMap.get("data_source"));//数据来源·
		tempMap.put("order_type_key", paramsMap.get("order_type_key"));//订单类型
		tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));//支付方式
	    //根据order_id 查询 订单的 商品数量 qty
		String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
		Map<String, Object> bizParams = new HashMap<String, Object>();
		bizParams.put("order_id",  paramsMap.get("order_id"));
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("service", "order.own.fgOrderDetailFind");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(order_service_url, reqParams);
		logger.info("传入order_id:"+paramsMap.get("order_id")+"查询订单结果:"+resultStr);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		String qty = "";
		if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
			qty = (String) dataMap.get("qty");
		} 
		if(qty == null || qty == "" || qty == "null" ){
			qty = "1";
		}
		String amount = MoneyUtil.moneyMul(StringUtil.formatStr(paramsMap.get("amount")), qty);
		logger.info("调用 mainOrderPay支付,qty ->" + qty+"  传入金额："+StringUtil.formatStr(paramsMap.get("amount"))+" 计算出要付总金额："+amount);
		tempMap.put("amount", amount);//支付金额
		tempMap.put("out_member_id", paramsMap.get("member_id"));//会员ID
		tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
		if(paramsMap.get("order_type_key").toString().equals("DDLX_07")){
			tempMap.put("detail", "自营商品支付");
		}else if (paramsMap.get("order_type_key").toString().equals("DDLX_21")){
			tempMap.put("detail", "会过商品支付");
		}	
		Map<String, String> outTradeBodyMap = new HashMap<String, String>();
		outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
		outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
		outTradeBodyMap.put("goods_code", paramsMap.get("goods_code") + "");
		outTradeBodyMap.put("order_id", paramsMap.get("order_id") + "");
		tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
		//tradeService.orderPay(tempMap, result);
	}

}
