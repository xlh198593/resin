package com.ande.buyb2c.receiptAddress.service;

import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.receiptAddress.entity.ReceiptAddress;

/**
 * @author chengzb
 * @date 2018年2月2日下午4:56:46
 */
public interface IReceiptAddressService extends IBaseService<ReceiptAddress>{
	public ReceiptAddress getAddress(Integer receiptAddressId,Integer customerId);
}
