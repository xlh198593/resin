package com.meitianhui.order.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.order.entity.BeikeMallOrderCoupons;

public interface BeikeMallOrderCouponsDao {
	/**
	 * 插入贝壳商城订单日志
	 */
	Integer insertBeikeMallOrderCoupons(List<BeikeMallOrderCoupons> couponsList) throws Exception;
	
	/**
	 * 	查询订单礼券
	 */
	Map<String,Object>  findOrderCoupons(Map<String,Object> paramMap);
	
	
	Integer  delOrderCoupons(Map<String,Object> paramMap);

}
