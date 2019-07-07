package com.ande.buyb2c.order.dao;

import java.util.List;
import java.util.Map;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.order.entity.OrderDetail;

public interface OrderDetailMapper extends IBaseDao<OrderDetail>{
	public int addBatch(List<OrderDetail> list);

	public List<Map<String,Object>> getCanRefundOrderList(Integer customerId);
	public int getCanRefundFreight(Integer orderId);
	//判断该订单下的商品是否全部退款
	public int getRefundCount(Integer orderId);
}