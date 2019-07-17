package com.meitianhui.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
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
import com.meitianhui.order.constant.OrderIDUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.dao.BeikeMallOrderCouponsDao;
import com.meitianhui.order.dao.FgOrderDao;
import com.meitianhui.order.dao.HongBaoOrderCouponsDao;
import com.meitianhui.order.dao.HongbaoOrderDao;
import com.meitianhui.order.dao.OrderTradeLogDao;
import com.meitianhui.order.entity.BeikeMallOrder;
import com.meitianhui.order.entity.BeikeMallOrderCoupons;
import com.meitianhui.order.entity.BeikeMallOrderItem;
import com.meitianhui.order.entity.BeikeMallOrderTardeLog;
import com.meitianhui.order.entity.HongBaoOrderCoupons;
import com.meitianhui.order.entity.HongBaoOrderTardeLog;
import com.meitianhui.order.entity.HongbaoOrder;
import com.meitianhui.order.entity.HongbaoOrderItem;
import com.meitianhui.order.entity.PcOrder;
import com.meitianhui.order.service.HongbaoOrderService;
import com.meitianhui.order.service.OrderService;

/**
 * 礼券专区服务实现层
 */
@Service
public class HongbaoOrderServiceImpl implements HongbaoOrderService {

	@Autowired
	public RedisUtil redisUtil;
	@Autowired
	private DocUtil docUtil;
	@Autowired
	private OrderService orderService;
	@Autowired
	private HongbaoOrderDao hongbaoOrderDao;
	@Autowired
	private OrderTradeLogDao orderTradeLogDao;
	@Autowired
	private HongBaoOrderCouponsDao hongBaoOrderCouponsDao;

	public static String GOODS_SERVICE_URL = PropertiesConfigUtil.getProperty("goods_service_url");
	
	public static String FINANCE_SERVICE_URL = PropertiesConfigUtil.getProperty("finance_service_url");
	
	public static String MEMBER_SERVICE_URL = PropertiesConfigUtil.getProperty("member_service_url");
	
	public static String SHELL_PROPERTY_SERVICE_URL = PropertiesConfigUtil.getProperty("shell_property_service_url");

	@Override
	public void hongbaoOrderCreate(Map<String, Object> paramsMap, ResultData result) throws Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_code", "consumer_id", "mobile",
					"delivery_address", "data_source", "nick_name" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String data_source = paramsMap.get("data_source") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String coupons_id = StringUtil.formatStr(paramsMap.get("coupons_id"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
			if (StringUtil.isBlank(qty)) {
				qty = "1";
			}
			// 手机号商品加锁
			lockKey = "[hongbaoOrderCreate]_" + goods_code + mobile;
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
			Map<String, Object> goodsData = hongbaoOrderFindForOrder(goods_code);
			Map<String, Object> goodsDataMap = (Map<String, Object>) goodsData.get("hongBaoGoods");

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
			List<Map<String, Object>> list = null;
			int i = 0;
			if (StringUtil.isNotBlank(coupons_id)) {
				i = 1;
				if (coupons_id.contains(",")) {
					String[] split = coupons_id.split(",");
					i = split.length;
				}
				/*// 用户使用的礼券不能超过商品应使用的礼券
				int coupon_price = (int) goodsDataMap.get("coupon_price");
				if (coupon_price < i) {
					throw new BusinessException("创建订单失败", "金额错误");
				}*/
				// 查询会员账户余额
				list = findGiftCouponList(consumer_id, coupons_id);
				if (list == null || list.size() == 0 || i != list.size()) {
					throw new BusinessException("创建订单失败", "账户礼券不足");
				}
			}
			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 扣库存
			orderService.hongbaoGoodsSaleQtyDeduction(goods_id, qty);
			try {
				Date date = new Date();
				// 进行订单创建
				HongbaoOrder order = new HongbaoOrder();
				order.setOrder_no(order_no);
				order.setOrder_date(date);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				order.setMember_id(consumer_id);
				order.setMember_mobile(mobile);
				Map<String, Object> memberInfo = new HashMap<>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setDesc1(goodsDataMap.get("title") + "");
				// order.setPayment_way_key(payment_way_key);
				String moneySub = MoneyUtil.moneySub(goodsDataMap.get("hongbao_price") + "", (399*i)+"");
				order.setSale_fee(new BigDecimal(moneySub));
				order.setQty(Integer.valueOf(qty));
				//礼包专区商品邮费默认为0
				order.setDelivery_fee(BigDecimal.valueOf(0));
				order.setDelivery_address(delivery_address);
				order.setContact_person(nick_name);
				order.setContact_tel(mobile);
				order.setData_source(data_source);
				order.setStatus(BeikeConstant.ORDER_STATUS_01);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setRemark(remark);
				String label = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
				order.setOrder_type(label);
				hongbaoOrderDao.insertHongbaoOrder(order);
				// 创建贝壳商城补充订单
				HongbaoOrderItem orderItem = new HongbaoOrderItem();
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setQty(Integer.valueOf(qty));
				orderItem.setSale_price(new BigDecimal(goodsDataMap.get("hongbao_price") + ""));
				orderItem.setService_price(BigDecimal.valueOf(0));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<>();
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				hongbaoOrderDao.insertHongbaoOrderItem(orderItem);
				if ("ZFFS_01".equals(payment_way_key) || "ZFFS_02".equals(payment_way_key)) {
					// 创建红包兑订单交易日志
					HongBaoOrderTardeLog orderTardeLog = new HongBaoOrderTardeLog();
					orderTardeLog.setOrder_no(order.getOrder_no());
					orderTardeLog.setStatus(Constant.ORDER_NONPAID);
					orderTardeLog.setTrade_type_key("JYLX_01");// 交易类型
					orderTardeLog.setData_source("SJLY_01");// 数据来源
					orderTardeLog.setModified_date(date);
					orderTardeLog.setCreated_date(date);
					orderTradeLogDao.insertHongBaoOrderTardeLog(orderTardeLog);
				}
				// 创建订单礼券关系表
				if (list != null && list.size() > 0) {
					List<HongBaoOrderCoupons> couponsList = new ArrayList<>();
					for (Map<String, Object> map : list) {
						HongBaoOrderCoupons orderCoupons = new HongBaoOrderCoupons();
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
					hongBaoOrderCouponsDao.insertHongBaoOrderCoupons(couponsList);
				}
				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_no", order.getOrder_no());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				qty = "-" + qty;
				orderService.hongbaoGoodsSaleQtyDeduction(goods_id, qty);
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
	public void h5OrderCreate(Map<String, Object> paramsMap, ResultData result) throws Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_id", "consumer_id", "mobile", "delivery_address", "data_source" });
			String goods_id = paramsMap.get("goods_id") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String data_source = paramsMap.get("data_source") + "";
			String mobile = paramsMap.get("mobile") + "";
			String amount = paramsMap.get("amount") + "";
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			if (StringUtil.isBlank(qty)) {
				qty = "1";
			}
			// 手机号商品加锁
			lockKey = "[hongbaoOrderCreate]_" + goods_id + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_id, 180);
			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<>();
			// 获取商品信息
			Map<String, Object> goodsData = findForOrder(goods_id);
			Map<String, Object> goodsDataMap = (Map<String, Object>) goodsData.get("hongBaoGoods");
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
			String goodsId = goodsDataMap.get("goods_id") + "";
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 扣库存
			orderService.hongbaoGoodsSaleQtyDeduction(goodsId, qty);
			try {
				Date date = new Date();
				// 进行订单创建
				HongbaoOrder order = new HongbaoOrder();
				order.setOrder_no(order_no);
				order.setOrder_date(date);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				order.setMember_id(consumer_id);
				order.setMember_mobile(mobile);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setDesc1(goodsDataMap.get("title") + "");
				//order.setPayment_way_key(payment_way_key);
				order.setSale_fee(new BigDecimal(amount));
				order.setQty(Integer.valueOf(qty));
				order.setDelivery_fee(new BigDecimal(amount));
				order.setDelivery_address(delivery_address);
				order.setContact_tel(mobile);
				order.setData_source(data_source);
				order.setStatus(BeikeConstant.ORDER_STATUS_01);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setRemark(remark);
				hongbaoOrderDao.insertHongbaoOrder(order);
				// 创建贝壳商城补充订单
				HongbaoOrderItem orderItem = new HongbaoOrderItem();
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setQty(Integer.valueOf(qty));
				orderItem.setSale_price(new BigDecimal(amount) );
				orderItem.setService_price(new BigDecimal(amount) );
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<>();
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				hongbaoOrderDao.insertHongbaoOrderItem(orderItem);
			
					//创建红包兑订单交易日志
					HongBaoOrderTardeLog orderTardeLog = new HongBaoOrderTardeLog();
					orderTardeLog.setOrder_no(order.getOrder_no());
					orderTardeLog.setStatus(Constant.ORDER_NONPAID);
					orderTardeLog.setTrade_type_key("JYLX_01");//交易类型
					orderTardeLog.setData_source("SJLY_01");//数据来源
					orderTardeLog.setModified_date(date);
					orderTardeLog.setCreated_date(date);
					orderTradeLogDao.insertHongBaoOrderTardeLog(orderTardeLog);
			
				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_no", order.getOrder_no());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				qty = "-" + qty;
				orderService.hongbaoGoodsSaleQtyDeduction(goods_id, qty);
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
	
	/**
	 * 查询消费者账户余额
	 */
	private List<Map<String,Object>> findGiftCouponList(String consumer_id,String coupons_id)
			throws Exception, SystemException, BusinessException {
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "finance.consumer.findGiftCouponList");
		if (coupons_id.contains(",")) {
			List<String> list = StringUtil.str2List(coupons_id, ",");
			bizParams.put("coupons_id_in", list);
		} else {
			bizParams.put("coupons_id", coupons_id);
		}
		bizParams.put("member_id", consumer_id);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(FINANCE_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		List<Map<String,Object>> couponDataMap = (List<Map<String,Object>>) resultMap.get("data");
		return couponDataMap;
	}
	
	private Map<String, Object> hongbaoOrderFindForOrder(String goods_code) throws Exception {
		// 组装查询参数
		Map<String, Object> bizParams = new HashMap<>();
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "goods.hongbaoGoodsFindForOrder");
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
	
	private Map<String, Object> findForOrder(String goods_id) throws Exception {
		// 组装查询参数
		Map<String, Object> bizParams = new HashMap<>();
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("service", "goods.hongbaoGoodsFindForOrder");
		bizParams.put("goods_id", goods_id);
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
	public void hongBaoOrderDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "order_id", "order_no" }, 1);
		Map<String, Object> map = new HashMap<>();
		HongbaoOrder order = hongbaoOrderDao.selectHongbaoOrder(paramsMap);
		if (order == null) {
			throw new BusinessException("未查到订单", "未查到订单,请稍后重试");
		}
		map.put("sale_fee", StringUtil.formatStr(order.getSale_fee()));
		map.put("qty", StringUtil.formatStr(order.getQty()));
		result.setResultData(map);
	}
	
	@Override
	public void hongBaoOrderUpdate(Map<String, Object> paramsMap, ResultData result) 
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_no" });
		paramsMap.put("status", BeikeConstant.ORDER_STATUS_02);
		Integer mun = hongbaoOrderDao.hongBaoOrderUpdate(paramsMap);
		if (mun != 1) {
			throw new BusinessException("未查到订单", "订单修改失败");
		}
		//查找商品id,数量 
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("order_no", paramsMap.get("order_no"));
		List<Map<String, Object>> orderList = hongbaoOrderDao.selectHongbaoOrderList(tempMap);
		if (orderList.isEmpty() || orderList.size() == 0) {
			throw new BusinessException("未查到订单", "未查到订单,请稍后重试");
		}
		Map<String, Object> hongBaoOrderMap = orderList.get(0);
		//增加实际销量
		String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
		Map<String, String> reqPar = new HashMap<>();
		Map<String, String> par = new HashMap<>();
		reqPar.put("service", "goods.hongBaoGoodsSalesVolumeUpdate");
		par.put("goods_id", StringUtil.formatStr(hongBaoOrderMap.get("item_goods_id")));
		par.put("sales_volume", StringUtil.formatStr(hongBaoOrderMap.get("qty")));
		reqPar.put("params", FastJsonUtil.toJson(par));
		HttpClientUtil.post(goods_service_url, reqPar);	
		
		Map<String,Object> param = new HashMap<>();
		param.put("message_type", "order");
		param.put("message_category", "place_order");//下单
		param.put("goods_title", hongBaoOrderMap.get("item_goods_title"));
		Object pic_info = FastJsonUtil.jsonToList(StringUtil.formatStr(hongBaoOrderMap.get("item_goods_pic_info"))).get(0);
		param.put("message_pic",pic_info); 
		param.put("order_no", hongBaoOrderMap.get("order_no"));
		param.put("member_id", hongBaoOrderMap.get("member_id"));
		sendMessage(param);
		
		//调用会员分销逻辑的逻辑
//		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
//		Map<String, String>  param = new HashMap<>();
//		Map<String, String> map = new HashMap<>();
//		param.put("service", "consumer.consumerBuildingMemberDistrbution");
//		map.put("member_id", StringUtil.formatStr(hongBaoOrderMap.get("member_id")));
//		param.put("params", FastJsonUtil.toJson(map));
//		HttpClientUtil.post(member_service_url, param);	
		
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
	
	@Override
	public void hongBaoOrderListPageFind(Map<String, Object> paramsMap, ResultData result)
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
		List<Map<String, Object>> fgOrderDetailList = hongbaoOrderDao.selectHongbaoOrderList(paramsMap);
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
			tempMap.put("order_no", map.get("order_no"));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 1800000 - new Date().getTime()) + "");
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", (Date) map.get("order_date"));
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("status", StringUtil.formatStr(map.get("status")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("item_goods_id", map.get("item_goods_id") + "");
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("qty", StringUtil.formatStr(map.get("qty")));
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("order_type", map.get("order_type") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info) && goods_pic_info.contains(",")) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}else {
				doc_ids.add(goods_pic_info);
			}
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	@Override
	public void hongbaoOrderForConfirmReceipt(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] {"order_id","order_no"}, 1);
		paramsMap.put("status", BeikeConstant.ORDER_STATUS_04);
		int updateFlag = hongbaoOrderDao.hongBaoOrderUpdate(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
	}
	
	@Override
	public void hongbaoOrderDetail(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "order_id", "order_no" }, 1);
		Map<String, Object> orderMap = hongbaoOrderDao.selectHongBaoOrderDetail(paramsMap);
		orderMap.put("diff_time", ((Date)orderMap.get("order_date")).getTime() + 1800000 - new Date().getTime());
		List<HongBaoOrderCoupons> orderCoupons = hongBaoOrderCouponsDao.selectHongBaoOrderCoupons(paramsMap);
		if (orderCoupons != null && orderCoupons.size() != 0) {
			HongBaoOrderCoupons coupons = orderCoupons.get(0);
			orderMap.put("lj", coupons.getCoupons_amount());
			orderMap.put("coupons_key", coupons.getCoupons_key());
			orderMap.put("coupons_mun", orderCoupons.size());
			orderMap.put("my", 0);
		}
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("orderMap", orderMap);
		//只有待收货和已完成的订单才查询物流信息
		String status = StringUtil.formatStr(orderMap.get("status"));
		if (BeikeConstant.ORDER_STATUS_03.equalsIgnoreCase(status) || BeikeConstant.ORDER_STATUS_04.equalsIgnoreCase(status)){
			String express_no = StringUtil.formatStr(orderMap.get("express_no"));
			String ecode = StringUtil.formatStr(orderMap.get("ecode"));
			if(StringUtil.isNotBlank(express_no)){
				String apiurl = PropertiesConfigUtil.getProperty("omp.express.apiurl")+"/kdi";
				String appcode = PropertiesConfigUtil.getProperty("omp.express.appcode");
				Map<String, String> headers = new HashMap<>();
				headers.put("Authorization", "APPCODE "+appcode);
				Map<String, String> params = new HashMap<>();
				params.put("no", express_no.trim());
				if (StringUtil.isNotBlank(ecode)) {
					params.put("type", ecode.trim());
				}
				String res = HttpClientUtil.doGet(apiurl, params, headers);
				Map<String, Object> resMap = FastJsonUtil.jsonToMap(res);
				resMap.put("add_time", orderMap.get("add_time"));
				resultMap.put("logisticsMap", resMap);
			}
		}
		result.setResultData(resultMap);
	}

	@Override
	public void findHongBaoOrderInfoByNo(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_no" });
		HongbaoOrder   order =  hongbaoOrderDao.selectHongbaoOrder(paramsMap);
		 if(null == order) {
			 throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		 }
		 Map<String,Object> resultMap = new HashMap<>();
		 resultMap.put("status", order.getStatus());
		 result.setResultData(resultMap);
		
	}

	@Override
	public void hongbaoOrderAnnul(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_ids" });
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("status", BeikeConstant.ORDER_STATUS_06);
		List<String> order_ids = StringUtil.str2List(StringUtil.formatStr(paramsMap.get("order_ids")), ",");
		tempMap.put("order_ids", order_ids);
		Integer integer = hongbaoOrderDao.hongBaoOrderUpdate(tempMap);
		if (integer == 0) {
			throw new BusinessException("取消订单失败", "取消订单失败");
		}
		tempMap.remove("status");
		Map<String, Object> orderInfo = hongbaoOrderDao.selectHongbaoOrderForCoIdGoId(tempMap);
		String coupons_ids = StringUtil.formatStr(orderInfo.get("coupons_ids"));
		String goods_ids = StringUtil.formatStr(orderInfo.get("goods_ids"));
		// 恢复商品库存
		orderService.hongbaoGoodsSaleQtyDeduction(goods_ids, "-1");

		String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
		Map<String, String> reqPar = new HashMap<>();
		Map<String, String> par = new HashMap<>();
		reqPar.put("service", "finance.consumer.updateGiftCouponMun");
		par.put("coupons_ids", coupons_ids);
		reqPar.put("params", FastJsonUtil.toJson(par));
		String resultStr = HttpClientUtil.post(finance_service_url, reqPar);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
	}
}
