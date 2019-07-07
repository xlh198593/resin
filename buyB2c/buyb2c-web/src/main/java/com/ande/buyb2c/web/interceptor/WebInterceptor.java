package com.ande.buyb2c.web.interceptor;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ande.buyb2c.common.util.RedisUtil;
import com.ande.buyb2c.user.entity.User;

/**
 * @author chengzb
 * @date 2018年1月31日下午6:10:55
 */
@Service
public class WebInterceptor implements HandlerInterceptor{
	@Resource
	private RedisUtil redisUtil;
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		
	}
	/**
	 *  true表示继续流程（如调用下一个拦截器或处理器）；
     *  false表示流程中断（如登录检查失败），不会继续调用其他的拦截器或处理器，此时我们需要通过response来产生响应；
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		String method = request.getMethod();
		System.out.println(method);
		if("OPTIONS".equals(method)){
			//跨域预请求需过滤
			return false;
		}
			String token=request.getParameter("token");
			 response.setHeader("Content-type", "application/json;charset=UTF-8");  
			if(StringUtils.isEmpty(token)){
				response.getWriter().write("{\"res\":301,\"result\":\"未登录\"}");
				return false;
			}
			if("null".equals(redisUtil.get(token))){
				response.getWriter().write("{\"res\":303,\"result\":\"账号在其他设备登录\"}");
				redisUtil.remove(token);
				return false;
			}
			User user=(User) redisUtil.get(token);
			if(StringUtils.isEmpty(user)){
				response.getWriter().write("{\"res\":301,\"result\":\"未登录\"}");
				return false;
			}
			
			return true;
		}
}
