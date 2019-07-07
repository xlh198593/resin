package com.ande.buyb2c.order.dao;

import java.util.List;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.order.entity.OrderAttribute;

public interface OrderAttributeMapper extends IBaseDao<OrderAttribute>{
	public int addBatch(List<OrderAttribute> list);
}