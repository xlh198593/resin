package com.ande.buyb2c.web.service.sms;

import com.ande.buyb2c.common.util.JsonResponse;

/**
 * @author chengzb
 * @date 2018年1月26日上午11:03:15 短信验证码
 */
public interface ISmsAuthenCodeService {
	boolean saveCode(String phone, String code);

	/**
	 * 
	 * @param phone
	 * @param code
	 * @return 对象中的res属性等于systemcode.sms_ok 为成功
	 */
	JsonResponse<String> checkCode(String phone, String code);
}
