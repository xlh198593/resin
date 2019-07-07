package com.ande.buyb2c.user.service.adminUser;

import javax.servlet.http.HttpServletRequest;

import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月26日下午5:38:46
 */
public interface IAdminUserService extends IBaseService<AdminUser> {
	public JsonResponse<AdminUser> toLogin(HttpServletRequest request,String userName,String password);
	public JsonResponse<String>  updatePassword(HttpServletRequest request,String oldPassword,String newPassword) throws Exception;
}
