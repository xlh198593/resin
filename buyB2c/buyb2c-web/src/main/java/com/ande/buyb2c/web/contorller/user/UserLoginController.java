package com.ande.buyb2c.web.contorller.user;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.user.service.frontUser.IUserService;

/**
 * @author chengzb
 * @date 2018年1月26日下午3:33:21
 * 前端用户 (已测)
 */
@RestController
@RequestMapping("/user")
public class UserLoginController extends AbstractController{
@Resource
private IUserService userService;
/**
 * 
 * @param phone
 * @param code
 * @return  登录注册
 */
@RequestMapping("/toLogin")
public JsonResponse<User> toLogin(String phone,String code){
	JsonResponse<User> json=new JsonResponse<User>();
	if(StringUtils.isEmpty(phone)||StringUtils.isEmpty(code)){
		json.setResult("phone或者code不能空");
		return json;
	}
	try {
		User user=userService.toLogin(phone, code);
		if(user!=null){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(user);
		}
	} catch (Exception e) {
		logError("用户注册失败", e);
	}
	return json;
}
}
