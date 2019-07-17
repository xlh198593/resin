package com.meitianhui.order.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.order.entity.FgShare;

public interface FgShareDao {

	int insertFgShare(FgShare fgShare) throws Exception;
	
	List<Map<String, Object>> selectFgShare(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> queryShareList(Map<String, Object> map) throws Exception;
	
	public int updateFgShare(Map<String, Object> map) throws Exception;
	
}
