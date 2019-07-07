package com.ande.buyb2c.order.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.order.entity.Order;

public interface OrderMapper extends IBaseDao<Order>{
	public Order getOrderById(Integer orderId);

	public Integer getOrderLast(Integer customerId);
	public Order getOrderByNo(String orderNo);

	public Integer cancelOrder(Integer orderId);

	public int updateOrderByOrderNo(Order order);
	//查各个状态的订单数
		public List<Integer> getOrderCount(Integer customerId);

		public BigDecimal getOrderTotalAmount();

		public BigDecimal getTodayOrderTotalAmount();

		public List<Integer> getAllOrderCount();
}