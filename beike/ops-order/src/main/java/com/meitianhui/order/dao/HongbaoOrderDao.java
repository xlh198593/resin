package com.meitianhui.order.dao;


import java.util.List;
import java.util.Map;

import com.meitianhui.order.entity.BeikeMallOrder;
import com.meitianhui.order.entity.BeikeMallOrderCoupons;
import com.meitianhui.order.entity.BeikeMallOrderItem;
import com.meitianhui.order.entity.HongbaoOrder;
import com.meitianhui.order.entity.HongbaoOrderItem;

public interface HongbaoOrderDao {

	/**
	 * 插入红包订单表
	 */
	void insertHongbaoOrder(HongbaoOrder order) throws Exception;

	/**
	 * 插入红包订单商品表 
	 */
	void insertHongbaoOrderItem(HongbaoOrderItem orderItem) throws Exception;

	/**
	 * 查询礼券专区商品订单列表
	 */
	List<Map<String, Object>> selectHongbaoOrderList(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 查询礼券专区商品订单
	 */
	HongbaoOrder selectHongbaoOrder(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 修改贝壳商城订单状态
	 */
	Integer hongBaoOrderUpdate(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 查询礼券专区商品详情
	 */
	Map<String, Object> selectHongBaoOrderDetail(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 查询订单订单对应的礼券id和商品id
	 */
	Map<String, Object> selectHongbaoOrderForCoIdGoId(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 查询订单id
	 */
	String selectHongbaoOrderForOrId(Map<String, Object> paramsMap) throws Exception;

}
