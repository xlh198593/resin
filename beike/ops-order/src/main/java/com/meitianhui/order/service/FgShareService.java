package com.meitianhui.order.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.order.entity.FgShare;


public interface FgShareService {

	void handleSaveShare(Map<String, Object> map,ResultData result) throws Exception;
	
	void selectFgShareByPage(Map<String, Object> map,ResultData result) throws Exception;
	
	public void updateFgShare(Map<String, Object> map,ResultData result) throws Exception;
	
	void queryAllFgShareList(Map<String, Object> map,ResultData result) throws Exception;
}
