package com.ande.buyb2c.receiptAddress.dao;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.receiptAddress.entity.ReceiptAddress;

public interface ReceiptAddressMapper extends IBaseDao<ReceiptAddress>{
	public ReceiptAddress getAddress(@Param("receiptAddressId")Integer receiptAddressId,
			@Param("customerId")Integer customerId);
}