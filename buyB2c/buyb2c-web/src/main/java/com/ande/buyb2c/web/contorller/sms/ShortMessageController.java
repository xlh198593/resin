package com.ande.buyb2c.web.contorller.sms;


import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.web.service.sms.ISmsAuthenCodeService;
import com.ande.buyb2c.web.service.sms.ISmsSendService;

/**
 * @author chengzb
 * @date 2018年1月26日上午9:28:25
 * 短信操作类 (已测)
 */
@RestController
@RequestMapping("/shortMessage")
public class ShortMessageController extends AbstractController{
@Resource
private ISmsSendService smsSendService;
@Resource
private ISmsAuthenCodeService smsAuthenCodeService;
/**
 * 发送短信验证码
 * @param phone
 * @return
 */
@RequestMapping("/sendSms")
public JsonResponse<String> send(HttpServletRequest request,String phone){
	JsonResponse<String> json=new JsonResponse<String>();
	if(!StringUtils.isEmpty(phone)){
		int rand=new Random().nextInt(888888)+111111;
		json = smsSendService.send(request,phone,"您的验证码是:"+rand);
		if(json.getRes()==SystemCode.SUCCESS.getCode()){
			//短信发送成功
			logInfo(request,"手机号为:"+phone+",短信发送成功");
			json.set(SystemCode.SUCCESS.getCode()
					, SystemCode.SUCCESS.getMsg());
			//保存验证码
			smsAuthenCodeService.saveCode(phone,rand+"");
		}
	}else{
		json.set(SystemCode.PHONE_IS_NULL.getCode()
				, SystemCode.PHONE_IS_NULL.getMsg());
	}
	return json;
}
/**
 * 校验验证码
 * @param phone
 * @return
 */
@RequestMapping("/checkCode")
public JsonResponse<String> checkCode(String phone,String code){
	JsonResponse<String> json=new JsonResponse<String>();
		if(!StringUtils.isEmpty(phone)&&!StringUtils.isEmpty(code)){
			json=smsAuthenCodeService.checkCode(phone, code);
		}else{
			json.set(SystemCode.PHONE_OR_CODE_IS_NULL.getCode()
					, SystemCode.PHONE_OR_CODE_IS_NULL.getMsg());
		}
	return json;
}
}
