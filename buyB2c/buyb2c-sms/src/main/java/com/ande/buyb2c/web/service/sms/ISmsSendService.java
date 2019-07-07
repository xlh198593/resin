package com.ande.buyb2c.web.service.sms;

import javax.servlet.http.HttpServletRequest;

import com.ande.buyb2c.common.util.JsonResponse;

/**
 * @author chengzb
 * @date 2018年1月26日上午11:26:53 短信发送
 */
public interface ISmsSendService {
	public JsonResponse<String> send(HttpServletRequest request, String phone, String param);
}
