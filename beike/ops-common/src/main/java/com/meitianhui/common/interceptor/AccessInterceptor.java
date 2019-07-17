package com.meitianhui.common.interceptor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;

/***
 * 访问拦截器，主要记录用户访问的日志
 */
public class AccessInterceptor implements HandlerInterceptor{

	private static final Log logger = LogFactory.getLog(AccessInterceptor.class);
	
	private static List<String> checkServices=new ArrayList<String>();
	

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String uri=request.getRequestURI();
		String service=request.getParameter("service");
		String token=request.getParameter("token");
		
		//logger.info("node转发的UserAgent："+UserAgent);
		//如果当前service在设置的service中，则进行处理
		if(getCheckServices().contains(service)){
			String userAgent=request.getParameter("UserAgent");
			logger.info("userAgent:"+userAgent);
			String[] agentArray=parseAgent(userAgent);
			if(agentArray==null || agentArray.length<3){
				logger.error("userAgent:"+userAgent+",User-Agent参数格式错误");
				return true;
			}
			// 获取消费者的收货地址信息
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "consumer.consumer.saveAppAccessLog");
			bizParams.put("access_method", uri+"["+service+"]");
			bizParams.put("app_type", !StringUtil.isEmpty(agentArray[0]) ? agentArray[0].trim():null);
			bizParams.put("app_type_detail",  !StringUtil.isEmpty(agentArray[1]) ? agentArray[1].trim():null);
			bizParams.put("app_version",  !StringUtil.isEmpty(agentArray[2]) ? agentArray[2].trim():null);
			if(agentArray.length>=4){
				bizParams.put("member_id",  !StringUtil.isEmpty(agentArray[3]) ? agentArray[3].trim():null);
			}
			bizParams.put("token",  token);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
			//Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		    logger.info("uri:"+uri+",service:"+service+",保存访问日志结果："+resultStr);
		}
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private static String[] parseAgent(String agent){
		if(StringUtil.isEmpty(agent)){
			return null;
		}
		return agent.split("\\|");
	}
	

	/**
	 * 需要拦截的service集合
	 */
	static {
		checkServices.add("infrastructure.consumerLogin");//领有惠app登陆
		checkServices.add("finance.consumer.mainOrderPay");//领有惠app支付接口
		checkServices.add("order.consumerFreeGetRecordCreate");//领有惠app淘淘领商品创建订单接口
	}
	
	private static List<String> getCheckServices(){
		return checkServices;
	}
	

}
