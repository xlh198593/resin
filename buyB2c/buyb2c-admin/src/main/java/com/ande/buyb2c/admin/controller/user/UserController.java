package com.ande.buyb2c.admin.controller.user;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.user.service.frontUser.IUserService;

/**
 * @author chengzb
 * @date 2018年1月26日下午4:39:16
 * 会员管理
 */
@RestController
@RequestMapping("/admin/user")
public class UserController extends AbstractController{
	@Resource
	private IUserService userService;
/**
 * 查询会员列表(已测)
 */
	@RequestMapping("/getUserPage")
	public JsonResponse<PageResult<User>> getUserPage(User user,PageResult<User> page){
		JsonResponse<PageResult<User>> json=new JsonResponse<PageResult<User>>();
		userService.queryByPage(page, user);
		if(page.getTotal()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(page);
		}
		return json;
	}
	/**
	 * 查询会员详情(已测)
	 */
		@RequestMapping("/getUserById")
		public JsonResponse<User> getUserById(Integer userId){
			JsonResponse<User> json=new JsonResponse<User>();
			User user=userService.selectByPrimaryKey(userId);
			if(user!=null){
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
				json.setObj(user);
			}
			return json;
		}
}
