package com.meitianhui.order.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.order.entity.HongBaoOrderCoupons;

public interface HongBaoOrderCouponsDao {
	/**
	 * 插入贝壳商城订单日志
	 */
	Integer insertHongBaoOrderCoupons(List<HongBaoOrderCoupons> couponsList) throws Exception;

	/**
	 * 查询礼券商城礼券关系表
	 */
	List<HongBaoOrderCoupons> selectHongBaoOrderCoupons(Map<String, Object> paramsMap) throws Exception;

}
