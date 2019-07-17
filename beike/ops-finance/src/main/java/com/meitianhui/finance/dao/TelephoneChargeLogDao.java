package com.meitianhui.finance.dao;

import java.util.Map;

import com.meitianhui.finance.entity.FDTelephoneChargeLog;

public interface TelephoneChargeLogDao {
	
	int insert(FDTelephoneChargeLog telephoneChargeLog) throws Exception;
	
	FDTelephoneChargeLog	selectTelephoneCharge(Map<String,Object> tempMap);
	
	int update(FDTelephoneChargeLog telephoneChargeLog) throws Exception;

}
