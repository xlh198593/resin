package com.meitianhui.order.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.meitianhui.order.entity.BeikeMallOrder;
import com.meitianhui.order.entity.BeikeMallOrderCoupons;
import com.meitianhui.order.entity.BeikeMallOrderItem;

public interface BeikeMallOrderDao {

	/**
	 * 插入贝壳商品订单表
	 */
	void insertBeikeMallOrder(BeikeMallOrder order) throws Exception;

	/**
	 * 插入贝壳商品订单补充表 
	 */
	void insertBeikeMallOrderItem(BeikeMallOrderItem orderItem) throws Exception;

	/**
	 * 查询贝壳商城订单列表
	 */
	List<Map<String, Object>> selectBeikeMallOrderList(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 查询贝壳商城超时订单列表
	 */
	List<Map<String, Object>> selectOrderTimeOutList(Map<String, Object> paramsMap) throws Exception;
	

	/**
	 * 修改贝壳商城订单状态
	 */
	Integer beikeMallOrderUpdate(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 取得订单的贝壳抵抗数量和会员ID
	 */
	Map<String,Object>  getBeikeCredit(@Param("orderNo") String orderNo) throws  Exception;
	
	
	/**
	 * 取贝壳商城订单的数量
	 * @param paramsMap
	 * @return
	 */
	Integer  getMallOrderNum(Map<String, Object> paramsMap);

	/**
	 * 查找当月购买天数超过2天的会员
	 */
	List<String> selectBeikeMallOrderMemberList(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 查询贝壳商城订单总贝壳数
	 */
	Integer selectBeikeMallOrderShell(Map<String, Object> paramsMap);

	/**
	 * 查询订单详情
	 */
	Map<String, Object> selectBeikeMallOrderDetail (Map<String, Object> paramsMap) throws Exception;

	/**
	 * 查询礼券
	 */
	List<BeikeMallOrderCoupons> selectBeikeMallOrderCoupons(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 	 通过订单号查询订单
	 */
	BeikeMallOrder  findOrderInfoByNo(Map<String, Object> paramsMap);
	
	/**
	 * 	 查询已经使用的礼券Id 和数量
	 */
	List<Map<String, Object>> selectMemberCouponsId(Map<String, Object> paramsMap);
}
