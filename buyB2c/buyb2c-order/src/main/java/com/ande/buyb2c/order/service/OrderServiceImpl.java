package com.ande.buyb2c.order.service;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.order.CalculateUtils;
import com.ande.buyb2c.order.dao.OrderDetailMapper;
import com.ande.buyb2c.order.dao.OrderMapper;
import com.ande.buyb2c.order.entity.Order;
import com.ande.buyb2c.order.entity.OrderDetail;
import com.github.pagehelper.PageHelper;
/**
 * @author chengzb
 * @date 2018年1月31日下午1:50:02
 */
@Service
public class OrderServiceImpl extends BaseServiceImpl<Order>implements IOrderService {
@Resource
private OrderMapper orderMapper;
@Resource
private OrderDetailMapper orderDetailMapper;
	@Override
	protected IBaseDao<Order> getMapper() {
		return orderMapper;
	}
	@Override
	public Order getOrderById(Integer orderId) {
		return orderMapper.getOrderById(orderId);
	}
	@Override
	public Integer getOrderLast(Integer customerId) {
		return orderMapper.getOrderLast(customerId);
	}
	@Transactional
	@Override
	public int insertSelective(Order order) throws Exception{
		List<OrderDetail> orderDetailList=order.getOrderDetailList();
		BigDecimal bigDecimal=new BigDecimal(0);
		for(OrderDetail orderDetail:orderDetailList){
			//设置 总金额
			BigDecimal big=CalculateUtils.mul(orderDetail.getGoodsPrice(),
					new BigDecimal(orderDetail.getGoodsNum()));
			orderDetail.setGoodsTotalPrice(big);
			bigDecimal=bigDecimal.add(big);
		}
		order.setOrderAmount(bigDecimal);//设置订单金额
		order.setOrderTotalAmount(bigDecimal);
		if(order.getFreight()!=null){
			order.setOrderTotalAmount(bigDecimal.add(order.getFreight()));//设置订单总金额
		}
		order.setCreateTime(new Date());
		order.setUpdateTime(new Date());
		orderMapper.insertSelective(order);
		
		for(OrderDetail orderDetail:orderDetailList){
			orderDetail.setOrderId(order.getOrderId());
		}
		orderDetailMapper.addBatch(orderDetailList);
		
		return 1;
	}
	@Override
	public Order getOrderByNo(String orderNo) {
		return orderMapper.getOrderByNo(orderNo);
	}
	@Override
	public Integer cancelOrder(Integer orderId) {
		return orderMapper.cancelOrder(orderId);
	}
	@Override
	public PageResult<Map<String,Object>> getCanRefundOrderPage(PageResult<Map<String,Object>> page,Integer customerId) {
		int pageNo=page.getPageNo();
    	int pageSize=page.getPageSize();
		pageNo = pageNo == 0?1:pageNo;
		pageSize = pageSize == 0?10:pageSize;
		PageHelper.startPage(pageNo,pageSize); 
		return PageResult.toPageResult(orderDetailMapper.getCanRefundOrderList(customerId),page);
	}
	@Override
	public int getCanRefundFreight(Integer orderId) {
		return orderDetailMapper.getCanRefundFreight(orderId);
	}
	@Override
	public int updateOrderByOrderNo(Order order) {
		return orderMapper.updateOrderByOrderNo(order);
	}
	@Override
	public List<Integer> getOrderCount(Integer customerId) {
		return orderMapper.getOrderCount(customerId);
	}
	@Override
	public BigDecimal getOrderTotalAmount() {
		return orderMapper.getOrderTotalAmount();
	}
	@Override
	public BigDecimal getTodayOrderTotalAmount() {
		return orderMapper.getTodayOrderTotalAmount();
	}
	@Override
	public List<Integer> getAllOrderCount() {
		return orderMapper.getAllOrderCount();
	}
}
