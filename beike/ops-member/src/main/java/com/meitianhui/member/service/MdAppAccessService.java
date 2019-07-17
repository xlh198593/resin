package com.meitianhui.member.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface MdAppAccessService {

	int insertAppAccessRecord(Map<String, Object> paramsMap, ResultData result) 
			throws BusinessException, SystemException, Exception;
	
	
}
