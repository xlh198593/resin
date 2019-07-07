package com.ande.buyb2c.web.service.sms;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.ande.base.sms.ISendMessageService;
import com.ande.base.sms.MD5;
import com.ande.base.sms.SMSJsonResponse;
import com.ande.base.sms.SMSSystemCode;
import com.ande.base.sms.entity.Message;
import com.ande.base.sms.entity.SendMessage;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;

/**
 * @author chengzb
 * @date 2018年1月26日上午11:27:54
 */
@Service
public class SmsSendServiceImpl implements ISmsSendService {
	@Resource(name = "yiMeiSendMessageServiceImpl")
	private ISendMessageService sendMessageService;

	@Override
	public JsonResponse<String> send(HttpServletRequest request, String phone, String param) {
		JsonResponse<String> json = new JsonResponse<String>();
		Message message = new Message();
		message.setCompanyId("test");
		message.setProductId("ggg");
		message.setSignName("测试");
		message.setPhone(phone);
		message.setParam(param);
		message.setSign(MD5.checkSign(message, "ppppppggggggddddd"));
		SMSJsonResponse<SendMessage> str = sendMessageService.send(request, message);
		if (str.getRes() == SMSSystemCode.SUCCESS.getCode()) {
			// 下发成功
			json.set(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
		} else if (str.getRes() == SMSSystemCode.REQUEST_OFFEN.getCode()) {
			json.set(SystemCode.REQUEST_OFFEN.getCode(), SystemCode.REQUEST_OFFEN.getMsg());
		}
		return json;
	}

}
