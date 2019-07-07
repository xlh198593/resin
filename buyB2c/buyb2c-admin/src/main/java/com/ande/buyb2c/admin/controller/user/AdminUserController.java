package com.ande.buyb2c.admin.controller.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.RedisUtil;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.entity.AdminUser;
import com.ande.buyb2c.user.service.adminUser.IAdminUserService;

/**
 * @author chengzb
 * @date 2018年1月26日下午5:42:31
 * 管理员(已测)
 */
@RestController
@RequestMapping("/admin/adminUser")
public class AdminUserController extends AbstractController{
@Resource
private IAdminUserService adminUserService;
@Resource
private RedisUtil redisUtil;
@Resource
private SessionUtil<AdminUser> sessionUtil;
/**
 * 查看账户信息
 */
	@RequestMapping("/getAdminUserById")
	public JsonResponse<AdminUser> getAdminUserById(HttpServletRequest request){
		JsonResponse<AdminUser> json=new JsonResponse<AdminUser>();
		AdminUser user=adminUserService.selectByPrimaryKey(sessionUtil.getAdminUser(request).getAdminId());
		if(user!=null){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			user.setPassword("");
			user.setRand("");
			json.setObj(user);
		}
		return json;
	}
	/**
	 * 更新账户设置
	 */
		@RequestMapping("/updateAdminUserById")
		public JsonResponse<AdminUser> updateAdminUserById(HttpServletRequest request,AdminUser adminUser){
			JsonResponse<AdminUser> json=new JsonResponse<AdminUser>();
			adminUser.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
			try {
				adminUserService.updateByPrimaryKeySelective(adminUser);
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
			} catch (Exception e) {
				logError("更新账户设置失败", e);
			}
			return json;
		}
		/**
		 * 修改密码
		 */
			@RequestMapping("/updatePassword")
			public JsonResponse<String> updatePassword(HttpServletRequest request,String oldPassword,String newPassword){
				JsonResponse<String> json=new JsonResponse<String>();
				if(StringUtils.isEmpty(newPassword)||StringUtils.isEmpty(oldPassword)){
					json.setResult("密码不能为空");
					return json;
				}
				try {
					json=adminUserService.updatePassword(request,oldPassword,newPassword);
				} catch (Exception e) {
					logError("更新账户密码失败", e);
				}
				return json;
			}
}
