package com.meitianhui.order.service;

import java.math.BigDecimal;
import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface OrderService {

	/**
	 * 物流信息查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void orderLogisticsFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 超级返订单结算列表
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void orderSettlementList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * huiguo结算列表
	 *
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void HgorderSettlementList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/***
	 * 订单结算付款
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author 丁硕
	 * @date 2016年12月15日
	 */
	public void handleOrderSettlementPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 精选特卖订单查询(本地生活)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void zjsOrderListForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 提货码搜索
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void orderByloadedCodeForStoresFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 扣库存(主表)
	 * 
	 * @Title: psGoodsSaleQtyDeduction
	 * @param goods_id
	 * @param sell_qty
	 * @throws BusinessException
	 * @throws Exception
	 * @author tiny
	 */
	public void psGoodsSaleQtyDeduction(String goods_id, String sell_qty) throws BusinessException, Exception;

	/**
	 * 还原库存(主表)
	 * 
	 * @Title: psGoodsSaleQtyDeduction
	 * @param goods_id
	 * @param sell_qty
	 * @throws BusinessException
	 * @throws Exception
	 * @author tiny
	 */
	public void psGoodsSaleQtyRestore(String goods_id, String sell_qty) throws BusinessException, Exception;

	/**
	 * 查询商品信息
	* @Title: psGoodsFindForOrder  
	* @param goods_id
	* @param goods_code
	* @param status
	* @return
	* @throws BusinessException
	* @throws SystemException
	* @throws Exception
	* @author tiny
	 */
	public Map<String, Object> psGoodsFindForOrder(String goods_id, String goods_code)
			throws BusinessException, SystemException, Exception;

	/**
	 * 余额支付
	 * 
	 * @Title: balancePay
	 * @param data_source
	 * @param buyer_id
	 * @param seller_id
	 * @param payment_way_key
	 * @param amount
	 * @param order_type_key
	 * @param out_trade_no
	 * @param detail
	 * @param out_trade_body
	 * @throws Exception
	 * @author tiny
	 */
	public void balancePay(String data_source, String buyer_id, String seller_id, String payment_way_key,
			BigDecimal amount, String order_type_key, String out_trade_no, String detail,
			Map<String, Object> out_trade_body) throws Exception;

	/**
	 * 订单赠送
	 * 
	 * @Title: orderReward
	 * @param data_source
	 * @param buyer_id
	 * @param seller_id
	 * @param payment_way_key
	 * @param amount
	 * @param order_type_key
	 * @param out_trade_no
	 * @param detail
	 * @param out_trade_body
	 * @throws Exception
	 * @author tiny
	 */
	public void orderReward(String data_source, String buyer_id, String seller_id, String payment_way_key,
			BigDecimal amount, String order_type_key, String out_trade_no, String detail,
			Map<String, Object> out_trade_body) throws Exception;

	/**
	 * 订单退款
	 * 
	 * @Title: orderRefund
	 * @param data_source
	 * @param buyer_id
	 * @param seller_id
	 * @param payment_way_key
	 * @param amount
	 * @param order_type_key
	 * @param out_trade_no
	 * @param detail
	 * @param out_trade_body
	 * @throws Exception
	 * @author tiny
	 */
	public void orderRefund(String data_source, String buyer_id, String seller_id, String payment_way_key,
			BigDecimal amount, String order_type_key, String out_trade_no, String detail,
			Map<String, Object> out_trade_body) throws Exception;
	
	/**
	 * 订单原路返还退款
	 * 
	 * @Title: orderRefund
	 * @param data_source
	 * @param buyer_id
	 * @param seller_id
	 * @param payment_way_key
	 * @param amount
	 * @param order_type_key
	 * @param out_trade_no
	 * @param detail
	 * @param out_trade_body
	 * @throws Exception
	 * @author tiny
	 */
	public void orderRefundBack(String data_source, String buyer_id, String seller_id, String payment_way_key,
			BigDecimal amount, String order_type_key, String out_trade_no, String detail,
			Map<String, Object> out_trade_body,String transactionNo) throws Exception;

	/**
	 * 门店会员关系创建
	 * 
	 * @Title: storesMemberRelCreate
	 * @param stores_id
	 * @param consumer_id
	 * @throws Exception
	 * @author tiny
	 */
	public void storesMemberRelCreate(final String stores_id, final String consumer_id);

	/**
	 * 新商品扣库存
	 */
	void newPsGoodsSaleQtyDeduction(String goods_id, String sell_qty, String sku_id) throws BusinessException, Exception;

	/**
	 * 新商品还原库存
	 */
	void newPsGoodsSaleQtyRestore(String goods_id, String qty, String sku_id) throws BusinessException, Exception;

	//public void saveOperateLog(String token, String order_no, String category, String event);
	

	/**
	 * 还原库存(主表)为新自营商品
	 */
	public void psGoodsSaleQtyForOwnRestore(Map<String, Object> paramsMap) throws BusinessException, Exception;

	/**
	 * 查询商品信息用于创建订单(0元购)
	 */
	Map<String, Object> gdFreeGetGoodsFindForOrder(String goods_id, String goods_code)
			throws BusinessException, SystemException, Exception;

	/**
	 * 0元购商品库存扣除
	 */
	void gdFreeGetGoodsSaleQtyDeduction(String goods_id, String sell_qty) throws Exception;

	/**
	 * 0元购商品库存恢复
	 */
	void gdFreeGetGoodsSaleQtyRestore(String goods_id, String restore_qty) throws Exception;

	/**
	 * 还原库存
	 */
	void beikeMallGoodsSaleQtyForOwnRestore(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 贝壳街市商品恢复库存
	 */
	void gdItemStoreSaleQtyForRestore(Map<String, Object> tempMap) throws Exception;

	/**
	 * 贝壳商城扣减库存
	 */
	void beikeMallGoodsSaleQtyDeduction(String goods_id, String qty, String sku_id) throws Exception;

	/**
	 * 礼券专区扣减库存
	 */
	void hongbaoGoodsSaleQtyDeduction(String goods_id, String qty) throws Exception;
}
