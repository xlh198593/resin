package com.ande.buyb2c.order.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.order.dao.RefundOrderMapper;
import com.ande.buyb2c.order.entity.RefundOrder;

/**
 * @author chengzb
 * @date 2018年2月6日下午2:02:23
 */
@Service
public class RefundOrderServiceImpl extends BaseServiceImpl<RefundOrder>
		implements IRefundOrderService {
@Resource
private RefundOrderMapper refundOrderMapper;
	@Override
	protected IBaseDao<RefundOrder> getMapper() {
		return refundOrderMapper;
	}

}
