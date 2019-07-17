package com.meitianhui.member.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.member.entity.MdHongbaoActivity;

public interface HongbaoActivityDao {

	/**
	 * 插入红包表
	 */
	int insertHongbaoActivity(MdHongbaoActivity hongbaoActivity) throws Exception;

	/**
	 * 查找红包活动表基本信息
	 */
	Map<String, Object> findHongbaoActivityInfo(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 查找红包活动表信息
	 */
	List<MdHongbaoActivity> findHongbaoActivity(Map<String, Object> tempMap) throws Exception;

	/**
	 * 修改红包活动表
	 */
	int hongbaoActivityEdit(Map<String, Object> tempMap) throws Exception;

}
