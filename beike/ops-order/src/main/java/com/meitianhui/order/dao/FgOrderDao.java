package com.meitianhui.order.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.meitianhui.order.entity.FgOrder;
import com.meitianhui.order.entity.FgOrderCommission;
import com.meitianhui.order.entity.FgOrderExtra;
import com.meitianhui.order.entity.FgOrderItem;
import com.meitianhui.order.entity.FgOrderRefundLog;
import com.meitianhui.order.entity.RcOrder;
import com.meitianhui.order.entity.RcOrderTardeLog;

public interface FgOrderDao {
	/**
	 * 新增超级返订单
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void insertFgOrder(FgOrder fgOrder) throws Exception;

	/**
	 * 新增消费者免费领记录
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void insertFgOrderExtra(FgOrderExtra fgOrderExtra) throws Exception;

	/**
	 * 超级返订单商品列表
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void insertFgOrderItem(FgOrderItem fgOrderItem) throws Exception;

	/**
	 * 领了么退款日志
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void insertFgOrderRefundLog(FgOrderRefundLog fgOrderRefundLog) throws Exception;

	/**
	 * 更新超级返订单详情
	 * 
	 * @param map
	 * @throws Exception
	 */
	public int updateFgOrder(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单验证(是否重复领取)
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderListCheck(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrder(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单商品列表查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderItem(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单商品结算列表查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderSettlement(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单查询(订单导入)
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderDetailForImport(Map<String, Object> map) throws Exception;

	/**
	 * 会过订单列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderListByOwn(Map<String, Object> map) throws Exception;

	/**
	 * 新自营商品订单列表查询
	 */
	public List<Map<String, Object>> selectFgOrderListByNewOwn(Map<String, Object> map) throws Exception;

	/**
	 * 会过订单列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderListByHuiguo(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderList(Map<String, Object> map) throws Exception;

	/**
	 * 超级返退款订单列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderByRefundList(Map<String, Object> map) throws Exception;

	/**
	 * 订单列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderListForSmallProgram(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单明细查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderDetail(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单明细查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectFgOrderDetailMoney(Map<String, Object> map) throws Exception;

	/**
	 * 超级返结算订单统计
	 * 
	 * @param map
	 * @throws Exception
	 */
	public Map<String, Object> selectFgOrderSettlementCount(Map<String, Object> map) throws Exception;

	/**
	 * 店东推荐消费者进行免费领记录统计
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectStoresRecomConsumerFreeGetCount(Map<String, Object> map) throws Exception;

	/**
	 * 消费者领了没订单对于的推荐门店
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectFgOrderExtra(Map<String, Object> map) throws Exception;

	/**
	 * 会员领了么数量统计
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectFgOrderCount(Map<String, Object> map) throws Exception;

	/**
	 * 查询领了么订单下单成功数
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderByStatus(Map<String, Object> map) throws Exception;

	/**
	 * 供应商下领了么数量统计
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<String> selectFgOrderSupplyCount(Map<String, Object> map) throws Exception;

	/**
	 * 违规订单统计
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectFgOrderViolationCount(Map<String, Object> map) throws Exception;

	/**
	 * 查询超时的订单
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectTimeoutFgOrder(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单超时自动取消
	 * 
	 * @param map
	 * @throws Exception
	 */
	public int fgOrderAutoCancel(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单明细查询
	 * 
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> selectFgOrderDetail2017(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 返回金额列表订单
	 * 
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> selectFgOrderDetailMoneyTabulationFind(Map<String, Object> paramsMap);

	/**
	 * 查询操作订单日志列表
	 * 
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> queryOperateLogList(Map<String, Object> paramsMap);

	/**
	 * 撤销订单日志查询(验证订单是否撤销返款过)
	 * 
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> selectOperateLog(String order_no);

	/* 保存操作日志 */
	public void saveOperateLog(Map<String, Object> params);

	/**
	 * 查询达到转账条件的店东
	 */
	List<Map<String, Object>> selectFgOrderCommissionForSevenDaysAgo() throws Exception;

	/**
	 * 将数据插入佣金表
	 */
	public int insertFgOrderCommission(FgOrderCommission fgOrderCommission);

	/**
	 * 超级返订单列表
	 * 
	 */
	public List<Map<String, Object>> selectFgOrderCommissionList(Map<String, Object> map) throws Exception;

	/**
	 * 更新佣金订单
	 */
	public int updateFgOrderCommission(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 查询总佣金
	 */
	public BigDecimal selectFgOrderCommissionTotal(Map<String, Object> paramsMap);

	/**
	 * 查询当周待转佣金
	 */
	public BigDecimal selectFgOrderCommissionWeekAmount(Map<String, Object> paramsMap);

	/**
	 * 查询该店东 会员数量
	 */
	public List<Map<String, Object>> selectFgOrderCommissionMemberCount(Map<String, Object> paramsMap);

	/**
	 * 查询当周待转佣金日志
	 */
	public List<Map<String, Object>> selectFgOrderCommissionWeekAmountLog(Map<String, Object> paramsMap)
			throws Exception;

	/**
	 * 查询超时的新自营商品的订单
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectTimeoutFgOrderForOwn(Map<String, Object> map) throws Exception;

	/**
	 * 新自营订单超时自动取消
	 * 
	 * @param map
	 * @throws Exception
	 */
	public int fgOrderForOwnAutoCancel(Map<String, Object> map) throws Exception;

	/**
	 * 充值订单创建
	 */
	public Integer rcOrderCreate(RcOrder rcOrder) throws Exception;

	/**
	 * 订单交易日志创建
	 */
	public Integer rcOrderTardeLogCreate(RcOrderTardeLog rcOrderTardeLog) throws Exception;
	
   /**
    * 查找充值日志订单
    */
	public List<RcOrderTardeLog> selectRcOrderTardeLog (Map<String, Object> temp)throws Exception;

	/**
	 * 查找充值订单
	 */
	public RcOrder selectRcOrder(Map<String, Object> paramsMap)throws Exception;

	/**
	 * 修改充值订单状态
	 */
	public Integer updateRcOrderToStatus(Map<String, Object> paramsMap)throws Exception;

	/**
	 * 修改日志订单状态
	 */
	public Integer updateRcOrderTradeLogToStatus(Map<String, Object> paramsMap)throws Exception;

	/**
	 * 查询充值记录(分页)
	 */
	public List<RcOrder> selectRcOrderList(Map<String, Object> paramsMap)throws Exception;

	/**
	 * 超时贝壳商品订单自动取消 
	 */
	void beikeMallOrderForAutoCancel(Map<String, Object> tempMap);

	/**
	 * 查找贝壳商城订单超时的商品
	 */
	List<Map<String, Object>> selectTimeoutBeikeMallOrder(Map<String, Object> tempMap);
	
	
	public List<String> selectTelephoneOrderStatus(List<String> list);
}
