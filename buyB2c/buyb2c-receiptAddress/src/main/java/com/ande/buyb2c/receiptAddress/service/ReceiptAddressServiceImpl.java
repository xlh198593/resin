package com.ande.buyb2c.receiptAddress.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.receiptAddress.dao.ReceiptAddressMapper;
import com.ande.buyb2c.receiptAddress.entity.ReceiptAddress;

/**
 * @author chengzb
 * @date 2018年2月2日下午4:57:42
 */
@Service
public class ReceiptAddressServiceImpl extends BaseServiceImpl<ReceiptAddress>
		implements IReceiptAddressService {
@Resource
private ReceiptAddressMapper receiptAddressMapper;
	@Override
	protected IBaseDao<ReceiptAddress> getMapper() {
		return receiptAddressMapper;
	}
	@Override
	public ReceiptAddress getAddress(Integer receiptAddressId,Integer customerId) {
		return  receiptAddressMapper.getAddress(receiptAddressId,customerId);
	}
}
