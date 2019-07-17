package com.meitianhui.member.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;

public interface HongbaoActivityService {

	/**
	 * 查询用户红包活动基本信息
	 */
	void hongbaoActivityInfo(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 抽取红包
	 */
	void drawHongbao(Map<String, Object> paramsMap, ResultData result) throws Exception;

}
