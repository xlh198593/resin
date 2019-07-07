package com.ande.buyb2c.admin.controller.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.entity.AdminUser;
import com.ande.buyb2c.user.service.adminUser.IAdminUserService;

/**
 * @author chengzb
 * @date 2018年1月27日上午11:21:19
 * 已测
 */
@RestController
@RequestMapping("/login/adminUser")
public class AdminUserLoginController extends AbstractController{
	@Resource
	private IAdminUserService adminUserService;
	/**
	 * 登录 
	 * 1 成功
	 * 411  用户名不存在或密码错误
	 */
		@RequestMapping("/toLogin")
		public JsonResponse<AdminUser> toLogin(HttpServletRequest request,String userName,String password){
			if(StringUtils.isEmpty(userName)||StringUtils.isEmpty(password)){
				JsonResponse<AdminUser> json=new JsonResponse<AdminUser>
							(SystemCode.FAILURE.getCode(),"用户名或密码不能为空");
				return json;
			}
			return adminUserService.toLogin(request, userName, password);
		}
}
