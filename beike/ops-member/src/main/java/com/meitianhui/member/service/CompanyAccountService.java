package com.meitianhui.member.service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

import java.util.Map;

public interface CompanyAccountService {

	public void login(Map<String, Object> paramsMap, ResultData result) throws Exception;

	public void sendSMS(Map<String, Object> paramsMap, ResultData result) throws Exception;

	public void invitation(Map<String, Object> paramsMap, ResultData result) throws Exception;

	public void findMemberInfo(Map<String, Object> paramsMap, ResultData result)throws Exception;

	public void findStoresInfo(Map<String, Object> paramsMap, ResultData result)throws Exception;






}
