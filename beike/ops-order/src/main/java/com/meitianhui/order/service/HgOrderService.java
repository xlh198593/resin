package com.meitianhui.order.service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

import java.util.Map;

/**
 * 会过
 * @author 陆游
 *
 */
public interface HgOrderService {


	/**
	 * 会过订单结算
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderSettlement(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 自营商贸领了么订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderCreateNotity(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 自营订单支付通知
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleOwnOrderPayNotify(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 会过订单支付通知
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleHuiguoOrderPayNotify(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 领了么订单验证(验证会员是领取过此商品)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetValidate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 消费者领了么订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerFreeGetRecordCreate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 消费者领了么订单创建(订单类型必填)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerOrderCreate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	

	/**
	 * 消费者会过商品订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void huiguoOrderCreate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	

	
	/**
	 * 消费者自营商品订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void OwnOrderCreate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	

	
	/**

	 * 店东领了么订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesFreeGetRecordCreate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 消费者现场领取
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleLocalFreeGet(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么订单导入
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFreeGetOrderImport(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么订单取消
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderCancelled(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么订单取消(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderCancelledForOp(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;


	/**
	 * 会过订单取消(运营)
	 *
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleHgOrderCancelledForOp(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;





	/**
	 * 会过订单编辑(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void hgOrderForOpEdit(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么订单确定
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderConfirm(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 查询是否成功下单
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */  
	public void selectFgOrderByStatus(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;

	
	/**
	 * 会过订单列表查询(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void hgOrderListForOpFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	
	/**
	 * 会过订单列表金额查询（运营）
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void hgOrderListForMoneyFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 订单详情查询(运营)
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void hgOrderDetailForOperationFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 订单详情查询(领有惠)
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void fgOrderDetailForConsumerFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 订单详情查询(内部调用)
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void fgOrderDetailForOwnFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
		
	/**
	 * 我要批结算订单统计查询(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderSettlementCount(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	/**
	 * huiguo结算订单统计查询(运营)
	 *
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void hgOrderSettlementCount(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	/**
	 * 供应商下领了么订单统计查询(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderSupplyCount(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;

	
	/**
	 * 领了么订单列表查询 自营
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderListByOwnFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 领了么订单列表查询 会过
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderListByHuiguoFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 领了么订单列表查询 淘淘领
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderList(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 领了么订单列表查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 领了么订单留言
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderMessage(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么违规记录统计
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderViolationCount(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 异常订单
	* @Title: fgOrderExceptionCount  
	* @param paramsMap
	* @param result
	* @throws BusinessException
	* @throws SystemException
	* @throws Exception
	* @author tiny
	 */
	public void fgOrderExceptionCount(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么订单返款撤销
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderRevoke(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;


	/**
	 * 会过订单返款撤销
	 *
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleHgOrderRevoke(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;


	/**
	 * 店东推荐的参加领了么的消费者列表
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesRecomConsumerFreeGetListFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 店东推荐的参加领了么的消费者列表(会员列表)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesRecomConsumerFreeGetListFindForMemberList(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么会员黑名单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgBlacklistCreate(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么会员黑名单查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgBlacklistFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么会员黑名单删除
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgBlacklistDelete(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;
	
	/**
	 * 领了么违规会员验证
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgViolationCheck(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;

	/**
	 * 返回金额列表订单
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException 
	 * @throws SystemException 
	 */
	public void hgOrderFormMoneyTabulationFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException;

	/**
	 * 会过订单结算
	 *
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleHgOrderSettlement(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;



}
