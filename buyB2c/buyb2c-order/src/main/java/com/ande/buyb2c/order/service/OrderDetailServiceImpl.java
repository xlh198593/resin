package com.ande.buyb2c.order.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.order.dao.OrderDetailMapper;
import com.ande.buyb2c.order.entity.OrderDetail;

/**
 * @author chengzb
 * @date 2018年3月9日下午6:23:12
 */
@Service
public class OrderDetailServiceImpl extends BaseServiceImpl<OrderDetail> implements
		IOrderDetailService {
@Resource
private OrderDetailMapper orderDetailMapper;
	@Override
	protected IBaseDao<OrderDetail> getMapper() {
		return orderDetailMapper;
	}

}
