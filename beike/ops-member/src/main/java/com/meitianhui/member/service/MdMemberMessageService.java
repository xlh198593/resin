package com.meitianhui.member.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;

public interface MdMemberMessageService {
	/**
	 * 推送消息
	 */
	void sendMessage(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查找推送消息
	 */
	void findMessage(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 用户读取消息
	 */
	void readMessage(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 消息中心首页
	 */
	void messageCenterHomePage(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 删除消息
	 */
	void deleteMessage(Map<String, Object> paramsMap, ResultData result) throws Exception;
}
