package com.ande.buyb2c.web.contorller.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.RedisUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.user.service.frontUser.IUserService;
import com.ande.buyb2c.web.UserJsonConvertUtil;

/**
 * @author chengzb
 * @date 2018年2月23日下午5:34:39
 */
@RestController
@RequestMapping("/front/user")
public class UserController extends AbstractController{
	@Resource
	private IUserService userService;
	@Resource
	private UserJsonConvertUtil userJsonConvertUtil;
	@Resource
	private RedisUtil redisUtil;
	/**
	 * 查询详情(已测)
	 */
		@RequestMapping("/getUserById")
		public JsonResponse<User> getUserById(HttpServletRequest request){
			JsonResponse<User> json=new JsonResponse<User>();
			User user=userService.selectByPrimaryKey(userJsonConvertUtil.getUser(request).getUserId());
			if(user!=null){
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
				json.setObj(user);
			}
			return json;
		}
		/**
		 * 修改
		 */
			@RequestMapping("/updateUser")
			public JsonResponse<String> updateUser(User user,HttpServletRequest request){
				JsonResponse<String> json=new JsonResponse<String>();
				user.setUserId(userJsonConvertUtil.getUser(request).getUserId());
				try {
					json.set(SystemCode.SUCCESS.getCode(), 
							SystemCode.SUCCESS.getMsg());
					userService.updateByPrimaryKeySelective(user);
					redisUtil.set(request.getParameter("token"),user);
				} catch (Exception e) {
					logError("修改用户资料异常", e);
				}
				return json;
			}
}
