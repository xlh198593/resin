package com.ande.buyb2c.order.service;



import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.order.entity.Order;

/**
 * @author chengzb
 * @date 2018年1月31日下午1:49:28
 */
public interface IOrderService extends IBaseService<Order>{
public Order getOrderById(Integer orderId);
/**
 * 查用户最新订单使用的收货地址  
 * 返回收货地址id
 */
public Integer getOrderLast(Integer customerId);

public Order getOrderByNo(String orderNo);
public Integer cancelOrder(Integer orderId);
///////////////////////////web端
/**
 * 查询可退款的订单
 */
public PageResult<Map<String,Object>> getCanRefundOrderPage(PageResult<Map<String,Object>> page,Integer customerId);
/**
 * 查询该订单单品是否可以退运费
 */
public int getCanRefundFreight(Integer orderId);

public int updateOrderByOrderNo(Order order);

//查各个状态的订单数
	public List<Integer> getOrderCount(Integer customerId);
	//后台首页统计查询
	public BigDecimal getOrderTotalAmount();
	public BigDecimal getTodayOrderTotalAmount();
	public List<Integer> getAllOrderCount();

}
