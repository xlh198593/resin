package com.ande.buyb2c.web.service.sms;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.RedisUtil;
import com.ande.buyb2c.common.util.SystemCode;

/**
 * @author chengzb
 * @date 2018年1月26日上午11:06:04
 */
@Service
public class SmsAuthenCodeServiceImpl implements ISmsAuthenCodeService {
	@Resource
	private RedisUtil redisUtil;

	@Override
	public boolean saveCode(String phone, String code) {
		return redisUtil.set("smscode:" + phone, code, 600L);
	}

	@Override
	public JsonResponse<String> checkCode(String phone, String code) {
		JsonResponse<String> json = new JsonResponse<String>();
		String returnCode = (String) redisUtil.get("smscode:" + phone);
		if (!StringUtils.isEmpty(returnCode) && returnCode.equals(code)) {
			json.set(SystemCode.SMS_OK.getCode(), SystemCode.SMS_OK.getMsg());
		}
		return json;
	}

}
