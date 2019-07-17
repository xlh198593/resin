package com.meitianhui.order.dao;

import com.meitianhui.order.entity.FgOrder;
import com.meitianhui.order.entity.FgOrderExtra;
import com.meitianhui.order.entity.FgOrderItem;
import com.meitianhui.order.entity.FgOrderRefundLog;

import com.meitianhui.order.entity.HgOrder;
import com.meitianhui.order.entity.HgOrderExtra;
import com.meitianhui.order.entity.HgOrderItem;

import java.util.List;
import java.util.Map;

public interface HgOrderDao {
	/**
	 * 新增会过订单
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
	 * 更新会过订单详情
	 * 
	 * @param map
	 * @throws Exception
	 */
	public int updateHgOrder(Map<String, Object> map) throws Exception;

	/**
	 * 超级返订单验证(是否重复领取)
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderListCheck(Map<String, Object> map) throws Exception;
	
	/**
	 * 会过订单查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectHgOrder(Map<String, Object> map) throws Exception;
	
	/**
	 * 会过订单商品列表查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectHgOrderItem(Map<String, Object> map) throws Exception;
	
	/**
	 * 超级返订单商品结算列表查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectHgOrderSettlement(Map<String, Object> map) throws Exception;


	/**
	 * 会过返结算订单统计
	 *
	 * @param map
	 * @throws Exception
	 */
	public Map<String, Object> selectHgOrderSettlementCount(Map<String, Object> map) throws Exception;



	/**
	 * 会过订单查询(订单导入)
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectHgOrderDetailForImport(Map<String, Object> map) throws Exception;



	
	/**
	 * 会过订单列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderListByOwn(Map<String, Object> map) throws Exception;
	
	
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
	 * 订单列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFgOrderListForSmallProgram(Map<String, Object> map) throws Exception;
	
	/**
	 * 会过订单明细查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectHgOrderDetail(Map<String, Object> map) throws Exception;
	
	/**
	 * 会过订单明细查询
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectHgOrderDetailMoney(Map<String, Object> map) throws Exception;


	/**
	 * 超级返结算订单统计
	 * 
	 * @param map
	 * @throws Exception
	 */
	public Map<String, Object> selectFgOrderSettlementCount(Map<String, Object> map) throws Exception;
	/**
	 * 会过返结算订单统计
	 *
	 * @param map
	 * @throws Exception
	 */
	public Map<String, Object> HgselectFgOrderSettlementCount(Map<String, Object> map) throws Exception;

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
	 * 查询会过订单下单成功数
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public  List<Map<String,Object>> selectHgOrderByStatus(Map<String, Object> map) throws Exception;
	
	/**
	 * 供应商下领了么数量统计
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public  List<String> selectFgOrderSupplyCount(Map<String, Object> map) throws Exception;
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
	 *  返回金额列表订单
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String, Object>> selectHgOrderDetailMoneyTabulationFind(Map<String, Object> paramsMap);
}
