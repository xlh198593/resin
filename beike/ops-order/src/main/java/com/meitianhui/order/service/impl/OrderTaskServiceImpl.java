package com.meitianhui.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.order.constant.BeikeConstant;
import com.meitianhui.order.constant.Constant;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.dao.BeikeMallOrderCouponsDao;
import com.meitianhui.order.dao.BeikeMallOrderDao;
import com.meitianhui.order.dao.FgOrderDao;
import com.meitianhui.order.dao.HongbaoOrderDao;
import com.meitianhui.order.dao.OdTaskDao;
import com.meitianhui.order.dao.OrderDao;
import com.meitianhui.order.dao.PcOrderDao;
import com.meitianhui.order.dao.PsOrderDao;
import com.meitianhui.order.dao.TsActivityDao;
import com.meitianhui.order.dao.TsOrderDao;
import com.meitianhui.order.entity.FgOrderCommission;
import com.meitianhui.order.service.HongbaoOrderService;
import com.meitianhui.order.service.OrderService;
import com.meitianhui.order.service.OrderTaskService;
import com.meitianhui.order.service.PcOrderService;
import com.meitianhui.order.service.PsOrderService;
import com.meitianhui.order.service.TsActivityService;
import com.meitianhui.order.service.TsOrderService;

/**
 * 定时任务
 * 
 * @ClassName: OrderTaskServiceImpl
 * @author tiny
 * @date 2017年5月14日 下午12:22:00
 *
 */
@Service
public class OrderTaskServiceImpl implements OrderTaskService {

	@Autowired
	public OrderDao orderDao;
	@Autowired
	public PsOrderDao psOrderDao;
	@Autowired
	public PcOrderDao pcOrderDao;
	@Autowired
	private FgOrderDao fgOrderDao;
	@Autowired
	public TsActivityDao tsActivityDao;
	@Autowired
	public TsOrderDao tsOrderDao;
	@Autowired
	public OdTaskDao odTaskDao;

	@Autowired
	private OrderService orderService;
	@Autowired
	private PsOrderService psOrderService;
	@Autowired
	private PcOrderService pcOrderService;
	@Autowired
	private TsActivityService tsActivityService;
	@Autowired
	private TsOrderService tsOrderService;
	@Autowired
	private BeikeMallOrderDao beikeMallOrderDao;
	@Autowired
	private HongbaoOrderDao hongbaoOrderDao;
	@Autowired
	private  BeikeMallOrderCouponsDao  beikeMallOrderCouponsDao;
	@Autowired
	private HongbaoOrderService hongbaoOrderService;

	@Override
	public void fgOrderAutoCancel(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {

		Map<String, Object> tempMap = new HashMap<String, Object>();
		// 超过1个小时未确认收货,自动取消
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 4, -2));
		tempMap.put("lt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 4, -1));
		List<Map<String, Object>> timeoutOrderList = fgOrderDao.selectTimeoutFgOrder(tempMap);
		// 先执行取消操作，然后再修改对应的库存
		fgOrderDao.fgOrderAutoCancel(tempMap);
		for (Map<String, Object> map : timeoutOrderList) {
			String contact_tel = map.get("contact_tel") + "";
			String order_no = map.get("order_no") + "";
			String msg = "编号为【" + order_no + "】的订单超时未确认，已被取消，如已付款请在厂商店铺退款，未付款无须理会。";
			String channel_id = map.get("channel_id")+"";
			if("1".equals(channel_id)){
				orderService.gdFreeGetGoodsSaleQtyRestore(map.get("goods_id") + "", map.get("qty") + "");
			}else {
				orderService.psGoodsSaleQtyRestore(map.get("goods_id") + "", map.get("qty") + "");
			}

			//OrderServiceImpl.sendMsg(contact_tel, msg);
		}
	}
	
	
	@Override
	public void fgOrderForOwnAutoCancel(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		Map<String, Object> tempMap = new HashMap<String, Object>();
		// 超过1个小时未确认收货,自动取消
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 4, -2));
		tempMap.put("lt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 4, -1));
		List<Map<String, Object>> timeoutOrderList = fgOrderDao.selectTimeoutFgOrderForOwn(tempMap);
		// 先执行取消操作，然后再修改对应的库存 无法直接区分是老订单 还是新自营订单？
		fgOrderDao.fgOrderForOwnAutoCancel(tempMap); 
		for (Map<String, Object> map : timeoutOrderList) {
			String contact_tel = map.get("contact_tel") + "";
			String order_no = map.get("order_no") + "";
			String msg = "编号为【" + order_no + "】的订单超时未确认，已被取消，如已付款请在厂商店铺退款，未付款无须理会。";
			tempMap.clear();
			tempMap.put("goods_id", map.get("goods_id"));
			tempMap.put("goods_code", map.get("goods_code"));
			tempMap.put("qty", map.get("qty"));
			tempMap.put("sku_id", map.get("sku_id"));
			orderService.psGoodsSaleQtyForOwnRestore(tempMap);
			//OrderServiceImpl.sendMsg(contact_tel, msg);
		}
	}

	
	@Override
	public void proPsGroupOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// 到时间自动成团
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -1));
		tempMap.put("lt_date", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<Map<String, Object>> orderList = psOrderDao.selectExpirePsSubOrder(tempMap);

		for (Map<String, Object> orderMap : orderList) {
			String order_id = orderMap.get("order_id") + "";
			String goods_id = orderMap.get("goods_id") + "";
			Integer qty_limit = Integer.parseInt(orderMap.get("qty_limit") + "");
			Integer sub_order_qty = Integer.parseInt(orderMap.get("sub_order_qty") + "");
			// 可以成团
			tempMap.clear();
			if (sub_order_qty >= qty_limit) {
				boolean flag = false;
				try {
					orderService.psGoodsSaleQtyDeduction(goods_id, sub_order_qty + "");
					flag = true;
				} catch (Exception e) {
					flag = false;
				}

				if (flag) {
					String discount_fee = orderMap.get("discount_fee") + "";
					String total_fee = MoneyUtil.moneyMul(discount_fee, sub_order_qty + "");
					// 更新主订单状态为已支付
					tempMap.put("order_id", order_id);
					tempMap.put("qty", sub_order_qty + "");
					tempMap.put("status", Constant.ORDER_PAYED);
					tempMap.put("sale_fee", total_fee);
					tempMap.put("total_fee", total_fee);
					tempMap.put("item_total_fee", total_fee);
					tempMap.put("remark", "达到成团条件");
					psOrderDao.updateExpirePsGroupOrder(tempMap);
					// 更新子订单状态为未发货
					tempMap.clear();
					tempMap.put("parent_order_id", order_id);
					tempMap.put("status", Constant.ORDER_DELIVERED);
					tempMap.put("param_status", Constant.ORDER_PAID);
					psOrderDao.updatePsSubOrder(tempMap);
				} else {
					// 商品库存不足,成团失败,进行退款操作
					groupOrderRefund(order_id, "商品库存不足,订单取消");
				}
			} else {
				// 未成团,进行团购预售订单退款操作
				groupOrderRefund(order_id, "未达到成团数量,订单取消");
			}
		}
	}

	/**
	 * 消费者团购预售订单退款
	 * 
	 * @param mobiles
	 * @param msg
	 */
	private void groupOrderRefund(String parent_order_id, String remark)
			throws BusinessException, SystemException, Exception {
		try {
			// 更新团购预售到时间订单状态
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", parent_order_id);
			tempMap.put("status", Constant.ORDER_CANCELLED);
			tempMap.put("remark", remark);
			psOrderDao.updateExpirePsGroupOrder(tempMap);
			// 已支付的团购预售子订单退款,先修改状态，然后再逐笔退款
			tempMap.clear();
			tempMap.put("parent_order_id", parent_order_id);
			tempMap.put("status", Constant.ORDER_PAID);
			List<Map<String, Object>> psSubOrderList = psOrderDao.selectPsGroupSubOrder(tempMap);

			// 修改订单状态
			for (Map<String, Object> subOrder : psSubOrderList) {
				tempMap.clear();
				String sub_order_id = subOrder.get("order_id") + "";
				tempMap.put("order_id", sub_order_id);
				tempMap.put("status", Constant.ORDER_CANCELLED);
				tempMap.put("modified_date",
						DateUtil.date2Str((Date) subOrder.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("remark", remark);
				int updateFlag = psOrderDao.updatePsSubOrder(tempMap);
				if (updateFlag == 0) {
					throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
				}
			}
			// 进行退款操作
			for (Map<String, Object> subOrder : psSubOrderList) {
				String member_id = subOrder.get("member_id") + "";
				String total_fee = subOrder.get("total_fee") + "";
				String order_no = subOrder.get("order_no") + "";
				// 订单退款
				Map<String, Object> out_trade_body = new HashMap<String, Object>();
				out_trade_body.put("parent_order_id", parent_order_id);
				out_trade_body.put("order_no", order_no);
				out_trade_body.put("total_fee", total_fee);
				orderService.orderRefund("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05",
						new BigDecimal(total_fee), "DDLX_09", order_no, "团购预售订单退款", out_trade_body);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void psOrderAutoReceived(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {

		// 超过7天自动确定收货
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -8));
		tempMap.put("lt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -6));
		List<Map<String, Object>> timeoutOrderList = psOrderDao.selectTimeoutDeliveredPsOrder(tempMap);
		Map<String, Object> bizParams = new HashMap<String, Object>();
		for (Map<String, Object> map : timeoutOrderList) {
			bizParams.clear();
			bizParams.put("order_id", map.get("order_id"));
			psOrderService.handlePsOrderReceived(bizParams, new ResultData());
		}
	}

	@Override
	public void pcOrderAutoReceived(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {

		// 超过3天自动确定收货
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -4));
		tempMap.put("lt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -2));
		List<Map<String, Object>> timeoutOrderList = pcOrderDao.selectTimeoutDeliveredPcOrder(tempMap);
		Map<String, Object> bizParams = new HashMap<String, Object>();
		for (Map<String, Object> map : timeoutOrderList) {
			bizParams.clear();
			bizParams.put("order_id", map.get("order_id"));
			pcOrderService.handlePcOrderReceived(bizParams, new ResultData());
		}
	}

	@Override
	public void tsActivityCheck(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {

		// 团购活动到时间后,判断是否成团(延迟两分钟)
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 4, -2));
		tempMap.put("lt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 5, -2));
		tempMap.put("activity_type", Constant.ACTIVITY_TYPE_LADDER);
		List<Map<String, Object>> expiriedActivityList = tsActivityDao.selectExpiriedActivity(tempMap);
		for (Map<String, Object> expiriedActivity : expiriedActivityList) {
			String activity_id = expiriedActivity.get("activity_id") + "";
			// 阶梯价格
			Integer min_num = Integer.parseInt(expiriedActivity.get("min_num") + "");
			Integer order_qty = Integer.parseInt(expiriedActivity.get("order_qty") + "");
			if (order_qty >= min_num) {
				tsActivityService.activitySuccessForLadder(activity_id);
			} else {
				tsActivityService.activityFailForLadder(activity_id, Constant.ORDER_FAIL, "未成团,自动取消",
						Constant.ORDER_CANCELLED, "未成团,自动取消");
			}
		}
	}

	@Override
	public void tsActivityAutoReceived(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {

		// 超过7天自动确定收货
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -10));
		tempMap.put("lt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -6));
		tsActivityDao.updateTimeoutDeliveredActivityForLadder(tempMap);
	}

	@Override
	public void tsOrderAutoReceived(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {

		// 超过7天自动确定收货
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -8));
		tempMap.put("lt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -4));
		tempMap.put("activity_type", Constant.ACTIVITY_TYPE_LADDER);
		List<Map<String, Object>> tsOrderList = tsOrderDao.selectTimeoutDeliveredTsOrder(tempMap);
		for (Map<String, Object> orderMap : tsOrderList) {
			tempMap.clear();
			tempMap.put("order_id", orderMap.get("order_id"));
			tsOrderService.handleTsOrderReceived(tempMap, new ResultData());
		}

	}

	@Override
	public void handleOdTaskTimeout(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {

		// 超时任务自动取消
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("gt_date", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 3, -3));
		tempMap.put("lt_date", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		odTaskDao.updateTimeoutOdTask(tempMap);
		odTaskDao.updateTimeoutOdTaskProcessing(tempMap);
	}

	@Override
	public void transmatic(Map<String, Object> paramsMap, ResultData result) throws Exception {
			List<Map<String, Object>> amountList = fgOrderDao.selectFgOrderCommissionForSevenDaysAgo();
			List<Map<String, Object>> list = new ArrayList<>();
			for (int i = 0; i < amountList.size(); i++) {
				BigDecimal sum_amount = (BigDecimal)(amountList.get(i).get("sum_amount"));
				if (sum_amount.compareTo(BigDecimal.ZERO) == 1) {
					list.add(amountList.get(i));
				}
			}
			if(list == null || list.size() == 0){
				throw new BusinessException("自动转账佣金异常", "本周没有需要转账的店东");
			}
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			bizParams.put("data", FastJsonUtil.toJson(list));
			reqParams.put("service", "finance.stores.transmatic");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.post(finance_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"),
						"转入零钱账户失败");
			}
			Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
			List<Map<String, Object>> succData = (List<Map<String, Object>>) dataMap.get("succData");
			if (succData != null && succData.size() > 0) {
				FgOrderCommission fgOrderCommission = new FgOrderCommission();
				for (Map<String, Object> map : succData) {
					Date date = new Date();
					fgOrderCommission.setStores_id((String) map.get("stores_id"));
					fgOrderCommission.setAmount(new BigDecimal("-"+map.get("sum_amount")));
					fgOrderCommission.setCommision_status("transmatic");
					fgOrderCommission.setModified_date(date);
					fgOrderCommission.setCreated_time(date);
					fgOrderCommission.setRemark("自动转账成功");
					int insertFalg = fgOrderDao.insertFgOrderCommission(fgOrderCommission);
					if(insertFalg == 0){
						throw new BusinessException("自动转账佣金异常", "生成佣金转账日志失败");
					}
				}
			}

	}

	@Override
	public void beikeMallOrderForAutoCancel(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		Map<String, Object> temp = new HashMap<String, Object>();
		// 超过30分钟未确认收货,自动取消
		String  beforeDate =  DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 5, -30);
		temp.put("order_date_end", beforeDate);
		temp.put("status", "wait_buyer_pay");
		List<Map<String, Object>> orderList = beikeMallOrderDao.selectOrderTimeOutList(temp);
		if(orderList == null || orderList.size() == 0){
			return;
		}
		for (Map<String, Object> order : orderList) {
			// 还原库存
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("goods_id", order.get("goods_id"));
			tempMap.put("goods_code", order.get("goods_code"));
			tempMap.put("qty", order.get("item_num"));
			tempMap.put("sku_id", order.get("sku_id"));
			orderService.psGoodsSaleQtyForOwnRestore(tempMap);
			paramsMap.put("order_id", order.get("order_id"));
			paramsMap.put("status", BeikeConstant.ORDER_STATUS_06);
			int updateFlag = beikeMallOrderDao.beikeMallOrderUpdate(paramsMap);
			// 如果是消费者取消,退回扣除的金币
			Long beike_credit =(Long) order.get("beike_credit");
			
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("order_no", order.get("order_no") + "");
			Map<String, Object> orderCouponMap = beikeMallOrderCouponsDao.findOrderCoupons(paramMap);
			String coupons_key = null;
			String member_coupons_id = null;
			if(null !=orderCouponMap ) {
				coupons_key=StringUtil.formatStr(orderCouponMap.get("coupons_key")) ;
				member_coupons_id=StringUtil.formatStr(orderCouponMap.get("coupons_id")) ;
			}
			if(StringUtils.isNotBlank(coupons_key)) {
				//把免邮券或者优惠券 给退回做update
				String url = PropertiesConfigUtil.getProperty("finance_service_url");;
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
			// 退还扣掉的贝壳
			if (beike_credit > 0) {
				// 交易的订单类型(贝壳商城订单)
				String order_type_key = "DDLX_11";
				String member_id = order.get("member_id") + "";
				String order_no = order.get("order_no") + "";
				Map<String, Object> out_trade_body = new HashMap<>();
				out_trade_body.put("order_no", order_no);
				Long  qty =  (Long) order.get("item_num");
				Long beikeShell =  qty * beike_credit;
				BigDecimal beike = new BigDecimal(beikeShell);
				orderService.orderRefund("SJLY_01", member_id, Constant.MEMBER_ID_MTH, "ZFFS_07",
						beike, order_type_key, order_no, "贝壳商城退款", out_trade_body);
			}
		}
		
		
		
		
	}
	
	@Override
	public void hongBaoOrderForAutoCancel(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		Map<String, Object> temp = new HashMap<String, Object>();
		// 超过30分钟未付款,自动取消
		temp.put("order_date_end", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 5, -30));
		temp.put("status", "wait_buyer_pay");
		String orderIds = hongbaoOrderDao.selectHongbaoOrderForOrId(temp);
		if(StringUtil.isNotBlank(orderIds)) {
			paramsMap.put("order_ids", orderIds);
			hongbaoOrderService.hongbaoOrderAnnul(paramsMap, result);
		}
	}
}
