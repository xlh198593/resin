package com.meitianhui.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//import javax.transaction.Transactional;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisLock;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.order.constant.BeikeConstant;
import com.meitianhui.order.constant.Constant;
import com.meitianhui.order.constant.HttpUtils;
import com.meitianhui.order.constant.OrderIDUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.controller.OrderController;
import com.meitianhui.order.dao.BeikeMallOrderCouponsDao;
import com.meitianhui.order.dao.BeikeMallOrderDao;
import com.meitianhui.order.dao.BeikeStreetOrderDao;
import com.meitianhui.order.dao.FgBlacklistDao;
import com.meitianhui.order.dao.FgOrderDao;
import com.meitianhui.order.dao.OrderDao;
import com.meitianhui.order.dao.OrderTradeLogDao;
import com.meitianhui.order.dao.PcOrderDao;
import com.meitianhui.order.entity.BeikeMallOrder;
import com.meitianhui.order.entity.BeikeMallOrderCoupons;
import com.meitianhui.order.entity.BeikeMallOrderItem;
import com.meitianhui.order.entity.BeikeMallOrderTardeLog;
import com.meitianhui.order.entity.FgBlacklist;
import com.meitianhui.order.entity.FgOrder;
import com.meitianhui.order.entity.FgOrderCommission;
import com.meitianhui.order.entity.FgOrderExtra;
import com.meitianhui.order.entity.FgOrderItem;
import com.meitianhui.order.entity.OdSettlement;
import com.meitianhui.order.entity.OdSettlementDetail;
import com.meitianhui.order.entity.RcOrder;
import com.meitianhui.order.entity.RcOrderTardeLog;
import com.meitianhui.order.service.FgOrderService;
import com.meitianhui.order.service.OrderService;
import com.meitianhui.order.service.PsOrderService;

/**
 * 领了么
 * 
 * @author Tiny
 *
 */
@SuppressWarnings("unchecked")
@Service
public class FgOrderServiceImpl implements FgOrderService {

	private static final Logger logger = Logger.getLogger(FgOrderServiceImpl.class);

	public static String GOODS_SERVICE_URL = PropertiesConfigUtil.getProperty("goods_service_url");
	
	public static String FINANCE_SERVICE_URL = PropertiesConfigUtil.getProperty("finance_service_url");
	
	public static String MEMBER_SERVICE_URL = PropertiesConfigUtil.getProperty("member_service_url");
	
	public static String SHELL_PROPERTY_SERVICE_URL = PropertiesConfigUtil.getProperty("shell_property_service_url");


	@Autowired
	public RedisUtil redisUtil;
	@Autowired
	private DocUtil docUtil;

	@Autowired
	private OrderDao orderDao;
	@Autowired
	private FgOrderDao fgOrderDao;
	@Autowired
	private PcOrderDao pcOrderDao;
	@Autowired
	private FgBlacklistDao fgBlacklistDao;
	@Autowired
	private OrderService orderService;
	@Autowired
	private BeikeMallOrderDao beikeMallOrderDao;
	@Autowired
	private PsOrderService psOrderService;
	@Autowired
	private BeikeStreetOrderDao beikeStreetOrderDao;
	@Autowired
	private OrderTradeLogDao orderTradeLogDao;
	@Autowired
	private BeikeMallOrderCouponsDao beikeMallOrderCouponsDao;
	
	
	@Override
	@Transactional
	public void handlefgOrderRefund(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id","member_id" });
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		String order_id = StringUtil.formatStr(paramsMap.get("order_id"));
		//运营传参数操作人
		String operator = StringUtil.formatStr(paramsMap.get("operator"));

		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", order_id);
		List<Map<String, Object>> tempOrderList = fgOrderDao.selectFgOrder(tempMap);
		Map<String, Object> order = new HashMap<String, Object>();
		String order_no = null;
		String refund_order_id = null;
		if (tempOrderList.size() > 0) {
			order = tempOrderList.get(0);
			order_no=order.get("order_no").toString();
			refund_order_id=order.get("order_id").toString();
			String currentStatus =  StringUtil.formatStr(order.get("status"));
			if(!currentStatus.equals("confirmed") && !currentStatus.equals("cancelled")){
				throw new BusinessException(RspCode.ORDER_ERROE, "订单当前状态为："+order.get("status").toString()+",不能进行退款操作");
			}
			//已取消订单验证是否撤销返款过
			if(currentStatus.equals("cancelled")){
				List<Map<String, Object>> orderLogList = fgOrderDao.selectOperateLog(order_no);
				if(null != orderLogList && orderLogList.size()>0){
					Map<String, Object> orderLog = orderLogList.get(0);
					String category = StringUtil.formatStr(orderLog.get("category"));
					if(!category.equals("撤销返款")){
						throw new BusinessException(RspCode.ORDER_ERROE, "该订单没有查到撤销返款记录不能退款操作，请核查！");
					}
				}else{
					throw new BusinessException(RspCode.ORDER_ERROE, "该订单没有查到撤销返款记录不能退款操作，请核查！");
				}
			}
		}
		
		Map<String, Object> tempParamsMap = new HashMap<String, Object>();
		tempParamsMap.put("status", "refund");
		tempParamsMap.put("order_no",order_no);
		tempParamsMap.put("order_id",refund_order_id);
		int updateFlag = fgOrderDao.updateFgOrder(tempParamsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		Map<String, Object> out_trade_body = new HashMap<String, Object>();
		String member_type_key = order.get("member_type_key").toString();
		out_trade_body.put("order_no", order.get("order_no").toString());
		out_trade_body.put("member_id", order.get("member_id").toString());
		out_trade_body.put("member_type_key", member_type_key);
		String qty = StringUtil.formatStr(order.get("qty"));
		if(qty == null || qty == "" || qty == "null" ){
			qty = "1";
		}
		String refundAmount = MoneyUtil.moneyMul(order.get("sale_fee").toString(),qty);
		String refundMemberId = order.get("member_id").toString();
		String trade_order_no = order.get("order_no").toString();
		orderService.orderRefund("SJLY_03", 
				refundMemberId, 
				Constant.MEMBER_ID_MTH, 
				"ZFFS_05", 
				new BigDecimal(refundAmount),
				"DDLX_07", 
				trade_order_no, 
				"会过自营订单退款", 
				out_trade_body);
		//记录日志到表fg_order_log
		 Map<String, Object> params = new HashMap<String, Object>();
         params.put("log_id", UUID.randomUUID().toString());
         params.put("category", "会过自营订单退款");
         params.put("order_no", trade_order_no);
         params.put("tracked_date", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
         params.put("event", operator + "|退款金额:" +refundAmount +"|退款到会员类型:"+member_type_key+"|会员ID:"+refundMemberId);
	     fgOrderDao.saveOperateLog(params);
	}	
	
	
	/**
	 * 原路返还退款
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	@Transactional
	public void handlefgOrderRefundBack(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		logger.info("退款请求进来了》》》》》》》》》》》》》》");
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id","member_id" });
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		String order_id = StringUtil.formatStr(paramsMap.get("order_id"));
		//运营传参数操作人
		String operator = StringUtil.formatStr(paramsMap.get("operator"));

		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", order_id);
		List<Map<String, Object>> tempOrderList = fgOrderDao.selectFgOrder(tempMap);
		Map<String, Object> order = new HashMap<String, Object>();
		String order_no = null;
		String refund_order_id = null;
		logger.info("order_id:"+order_id+"查询的结果数量："+tempOrderList.size());
		if (tempOrderList.size() > 0) {
			order = tempOrderList.get(0);
			order_no=order.get("order_no").toString();
			refund_order_id=order.get("order_id").toString();
			String currentStatus =  StringUtil.formatStr(order.get("status"));
			if(!currentStatus.equals("confirmed") && !currentStatus.equals("cancelled")){
				throw new BusinessException(RspCode.ORDER_ERROE, "订单当前状态为："+order.get("status").toString()+",不能进行退款操作");
			}
			//已取消订单验证是否撤销返款过
			if(currentStatus.equals("cancelled")){
				List<Map<String, Object>> orderLogList = fgOrderDao.selectOperateLog(order_no);
				if(null != orderLogList && orderLogList.size()>0){
					Map<String, Object> orderLog = orderLogList.get(0);
					String category = StringUtil.formatStr(orderLog.get("category"));
					if(!category.equals("撤销返款")){
						throw new BusinessException(RspCode.ORDER_ERROE, "该订单没有查到撤销返款记录不能退款操作，请核查！");
					}
				}else{
					throw new BusinessException(RspCode.ORDER_ERROE, "该订单没有查到撤销返款记录不能退款操作，请核查！");
				}
			}
		}
		logger.info("order_id:"+order_id+"到了这里");
		Map<String, Object> tempParamsMap = new HashMap<String, Object>();
		tempParamsMap.put("status", "refund");
		tempParamsMap.put("order_no",order_no);
		tempParamsMap.put("order_id",refund_order_id);
		JSONObject json=new JSONObject();
		json.put("remark", "原路返还退款");
		json.put("operator", operator);
		tempParamsMap.put("biz_remark",json.toString());
		
		int updateFlag = fgOrderDao.updateFgOrder(tempParamsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		
		Map<String, Object> out_trade_body = new HashMap<String, Object>();
		String member_type_key = order.get("member_type_key").toString();
		out_trade_body.put("order_no", order.get("order_no").toString());
		out_trade_body.put("member_id", order.get("member_id").toString());
		out_trade_body.put("member_type_key", member_type_key);
		String qty = StringUtil.formatStr(order.get("qty"));
		if(qty == null || qty == "" || qty == "null" ){
			qty = "1";
		}
		String refundAmount = MoneyUtil.moneyMul(order.get("sale_fee").toString(),qty);
		String refundMemberId = order.get("member_id").toString();
		String trade_order_no = order.get("order_no").toString();
		logger.info("order_id:"+order_id+"到了这里123");
		String payment_way_type="";
		String transaction_no="";
	    if (tempOrderList.size() > 0) {
				order = tempOrderList.get(0);
				String payment_way_key=order.get("payment_way_key").toString();
				transaction_no=order.get("transaction_no").toString();
				if ("ZFFS_01".equals(payment_way_key) || "ZFFS_10".equals(payment_way_key) || "ZFFS_15".equals(payment_way_key) || "ZFFS_16".equals(payment_way_key) ) {  //支付宝退款
					payment_way_type="yuanlurefund_alipay";
				}else if("ZFFS_02".equals(payment_way_key) || "ZFFS_17".equals(payment_way_key) || "ZFFS_26".equals(payment_way_key)){//微信退款
					payment_way_type="yuanlurefund_weixin";
				}else{
					throw new BusinessException(RspCode.ORDER_PROCESSING, "非微信支付宝支付，不能退款");
				}
	     }
	    logger.info("order_id:"+order_id+"开始进行退款，退款方式:"+payment_way_type);
	    orderService.orderRefundBack("SJLY_03", 
				refundMemberId, 
				Constant.MEMBER_ID_MTH, 
				payment_way_type, 
				new BigDecimal(refundAmount),
				"DDLX_07", 
				trade_order_no, 
				"会过自营订单退款", 
				out_trade_body,
				transaction_no);
		
		 //记录日志到表fg_order_log
		 Map<String, Object> params = new HashMap<String, Object>();
         params.put("log_id", UUID.randomUUID().toString());
         params.put("category", "会过自营订单原路返还退款");
         params.put("order_no", trade_order_no);
         params.put("tracked_date", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
         params.put("event", operator + "|退款金额:" +refundAmount +"|退款到会员类型:"+member_type_key+"|会员ID:"+refundMemberId);
	     fgOrderDao.saveOperateLog(params);
	     logger.info("退款请求结束》》》》》》》》》》》》》》");
	}
	
	
	@Override
	public void handleOwnOrderPayNotify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id","transaction_no"});
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", paramsMap.get("order_id"));
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_ERROE, "订单不存在");
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单状态异常");
		}
		tempMap.clear();
		tempMap.put("order_id", order.get("order_id"));
		tempMap.put("status", Constant.ORDER_CONFIRMED);
		tempMap.put("transaction_no", paramsMap.get("transaction_no"));
		tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
		tempMap.put("external_order_no", order.get("order_no"));
		tempMap.put("external_buyer_name", order.get("member_mobile"));//自营的淘宝关联名字用会员手机号码来做标识查询
		
		int updateFlag = fgOrderDao.updateFgOrder(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_no", order.get("order_no"));
		result.setResultData(paramMap);
	}
	
	@Override
	public void handleHuiguoOrderPayNotify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id","transaction_no"});
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", paramsMap.get("order_id"));
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_ERROE, "订单不存在");
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单状态异常");
		}
		tempMap.clear();
		tempMap.put("order_id", order.get("order_id"));
		tempMap.put("status", Constant.ORDER_CONFIRMED);
		tempMap.put("transaction_no", paramsMap.get("transaction_no"));
		tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
		tempMap.put("external_order_no", order.get("order_no"));
		tempMap.put("external_buyer_name", order.get("member_mobile"));//自营的淘宝关联名字用会员手机号码来做标识查询
		
		int updateFlag = fgOrderDao.updateFgOrder(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_no", order.get("order_no"));
		result.setResultData(paramMap);
	}
	
	@Override
	public void handleFgOrderCreateNotity(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_id", "payment_way_key",
				"delivery_address", "contact_person", "contact_tel" ,"transaction_no" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", paramsMap.get("order_id"));
		//tempMap.put("order_type", Constant.ORDER_TYPE_MEITIANHUI);
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_ERROE, "订单不存在");
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单状态异常");
		}
		tempMap.clear();
		tempMap.put("order_id", order.get("order_id"));
		tempMap.put("status", Constant.ORDER_CONFIRMED);
		tempMap.put("external_order_no", order.get("order_no"));
		tempMap.put("transaction_no", paramsMap.get("transaction_no"));
		tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
		tempMap.put("remark", StringUtil.formatStr(paramsMap.get("remark")));
		tempMap.put("delivery_address", StringUtil.formatStr(paramsMap.get("delivery_address")));
		tempMap.put("contact_person", StringUtil.formatStr(paramsMap.get("contact_person")));
		tempMap.put("contact_tel", StringUtil.formatStr(paramsMap.get("contact_tel")));
		int updateFlag = fgOrderDao.updateFgOrder(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_no", order.get("order_no"));
		result.setResultData(paramMap);
	}

	public int insertFgOrderCommission(Map<String, Object> order) throws BusinessException, SystemException, Exception {
		logger.info("开始插入佣金日志");
		FgOrderCommission fgOrderCommission = new FgOrderCommission();
		Date date = new Date();
		fgOrderCommission.setOrder_id(StringUtil.formatStr(order.get("order_no")));
		fgOrderCommission.setMember_id(StringUtil.formatStr(order.get("member_id")));
		// 查找出stores_id
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("consumer_id", StringUtil.formatStr(order.get("member_id")));
		paramMap.put("order_id", StringUtil.formatStr(order.get("order_id")));
		Map<String, Object> extra = fgOrderDao.selectFgOrderExtra(paramMap);

		fgOrderCommission.setStores_id(StringUtil.formatStr(extra.get("stores_id")));
		fgOrderCommission.setMember_phone(StringUtil.formatStr(order.get("member_mobile")));
		fgOrderCommission.setAmount((BigDecimal) order.get("order_commision"));
		fgOrderCommission.setCommision_status("normal");
		fgOrderCommission.setCreated_time(date);
		fgOrderCommission.setModified_date(date);
		fgOrderCommission.setRemark("交易成功,正常返佣");
		int falg = fgOrderDao.insertFgOrderCommission(fgOrderCommission);
		logger.info("插入佣金日志"+fgOrderCommission.toString());
		return falg;
	}

	@Override
	public void freeGetValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		String already_got = "false";
		String in_blacklist = "false";
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_code", "mobile" });
		String goods_code = paramsMap.get("goods_code") + "";
		String mobile = paramsMap.get("mobile") + "";
		// 查询会员是否已经领过商品了(一个月只能领取一次)
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("goods_code", goods_code);
		tempMap.put("member_mobile", mobile);
		tempMap.put("status_neq", Constant.ORDER_CANCELLED);
		String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
		String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
		tempMap.put("order_date_start", order_date_start);
		tempMap.put("order_date_end", new_date);
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
		if (orderList.size() > 0) {
			already_got = "true";
		}

		// 检测手机号是不是已经列入黑名单了
		tempMap.put("account", paramsMap.get("mobile"));
		tempMap.put("account_type", Constant.ACCOUNT_TYPE_MEITIANHUI);
		List<Map<String, Object>> orderItemList = fgBlacklistDao.selectFgBlacklist(tempMap);
		if (orderItemList.size() > 0) {
			in_blacklist = "true";
		}
		resultDataMap.put("already_got", already_got);
		resultDataMap.put("in_blacklist", in_blacklist);
		result.setResultData(resultDataMap);
	}

	
	@Override
	public void consumerOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile",
							"order_type", "data_source", "delivery_address", 
							"contact_person", "contact_tel" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String orderType = StringUtil.formatStr(paramsMap.get("order_type"));
			String dataSource = StringUtil.formatStr(paramsMap.get("data_source"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String contact_person = StringUtil.formatStr(paramsMap.get("contact_person"));
			String contact_tel = StringUtil.formatStr(paramsMap.get("contact_tel"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_code", goods_code);
			tempMap.put("member_mobile", mobile);
			tempMap.put("status_neq", Constant.ORDER_CANCELLED);
			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
			tempMap.put("order_date_start", order_date_start);
			tempMap.put("order_date_end", new_date);
			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
			if (orderList.size() > 0) {
				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
			}
			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type="";
			if(orderType.equals("taobao")){
				order_type = Constant.ORDER_TYPE_TAOBAO;
			}else if(orderType.equals("meitianhui")){
				order_type = Constant.ORDER_TYPE_MEITIANHUI;
			}else if(orderType.equals("huiguo")){
				order_type = Constant.ORDER_TYPE_HUIGUO;
			}
			
			

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();

			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");
			
			freeGetGoodsTaobaoSalesEdit(goods_id);

			try {
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				order.setPayment_way_key("ZFFS_01");
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(contact_tel);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source(dataSource);
				order.setDelivery_address(delivery_address);
				order.setRemark(remark);
				order.setQty(qty);
				if (goodsDataMap.get("commission") == null) {
					order.setOrder_commision(new BigDecimal("0"));
				}else{
					order.setOrder_commision((BigDecimal) goodsDataMap.get("commission"));
				}
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);

				// 消费者扣除的金币(支付金额-优惠金额)
				Integer consumer_pay_gold_amount = new BigDecimal(sub_amount).intValue();
				if (consumer_pay_gold_amount > 0) {
					// 交易的订单类型
					String order_type_key = "DDLX_07";
					if (data_source.equals(Constant.ORDER_TYPE_TAOBAO)) {
						order_type_key = "DDLX_10";
					}
					Map<String, Object> out_trade_body = new HashMap<String, Object>();
					out_trade_body.put("member_id", consumer_id);
					out_trade_body.put("goods_id", goodsDataMap.get("goods_id"));
					out_trade_body.put("title", goodsDataMap.get("title"));
					out_trade_body.put("consumer_pay_gold_amount", consumer_pay_gold_amount);
					orderService.balancePay("SJLY_01", consumer_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
							new BigDecimal(consumer_pay_gold_amount), order_type_key, order_no, "淘淘领", out_trade_body);
				}

				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

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
	public void huiguoOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile",
							"order_type", "data_source", "delivery_address", 
							"contact_person", "contact_tel" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String orderType = StringUtil.formatStr(paramsMap.get("order_type"));
			String dataSource = StringUtil.formatStr(paramsMap.get("data_source"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String contact_person = StringUtil.formatStr(paramsMap.get("contact_person"));
			String contact_tel = StringUtil.formatStr(paramsMap.get("contact_tel"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<String, Object>();
//			tempMap.put("goods_code", goods_code);
//			tempMap.put("member_mobile", mobile);
//			tempMap.put("status_neq", Constant.ORDER_CANCELLED);
//			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
//			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
//			tempMap.put("order_date_start", order_date_start);
//			tempMap.put("order_date_end", new_date);
//			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
//			if (orderList.size() > 0) {
//				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
//						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
//			}
			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type="";
			if(orderType.equals("taobao")){
				order_type = Constant.ORDER_TYPE_TAOBAO;
			}else if(orderType.equals("meitianhui")){
				order_type = Constant.ORDER_TYPE_MEITIANHUI;
			}else if(orderType.equals("huiguo")){
				order_type = Constant.ORDER_TYPE_HUIGUO;
			}
			
			

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();

			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");

			freeGetGoodsTaobaoSalesEdit(goods_id);

			try {
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				order.setPayment_way_key("ZFFS_01");
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(contact_tel);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source(dataSource);
				order.setDelivery_address(delivery_address);
				order.setRemark(remark);
				order.setQty(qty);
				if (goodsDataMap.get("commission") == null) {
					order.setOrder_commision(new BigDecimal("0"));
				}else{
					order.setOrder_commision((BigDecimal) goodsDataMap.get("commission"));
				}
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);

//会过商品订单  不扣除金币了
				// 消费者扣除的金币(支付金额-优惠金额)
//				Integer consumer_pay_gold_amount = new BigDecimal(sub_amount).intValue();
//				if (consumer_pay_gold_amount > 0) {
//					// 交易的订单类型
//					String order_type_key = "DDLX_07";
//					if (data_source.equals(Constant.ORDER_TYPE_TAOBAO)) {
//						order_type_key = "DDLX_10";
//					}
//					Map<String, Object> out_trade_body = new HashMap<String, Object>();
//					out_trade_body.put("member_id", consumer_id);
//					out_trade_body.put("goods_id", goodsDataMap.get("goods_id"));
//					out_trade_body.put("title", goodsDataMap.get("title"));
//					out_trade_body.put("consumer_pay_gold_amount", consumer_pay_gold_amount);
//					orderService.balancePay("SJLY_01", consumer_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
//							new BigDecimal(consumer_pay_gold_amount), order_type_key, order_no, "淘淘领", out_trade_body);
//				}

				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

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
	public void OwnOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile",
							"order_type", "data_source", "delivery_address", 
							"contact_person", "contact_tel" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String orderType = StringUtil.formatStr(paramsMap.get("order_type"));
			String dataSource = StringUtil.formatStr(paramsMap.get("data_source"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String contact_person = StringUtil.formatStr(paramsMap.get("contact_person"));
			String contact_tel = StringUtil.formatStr(paramsMap.get("contact_tel"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			
			if(payment_way_key == null || payment_way_key == "" || payment_way_key == "null" ){
				payment_way_key = "ZFFS_01";
			}
			
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<String, Object>();
//			tempMap.put("goods_code", goods_code);
//			tempMap.put("member_mobile", mobile);
//			tempMap.put("status_neq", Constant.ORDER_CANCELLED);
//			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
//			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
//			tempMap.put("order_date_start", order_date_start);
//			tempMap.put("order_date_end", new_date);
//			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
//			if (orderList.size() > 0) {
//				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
//						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
//			}
			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type="";
			if(orderType.equals("taobao")){
				order_type = Constant.ORDER_TYPE_TAOBAO;
			}else if(orderType.equals("meitianhui")){
				order_type = Constant.ORDER_TYPE_MEITIANHUI;
			}else if(orderType.equals("huiguo")){
				order_type = Constant.ORDER_TYPE_HUIGUO;
			}
			
			

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();

			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");

			freeGetGoodsTaobaoSalesEdit(goods_id);
			
			try {
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				order.setPayment_way_key(payment_way_key);
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(contact_tel);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source(dataSource);
				order.setDelivery_address(delivery_address);
				order.setRemark(remark);
				order.setQty(qty);
				order.setSelf_label(StringUtil.formatStr(goodsDataMap.get("self_label")));
				if (goodsDataMap.get("commission") == null) {
					order.setOrder_commision(new BigDecimal("0"));
				}else{
					order.setOrder_commision((BigDecimal) goodsDataMap.get("commission"));
				}
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);

// 会过商品订单不扣除 金币了
//				// 消费者扣除的金币(支付金额-优惠金额)
//				Integer consumer_pay_gold_amount = new BigDecimal(sub_amount).intValue();
//				if (consumer_pay_gold_amount > 0) {
//					// 交易的订单类型
//					String order_type_key = "DDLX_07";
//					if (data_source.equals(Constant.ORDER_TYPE_TAOBAO)) {
//						order_type_key = "DDLX_10";
//					}
//					Map<String, Object> out_trade_body = new HashMap<String, Object>();
//					out_trade_body.put("member_id", consumer_id);
//					out_trade_body.put("goods_id", goodsDataMap.get("goods_id"));
//					out_trade_body.put("title", goodsDataMap.get("title"));
//					out_trade_body.put("consumer_pay_gold_amount", consumer_pay_gold_amount);
//					orderService.balancePay("SJLY_01", consumer_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
//							new BigDecimal(consumer_pay_gold_amount), order_type_key, order_no, "淘淘领", out_trade_body);
//				}

				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

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
	public void consumerFreeGetRecordCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile"});
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String channel_id = (String) paramsMap.get("channel_id");
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_code", goods_code);
			tempMap.put("member_mobile", mobile);
			tempMap.put("status_neq", Constant.ORDER_CANCELLED);
			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
			tempMap.put("order_date_start", order_date_start);
			tempMap.put("order_date_end", new_date);
			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
			if (orderList.size() > 0) {
				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
			}
			// 获取商品信息
			Map<String, Object> goodsDataMap ;
			if (StringUtil.isNotBlank(channel_id) && "1".equals(channel_id)) {
				goodsDataMap = orderService.gdFreeGetGoodsFindForOrder(null, goods_code);
			}else {
				goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			}

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type = Constant.ORDER_TYPE_TAOBAO;

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();

			// 扣库存
			if(StringUtil.isNotBlank(channel_id)&&"1".equals(channel_id)){
				orderService.gdFreeGetGoodsSaleQtyDeduction(goods_id, "1");
			}else {
				orderService.psGoodsSaleQtyDeduction(goods_id, "1");
			}
			//增加销量
			if(StringUtil.isNotBlank(channel_id)&&"1".equals(channel_id)){
				gdFreeGetGoodsTaobaoSalesEdit(goods_id);
			}else {
				freeGetGoodsTaobaoSalesEdit(goods_id);
			}
			try {
				if ("meitianhui".equals(paramsMap.get("order_type") + "")) {
					order_type = paramsMap.get("order_type") + "";
				}
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				if ("meitianhui".equals(paramsMap.get("order_type") + "")) {
					order.setPayment_way_key("ZFFS_02");
					order.setData_source("SJLY_17");
				} else {
					order.setPayment_way_key("ZFFS_01");
					order.setData_source("SJLY_01");
				}
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(mobile);
				order.setContact_person(nick_name);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setQty(qty);
				if(StringUtil.isNotBlank(channel_id)&&"1".equals(channel_id)){
					order.setChannel_id(Integer.valueOf(channel_id));
				}
				if (goodsDataMap.get("commission") == null) {
					order.setOrder_commision(new BigDecimal("0"));
				}else{
					order.setOrder_commision((BigDecimal) goodsDataMap.get("commission"));
				}
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);

				// 消费者扣除的金币(支付金额-优惠金额)
				Integer consumer_pay_gold_amount = new BigDecimal(sub_amount).intValue();
				if (consumer_pay_gold_amount > 0) {
					// 交易的订单类型
					String order_type_key = "DDLX_07";
					if (data_source.equals(Constant.ORDER_TYPE_TAOBAO)) {
						order_type_key = "DDLX_10";
					}
					Map<String, Object> out_trade_body = new HashMap<String, Object>();
					out_trade_body.put("member_id", consumer_id);
					out_trade_body.put("goods_id", goodsDataMap.get("goods_id"));
					out_trade_body.put("title", goodsDataMap.get("title"));
					out_trade_body.put("consumer_pay_gold_amount", consumer_pay_gold_amount);
					orderService.balancePay("SJLY_01", consumer_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
							new BigDecimal(consumer_pay_gold_amount), order_type_key, order_no, "淘淘领", out_trade_body);
				}

				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				if(StringUtil.isNotBlank(channel_id)){
					orderService.gdFreeGetGoodsSaleQtyRestore(goods_id, "1");
				}else {
					orderService.psGoodsSaleQtyRestore(goods_id, "1");
				}
				
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

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
	 * 销售量增加(淘淘领)
	 */
	private void freeGetGoodsTaobaoSalesEdit(String goods_id) throws Exception, SystemException, BusinessException {
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "psGoods.consumer.freeGetGoodsTaobaoSalesEdit");
		bizParams.put("goods_id", goods_id);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
	}
	
	/**
	 * 销售量增加(0元购)
	 */
	private void gdFreeGetGoodsTaobaoSalesEdit(String goods_id) throws Exception, SystemException, BusinessException {
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "psGoods.consumer.gdFreeGetGoodsTaobaoSalesEdit");
		bizParams.put("goods_id", goods_id);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
	}

	@Override
	public void storesFreeGetRecordCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_code", "stores_id", "mobile" });
			String goods_code = paramsMap.get("goods_code") + "";
			String member_id = paramsMap.get("stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			// 查询会员是否已经领过商品了
			bizParams.clear();
			bizParams.put("goods_code", goods_code);
			bizParams.put("member_mobile", mobile);
			bizParams.put("status_neq", Constant.ORDER_CANCELLED);
			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
			bizParams.put("order_date_start", order_date_start);
			bizParams.put("order_date_end", new_date);
			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(bizParams);
			if (orderList.size() > 0) {
				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
			}

			// 查询门店联系信息(redis缓存)
			Map<String, Object> storesDataMap = null;
			String storeForOrderFind_key = "[storesFreeGetRecordCreate_storeForOrderFind]_" + member_id;
			Object obj_storesData = redisUtil.getObj(storeForOrderFind_key);
			if (obj_storesData != null) {
				storesDataMap = (Map<String, Object>) obj_storesData;
			} else {
				// 查询门店信息
				reqParams.put("service", "member.storeForOrderFind");
				bizParams.put("stores_id", member_id);
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				String resultStr = HttpClientUtil.postShort(OrderServiceImpl.MEMBER_SERVICE_URL, reqParams);
				resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"),
							(String) resultMap.get("error_msg"));
				}
				storesDataMap = (Map<String, Object>) resultMap.get("data");
				redisUtil.setObj(storeForOrderFind_key, storesDataMap, 1800);
			}

			String stores_name = StringUtil.formatStr(storesDataMap.get("stores_name"));
			String contact_person = StringUtil.formatStr(storesDataMap.get("contact_person"));

			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type = "taobao";
			// 返现金额
			String sub_price = MoneyUtil.moneySub(market_price, discount_price);

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");
			try {
				// 订单赠送交易成功后，进行订单创建
				Date date = new Date();
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(OrderIDUtil.getOrderNo());
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(member_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_STORES);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", stores_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				order.setPayment_way_key("ZFFS_01");
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_price));
				order.setContact_tel(mobile);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source("SJLY_02");
				if (goodsDataMap.get("commission") == null) {
					order.setOrder_commision(new BigDecimal("0"));
				}else{
					order.setOrder_commision((BigDecimal) goodsDataMap.get("commission"));
				}
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setTotal_fee(new BigDecimal(sub_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				fgOrderDao.insertFgOrderItem(orderItem);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
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
	public void handleFreeGetOrderImport(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		RedisLock pointLock = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "qty", "contact_person", "contact_tel", "delivery_address",
							"logistics", "data_source", "external_order_no", "external_buyer_name", "amount" });
			String goods_code = paramsMap.get("goods_code") + "";
			String external_buyer_name = StringUtil.formatStr(paramsMap.get("external_buyer_name"));
			// redis加锁,同一个商品一个买家只能有一个进程处理
			String lockKey = "freeGetOrderImport_" + goods_code + external_buyer_name;
			lock = new RedisLock(redisUtil, lockKey, 30 * 1000); // 30秒超时时间
			lock.lock();
			
			Map<String, Object> tempMap = new HashMap<String, Object>();
			List<Map<String, Object>> orderDetailList = new ArrayList<Map<String, Object>>();
			
			String temp_order_type = StringUtil.formatStr(paramsMap.get("order_type"));	
			if(temp_order_type == null || temp_order_type == "" || temp_order_type == "null"){
				throw new BusinessException(RspCode.ORDER_ERROE, "订单类型为空");
			}
			if(temp_order_type.equals("自营") || temp_order_type.equals("会过")){	
				String transaction_no  =  StringUtil.formatStr(paramsMap.get("transaction_no"));
				if(transaction_no == null || transaction_no == "" || transaction_no == "null"){
					throw new BusinessException(RspCode.ORDER_ERROE, "商户订单号为空");
				}
				tempMap.put("transaction_no", transaction_no);
				tempMap.put("status", Constant.ORDER_CLOSED);
				String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
				tempMap.put("order_date_start", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 2, -1));
				tempMap.put("order_date_end", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -1));
				orderDetailList = fgOrderDao.selectFgOrderDetailForImport(tempMap);
				logger.info("订单导入"+temp_order_type+"商品返款" +"transaction_no="+paramsMap.get("transaction_no")+"goods_code="+goods_code);
			}else{
				// 如果是淘系的商品 则判断买家淘宝账号是否已经领过此商品
				tempMap.put("goods_code", goods_code);
				tempMap.put("external_buyer_name", external_buyer_name);
				tempMap.put("status", Constant.ORDER_CLOSED);
				String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
				tempMap.put("order_date_start", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 2, -1));
				tempMap.put("order_date_end", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -1));
				orderDetailList = fgOrderDao.selectFgOrderDetailForImport(tempMap);
				logger.info("订单导入"+temp_order_type+"商品返款    淘淘领" +"goods_code="+goods_code);
			}
			
			if (orderDetailList.size() > 0) {
				throw new BusinessException(RspCode.ORDER_ERROE, "用户重复领取");
			}
			
			// 通过淘宝订单号和商品编号查询对应的预订单是否存在(状态要为确认)
			tempMap.clear();
			tempMap.put("goods_code", goods_code);
			tempMap.put("external_order_no", paramsMap.get("external_order_no"));
			tempMap.put("status", Constant.ORDER_CONFIRMED);
			orderDetailList = fgOrderDao.selectFgOrderDetailForImport(tempMap);
			if (orderDetailList.size() == 0) {
				throw new BusinessException(RspCode.ORDER_NOT_EXIST, "订单未确定或不存在");
			}
			Map<String, Object> orderDetail = orderDetailList.get(0);
			String member_mobile = orderDetail.get("member_mobile") + "";
			// 检测淘宝用户是否在黑名单中,如果是则对应的手机号也加入黑名单
			// freeGetOrderImportCheck(external_buyer_name, member_mobile);
			// 2017-8-9 丁忍修改：取消自动把每天惠账号加入黑名单

			// 返现金额
			String amount = "0.00";
			String order_type = StringUtil.formatStr(orderDetail.get("order_type"));
			String order_no = orderDetail.get("order_no") + "";
			amount = orderDetail.get("total_fee") + "";
			String member_id = orderDetail.get("member_id") + "";

			String temp_member_type = orderDetail.get("member_type_key") + "";
			if (temp_member_type.equals(Constant.MEMBER_TYPE_CONSUMER)) {
				// redis加锁,同一个买家只能有一个进程处理
				String pointLockKey = "freeGetOrderImport_" + external_buyer_name;
				pointLock = new RedisLock(redisUtil, pointLockKey, 30 * 1000); // 30秒超时时间
				pointLock.lock();
				//
				tempMap.clear();
				tempMap.put("member_id", member_id);
				tempMap.put("status", Constant.ORDER_CLOSED);
				List<Map<String, Object>> tempList1 = new ArrayList<Map<String, Object>>();
				tempList1 = fgOrderDao.selectFgOrder(tempMap);
				// 当订单数量小于1时，证明该会员已完成的订单为0，所以可以给对应的推荐人加积分
				if (tempList1.size() < 1) {
					// ***********************************
					// 1.调用查询推荐人接口，获取推荐人信息
					// ***********************************
					String user_service_url = PropertiesConfigUtil.getProperty("member_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					Map<String, String> bizParams = new HashMap<String, String>();
					reqParams.put("service", "consumer.consumer.userRecommendFind");
					bizParams.put("member_id", member_id);
					reqParams.put("params", FastJsonUtil.toJson(bizParams));
					String resultStr = HttpClientUtil.postShort(user_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						// ********************************************************************
						// 2. 进行交易冲正 会员加积分 公司减积分
						// ********************************************************************
						Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
						String reference_id = (String) dateMap.get("reference_id");

						if (reference_id != null) {
							Map<String, Object> out_trade_body = new HashMap<String, Object>();
							Integer point = new BigDecimal(5).intValue(); // 默认加5积分

							out_trade_body.clear();
							out_trade_body.put("reference_id", reference_id);
							out_trade_body.put("order_no", order_no);
							out_trade_body.put("amount", point);
							out_trade_body.put("desc1", orderDetail.get("desc1"));
							orderService.balancePay("SJLY_03", Constant.MEMBER_ID_MTH, reference_id, "ZFFS_09",
									new BigDecimal(point), "DDLX_20", order_no, "推荐人赠送积分", out_trade_body);
						}

						// String finance_service_url =
						// PropertiesConfigUtil.getProperty("finance_service_url");
						// reqParams.clear();
						// bizParams.clear();
						// reqParams.put("service",
						// "finance.consumer.memberPointEdit");
						// bizParams.put("member_id", reference_id);
						// bizParams.put("booking_mark", "income");
						// bizParams.put("point_values", "5");
						// bizParams.put("member_type_key", "consumer");
						//
						// Map<String, Object> remarkMap = new HashMap<String,
						// Object>();
						// remarkMap.put("transaction_no", "0");
						// remarkMap.put("business_type_key", "0");
						// remarkMap.put("payment_way_key", "0");
						// remarkMap.put("detail", "推荐用户赠送积分");
						// remarkMap.put("transaction_member_id", "0");
						// remarkMap.put("transaction_member_type_key", "0");
						// remarkMap.put("transaction_member_name", "0");
						// remarkMap.put("transaction_member_contact", "0");
						//
						//
						// bizParams.put("remark",
						// FastJsonUtil.toJson(remarkMap));
						// reqParams.put("params",
						// FastJsonUtil.toJson(bizParams));
						// String tempResultStr =
						// HttpClientUtil.postShort(finance_service_url,
						// reqParams);
						// Map<String, Object> TempResultMap =
						// FastJsonUtil.jsonToMap(tempResultStr);
						// if (((String)
						// TempResultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC))
						// {
						// System.out.println("添加积分成功");
						// }
					}
				}
			}

			// 更新领了么订单信息
			tempMap.clear();
			tempMap.put("data_source", StringUtil.formatStr(paramsMap.get("data_source")));
			tempMap.put("external_buyer_name", external_buyer_name);
			tempMap.put("external_buyer_pay_no", StringUtil.formatStr(paramsMap.get("external_buyer_pay_no")));
			tempMap.put("status", Constant.ORDER_CLOSED);
			tempMap.put("logistics", paramsMap.get("logistics") + "");
			tempMap.put("modified_date",
					DateUtil.date2Str((Date) orderDetail.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("order_id", orderDetail.get("order_id") + "");
			// 非每天惠的才更新收货信息
			if (!order_type.equals(Constant.ORDER_TYPE_MEITIANHUI)) {
				tempMap.put("contact_tel", paramsMap.get("contact_tel") + "");
				tempMap.put("contact_person", paramsMap.get("contact_person") + "");
				tempMap.put("delivery_address", paramsMap.get("delivery_address") + "");
			}
			int updateFlag = fgOrderDao.updateFgOrder(tempMap);
			if (updateFlag == 0) {
				throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
			}
			// 交易的订单类型
			String order_type_key = "DDLX_07";
			if (order_type.equals(Constant.ORDER_TYPE_TAOBAO)) {
				order_type_key = "DDLX_10";
			}
			// 领了么返款(订单赠送)
			Map<String, Object> out_trade_body = new HashMap<String, Object>();
			out_trade_body.put("member_id", member_id);
			out_trade_body.put("order_no", order_no);
			amount = MoneyUtil.moneyMul(amount, StringUtil.formatStr(paramsMap.get("qty"))); 
			out_trade_body.put("amount", amount);
			out_trade_body.put("desc1", orderDetail.get("desc1"));
			orderService.orderReward("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05", new BigDecimal(amount),
					order_type_key, order_no, "淘淘领返款", out_trade_body);
			// 如果是消费者,则给对应的门店进行返佣
			String member_type_key = orderDetail.get("member_type_key") + "";
			String discount_fee = orderDetail.get("discount_fee") + "";
			if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
				// 创建对应的佣金日志,用于7个工作日后对用户进行返佣.
				BigDecimal commision = (BigDecimal)orderDetail.get("order_commision");
				if (commision != null && commision.compareTo(BigDecimal.ZERO) == 1) {
					String order_commision_str = MoneyUtil.moneyMul(orderDetail.get("order_commision").toString(), StringUtil.formatStr(paramsMap.get("qty")));
					BigDecimal order_commision = new BigDecimal(order_commision_str);
					orderDetail.put("order_commision", order_commision);
					int insertFlag = insertFgOrderCommission(orderDetail);
					if (insertFlag == 0) {
						throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
					}
				}

				// 查询消费者对应的推荐门店
				tempMap.clear();
				tempMap.put("order_id", orderDetail.get("order_id"));
				tempMap.put("consumer_id", member_id);
				Map<String, Object> fgOrderExtraMap = fgOrderDao.selectFgOrderExtra(tempMap);
				if (null != fgOrderExtraMap) {
					String stores_id = fgOrderExtraMap.get("stores_id") + "";
					// 店东获取消费者支付金额对应的金币值
					Integer commission = new BigDecimal(discount_fee).intValue();
					if (commission > 0) {
						out_trade_body.clear();
						out_trade_body.put("stores_id", stores_id);
						out_trade_body.put("order_no", order_no);
						out_trade_body.put("amount", commission);
						out_trade_body.put("desc1", orderDetail.get("desc1"));
						orderService.balancePay("SJLY_03", Constant.MEMBER_ID_MTH, stores_id, "ZFFS_08",
								new BigDecimal(commission), order_type_key, order_no, "平台返利", out_trade_body);
					}
				}
				// 消费者返款 解封见面礼
				// String order_date = DateUtil.date2Str((Date)
				// orderDetail.get("order_date"), DateUtil.fmt_yyyyMMdd);
				// gcActivityFaceGiftPay(member_id, member_type_key, order_no,
				// order_date);

			} else if (member_type_key.equals(Constant.MEMBER_TYPE_STORES)) {
				// 如果是门店的商品,则导入商品到门店的商品库中
				psOrderService.psGoodsImport(member_id, goods_code, 1, "淘淘领商品导入", "lingleme");
			}
			// 违规用户冻结返款金额
			balanceFreeze(member_id, member_type_key, amount, member_mobile);

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (pointLock != null) {
				pointLock.unlock();
			}
		}
	}

	/**
	 * 自动添加黑名单
	 * 
	 * @param member_mobile
	 * @param taobao_account
	 * @throws Exception
	 */
	private void freeGetOrderImportCheck(String external_buyer_name, String member_mobile) throws Exception {
		// 检测订单是否存在
		Map<String, Object> tempMap = new HashMap<String, Object>();
		// 检测淘宝账号是不是已经列入黑名单了
		tempMap.put("taobao_account", external_buyer_name);
		List<Map<String, Object>> fgBlacklist = fgBlacklistDao.selectFgBlacklist(tempMap);
		if (fgBlacklist.size() > 0) {
			// 如果淘宝账号加入了黑名单,检测手机号是否也是黑名单，如果不是，新增一条以手机号和淘宝账号的黑名单记录
			tempMap.clear();
			fgBlacklist.clear();
			tempMap.put("mobile", member_mobile);
			fgBlacklist = fgBlacklistDao.selectFgBlacklist(tempMap);
			if (fgBlacklist.size() == 0) {
				FgBlacklist blacklist = new FgBlacklist();
				// blacklist.setMobile(member_mobile);
				// blacklist.setTaobao_account(external_buyer_name);
				blacklist.setBlacklist_id(IDUtil.getUUID());
				blacklist.setCreated_date(new Date());
				blacklist.setRemark("返款时自动添加");
				fgBlacklistDao.insertFgBlacklist(blacklist);
			}
		}
	}

	/**
	 * 黑名单中的用户自动冻结余额
	 * 
	 * @param member_mobile
	 * @param taobao_account
	 * @throws Exception
	 */
	private void balanceFreeze(final String member_id, final String member_type_key, final String amount,
			final String member_mobile) throws Exception {
		OrderController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// 休眠一秒,等待数据库数据刷新
					Thread.sleep(1000);
					// 检测手机号是否是黑名单
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("account", member_mobile);
					tempMap.put("account_type", Constant.ACCOUNT_TYPE_MEITIANHUI);
					List<Map<String, Object>> fgBlacklist = fgBlacklistDao.selectFgBlacklist(tempMap);
					if (fgBlacklist.size() > 0) {
						Map<String, String> reqParams = new HashMap<String, String>();
						Map<String, Object> bizParams = new HashMap<String, Object>();
						reqParams.put("service", "finance.balanceFreeze");
						bizParams.put("data_source", "SJLY_03");
						bizParams.put("member_id", member_id);
						bizParams.put("member_type_key", member_type_key);
						bizParams.put("amount", amount);
						bizParams.put("detail", "返款冻结");
						reqParams.put("params", FastJsonUtil.toJson(bizParams));
						HttpClientUtil.postShort(OrderServiceImpl.FINANCE_SERVICE_URL, reqParams);
					}
				} catch (Exception e) {
					logger.error("返款冻结异常", e);
				}
			}
		});
	}

	@Override
	public void handleLocalFreeGet(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "data_source" });
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_CONFIRMED)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		Map<String, Object> memberInfoMap = FastJsonUtil.jsonToMap(order.get("member_info") + "");
		String mobile = StringUtil.formatStr(memberInfoMap.get("mobile"));
		String member_id = order.get("member_id") + "";
		String member_type_key = order.get("member_type_key") + "";
		String order_no = order.get("order_no") + "";
		String amount = order.get("total_fee") + "";

		// 更新领了么订单信息
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("data_source", paramsMap.get("data_source"));
		tempMap.put("external_buyer_name", mobile);
		tempMap.put("external_buyer_pay_no", member_id);
		tempMap.put("status", Constant.ORDER_CLOSED);
		tempMap.put("modified_date", DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		tempMap.put("order_id", order.get("order_id") + "");
		// 设置物流信息为现场领
		Map<String, Object> logisticsMap = new HashMap<String, Object>();
		logisticsMap.put("company", "现场领");
		logisticsMap.put("number", "");
		tempMap.put("logistics", FastJsonUtil.toJson(logisticsMap));
		int updateFlag = fgOrderDao.updateFgOrder(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) > 0) {
			// 领了么返款(订单赠送)
			Map<String, Object> out_trade_body = new HashMap<String, Object>();
			out_trade_body.put("order_no", order_no);
			out_trade_body.put("member_id", member_id);
			out_trade_body.put("member_type_key", member_type_key);
			orderService.orderReward("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05", new BigDecimal(amount),
					"DDLX_07", order_no, "淘淘领返款", out_trade_body);
		}
		// 如果是消费者,则给对应的门店进行返佣
		String discount_fee = order.get("discount_fee") + "";
		if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
			// 查询消费者对应的推荐门店
			Map<String, Object> out_trade_body = new HashMap<String, Object>();
			out_trade_body.put("order_id", order.get("order_id"));
			out_trade_body.put("consumer_id", member_id);
			Map<String, Object> fgOrderExtraMap = fgOrderDao.selectFgOrderExtra(out_trade_body);
			if (null != fgOrderExtraMap) {
				String stores_id = fgOrderExtraMap.get("stores_id") + "";
				// 店东获取消费者支付金额对应的金币值
				Integer commission = new BigDecimal(discount_fee).intValue();
				if (commission > 0) {
					tempMap.clear();
					tempMap.put("stores_id", stores_id);
					tempMap.put("order_no", order_no);
					tempMap.put("amount", commission);
					tempMap.put("desc1", "现场领");
					orderService.balancePay("SJLY_03", Constant.MEMBER_ID_MTH, stores_id, "ZFFS_08",
							new BigDecimal(commission), "DDLX_07", order_no, "平台返利", tempMap);
				}
			}
		}
	}

	@Override
	public void handleFgOrderCancelled(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_id", "remark" });
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		// 获取订单商品列表信息
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderItem(paramsMap);
		Map<String, Object> orderItem = orderItemList.get(0);
		paramsMap.put("modified_date",
				DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", Constant.ORDER_CANCELLED);
		int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		// 还原库存
		String flags = StringUtil.formatStr(order.get("flags"));
		if(StringUtil.isNotEmpty(flags) && "1".equals(flags)){
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", orderItem.get("goods_id"));
			tempMap.put("goods_code", orderItem.get("goods_code"));
			tempMap.put("qty", orderItem.get("qty"));
			tempMap.put("sku_id", orderItem.get("sku_id"));
			orderService.psGoodsSaleQtyForOwnRestore(tempMap);
		}else{
			String channel_id = order.get("channel_id")+"";
			if("1".equals(channel_id)){//0元购的订单
				orderService.gdFreeGetGoodsSaleQtyRestore(orderItem.get("goods_id") + "", orderItem.get("qty") + "");
			}else {
				orderService.psGoodsSaleQtyRestore(orderItem.get("goods_id") + "", orderItem.get("qty") + "");
			}
		}

		// 如果是消费者取消,退回扣除的金币
		if (((String) order.get("member_type_key")).equals(Constant.MEMBER_TYPE_CONSUMER)) {

			//如果订单是会过或者是自营的 则不退回扣除的金币
			if((order.get("order_type")).equals("taobao")){
			
				Integer total_fee = new BigDecimal(order.get("total_fee") + "").intValue();
				if (total_fee > 0) {
					// 交易的订单类型
					String order_type_key = "DDLX_07";
					if (StringUtil.formatStr(order.get("order_type")).equals(Constant.ORDER_TYPE_TAOBAO)) {
						order_type_key = "DDLX_10";
					}
					// 领了么取消订单退金币
					String member_id = order.get("member_id") + "";
					String order_no = order.get("order_no") + "";
					Map<String, Object> out_trade_body = new HashMap<String, Object>();
					out_trade_body.put("order_no", order_no);
					orderService.orderRefund("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
							new BigDecimal(total_fee), order_type_key, order_no, "淘淘领退款", out_trade_body);
				}
			}
		}
	}

	@Override
	public void handleFgOrderCancelledForOp(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "biz_remark" });
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		// 自营的订单才会容许取消订单
		if (!((String) order.get("status")).equals(Constant.ORDER_CONFIRMED)
				&& !((String) order.get("order_type")).equals(Constant.ORDER_TYPE_MEITIANHUI)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}

		// 获取订单商品列表信息
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderItem(paramsMap);
		Map<String, Object> orderItem = orderItemList.get(0);
		paramsMap.put("modified_date",
				DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", Constant.ORDER_CANCELLED);
		paramsMap.put("remark", paramsMap.get("biz_remark"));
		int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		
		// 如果是自营商品，进行订单退款操作
		/*if (((String) order.get("order_type")).equals(Constant.ORDER_TYPE_MEITIANHUI)) {
			// 退还支付金额
			BigDecimal sale_fee = new BigDecimal(order.get("sale_fee") + "");
			if (sale_fee.compareTo(BigDecimal.ZERO) > 0) {
				String member_id = order.get("member_id") + "";
				String order_no = order.get("order_no") + "";
				Map<String, Object> out_trade_body = new HashMap<String, Object>();
				out_trade_body.put("order_no", order_no);
				orderService.orderRefund("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05", sale_fee, "DDLX_07",
						order_no, "淘淘领退款", out_trade_body);
			}
		}*/
		// 还原库存
		orderService.psGoodsSaleQtyRestore(orderItem.get("goods_id") + "", orderItem.get("qty") + "");
	   //记录日志到表fg_order_log
	   String order_no = order.get("order_no") + "";
	   String operator = StringUtil.formatStr(paramsMap.get("operator"));//运营传参数操作人
	   Map<String, Object> params = new HashMap<String, Object>();
       params.put("log_id", UUID.randomUUID().toString());
       params.put("category", "订单取消");
       params.put("order_no", order_no);
       params.put("tracked_date", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
       params.put("event", operator + "|取消订单"+"|填写取消原因:"+paramsMap.get("biz_remark"));
	   fgOrderDao.saveOperateLog(params);
	}





    @Override
    public void handleHgOrderCancelledForOp(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "biz_remark" });
        List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
        if (orderList.size() == 0) {
            throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
        }
        Map<String, Object> order = orderList.get(0);
        // 会过的订单才会容许取消订单
        if (!((String) order.get("status")).equals(Constant.ORDER_CONFIRMED)
                && !((String) order.get("order_type")).equals(Constant.ORDER_TYPE_HUIGUO)) {
            throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
        }

        // 获取订单商品列表信息
        List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderItem(paramsMap);
        Map<String, Object> orderItem = orderItemList.get(0);
        paramsMap.put("modified_date",
                DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
        paramsMap.put("status", Constant.ORDER_CANCELLED);
        paramsMap.put("remark", paramsMap.get("biz_remark"));
        int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
        if (updateFlag == 0) {
            throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
        }

        // 如果是会过商品，进行订单退款操作
        if (((String) order.get("order_type")).equals(Constant.ORDER_TYPE_HUIGUO)) {
            // 退还支付金额
            BigDecimal sale_fee = new BigDecimal(order.get("sale_fee") + "");
            if (sale_fee.compareTo(BigDecimal.ZERO) > 0) {
                String member_id = order.get("member_id") + "";
                String order_no = order.get("order_no") + "";
                Map<String, Object> out_trade_body = new HashMap<String, Object>();
                out_trade_body.put("order_no", order_no);
                orderService.orderRefund("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05", sale_fee, "DDLX_21",
                        order_no, "会过退款", out_trade_body);
            }
        }
        // 还原库存
        orderService.psGoodsSaleQtyRestore(orderItem.get("goods_id") + "", orderItem.get("qty") + "");
    }



	@Override
	public void fgOrderForOpEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id" });
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		String external_order_no = StringUtil.formatStr(paramsMap.get("external_order_no"));
		if (!StringUtils.isEmpty(external_order_no)) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("external_order_no", external_order_no);
			List<Map<String, Object>> tempOrderList = fgOrderDao.selectFgOrder(tempMap);
			if (tempOrderList.size() > 0) {
				Map<String, Object> order = tempOrderList.get(0);
				String temp_order_id = StringUtil.formatStr(order.get("order_id"));
				if (!temp_order_id.equals(paramsMap.get("order_id"))) {
					throw new BusinessException(RspCode.ORDER_ERROE, "订单号已被使用");
				}
			}
		}
		fgOrderDao.updateFgOrder(paramsMap);
	}
	
	@Override
	public void fgOrderForLogisticEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id","logistics_number"});
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", paramsMap.get("order_id"));
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		Map<String, Object> logisticMap = FastJsonUtil.jsonToMap(order.get("logistics")+"");
		Map<String, Object> newlogisticsMap = new HashMap<String, Object>();
		String logistics_number = StringUtil.formatStr(paramsMap.get("logistics_number"));
		String company = "";
		if(null != logisticMap){
			company = StringUtil.formatStr(logisticMap.get("company"));
		}
		newlogisticsMap.put("company", company);
		newlogisticsMap.put("number", logistics_number);
		tempMap.put("logistics", FastJsonUtil.toJson(newlogisticsMap));
		fgOrderDao.updateFgOrder(tempMap);
		//记录日志到表fg_order_log
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("log_id", UUID.randomUUID().toString());
        params.put("category", "设置物流信息");
        params.put("order_no", order.get("order_no"));
        params.put("tracked_date", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        params.put("event",  paramsMap.get("operator")+ "|设置物流单号:" + logistics_number);
	    fgOrderDao.saveOperateLog(params);
	}

	@Override
	public void handleFgOrderConfirm(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {

			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_id", "external_order_no" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("external_order_no", paramsMap.get("external_order_no"));

			String order_id = paramsMap.get("order_id") + "";
			lockKey = "[handleFgOrderConfirm]_" + order_id;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
			if (orderList.size() > 0) {
				throw new BusinessException(RspCode.ORDER_ERROE, "订单号已存在");
			}
			tempMap.clear();
			tempMap.put("order_id", paramsMap.get("order_id"));
			orderList = fgOrderDao.selectFgOrder(tempMap);
			if (orderList.size() == 0) {
				throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
			}
			Map<String, Object> order = orderList.get(0);
			if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
				throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
			}
			paramsMap.put("modified_date",
					DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
			paramsMap.put("status", Constant.ORDER_CONFIRMED);
			int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
			if (updateFlag == 0) {
				throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
			}
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
	public void selectFgOrderByStatus(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		List<Map<String, Object>> list = fgOrderDao.selectFgOrderByStatus(paramsMap);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map<String, Object> maps : list) {
				tempMap.put("count_num", maps.get("count_num"));
			}
			result.setResultData(tempMap);
		}
	}
	
	@Override
	public void fgOrderListForOpByRefundFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
			List<String> list = StringUtil.str2List(status, ",");
			if (list.size() > 1) {
				paramsMap.remove("status");
				paramsMap.put("status_in", list);
			}
		}
		String external_order_no = StringUtil.formatStr(paramsMap.get("external_order_no"));
		if (!StringUtils.isEmpty(external_order_no)) {
			List<String> list = StringUtil.str2List(external_order_no, ",");
			if (list.size() > 1) {
				paramsMap.remove("external_order_no");
				paramsMap.put("external_order_no_in", list);
			}
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderByRefundList(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("settle_status", map.get("settle_status"));
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			tempMap.put("member_id", StringUtil.formatStr(map.get("member_id")));
			tempMap.put("member_mobile", StringUtil.formatStr(map.get("member_mobile")));
			tempMap.put("modified_date", DateUtil.date2Str((Date) map.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
			
			tempMap.put("operator", getOperator(StringUtil.formatStr(map.get("biz_remark"))));
			
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	private String getOperator(String value){
		try{
			JSONObject json=JSONObject.parseObject(value);
			if(json!=null&&json.containsKey("operator")){
				return json.getString("operator");
			}
		}catch(Exception e){
		}
		
		return null;
	}

	@Override
	public void fgOrderListForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
			List<String> list = StringUtil.str2List(status, ",");
			if (list.size() > 1) {
				paramsMap.remove("status");
				paramsMap.put("status_in", list);
			}
		}
		String external_order_no = StringUtil.formatStr(paramsMap.get("external_order_no"));
		if (!StringUtils.isEmpty(external_order_no)) {
			List<String> list = StringUtil.str2List(external_order_no, ",");
			if (list.size() > 1) {
				paramsMap.remove("external_order_no");
				paramsMap.put("external_order_no_in", list);
			}
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		// 2017/11/20 (丁龙添加) (数据分表,2017-09-30号后的数据重新创建了新的数据表)
		/*
		 * if (StringUtils.isNotBlank((String)
		 * paramsMap.get("order_date_start")) && StringUtils.isNotBlank((String)
		 * paramsMap.get("order_date_end"))) { String[] startTimes = ((String)
		 * paramsMap.get("order_date_start")).split("-"); String[] endTimes =
		 * ((String) paramsMap.get("order_date_end")).split("-"); long
		 * year_month_startTime = Long.parseLong(startTimes[0] + startTimes[1]);
		 * long year_month_endTime = Long.parseLong(endTimes[0] + endTimes[1]);
		 * //查询数据 List<Map<String, Object>> orderItemList1 = null;
		 * List<Map<String, Object>> orderItemList2 = null; if
		 * (year_month_startTime <= 201709) { orderItemList1 =
		 * fgOrderDao.selectFgOrderDetail2017(paramsMap); } if
		 * (year_month_endTime > 201709) { orderItemList2 =
		 * fgOrderDao.selectFgOrderDetail(paramsMap); } if (orderItemList1 !=
		 * null) { orderItemList.addAll(orderItemList1); } if (orderItemList2 !=
		 * null) { orderItemList.addAll(orderItemList2); } }
		 */
		//List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderDetail(paramsMap);
		List<Map<String, Object>> orderItemList = beikeMallOrderDao.selectBeikeMallOrderList(paramsMap);

		for (Map<String, Object> map : orderItemList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("desc1", map.get("desc1"));
			tempMap.put("item_num", map.get("item_num") + "");
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("delivery_address", map.get("delivery_address") + "");
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("external_buyer_name", StringUtil.formatStr(map.get("external_buyer_name")));
			tempMap.put("external_buyer_pay_no", StringUtil.formatStr(map.get("external_buyer_pay_no")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("member_id", map.get("member_id") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			Map<String, Object> memberInfoMap = FastJsonUtil.jsonToMap(map.get("member_info") + "");
			tempMap.put("member_mobile", memberInfoMap.get("mobile"));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("data_source", StringUtil.formatStr(map.get("data_source")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("settle_status", map.get("settle_status"));
			tempMap.put("item_qty", map.get("item_qty") + "");
			tempMap.put("item_goods_id", map.get("item_goods_id"));
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_goods_code", StringUtil.formatStr(map.get("item_goods_code")));
			tempMap.put("item_supplier", StringUtil.formatStr(map.get("item_supplier")));
			tempMap.put("item_manufacturer", StringUtil.formatStr(map.get("item_manufacturer")));
			tempMap.put("item_goods_unit", map.get("item_goods_unit") + "");
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("vip_fee", map.get("vip_fee") + "");
			tempMap.put("item_discount_fee", map.get("item_discount_fee") + "");
			tempMap.put("item_service_fee", map.get("item_service_fee") + "");
			tempMap.put("beike_credit", map.get("beike_credit") + "");
			tempMap.put("transaction_no", StringUtil.formatStr(map.get("transaction_no")));
			tempMap.put("qty", StringUtil.formatStr(map.get("qty")));
			tempMap.put("self_label", StringUtil.formatStr(map.get("self_label")));
			if(StringUtil.formatStr(map.get("qty")).equals("")){
				tempMap.put("qty","1");
			}
			String data_source = "taobao";
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(map.get("item_remark") + "");
				data_source = StringUtil.formatStr(itemRemarkMap.get("data_source"));
			}
			tempMap.put("item_data_source", data_source);


			Object flags=map.get("flags");
			if(flags!=null && !"null".equals(flags) && StringUtil.isNotEmpty(flags.toString()) && 1==Integer.parseInt(flags.toString())){
				Object sku_id=map.get("sku_id");
				tempMap.put("flags", flags);
				tempMap.put("sku_id", sku_id);

				if(sku_id!=null && !"null".equals(sku_id.toString()) && StringUtil.isNotEmpty(sku_id.toString())){
					Map<String, String> reqParams = new HashMap<String, String>();
					Map<String, Object> bizParams = new HashMap<String, Object>();
					reqParams.put("service", "goods.queryGdGoodsSkuidBySkuId");
					bizParams.put("sku_id", sku_id);
					reqParams.put("params", FastJsonUtil.toJson(bizParams));
					logger.info("超级返订单列表查询，根据skuid查询商品属性sku_id："+sku_id+",订单编号:"+map.get("order_no"));
					String resultStr = HttpClientUtil.postShort(OrderServiceImpl.GOODS_SERVICE_URL, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
					}
					Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");

					tempMap.put("attr_zname", dataMap.get("attr_zname"));
					tempMap.put("attr_fname", dataMap.get("attr_fname"));
					tempMap.put("attr_zvalue", dataMap.get("attr_zvalue"));
					tempMap.put("attr_fvalue", dataMap.get("attr_fvalue"));

					Object attr_fid=dataMap.get("attr_fid");
					if(attr_fid!=null && !"null".equals(attr_fid.toString()) && StringUtil.isNotEmpty(attr_fid.toString())){
						tempMap.put("guige", dataMap.get("attr_zname")+":"+dataMap.get("attr_zvalue")+","+dataMap.get("attr_fname")+","+dataMap.get("attr_fvalue"));
					}else{
						tempMap.put("guige", dataMap.get("attr_zname")+":"+dataMap.get("attr_zvalue"));
					}


				}

			}



//			if(StringUtil.formatStr(map.get("external_order_no")) != ""){
//				Map<String, String> reqParams = new HashMap<String, String>();
//				Map<String, Object> bizParams = new HashMap<String, Object>();
//				reqParams.put("service", "finance.consumer.transactionsnoByOuttradnoFind");
//				bizParams.put("order_id", StringUtil.formatStr(map.get("order_id")));
//				reqParams.put("params", FastJsonUtil.toJson(bizParams));
//				String resultStr = HttpClientUtil.postShort(OrderServiceImpl.FINANCE_SERVICE_URL, reqParams);
//				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
//				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
//					throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
//				}
//				Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
//				tempMap.put("transaction_no", StringUtil.formatStr(dateMap.get("transaction_no")));
//				tempMap.put("payment_way_key", StringUtil.formatStr(dateMap.get("payment_way_key")));
//			}else{
//				tempMap.put("transaction_no", "");
//				tempMap.put("payment_way_key", "");
//			}

			if(paramsMap.get("order_type")!=null && "taobao".equals(paramsMap.get("order_type"))){
				if("merchants".equals(paramsMap.get("item_data_source")) && data_source.equals("merchants")){
					resultList.add(tempMap);
				}else if("meitianhui".equals(paramsMap.get("item_data_source"))  && data_source.equals("meitianhui")){
					resultList.add(tempMap);
				}else if("taobao".equals(paramsMap.get("item_data_source"))  && data_source.equals("taobao")){
					resultList.add(tempMap);
				}else if("all".equals(paramsMap.get("item_data_source"))){
					resultList.add(tempMap);
				}
			}else{
				resultList.add(tempMap);
			}

		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	
	 

	@Override
    public void fgOrderListForMoneyFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
			List<String> list = StringUtil.str2List(status, ",");
			if (list.size() > 1) {
				paramsMap.remove("status");
				paramsMap.put("status_in", list);
			}
		}
		String external_order_no = StringUtil.formatStr(paramsMap.get("external_order_no"));
		if (!StringUtils.isEmpty(external_order_no)) {
			List<String> list = StringUtil.str2List(external_order_no, ",");
			if (list.size() > 1) {
				paramsMap.remove("external_order_no");
				paramsMap.put("external_order_no_in", list);
			}
		}
		Map<String, Object> orderItemList = fgOrderDao.selectFgOrderDetailMoney(paramsMap);
		Map<String, Object> map = new HashMap<String, Object>();
		if(null != orderItemList){
			String toal_fee=orderItemList.get("total_fee").toString();
			map.put("total_fee", toal_fee);// 返款总额
			String settled_price=orderItemList.get("settled_price").toString();
			map.put("discount_fee", settled_price);// 结算总额
			String sale_fee=orderItemList.get("sale_fee").toString();
			map.put("sale_fee", sale_fee);// 预付总额
			String all_discount_fee=orderItemList.get("all_discount_fee").toString();
			map.put("all_discount_fee", all_discount_fee);// 到手总额
		}else{
			map.put("total_fee", "00.00");// 返款总额
			map.put("discount_fee", "00.00");// 结算总额
			map.put("sale_fee", "00.00");// 预付总额
			map.put("all_discount_fee", "00.00");// 预付总额
		}
		result.setResultData(map); 
	}



	@Override
	public void fgOrderFormMoneyTabulationFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_code" });
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderDetailMoneyTabulationFind(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		for (Map<String, Object> map : orderItemList) {
			tempMap.put("sale", map.get("sale").toString());// 预付价
			tempMap.put("total_fee", map.get("total_fee").toString());// 返款总额
			tempMap.put("discount_fee", map.get("settled_price").toString());// 结算总额
			tempMap.put("sale_fee", map.get("sale_fee").toString());// 预付总额
			tempMap.put("goods_code", map.get("goods_code").toString());// 商品码
			tempMap.put("goods_title", map.get("goods_title").toString());// 商品标题
			tempMap.put("item_num", map.get("item_num").toString());// 订单总数
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void fgOrderDetailForOperationFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "external_order_no" });
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("external_order_no", paramsMap.get("external_order_no"));
		map.put("status", "confirmed");
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderDetail(paramsMap);
		map.clear();
		map.put("order_no", orderItemList.get(0).get("order_no"));
		map.put("member_mobile", orderItemList.get(0).get("member_mobile"));
		result.setResultData(map);
	}

	@Override
	public void fgOrderDetailForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_no" });
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderDetail(paramsMap);
		map.put("order_no", orderItemList.get(0).get("order_no"));
		map.put("status", orderItemList.get(0).get("status"));
		result.setResultData(map);
	}

	@Override
	public void fgOrderDetailForOwnFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id" });
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderDetail(paramsMap);
		if (orderItemList.size() > 0) {
			map.put("sale_fee", StringUtil.formatStr(orderItemList.get(0).get("sale_fee")));
			map.put("qty", StringUtil.formatStr(orderItemList.get(0).get("qty")));
		}
		result.setResultData(map);
	}

	
	@Override
	public void fgOrderListByOwnFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderListByOwn(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_code", map.get("item_goods_code") + "");
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	@Override
	public void fgOrderListByHuiguoFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderListByHuiguo(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_code", map.get("item_goods_code") + "");
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	
	@Override
	public void fgOrderList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderList(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap;
			try {
				goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			} catch (Exception e) {
				goodsDataMap = orderService.gdFreeGetGoodsFindForOrder(null, goods_code);
			}
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}

	@Override
	public void fgOrderListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderListForSmallProgram(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	
	
	@Override
	public void handleFgOrderMessage(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "remark" });
		fgOrderDao.updateFgOrder(paramsMap);
	}

	@Override
	public void fgOrderExceptionCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		paramsMap.put("status", Constant.ORDER_CONFIRMED);
		String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
		paramsMap.put("order_date_start", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -7));
		paramsMap.put("order_date_end", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -3));
		Map<String, Object> countMap = new HashMap<String, Object>();
		countMap.put("count_num", "0");
		Map<String, Object> fgOrderCountMap = fgOrderDao.selectFgOrderCount(paramsMap);
		if (null != fgOrderCountMap) {
			countMap.put("count_num", fgOrderCountMap.get("count_num") + "");
		}
		result.setResultData(countMap);
	}

	@Override
	public void fgOrderViolationCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		Map<String, Object> countMap = new HashMap<String, Object>();
		countMap.put("violation_num", "0");
		countMap.put("total_num", "0");
		Map<String, Object> fgOrderCountMap = fgOrderDao.selectFgOrderViolationCount(paramsMap);
		if (null != fgOrderCountMap) {
			String violation_num = StringUtil.formatStr(fgOrderCountMap.get("violation_num"));
			if (StringUtils.isNotEmpty(violation_num)) {
				countMap.put("violation_num", violation_num);
			}
			countMap.put("total_num", fgOrderCountMap.get("total_num") + "");
		}
		result.setResultData(countMap);
	}

	@Override
	public void handleFgOrderRevoke(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "biz_remark" });
		String biz_remark = paramsMap.get("biz_remark") + "";
		//运营传参数操作人
		String operator = StringUtil.formatStr(paramsMap.get("operator"));
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_CLOSED)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		if (!((String) order.get("settle_status")).equals(Constant.ORDER_SETTLE_PENDING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单已结算,不能进行撤销操作");
		}
		// 将返给消费者的钱扣回来
		String member_id = order.get("member_id") + "";
		String member_type_key = order.get("member_type_key") + "";
		String order_no = order.get("order_no") + "";
		String total_fee = order.get("total_fee") + "";

		// 如果账户余额为负数，则加入黑名单
		String member_mobile = order.get("member_mobile") + "";
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "finance.memberAssetFind");
		bizParams.put("member_id", member_id);
		bizParams.put("member_type_key", member_type_key);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(OrderServiceImpl.FINANCE_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
		String cash_balance = dateMap.get("cash_balance") + "";

		// 如果会员余额为0，则自动加入黑名单
		if (MoneyUtil.moneySub(new BigDecimal(cash_balance), new BigDecimal(total_fee))
				.compareTo(BigDecimal.ZERO) < 0) {
			bizParams.clear();
			bizParams.put("account", member_mobile);
			bizParams.put("account_type", Constant.ACCOUNT_TYPE_MEITIANHUI);
			List<Map<String, Object>> fgBlacklist = fgBlacklistDao.selectFgBlacklist(bizParams);
			if (fgBlacklist.size() == 0) {
				FgBlacklist blacklist = new FgBlacklist();
				blacklist.setBlacklist_id(IDUtil.getUUID());
				// blacklist.setMobile(member_mobile);2017-8-9修改
				blacklist.setAccount(member_mobile);
				blacklist.setAccount_type("meitianhui");
				blacklist.setCreated_date(new Date());
				blacklist.setRemark("返款时自动添加");
				fgBlacklistDao.insertFgBlacklist(blacklist);
				biz_remark += "|会员余额不足，已加入黑名单";
			}
		}

		paramsMap.put("modified_date",
				DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", Constant.ORDER_CANCELLED);
		paramsMap.put("remark", paramsMap.get("biz_remark") + Constant.ORDER_REVOKED);
		paramsMap.put("biz_remark", biz_remark);
		int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		//判断佣金日志是否有进行过返佣,如果没有就不进行撤佣操作
		Map<String,Object> tempMap = new HashMap<>();
		tempMap.put("order_id", order_no);
		tempMap.put("member_id", member_id);
		List<Map<String, Object>> list = fgOrderDao.selectFgOrderCommissionList(tempMap);
		if (list != null && list.size() == 1) {
			Map<String, Object> commissionMap = list.get(0);
			if (commissionMap.get("commision_status") != null
					&& "normal".equals(commissionMap.get("commision_status") + "")) {
				FgOrderCommission fgOrderCommission = new FgOrderCommission();
				Date date = new Date();
				fgOrderCommission.setOrder_id(commissionMap.get("order_id")+"");
				fgOrderCommission.setMember_id(commissionMap.get("member_id")+"");
				// 查找出stores_id
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("consumer_id", StringUtil.formatStr(order.get("member_id")));
				paramMap.put("order_id", StringUtil.formatStr(order.get("order_id")));
				Map<String, Object> extra = fgOrderDao.selectFgOrderExtra(paramMap);
				fgOrderCommission.setStores_id(StringUtil.formatStr(extra.get("stores_id")));
				fgOrderCommission.setMember_phone(commissionMap.get("member_phone")+"");
				fgOrderCommission.setAmount(new BigDecimal("-" + commissionMap.get("amount")));
				fgOrderCommission.setCommision_status("revoke");
				fgOrderCommission.setCreated_time(date);
				fgOrderCommission.setModified_date((Date) commissionMap.get("created_time"));
				fgOrderCommission.setRemark("消费者退货,撤销返佣");
				int flag = fgOrderDao.insertFgOrderCommission(fgOrderCommission);
				if (flag == 0) {
					throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
				}
			}
		}
		
		Map<String, Object> out_trade_body = new HashMap<String, Object>();
		out_trade_body.put("order_no", order_no);
		out_trade_body.put("member_id", member_id);
		out_trade_body.put("member_type_key", member_type_key);
		String qty = StringUtil.formatStr(order.get("qty"));
		if (qty == null || qty == "" || qty == "null") {
			qty = "1";
		}
		String revokedAmount = MoneyUtil.moneyMul(total_fee, qty);
		orderService.orderRefund("SJLY_03", Constant.MEMBER_ID_MTH, member_id, "ZFFS_05", new BigDecimal(revokedAmount),
				"DDLX_07", order_no, "淘淘领撤销返款", out_trade_body);
		// 记录日志到表fg_order_log
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("log_id", UUID.randomUUID().toString());
		params.put("category", "撤销返款");
		params.put("order_no", order_no);
		params.put("tracked_date", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		params.put("event", operator + "|撤销返款金额:" + new BigDecimal(revokedAmount) + "|撤销返款会员类型:" + member_type_key
				+ "|会员ID:" + member_id);
		fgOrderDao.saveOperateLog(params);
	}



	@Override
	public void handleHgOrderRevoke(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "biz_remark" });
		String biz_remark = paramsMap.get("biz_remark") + "";
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_CLOSED)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		if (!((String) order.get("settle_status")).equals(Constant.ORDER_SETTLE_PENDING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单已结算,不能进行撤销操作");
		}
		// 将返给消费者的钱扣回来
		String member_id = order.get("member_id") + "";
		String member_type_key = order.get("member_type_key") + "";
		String order_no = order.get("order_no") + "";
		String total_fee = order.get("total_fee") + "";

		// 如果账户余额为负数，则加入黑名单
		String member_mobile = order.get("member_mobile") + "";
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "finance.memberAssetFind");
		bizParams.put("member_id", member_id);
		bizParams.put("member_type_key", member_type_key);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(OrderServiceImpl.FINANCE_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
		String cash_balance = dateMap.get("cash_balance") + "";

		// 如果会员余额为0，则自动加入黑名单
		if (MoneyUtil.moneySub(new BigDecimal(cash_balance), new BigDecimal(total_fee))
				.compareTo(BigDecimal.ZERO) < 0) {
			bizParams.clear();
			bizParams.put("account", member_mobile);
			bizParams.put("account_type", Constant.ACCOUNT_TYPE_MEITIANHUI);
			List<Map<String, Object>> fgBlacklist = fgBlacklistDao.selectFgBlacklist(bizParams);
			if (fgBlacklist.size() == 0) {
				FgBlacklist blacklist = new FgBlacklist();
				blacklist.setBlacklist_id(IDUtil.getUUID());
				// blacklist.setMobile(member_mobile);2017-8-9修改
				blacklist.setAccount(member_mobile);
				blacklist.setAccount_type("meitianhui");
				blacklist.setCreated_date(new Date());
				blacklist.setRemark("返款时自动添加");
				fgBlacklistDao.insertFgBlacklist(blacklist);
				biz_remark += "|会员余额不足，已加入黑名单";
			}
		}

		paramsMap.put("modified_date",
				DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", Constant.ORDER_CANCELLED);
		paramsMap.put("remark", paramsMap.get("biz_remark") + "");
		paramsMap.put("biz_remark", biz_remark);
		int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}

		Map<String, Object> out_trade_body = new HashMap<String, Object>();
		out_trade_body.put("order_no", order_no);
		out_trade_body.put("member_id", member_id);
		out_trade_body.put("member_type_key", member_type_key);



		orderService.orderRefund("SJLY_03", Constant.MEMBER_ID_MTH, member_id, "ZFFS_05", new BigDecimal(total_fee),
				"DDLX_21", order_no, "会过撤销返款", out_trade_body);
		/**
		 * 数据字典
		 * SJLY_03 运营管理系统
		 * ZFFS_05 零钱
		 * DDLX_21 会过订单
		 */
	}




	@Override
	public void storesRecomConsumerFreeGetListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
		paramsMap.put("order_date_start", DateUtil.getFormatDate(DateUtil.fmt_yyyyMM) + "-01");
		paramsMap.put("order_date_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
		List<Map<String, Object>> recomList = fgOrderDao.selectStoresRecomConsumerFreeGetCount(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> recomMap : recomList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", recomMap.get("consumer_id"));
			map.put("name", StringUtil.formatStr(recomMap.get("name")));
			map.put("contact", StringUtil.formatStr(recomMap.get("contact")));
			resultList.add(map);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("list", resultList);
		result.setResultData(resultMap);
	}

	@Override
	public void storesRecomConsumerFreeGetListFindForMemberList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
		List<Map<String, Object>> recomList = fgOrderDao.selectStoresRecomConsumerFreeGetCount(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> recomMap : recomList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", recomMap.get("consumer_id"));
			map.put("name", StringUtil.formatStr(recomMap.get("name")));
			map.put("contact", StringUtil.formatStr(recomMap.get("contact")));
			resultList.add(map);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("list", resultList);
		result.setResultData(resultMap);
	}

	@Override
	public void fgOrderSettlementCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
			List<String> list = StringUtil.str2List(status, ",");
			if (list.size() > 1) {
				paramsMap.remove("status");
				paramsMap.put("status_in", list);
			}
		}
		// 统计金额和数量
		Map<String, Object> settlementCountMap = fgOrderDao.selectFgOrderSettlementCount(paramsMap);
		Map<String, Object> countMap = new HashMap<String, Object>();
		countMap.put("total_fee", "0.00");
		countMap.put("count_num", "0");
		if (null != settlementCountMap) {
			if (null != settlementCountMap.get("total_fee")) {
				countMap.put("total_fee", settlementCountMap.get("total_fee"));
			}
			countMap.put("count_num", settlementCountMap.get("count_num") + "");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("count", countMap);
		result.setResultData(map);
	}

	@Override
	public void fgOrderSupplyCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "supplier_id" });
		List<String> tempList = fgOrderDao.selectFgOrderSupplyCount(paramsMap);
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		int a = 0, b = 0, c = 0, d = 0, e = 0;
		for (String str : tempList) {
			if (str.equals("confirmed")) {
				a++;
			} else if (str.equals("closed")) {
				b++;
			} else if (str.equals("processing")) {
				c++;
			} else if (str.equals("cancelled")) {
				d++;
			} else if (str.equals("received")) {
				e++;
			}
		}
		countMap.put("confirmed", a);
		countMap.put("closed", b);
		countMap.put("processing", c);
		countMap.put("cancelled", d);
		countMap.put("received", e);
		result.setResultData(countMap);
	}

	@Override
	public void handleFgOrderSettlement(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "operator", "order_date" });
		String operator = paramsMap.get("operator") + "";
		Map<String, List<Map<String, Object>>> settlementMap = new HashMap<String, List<Map<String, Object>>>();
		// 查询需要结算的订单列表
		paramsMap.put("settle_status", Constant.ORDER_SETTLE_PENDING);
		paramsMap.put("status", Constant.ORDER_CLOSED);
		List<Map<String, Object>> settlementList = fgOrderDao.selectFgOrderSettlement(paramsMap);

		// 商品出结算清单
		for (Map<String, Object> map : settlementList) {
			String supplier_id = map.get("item_supplier_id") + "";
			if (null != settlementMap.get(supplier_id)) {
				List<Map<String, Object>> orderList = settlementMap.get(supplier_id);
				orderList.add(map);
			} else {
				List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();
				orderList.add(map);
				settlementMap.put(supplier_id, orderList);
			}
		}
		int succ_count = 0;
		// 按供应商进行订单结算
		Map<String, Object> bizParams = new HashMap<String, Object>();
		for (String supplier_id : settlementMap.keySet()) {
			// 结算订单
			OdSettlement settlement = new OdSettlement();
			// 结算订单列表
			List<OdSettlementDetail> detailList = new ArrayList<OdSettlementDetail>();
			settlement.setSettlement_id(IDUtil.getTimestamp(7));
			Date date = new Date();
			String settlementAmount = "0"; // 结算金额
			// String orderAmount = "0"; //订单金额
			String supplier = "";
			Integer order_qty = 0;
			List<Map<String, Object>> tempList = settlementMap.get(supplier_id);
			// 生成结算清单
			for (Map<String, Object> tempMap : tempList) {
				// 以结算的金额为主
				String qty = tempMap.get("item_qty") + "";
				String total_fee = MoneyUtil.moneyMul(tempMap.get("item_settled_price") + "", qty);
				settlementAmount = MoneyUtil.moneyAdd(settlementAmount, total_fee); // 结算金额
				supplier = StringUtil.formatStr(tempMap.get("item_supplier"));
				OdSettlementDetail detail = new OdSettlementDetail();
				detail.setData_source(Constant.ORDER_TYPE_CASHBACK);
				detail.setDetail_id(IDUtil.getUUID());
				detail.setSettlement_id(settlement.getSettlement_id());
				detail.setOrder_id(tempMap.get("order_id") + "");
				detail.setOrder_no(tempMap.get("order_no") + "");
				detail.setGoods_id(tempMap.get("item_goods_id") + "");
				detail.setGoods_code(StringUtil.formatStr(tempMap.get("item_goods_code")));
				detail.setGoods_title(StringUtil.formatStr(tempMap.get("item_goods_title")));
				detail.setSupplier_id(supplier_id);
				detail.setSupplier(supplier);
				detail.setQty(Integer.parseInt(tempMap.get("item_qty") + ""));
				detail.setTotal_fee(new BigDecimal(total_fee));
				detail.setCreated_date(date);
				detail.setModified_date(date);
				detail.setStatus(Constant.ORDER_SETTLE_SETTLED);
				detailList.add(detail);
				bizParams.clear();
				bizParams.put("order_id", detail.getOrder_id());
				bizParams.put("settle_status", Constant.ORDER_SETTLE_SETTLED);
				bizParams.put("modified_date",
						DateUtil.date2Str((Date) tempMap.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
				int updateFlag = fgOrderDao.updateFgOrder(bizParams);
				if (updateFlag == 0) {
					throw new BusinessException(RspCode.ORDER_PROCESSING, "请刷新订单后再进行结算操作");
				}
				order_qty += 1;
			}
			settlement.setData_source(Constant.ORDER_TYPE_CASHBACK);
			// 订单金额
			settlement.setOrder_amount(new BigDecimal(settlementAmount));
			// 平台服务费
			settlement.setCommission_fee(new BigDecimal("0.00"));
			// 结算金额
			settlement.setAmount(new BigDecimal(settlementAmount));
			settlement.setOperator(operator);
			settlement.setSettled_date(DateUtil.str2Date(paramsMap.get("order_date") + "", DateUtil.fmt_yyyyMMdd));
			settlement.setSupplier_id(supplier_id);
			settlement.setSupplier(supplier);
			settlement.setCreated_date(date);
			settlement.setModified_date(date);
			settlement.setStatus(Constant.ORDER_SETTLE_SETTLED);
			// 计算成功数
			succ_count = succ_count + detailList.size();
			orderDao.insertOdSettlementDetailList(detailList);
			settlement.setOrder_qty(order_qty);
			orderDao.insertOdSettlement(settlement);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("fail_count", settlementList.size() - succ_count);
		resultMap.put("succ_count", succ_count);
		result.setResultData(resultMap);
	}

	@Override
	public void fgBlacklistCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "account", "account_type" });
		List<Map<String, Object>> orderItemList = fgBlacklistDao.selectFgBlacklist(paramsMap);
		if (orderItemList.size() > 0) {
			throw new BusinessException(RspCode.BLACKLIST_REQ_ERROR, "会员已经加入了黑名单");
		}
		FgBlacklist blacklist = new FgBlacklist();
		BeanConvertUtil.mapToBean(blacklist, paramsMap);
		blacklist.setBlacklist_id(IDUtil.getUUID());
		blacklist.setCreated_date(new Date());
		fgBlacklistDao.insertFgBlacklist(blacklist);
	}

	@Override
	public void fgBlacklistFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> orderItemList = fgBlacklistDao.selectFgBlacklist(paramsMap);
		for (Map<String, Object> map : orderItemList) {
			map.put("remark", StringUtil.formatStr(map.get("remark")));
			map.put("account", StringUtil.formatStr(map.get("account")));
			map.put("account_type", StringUtil.formatStr(map.get("account_type")));
			map.put("created_date", DateUtil.date2Str((Date) map.get("created_date"), DateUtil.fmt_yyyyMMddHHmmss));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", orderItemList);
		result.setResultData(map);
	}

	@Override
	public void fgBlacklistDelete(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "blacklist_id" });
		String blacklist_id = paramsMap.get("blacklist_id") + "";
		fgBlacklistDao.deleteFgBlacklist(blacklist_id);
	}

	@Override
	public void fgViolationCheck(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });

	}

	/**
	 * 解封见面礼
	 * 
	 * @param member_id
	 * @param member_type_key
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	private void gcActivityFaceGiftPay(final String member_id, final String member_type_key, final String order_no,
			final String order_date) throws BusinessException, SystemException, Exception {
		try {
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "gcActivity.platform.gcActivityFaceGiftPay");
			bizParams.put("member_id", member_id);
			bizParams.put("member_type_key", member_type_key);
			bizParams.put("order_no", order_no);
			bizParams.put("order_date", order_date);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(OrderServiceImpl.GOODS_SERVICE_URL, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
		} catch (Exception e) {
			logger.error("解封见面礼异常", e);
		}
	}
	
	/**
	 * 查询订单操作日志列表
	 */
	@Override
	public void queryOperateLogList(Map<String, Object> paramsMap, ResultData result) 
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> orderLogList = fgOrderDao.queryOperateLogList(paramsMap);
		for (Map<String, Object> map : orderLogList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("category", map.get("category"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("tracked_date",DateUtil.date2Str((Date) map.get("tracked_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("event", map.get("event"));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void fgOrderCommissionList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> fgOrderCommissionList = fgOrderDao.selectFgOrderCommissionList(paramsMap);
		Map<String, Object> map = new HashMap<>();
		map.put("list", fgOrderCommissionList);
		result.setResultData(map);
	}

	@Override
	public void fgOrderCommissionToAmountMemberTotal(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
		// 查询当月待转佣金
		BigDecimal weekAmount = null;
		// 查询会员总数
		List<Map<String, Object>> memberCount = null;
		BigDecimal total = null;
		try {
			weekAmount = fgOrderDao.selectFgOrderCommissionWeekAmount(paramsMap);
			memberCount = fgOrderDao.selectFgOrderCommissionMemberCount(paramsMap);
			// 查询总佣金
			total = fgOrderDao.selectFgOrderCommissionTotal(paramsMap);
		} catch (Exception e) {
			logger.error("查询佣金,会员,总佣金异常", e);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("member", memberCount);
		map.put("memberCount", memberCount.size());
		map.put("weekAmount", weekAmount);
		map.put("totalAmount", total);
		result.setResultData(map);
	}

	@Override
	public void fgOrderCommissionWeekAmountLog(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> LogList = fgOrderDao.selectFgOrderCommissionWeekAmountLog(paramsMap);
		Map<String, Object> map = new HashMap<>();
		map.put("list", LogList);
		result.setResultData(map);
	}
	
	@Override
	public void newOwnOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile",
							"order_type", "data_source", "delivery_address", 
							"contact_person", "contact_tel" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String orderType = StringUtil.formatStr(paramsMap.get("order_type"));
			String dataSource = StringUtil.formatStr(paramsMap.get("data_source"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String contact_person = StringUtil.formatStr(paramsMap.get("contact_person"));
			String contact_tel = StringUtil.formatStr(paramsMap.get("contact_tel"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
			String sku_id = (String)paramsMap.get("sku_id");
			Boolean flags = (Boolean)paramsMap.get("flags");
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			
			if(payment_way_key == null || payment_way_key == "" || payment_way_key == "null" ){
				payment_way_key = "ZFFS_01";
			}
			
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<>();
			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}
			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type="";
			if(orderType.equals("taobao")){
				order_type = Constant.ORDER_TYPE_TAOBAO;
			}else if(orderType.equals("meitianhui")){
				order_type = Constant.ORDER_TYPE_MEITIANHUI;
			}else if(orderType.equals("huiguo")){
				order_type = Constant.ORDER_TYPE_HUIGUO;
			}
			
			

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();
			if(StringUtil.isNotBlank(sku_id)){
				Set<String> sku_ids = new HashSet<>();
				sku_ids.add(sku_id);
				Map<String, Object> resultMap = findPsGoodsSkuIdBySkuIds(sku_ids);
				List<Map<String, Object>> valueList = (List<Map<String, Object>>)resultMap.get("data");
				Map<String, Object> valueMap = valueList.get(0);
				sub_amount = valueMap.get("total_sales")+"";
				market_price = valueMap.get("market_price")+"";
				discount_price = valueMap.get("discount_price")+"";
			}
			// 扣库存
			orderService.newPsGoodsSaleQtyDeduction(goods_id, qty,sku_id);

			freeGetGoodsTaobaoSalesEdit(goods_id);
			
			try {
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(Integer.valueOf(qty));
				order.setFlags(flags);
				order.setPayment_way_key(payment_way_key);
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(contact_tel);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source(dataSource);
				order.setDelivery_address(delivery_address);
				order.setRemark(remark);
				order.setQty(qty);
				order.setSelf_label(StringUtil.formatStr(goodsDataMap.get("self_label")));
				if (goodsDataMap.get("commission") == null) {
					order.setOrder_commision(new BigDecimal("0"));
				}else{
					order.setOrder_commision((BigDecimal) goodsDataMap.get("commission"));
				}
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(Integer.valueOf(qty));
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				orderItem.setSku_id(sku_id);
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);
				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				orderService.newPsGoodsSaleQtyRestore(goods_id, qty,sku_id);
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

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
	public void fgOrderListByNewOwnFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<String> doc_ids = new ArrayList<>();
		Set<String> sku_ids = new HashSet<>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderListByNewOwn(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_code", map.get("item_goods_code") + "");
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			String sku_id = StringUtil.formatStr(map.get("sku_id"));
			if(StringUtil.isNotBlank(sku_id)){
				sku_ids.add(sku_id);
				tempMap.put("sku_id", sku_id);
			}
			resultList.add(tempMap);
		}
		if (sku_ids != null && sku_ids.size() > 0) {
			Map<String, Object> resultMap = findPsGoodsSkuIdBySkuIds(sku_ids);
			List<Map<String, Object>> valueList = (List<Map<String, Object>>)resultMap.get("data");
			for (Map<String, Object> rMap : resultList) {
				for (Map<String, Object> vMap : valueList) {
					String rSku_id = (String)rMap.get("sku_id");
					String vSku_id = (String)vMap.get("sku_id");
					if (StringUtil.isNotBlank(rSku_id)&&rSku_id.equals(vSku_id)) {
						rMap.put("total_fee", vMap.get("total_sales"));
						rMap.put("sale_fee", vMap.get("market_price"));
						rMap.put("attr_zvalue", vMap.get("attr_zvalue"));
						rMap.put("prop_zname", vMap.get("prop_zname"));
						rMap.put("attr_fvalue", vMap.get("attr_fvalue"));
						rMap.put("prop_fname", vMap.get("prop_fname"));
					}
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}


	/**
	 * 获得sku表的数据
	 */
	private Map<String, Object> findPsGoodsSkuIdBySkuIds(Set<String> sku_ids)
			throws Exception, SystemException, BusinessException {
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "goods.selectPsGoodsSkuidBySkuId");
		bizParams.put("sku_ids", sku_ids);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		return resultMap;
	}


	@Override
	public void rcOrderCreate(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "transaction_no", "amount", "payment_way_key",
				"member_type_key", "member_id", "remark", "data_source", "trade_type_key" });
		try {
			RcOrder rcOrder = null;
			Date date = new Date();
			String payment_way_key = (String)paramsMap.get("payment_way_key");
			if("ZFFS_01".equals(payment_way_key) ||"ZFFS_02".equals(payment_way_key) ||"ZFFS_10".equals(payment_way_key)){
				// 创建充值订单
				rcOrder = new RcOrder();
				rcOrder.setOrder_no(IDUtil.getUUID());
				rcOrder.setTransaction_no((String) paramsMap.get("transaction_no"));
				rcOrder.setOrder_date(date);
				rcOrder.setAmount(new BigDecimal(paramsMap.get("amount")+""));
				rcOrder.setReward_point(rcOrder.getAmount().intValue());
				rcOrder.setReward_shell(rcOrder.getAmount().intValue() * 2);
				rcOrder.setPayment_way_key((String) paramsMap.get("payment_way_key"));
				rcOrder.setMember_type_key((String) paramsMap.get("member_type_key"));
				rcOrder.setMember_id((String) paramsMap.get("member_id"));
				rcOrder.setMember_info((String) paramsMap.get("member_info"));
				rcOrder.setStatus(Constant.ORDER_NONPAID);
				rcOrder.setCreated_date(date);
				rcOrder.setModified_date(date);
				rcOrder.setRemark((String) paramsMap.get("remark"));
				rcOrder.setData_source((String) paramsMap.get("data_source"));
				rcOrder.setTrade_type_key((String) paramsMap.get("trade_type_key"));
				Integer orderInt = fgOrderDao.rcOrderCreate(rcOrder);
				if(orderInt == 0){
					throw new BusinessException("充值订单接口错误", "生成充值订单异常");
				}
			}
			Map<String,Object> resultMap = new  HashMap<>();
			// 创建充值订单日志
			RcOrderTardeLog rcOrderTardeLog = new RcOrderTardeLog();
			Map<String,Object> temp = new HashMap<>();
			temp.put("trade_no", paramsMap.get("out_trade_no"));
			List<RcOrderTardeLog> tardeLog = fgOrderDao.selectRcOrderTardeLog(temp);
			if(tardeLog == null || tardeLog.isEmpty()){
				rcOrderTardeLog.setOrder_no(rcOrder.getOrder_no());
			}else{
				rcOrderTardeLog.setOrder_no(tardeLog.get(0).getOrder_no());
			}
			rcOrderTardeLog.setAmount(new BigDecimal((String) paramsMap.get("amount")));
			rcOrderTardeLog.setTrade_no((String) paramsMap.get("out_trade_no"));
			rcOrderTardeLog.setPayment_way_key((String) paramsMap.get("payment_way_key"));
			rcOrderTardeLog.setStatus(Constant.ORDER_NONPAID);
			rcOrderTardeLog.setCreated_date(date);
			rcOrderTardeLog.setModified_date(date);
			rcOrderTardeLog.setRemark((String) paramsMap.get("remark"));
			rcOrderTardeLog.setData_source((String) paramsMap.get("data_source"));
			String trade_type_key = (String) paramsMap.get("trade_type_key");
			rcOrderTardeLog.setTrade_type_key(trade_type_key);
			Integer tardeLogCreateIntCash = fgOrderDao.rcOrderTardeLogCreate(rcOrderTardeLog);
			if(tardeLogCreateIntCash == 0){
				throw new BusinessException("充值订单接口错误", "生成充值订单日志异常");
			}
			
			
			result.setResultData(resultMap);
			
		} catch (BusinessException e) {
			throw new BusinessException("充值订单接口错误", "生成充值订单接口异常");
		}
	}


	@Override
	public void updateRcOrder(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "transaction_no", "payment_way_key", "member_id", "out_trade_no", "type"});
		String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
		String out_trade_no = (String) paramsMap.get("out_trade_no");
		String payment_way_key = (String) paramsMap.get("payment_way_key");
		if ("ZFFS_01".equals(payment_way_key) || "ZFFS_02".equals(payment_way_key) || "ZFFS_10".equals(payment_way_key)) {
			// 16:26 2019/5/7 欧少辉
			// 如果是体验会员只需要做绑定关系不用分成
			String type = StringUtil.formatStr(paramsMap.get("type"));
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			if("5".equals(type)){
				Map<String, String> reqParams = Maps.newHashMap();
				Map<String, String>	bizParams = Maps.newHashMap();
				reqParams.put("service", "member.insertOrUpdateMemberDistribution");
				bizParams.put("member_id", member_id);
				bizParams.put("type", "5");
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				HttpClientUtil.post(member_service_url, reqParams);
			}else {
				//调用会员分销逻辑,写入分销表
				Map<String, String> param = new HashMap<>();
				Map<String, String> map = new HashMap<>();
				param.put("service", "consumer.consumerBuildingMemberDistrbution");
				map.put("member_id", StringUtil.formatStr(paramsMap.get("member_id")));
				param.put("params", FastJsonUtil.toJson(map));
				HttpClientUtil.post(member_service_url, param);
			}
			paramsMap.put("status", "paid");// 已支付
			Integer i = fgOrderDao.updateRcOrderToStatus(paramsMap);
			if (i == 0) {
				throw new BusinessException("充值订单接口错误", "修改订单状态失败");
			}
			String amout_year = PropertiesConfigUtil.getProperty("member_recharge_amout_year");
			RcOrder rcOrder = fgOrderDao.selectRcOrder(paramsMap);
			BigDecimal amount = rcOrder.getAmount();
//			paramsMap.put("recharge_type", "year");
//			if (amount.compareTo(new BigDecimal(amout_year)) == 0) {
//				paramsMap.put("recharge_type", "year");
//			} else {
//				throw new BusinessException("金额错误", "金额错误");
//			}
			consumerVipTimeUpdate(paramsMap);
		}
		paramsMap.clear();
		paramsMap.put("trade_no", out_trade_no);
		paramsMap.put("status", "paid");
		Integer res = fgOrderDao.updateRcOrderTradeLogToStatus(paramsMap);
		if (res == 0) {
			throw new BusinessException("充值订单接口错误", "修改订单日志状态失败");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("message_type", "order");
		params.put("message_category", "vip_order");//下单
		params.put("order_no", out_trade_no);
		params.put("member_id", member_id);
		sendMessage(params);
	}

	//充值时调用的方法，其它方法没用
	private void consumerVipTimeUpdate(Map<String, Object> paramsMap)
			throws SystemException, Exception, BusinessException {
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "consumer.mdConsumerVipTimeUpdate");
		bizParams.put("consumer_id", paramsMap.get("member_id"));
		bizParams.put("recharge_type", paramsMap.get("recharge_type"));
		bizParams.put("type", paramsMap.get("type"));
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		logger.info("延续会员时长返回信息->" + resultStr);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException("延续会员时长失败", "延续会员时长失败");
		}
		Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
		String is_New = StringUtil.formatStr(data.get("is_New"));
		String type = StringUtil.formatStr(data.get("type"));
		//同时传入到finance  里面增加 增加记录。
		String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
		reqParams.clear();
		bizParams.clear();
		
		reqParams.put("service", "finance.consumer.editMemberCoupon");
		bizParams.put("member_id", paramsMap.get("member_id"));
		bizParams.put("recharge_type", paramsMap.get("recharge_type"));
		bizParams.put("is_New", is_New);
		bizParams.put("type", type);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String result = HttpClientUtil.post(finance_service_url, reqParams);
		
	}
	
	@Override
	public void rcOrderPageListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		logger.debug("开始查询消费者充值信息====>  member_id = " + paramsMap.get("member_id"));
		ValidateUtil.validateParams(paramsMap, new String[] {"member_id"});
		paramsMap.put("status", "paid");
		List<RcOrder> rcOrderList =  fgOrderDao.selectRcOrderList(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<>();
		for (RcOrder rcOrder : rcOrderList) {
			Map<String,Object> temp = new HashMap<>();
			temp.put("amount", rcOrder.getAmount());
			temp.put("payment_way_key", rcOrder.getPayment_way_key());
			temp.put("created_date", DateUtil.date2Str(rcOrder.getCreated_date(), DateUtil.fmt_yyyyMMddHHmmss));
			resultList.add(temp);
		}
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("list", resultList);
		result.setResultData(resultData);
		logger.debug("结束查询消费者充值信息====>  member_id = " + paramsMap.get("member_id"));
	}


	@Override
	public void beikeMallOrderCreate(Map<String, Object> paramsMap, ResultData result) 
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		Map<String, Object> goodsDataMap = new HashMap<>();
		BeikeMallOrder order = new BeikeMallOrder();
		//String consumer_id = null;
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_code", "consumer_id", "mobile",
					"delivery_address", "data_source" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String data_source = paramsMap.get("data_source") + "";
			String mobile = paramsMap.get("mobile") + "";
			String coupons_id_my = StringUtil.formatStr(paramsMap.get("coupons_id_my"));
			String actual_price = StringUtil.formatStr(paramsMap.get("actual_price"));
			String coupons_id_lq = StringUtil.formatStr(paramsMap.get("coupons_id_lq"));
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			String sku_id = (String) paramsMap.get("sku_id");
			if (StringUtil.isBlank(qty)) {
				qty = "1";
			}
			// 手机号商品加锁
			lockKey = "[beikeMallOrderCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000); 
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<>();
			// 获取商品信息
			Map<String, Object> goodsData = beikeMallGoodsFindForOrder(sku_id, goods_code);
			goodsDataMap =(Map<String, Object>) goodsData.get("beikeMallGoods");
			Map<String, Object> goodsSkuid =(Map<String, Object>) goodsData.get("goodsSkuidList");
			String sale_price = goodsSkuid.get("sale_price") + "";
			String vip_price = goodsSkuid.get("vip_price") + "";
			String beike_credit = goodsSkuid.get("beike_credit") + "";
			
			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}
			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 扣库存
			orderService.beikeMallGoodsSaleQtyDeduction(goods_id, qty, sku_id);
			
			//查询消费者是否是会员
			Map<String, Object> consumerDataMap = consumerVipTime(consumer_id);
//			Long vip_end_time = (Long)consumerDataMap.get("vip_end_time");
//			Date nowDate = DateUtil.str2Date(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd);
			Boolean falg;
//			if (vip_end_time == null || vip_end_time - nowDate.getTime() < 0) {
//				throw new BusinessException("创建订单失败", "会员时间已到期");
//			}else {
				falg = true;
				// 查询会员账户余额
				Map<String, Object> consumerFdMemberAsset = consumerFdMemberAsset(consumer_id);
				BigDecimal shell = new BigDecimal(consumerFdMemberAsset.get("shell") + "");
				String moneyMul = MoneyUtil.moneyMul(beike_credit, qty);
				if (shell.compareTo(new BigDecimal(moneyMul)) == -1) {
					throw new BusinessException("创建订单失败", "账户贝壳余额不足");
				}
//			}
			List<Map<String, Object>> list = new ArrayList<>();
			String coupons_id = "";
			if (StringUtil.isNotBlank(coupons_id_my) && StringUtil.isNotBlank(coupons_id_lq)) {
				coupons_id = coupons_id_my + "," + coupons_id_lq;
			} else {
				if (StringUtil.isNotBlank(coupons_id_my)) {
					coupons_id = coupons_id_my;
				} else if (StringUtil.isNotBlank(coupons_id_lq)) {
					coupons_id = coupons_id_lq;
				}
			}
			if(StringUtil.isNotBlank(coupons_id)){
				Map<String, Object> resultMap = updateGiftCoupon(consumer_id, coupons_id,"1");
				list = (List<Map<String, Object>>) resultMap.get("data");
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			try {
				Date date = new Date();
				// 进行订单创建
				order = new BeikeMallOrder();
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(Integer.valueOf(qty));
				order.setVip_fee(new BigDecimal(vip_price));
				order.setBeike_credit(Integer.valueOf(beike_credit));
				order.setSale_fee(new BigDecimal(sale_price));
				order.setDelivery_address(delivery_address);
				order.setDelivery_fee(new BigDecimal(goodsDataMap.get("shipping_fee") + ""));
				order.setContact_tel(mobile);
				order.setContact_person(nick_name);
				order.setActual_price(new BigDecimal(actual_price));
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(BeikeConstant.ORDER_STATUS_01);
				order.setOrder_date(date);
				order.setRemark(remark);
				order.setOrder_date(date);
				order.setData_source(data_source);
				beikeMallOrderDao.insertBeikeMallOrder(order);
				// 创建贝壳商城补充订单
				BeikeMallOrderItem orderItem = new BeikeMallOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(Integer.valueOf(qty));
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(sale_price));
				orderItem.setVip_price(new BigDecimal(vip_price));
				orderItem.setService_price(new BigDecimal(goodsDataMap.get("shipping_fee") + ""));
				orderItem.setBeike_amount(Integer.valueOf(beike_credit));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				orderItem.setSku_id(sku_id);
				beikeMallOrderDao.insertBeikeMallOrderItem(orderItem);
				// 创建贝壳商城订单交易日志
				BeikeMallOrderTardeLog orderTardeLog = new BeikeMallOrderTardeLog();
				orderTardeLog.setOrder_no(order.getOrder_no());
				orderTardeLog.setStatus(Constant.ORDER_NONPAID);
				orderTardeLog.setTrade_type_key("JYLX_01");// 交易类型
				orderTardeLog.setData_source("SJLY_01");// 数据来源
				orderTardeLog.setModified_date(date);
				orderTardeLog.setCreated_date(date);
				orderTradeLogDao.insertBeikeMallOrderTradeLog(orderTardeLog);
				// 创建订单礼券关系表
				if (list != null && list.size()>0) {
					List<BeikeMallOrderCoupons> couponsList = new ArrayList<>();
					for (Map<String, Object> map : list) { 
						BeikeMallOrderCoupons orderCoupons = new BeikeMallOrderCoupons();
						orderCoupons.setCoupons_key(map.get("coupons_key") + "");
						orderCoupons.setCoupons_name(map.get("coupons_name") + "");
						orderCoupons.setCoupons_amount(Integer.parseInt(map.get("coupons_amount") + ""));
						orderCoupons.setCoupons_subtract(Integer.parseInt(map.get("coupons_subtract") + ""));
						orderCoupons.setCoupons_id(map.get("member_coupons_id") + "");
						orderCoupons.setOrder_no(order_no);
						orderCoupons.setCoupons_validity(DateUtil.parseToDate(map.get("coupons_validity") + ""));
						orderCoupons.setCreated_date(date);
						couponsList.add(orderCoupons);
					}
					beikeMallOrderCouponsDao.insertBeikeMallOrderCoupons(couponsList);
				}
				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_no", order.getOrder_no());
				tempMap.put("member_status", falg);
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存 
				qty = "-"+qty;
				orderService.beikeMallGoodsSaleQtyDeduction(goods_id, qty, sku_id);
				if (StringUtil.isNotBlank(coupons_id)) {
					updateGiftCoupon(consumer_id, coupons_id, "-1");
				}
				throw e;
			}
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

	private Map<String, Object> updateGiftCoupon(String consumer_id, String coupons_id,String coupons_mun)
			throws Exception, SystemException, BusinessException {
		//查询会员优惠券
		Map<String, Object> bizParams = new HashMap<>();
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "finance.consumer.updateGiftCoupon");
		bizParams.put("coupons_id", coupons_id);
		bizParams.put("member_id", consumer_id);
		bizParams.put("coupons_mun", coupons_mun);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(FINANCE_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		return resultMap;
	}


	/**
	 * 查询消费者会员是否到期
	 */
	private Map<String, Object> consumerVipTime(String consumer_id)
			throws Exception, SystemException, BusinessException {
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "consumer.consumer.consumerVipTime");
		bizParams.put("consumer_id", consumer_id);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(MEMBER_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> consumerDataMap = (Map<String, Object>) resultMap.get("data");
		return consumerDataMap;
	}
	
	/**
	 * 查询消费者账户余额
	 */
	private Map<String, Object> consumerFdMemberAsset(String consumer_id)
			throws Exception, SystemException, BusinessException {
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "consumer.consumerFdMemberAsset");
		bizParams.put("member_id", consumer_id);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(SHELL_PROPERTY_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> consumerDataMap = (Map<String, Object>) resultMap.get("data");
		return consumerDataMap;
	}

	/**
	 * 获取商品信息
	 */
	private Map<String, Object> beikeMallGoodsFindForOrder(String sku_id, String goods_code)
			throws BusinessException, SystemException, Exception {
		// 组装查询参数
		Map<String, Object> bizParams = new HashMap<String, Object>();
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("service", "goods.beikeMallGoodsFindForOrder");
		bizParams.put("goods_code", goods_code);
		bizParams.put("sku_id", sku_id);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> goodsDataMap = (Map<String, Object>) resultMap.get("data");
		return goodsDataMap;
	}
	
	
	/**
	 * 获取礼包商品信息
	 */
	private Map<String, Object> hongBaoGoodsFindForOrder(String goods_code)
			throws BusinessException, SystemException, Exception {
		// 组装查询参数
		Map<String, Object> bizParams = new HashMap<String, Object>();
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("service", "goods.hongBaoGoodsFindForOrder");
		bizParams.put("goods_code", goods_code);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> goodsDataMap = (Map<String, Object>) resultMap.get("data");
		return goodsDataMap;
	}
	
	
	@Override
	public void beikeMallOrderListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		String status = StringUtil.formatStr(paramsMap.get("status"));
		List<String> status_in = new ArrayList<>();
		if(StringUtil.isEmpty(status)){
			status_in.add(BeikeConstant.ORDER_STATUS_01);
			status_in.add(BeikeConstant.ORDER_STATUS_02);
			status_in.add(BeikeConstant.ORDER_STATUS_03);
			status_in.add(BeikeConstant.ORDER_STATUS_04);
			paramsMap.put("status_in", status_in);
		} else if (status.equalsIgnoreCase(BeikeConstant.ORDER_STATUS_02)) {
			status_in.add(BeikeConstant.ORDER_STATUS_02);
			status_in.add(BeikeConstant.ORDER_STATUS_03);
			paramsMap.put("status_in", status_in);
			paramsMap.remove("status");
		}
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<String> doc_ids = new ArrayList<>();
		Set<String> sku_ids = new HashSet<>();
		List<Map<String, Object>> fgOrderDetailList = beikeMallOrderDao.selectBeikeMallOrderList(paramsMap);
		if(fgOrderDetailList.isEmpty()||fgOrderDetailList.size()==0){
			//如果没查到数据 直接返回空集合
			Map<String, Object> map = new HashMap<>();
			map.put("list", resultList);
			map.put("doc_url", map);
			result.setResultData(map);
			return;
		}
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("actual_price", map.get("actual_price"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_fee", map.get("delivery_fee") + "");
			tempMap.put("beike_credit", map.get("beike_credit") + "");
			tempMap.put("vip_fee", map.get("vip_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("status", StringUtil.formatStr(map.get("status")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("settle_status", map.get("settle_status"));
			tempMap.put("item_goods_code", map.get("item_goods_code") + "");
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_num", StringUtil.formatStr(map.get("item_num")));
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String sku_id = StringUtil.formatStr(map.get("sku_id"));
			if (StringUtil.isNotBlank(sku_id)) {
				sku_ids.add(sku_id);
				tempMap.put("sku_id", sku_id);
			}
			resultList.add(tempMap);
		}
		Map<String, Object> resultMap = findPsGoodsSkuIdBySkuIds(sku_ids);
		List<Map<String, Object>> valueList = (List<Map<String, Object>>) resultMap.get("data");
		for (Map<String, Object> rMap : resultList) {
			for (Map<String, Object> vMap : valueList) {
				String rSku_id = (String) rMap.get("sku_id");
				String vSku_id = (String) vMap.get("sku_id");
				if (StringUtil.isNotBlank(rSku_id) && rSku_id.equals(vSku_id)) {
					rMap.put("attr_zvalue", vMap.get("attr_zvalue"));
					rMap.put("prop_zname", vMap.get("prop_zname"));
					rMap.put("attr_fvalue", vMap.get("attr_fvalue"));
					rMap.put("prop_fname", vMap.get("prop_fname"));
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	@Override
	public void beikeMallOrderDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] {"order_id","order_no"}, 1);
		Map<String, Object> map = new HashMap<>();
		List<Map<String,Object>> OrderList = beikeMallOrderDao.selectBeikeMallOrderList(paramsMap);
		if (OrderList.isEmpty() || OrderList.size() == 0) {
			throw new BusinessException("未查到订单", "未查到订单,请稍后重试");
		}
		map.put("sale_fee", StringUtil.formatStr(OrderList.get(0).get("sale_fee")));
		map.put("delivery_fee", StringUtil.formatStr(OrderList.get(0).get("delivery_fee")));
		map.put("beike_credit", StringUtil.formatStr(OrderList.get(0).get("beike_credit")));
		map.put("vip_fee", StringUtil.formatStr(OrderList.get(0).get("vip_fee")));
		map.put("qty", StringUtil.formatStr(OrderList.get(0).get("item_num")));
		map.put("payment_way_key", StringUtil.formatStr(OrderList.get(0).get("payment_way_key")));
		OrderList.clear();
		result.setResultData(map);
	}


	@Override
	public void beikeMallOrderUpdate(Map<String, Object> paramsMap, ResultData result) 
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_no"});		 
		paramsMap.put("status", BeikeConstant.ORDER_STATUS_02);
		Integer mun =  beikeMallOrderDao.beikeMallOrderUpdate(paramsMap);
		paramsMap.put("status", Constant.ORDER_PAID);
		Integer i =  orderTradeLogDao.beikeMallOrderTradeLogUpdate(paramsMap);
		if (mun != 1 || i != 1) {
			throw new BusinessException("未查到订单", "未查到订单,修改订单状态失败");
		}

		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("order_no", paramsMap.get("order_no"));
		List<Map<String, Object>> orderList = beikeMallOrderDao.selectBeikeMallOrderList(tempMap);
		if (orderList.isEmpty() || orderList.size() == 0) {
			throw new BusinessException("未查到订单", "未查到订单,请稍后重试");
		}
		Map<String, Object> orderMap = orderList.get(0);
		//增加实际销量
		String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
		Map<String, String> reqPar = new HashMap<>();
		Map<String, String> par = new HashMap<>();
		reqPar.put("service", "goods.beikeMallGoodsSalesVolumeUpdate");
		par.put("goods_id", StringUtil.formatStr(orderMap.get("item_goods_id")));
		par.put("item_num", StringUtil.formatStr(orderMap.get("item_num")));
		reqPar.put("params", FastJsonUtil.toJson(par));
		HttpClientUtil.post(goods_service_url, reqPar);
		
		//查询订单金额
		String  sale_fee =StringUtil.formatStr(orderList.get(0).get("sale_fee"));
		String  vipFee =StringUtil.formatStr(orderList.get(0).get("vip_fee"));
		String  orderMoney;
		if(StringUtils.equals(sale_fee, "0.00")) {
			orderMoney  =vipFee;  
		}else {
			orderMoney = sale_fee;
		}
		
		BigDecimal vip_fee =(BigDecimal) orderMap.get("vip_fee");
		String member_id = (String)orderMap.get("member_id");
		if (vip_fee.compareTo(BigDecimal.ZERO) == 1) {
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, String> reqParams = new HashMap<>();
			Map<String, String> paramMap = new HashMap<>();
			reqParams.put("service", "member.consumerInfoUpdate");
			paramMap.put("member_id", member_id);
			paramMap.put("growth_value", vip_fee+"");
			reqParams.put("params", FastJsonUtil.toJson(paramMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
		}

		// 通过订单号，进行查询贝壳抵抗数量
		Map<String,Object>  memberBeikeMap  =  beikeMallOrderDao.getBeikeCredit(paramsMap.get("order_no").toString());
		//查询会员是否过期，会员贝壳数是否够，再减去相应的贝壳数
		logger.info("贝壳商城会员贝壳通知->" + memberBeikeMap.get("memberId").toString());
		
		//查询订单数量
		tempMap.clear();
		tempMap.put("member_id", memberBeikeMap.get("memberId").toString());
		Integer   orderCount =  beikeMallOrderDao.getMallOrderNum(tempMap);
		
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, String> params = new HashMap<>();
		params.put("consumer_id", memberBeikeMap.get("memberId").toString());
		if(orderCount ==1) {
			params.put("orderCount","1");
			params.put("orderMoney", orderMoney);
		}
		reqParams.put("service", "consumer.consumer.consumerVipTime");
		reqParams.put("params", FastJsonUtil.toJson(params));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException("未查到会员信息", "未查到会员结束时间");
		}
		//得到会员结束时间，跟现在时间进行比较
		Map<String, Object> consumerMap = (Map<String, Object>) resultMap.get("data");
		logger.info("得到会员数据："+FastJsonUtil.toJson(consumerMap).toString());
		Long vipEndTime = (Long) consumerMap.get("vip_end_time");
		/*if(null != vipEndTime ) {
			//截取字符串  
			String  vipEndLong =  String.valueOf(vipEndTime); 
			Date vipEndDate = DateUtil.parseToDate(DateUtil.TimeStamp2Date(vipEndLong.substring(0, vipEndLong.length()-3), "yyyy-MM-dd HH:mm:ss"));
			Date  newDate  = DateUtil.parseToDate(DateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss"));
			if(vipEndDate.before(newDate)) {
				throw new BusinessException("会员已经过期", "贝壳商城订单的会员已经过期");
			}
		}*/
		Map<String,Object> param = new HashMap<>();
		param.put("message_type", "order");
		param.put("message_category", "place_order");//下单
		param.put("goods_title", orderMap.get("item_goods_title"));
		Object pic_info = FastJsonUtil.jsonToList(StringUtil.formatStr(orderMap.get("item_goods_pic_info"))).get(0);
		param.put("message_pic",pic_info); 
		param.put("order_no", orderMap.get("order_no"));
		param.put("member_id", orderMap.get("member_id"));
		sendMessage(param);
		result.setResultData(memberBeikeMap);
	}
	
	@Override
	public void handleBeikeMallOrderCancelled(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_id"});
		List<Map<String, Object>> orderList = beikeMallOrderDao.selectBeikeMallOrderList(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(BeikeConstant.ORDER_STATUS_01)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		paramsMap.put("status", BeikeConstant.ORDER_STATUS_06);
		int updateFlag = beikeMallOrderDao.beikeMallOrderUpdate(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		// 还原库存
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("goods_id", order.get("item_goods_id"));
		tempMap.put("goods_code", order.get("item_goods_code"));
		tempMap.put("qty", order.get("item_num"));
		tempMap.put("sku_id", order.get("sku_id"));
		orderService.psGoodsSaleQtyForOwnRestore(tempMap);
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("order_no", order.get("order_no"));
		Map<String, Object> orderCouponMap = beikeMallOrderCouponsDao.findOrderCoupons(paramMap);
		String coupons_key = null;
		String member_coupons_id = null;
		if(null !=orderCouponMap ) {
			coupons_key=StringUtil.formatStr(orderCouponMap.get("coupons_key")) ;
			member_coupons_id=StringUtil.formatStr(orderCouponMap.get("coupons_id")) ;
		}
		if(StringUtils.isNotBlank(coupons_key)) {
			//把免邮券或者优惠券 给退回做update
			String url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.updateMemberGift");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", order.get("member_id") + "");
			params.put("coupons_key",coupons_key);
			params.put("member_coupons_id", member_coupons_id);
			requestData.put("params", FastJsonUtil.toJson(params));
			String resultStr = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"),
						(String) resultMap.get("error_msg"));
			}
			paramMap.clear();
			paramMap.put("order_no", order.get("order_no") + "");
			beikeMallOrderCouponsDao.delOrderCoupons(paramMap);
		}
		
		// 如果是消费者取消,退回扣除的金币
		if (((String) order.get("member_type_key")).equals(Constant.MEMBER_TYPE_CONSUMER)) {
			Long beike_credit =(Long) order.get("beike_credit");
			Long item_num =(Long) order.get("item_num");
			// 退还扣掉的贝壳
			if (beike_credit > 0) {
				// 交易的订单类型(贝壳商城订单)
				String order_type_key = "DDLX_11";
				String member_id = order.get("member_id") + "";
				String order_no = order.get("order_no") + "";
				Map<String, Object> out_trade_body = new HashMap<>();
				out_trade_body.put("order_no", order_no);
				BigDecimal beike = new BigDecimal(beike_credit*item_num);
				orderService.orderRefund("SJLY_01", member_id, Constant.MEMBER_ID_MTH, "ZFFS_07",
						beike, order_type_key, order_no, "贝壳商城退款", out_trade_body);
			}
		}
	}


	@Override
	public void beikeMallOrderForComment(Map<String, Object> paramsMap, ResultData result) throws  Exception {
		ValidateUtil.validateParams(paramsMap, new String[] {"order_id"});
		int updateFlag = beikeMallOrderDao.beikeMallOrderUpdate(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
	}
	
	@Override
	public void handlehongBaoOrderCancelled(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] {"order_ids","order_id"}, 1);
		List<Map<String, Object>> orderList = pcOrderDao.selectHongbaoOrderList(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		for (Map<String, Object> order : orderList) {
			if (!((String) order.get("status")).equals(BeikeConstant.ORDER_STATUS_01)) {
				throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
			}
			paramsMap.put("status", BeikeConstant.ORDER_STATUS_06);
			int updateFlag = pcOrderDao.updatePcOrder(paramsMap);
			if (updateFlag == 0) {
				throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
			}
		}
	}


	@Override
	public void beikeMallOrderForConfirmReceipt(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] {"order_id","order_no"}, 1);
		paramsMap.put("status", BeikeConstant.ORDER_STATUS_04);
		int updateFlag = beikeMallOrderDao.beikeMallOrderUpdate(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("member_id", paramsMap.get("member_id"));
		List<String> memberList = beikeMallOrderDao.selectBeikeMallOrderMemberList(tempMap);
		if(memberList.size() == 2){
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("service", "consumer.updateConsumerGrowthValue");
			bizParams.put("member_id", memberList.get(0));
			bizParams.put("growth_value", 10);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			HttpClientUtil.postShort(MEMBER_SERVICE_URL, reqParams);
		}
	}


	@Override
	public void beikeStreetOrderDetailFind(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParamsNum(paramsMap,  new String[] { "order_id" ,"order_no"}, 1);
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> OrderList = beikeStreetOrderDao.selectBeikeStreetOrderList(paramsMap);
		if (OrderList.isEmpty() || OrderList.size() == 0) {
			throw new BusinessException("未查到订单", "未查到订单,请稍后重试");
		}
		map.put("sale_fee", StringUtil.formatStr(OrderList.get(0).get("sale_fee")));
		map.put("beike_credit", StringUtil.formatStr(OrderList.get(0).get("beike_credit")));
		map.put("vip_fee", StringUtil.formatStr(OrderList.get(0).get("vip_fee")));
		map.put("qty", StringUtil.formatStr(OrderList.get(0).get("item_num")));
		map.put("payment_way_key", StringUtil.formatStr(OrderList.get(0).get("payment_way_key")));
		OrderList.clear();
		result.setResultData(map);
	}
	

	@Override
	public void beikeStreetOrderUpdate(Map<String, Object> paramsMap, ResultData result) 
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_no" });
		paramsMap.put("status", BeikeConstant.ORDER_STATUS_02);
		Integer mun = beikeStreetOrderDao.beikeStreetOrderUpdate(paramsMap);
		paramsMap.put("status", Constant.ORDER_PAID);
		Integer i = orderTradeLogDao.beikeStreetOrderTradeLogUpdate(paramsMap);
		if (mun != 1 || i == 1) {
			throw new BusinessException("未查到订单", "未查到订单,修改订单状态失败");
		}
	}
	
	@Override
	public void beikeStreetOrdeListByPageFind (Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "consumer_id" });
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if(StringUtil.isEmpty(status)){
			List<String> status_in = new ArrayList<>();
			status_in.add("wait_buyer_pay");
			status_in.add("wait_buyer_confirm_goods");
			status_in.add("trade_finished");
			paramsMap.put("status_in", status_in);
		}
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<Map<String, Object>> fgOrderDetailList = beikeStreetOrderDao.selectBeikeStreetOrdeList(paramsMap);
		if(fgOrderDetailList.isEmpty()||fgOrderDetailList.size()==0){
			//如果没查到数据 直接返回空集合
			Map<String, Object> map = new HashMap<>();
			map.put("list", resultList);
			map.put("doc_url", map);
			result.setResultData(map);
			return;
		}
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("stores_id", map.get("stores_id"));
			tempMap.put("stores_name", map.get("stores_name"));
			tempMap.put("loaded_code", map.get("loaded_code"));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("vip_fee", map.get("vip_fee") + "");
			tempMap.put("beike_credit", map.get("beike_credit") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("status", StringUtil.formatStr(map.get("status")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("item_goods_code", map.get("item_store_id") + "");
			tempMap.put("item_goods_title", map.get("item_name") + "");
			tempMap.put("item_num", StringUtil.formatStr(map.get("item_num")));
			tempMap.put("item_goods_pic_info", map.get("image_info") + "");
			String pic_info = StringUtil.formatStr(map.get("image_info"));
			if (StringUtil.isNotEmpty(pic_info)) {
				String pic_info_url = docUtil.imageUrlFind(pic_info);
				tempMap.put("goods_pic_info_url", pic_info_url);
			}
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}
	
	
	@Override
	public void handleBeikeStreetOrderCancelled (Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] {"order_id"});
		paramsMap.put("status", BeikeConstant.ORDER_STATUS_01);
		List<Map<String, Object>> orderList = beikeStreetOrderDao.selectBeikeStreetOrdeList(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		paramsMap.put("status", BeikeConstant.ORDER_STATUS_06);
		int updateFlag = beikeStreetOrderDao.beikeStreetOrderUpdate(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		// 还原库存
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("item_store_id", order.get("item_store_id"));
		tempMap.put("qty", order.get("item_num"));
		orderService.gdItemStoreSaleQtyForRestore(tempMap);
		//BigDecimal beike_credit =(BigDecimal) order.get("beike_credit");
		// 退还扣掉的贝壳
		/*if (beike_credit.compareTo(new BigDecimal("0"))==1) {
			// 交易的订单类型(贝壳商城订单)
			String order_type_key = "DDLX_11";
			String member_id = order.get("consumer_id") + "";
			String order_no = order.get("order_no") + "";
			Map<String, Object> out_trade_body = new HashMap<>();
			out_trade_body.put("order_no", order_no);
			orderService.orderRefund("SJLY_01", member_id, Constant.MEMBER_ID_MTH, "ZFFS_07",
					beike_credit, order_type_key, order_no, "贝壳街市退款", out_trade_body);
		}*/
	}

	@Override
	public void beikeMallOrderShell(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		paramsMap.put("status", "trade_finished");
		Integer beike_credit =  beikeMallOrderDao.selectBeikeMallOrderShell(paramsMap);
		Map<String,Integer> resultMap = new HashMap<>();
		resultMap.put("beike_credit", beike_credit);
		result.setResultData(resultMap);
	}


	@Override
	public void beikeMallOrderDetail(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_no" });
		Map<String, Object> orderMap = beikeMallOrderDao.selectBeikeMallOrderDetail(paramsMap);
		if (orderMap == null){
			throw new BusinessException("操作失败", "操作失败,未查到该订单");
		}
		orderMap.put("diff_time", (((Date) orderMap.get("order_date")).getTime() + 1800000 - new Date().getTime()));
		List<BeikeMallOrderCoupons> orderCoupons = beikeMallOrderDao.selectBeikeMallOrderCoupons(paramsMap);
		String actual_price = "";
		if(orderMap.get("actual_price") == null){
			actual_price = "0";
		} else{
			actual_price = orderMap.get("actual_price")+"";
		}
		String moneySub = MoneyUtil.moneySub(actual_price, "8");
		if (orderCoupons != null && orderCoupons.size() != 0) {
			for (BeikeMallOrderCoupons beikeMallOrderCoupons : orderCoupons) {
				String coupons_key = beikeMallOrderCoupons.getCoupons_key();
				if (orderCoupons.size() == 1) {
					if (coupons_key.startsWith("my")) {
						orderMap.put("my", 0);
						orderMap.put("lq", 0);
					} else if (coupons_key.startsWith("lq")) {
						orderMap.put("lq", beikeMallOrderCoupons.getCoupons_subtract());
						if (new BigDecimal(moneySub).compareTo(new BigDecimal("79")) == -1) {
							orderMap.put("my", 8);
						} else {
							orderMap.put("my", 0);
						}
					}
				} else if (orderCoupons.size() == 2) {
					if (coupons_key.startsWith("my")) {
						orderMap.put("my", 0);
					} else if (coupons_key.startsWith("lq")) {
						orderMap.put("lq", beikeMallOrderCoupons.getCoupons_subtract());
					}
				}
			}
		} else {
			if (new BigDecimal(moneySub).compareTo(new BigDecimal("79")) == -1) {
				orderMap.put("my", 8);
			} else {
				orderMap.put("my", 0);
			}
			orderMap.put("lq", 0);
		}
		String beike_credit = orderMap.get("beike_credit")+"";
		String vip_fee = orderMap.get("vip_fee")+"";
		String item_num = orderMap.get("item_num")+"";
		String pay_date = StringUtil.formatStr(orderMap.get("pay_date"));
		vip_fee = MoneyUtil.moneyMul(vip_fee, item_num);
		beike_credit = MoneyUtil.moneyMul(beike_credit, item_num);
		orderMap.put("vip_fee",new BigDecimal(vip_fee));
		orderMap.put("beike_credit",new BigDecimal(beike_credit));
		if(StringUtil.isNotBlank(pay_date)){
			orderMap.put("pay_date",(Date)orderMap.get("pay_date"));
		}
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("orderMap", orderMap);
		//只有待收货和已完成的订单才查询物流信息
		String status = StringUtil.formatStr(orderMap.get("status"));
		if (BeikeConstant.ORDER_STATUS_03.equalsIgnoreCase(status) || BeikeConstant.ORDER_STATUS_04.equalsIgnoreCase(status)){
			String express_no = StringUtil.formatStr(orderMap.get("express_no"));
			String ecode = StringUtil.formatStr(orderMap.get("ecode"));
			if(StringUtil.isNotBlank(express_no)){
				String host = PropertiesConfigUtil.getProperty("omp.express.apiurl");
				String path = "/kdi";
				String method = "GET";
				String appcode = PropertiesConfigUtil.getProperty("omp.express.appcode");
				Map<String, String> headers = new HashMap<>();
				headers.put("Authorization", "APPCODE "+appcode);
				Map<String, String> querys = new HashMap<>();
				querys.put("no", express_no.trim());
				if (StringUtil.isNotBlank(ecode)) {
					querys.put("type", ecode.trim());
				}
				HttpResponse res = HttpUtils.doGet(host,path,method, headers,querys);
				Map<String, Object> resMap = FastJsonUtil.jsonToMap(EntityUtils.toString(res.getEntity()));
				resMap.put("add_time", orderMap.get("add_time"));
				resultMap.put("logisticsMap", resMap);
			}
		}
		result.setResultData(resultMap);
	}


	@Override
	public void findOrderInfoByNo(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_no" });
		 BeikeMallOrder   order =  beikeMallOrderDao.findOrderInfoByNo(paramsMap);
		 if(null == order) {
			 throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		 }
		 Map<String,Object> resultMap = new HashMap<>();
		 resultMap.put("status", order.getStatus());
		 resultMap.put("amount", order.getVip_fee());
		 resultMap.put("shell", order.getBeike_credit());
		 resultMap.put("item_num", order.getItem_num());
		 result.setResultData(resultMap);
	}


	@Override
	public void updateTelephoneChargeOrder(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap,
				new String[] { "transaction_no", "payment_way_key", "member_id", "out_trade_no" });
		String out_trade_no = (String) paramsMap.get("out_trade_no");
		paramsMap.put("status", "paid");// 已支付
		Integer i = fgOrderDao.updateRcOrderToStatus(paramsMap);
		if (i == 0) {
			throw new BusinessException("话费充值", "话费充值，修改订单状态失败");
		}			
		paramsMap.clear();
		paramsMap.put("trade_no", out_trade_no);
		paramsMap.put("status", "paid");
		Integer res = fgOrderDao.updateRcOrderTradeLogToStatus(paramsMap);
		if (res == 0) {
			throw new BusinessException("话费充值接口错误", "修改订单日志状态失败");
		}
	}


	@Override
	public void selectTelephoneOrderStatus(Map<String, Object> paramsMap, ResultData result) throws Exception {
		//查询电话充值订单状态
		ValidateUtil.validateParams(paramsMap,
				new String[] { "transactionNoList"});
		String transactionNoList  =   (String) paramsMap.get("transactionNoList");
		List<String> noList =  FastJsonUtil.jsonToList(transactionNoList);
		List<String>  statusList = fgOrderDao.selectTelephoneOrderStatus(noList);
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("statusList", statusList);
		result.setResultData(resultMap);
	}


	@Override
	public void selectMemberCouponsId(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap,new String[] {"member_id"});
		List<Map<String, Object>> mapList =   beikeMallOrderDao.selectMemberCouponsId(paramsMap);
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("mapList", mapList);
		result.setResultData(resultMap);
	}
	
	private void sendMessage(Map<String, Object> paramsMap) throws Exception, SystemException, BusinessException {
		// 推送消息
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "member.sendMessage");
		reqParams.put("params", FastJsonUtil.toJson(paramsMap));
		String resultStr = HttpClientUtil.postShort(MEMBER_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
	}
}
