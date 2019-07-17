package com.meitianhui.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.order.constant.BeikeConstant;
import com.meitianhui.order.constant.Constant;
import com.meitianhui.order.constant.OrderIDUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.dao.BeikeStreetOrderDao;
import com.meitianhui.order.dao.BeikeStreetOrderItemDao;
import com.meitianhui.order.dao.OrderTradeLogDao;
import com.meitianhui.order.entity.BeikeStreetOrder;
import com.meitianhui.order.entity.BeikeStreetOrderItem;
import com.meitianhui.order.entity.BeikeStreetOrderTardeLog;
import com.meitianhui.order.service.BeikeStreetOrderService;
/**
 * 贝壳街市订单接口实现类
 *
 */
@Service
public class BeikeStreetOrderServiceImpl implements BeikeStreetOrderService {
	
	@Autowired
	private BeikeStreetOrderDao beikeStreetOrderDao;
	@Autowired
	private BeikeStreetOrderItemDao beikeStreetOrderItemDao;
	@Autowired
	private OrderTradeLogDao orderTradeLogDao;
	
	public static String MEMBER_SERVICE_URL = PropertiesConfigUtil.getProperty("member_service_url");

	@Override
	public void beikeStreetOrderCreate(Map<String, Object> paramsMap, ResultData result) throws Exception {

		ValidateUtil.validateParams(paramsMap, new String[] { "stores_id", "stores_name", "consumer_id", "item_num",
				"payment_way_key", "delivery_address", "item_store_id" });
		String consumer_id = StringUtil.formatStr(paramsMap.get("consumer_id"));
		String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
		//查询消费者是否是会员
		Map<String, Object> consumerDataMap = consumerVipTime(consumer_id);
		Long vip_end_time = (Long)consumerDataMap.get("vip_end_time");
		Boolean falg;
		if(vip_end_time-new Date().getTime() > 0){ 
			falg = true;
		}else {
			falg = false;
		}
		// 解析商品信息
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "goods.itemStoreForOrderFind");
		bizParams.put("item_store_id", StringUtil.formatStr(paramsMap.get("item_store_id")));
		bizParams.put("store_id", StringUtil.formatStr(paramsMap.get("stores_id")));
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(OrderServiceImpl.GOODS_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException(resultMap.get("error_code") + "", resultMap.get("error_msg") + "");
		}
		Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
		String sale_price = StringUtil.formatStr(data.get("sale_price"));
		String vip_price = StringUtil.formatStr(data.get("vip_price"));
		String beike_credit = StringUtil.formatStr(data.get("beike_price"));
		Date date = new Date();
		BeikeStreetOrder order = new BeikeStreetOrder();
		BeanConvertUtil.mapToBean(order, paramsMap);
		order.setOrder_id(IDUtil.getUUID());
		order.setOrder_no(OrderIDUtil.getOrderNo());
		order.setStatus(BeikeConstant.ORDER_STATUS_01);
		order.setCreated_date(date);
		order.setOrder_date(date);
		if (falg) {
			order.setVip_fee(new BigDecimal(vip_price));
			order.setBeike_credit(new BigDecimal(beike_credit));
			order.setSale_fee(new BigDecimal("0.00"));
			if ("ZFFS_01".equals(payment_way_key)) {
				order.setPayment_way_key("ZFFS_03");//组合支付  支付宝+贝壳支付
			}else if("ZFFS_02".equals(payment_way_key)){
				order.setPayment_way_key("ZFFS_04");//组合支付  微信+贝壳支付
			}
		} else {
			order.setVip_fee(new BigDecimal("0.00"));
			order.setBeike_credit(new BigDecimal("0"));
			order.setSale_fee(new BigDecimal(sale_price));
			order.setPayment_way_key(payment_way_key);
		}
		order.setLoaded_code(IDUtil.getShortUUID());
		order.setModified_date(date);
		beikeStreetOrderDao.insertBeikeStreetOrder(order);

		BeikeStreetOrderItem pcOrderItem = new BeikeStreetOrderItem();
		pcOrderItem.setItem_name((String) data.get("item_name"));
		pcOrderItem.setImage_info((String) data.get("image_info"));
		pcOrderItem.setOrder_item_id(IDUtil.getUUID());
		pcOrderItem.setItem_store_id(StringUtil.formatStr(paramsMap.get("item_store_id")));
		pcOrderItem.setOrder_id(order.getOrder_id());
		pcOrderItem.setQty(Integer.valueOf(paramsMap.get("item_num") + ""));
		pcOrderItem.setCreated_date(date);
		pcOrderItem.setModified_date(date);
		beikeStreetOrderItemDao.insertBeikeStreetOrderItem(pcOrderItem);
		
		if("ZFFS_01".equals(payment_way_key)||"ZFFS_02".equals(payment_way_key)){
			//创建贝壳街市订单交易日志
			BeikeStreetOrderTardeLog orderTardeLog = new BeikeStreetOrderTardeLog();
			orderTardeLog.setOrder_no(order.getOrder_no());
			orderTardeLog.setStatus(Constant.ORDER_NONPAID);
			orderTardeLog.setTrade_type_key("JYLX_01");//交易类型
			orderTardeLog.setData_source("SJLY_01");//数据来源
			orderTardeLog.setModified_date(date);
			orderTardeLog.setCreated_date(date);
			orderTradeLogDao.insertBeikeStreetOrderTardeLog(orderTardeLog);
		}
		
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("order_no", order.getOrder_no());
		tempMap.put("member_status", falg);
		tempMap.put("loaded_code", order.getLoaded_code());
		result.setResultData(tempMap);
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
}
