package com.ande.buyb2c.web.contorller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.ande.buyb2c.common.util.HttpsUtil;
import com.ande.buyb2c.common.util.RedisUtil;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.user.service.frontUser.IUserService;

/**
 * @author adayang
 * @version 2017年2月8日下午5:55:14
 *  微信授权 静默授权
 */

@Controller
@RequestMapping("/wxAuthor")
public class WxAuthors {
	private static Logger log =LoggerFactory.getLogger(WxAuthors.class);
	@Resource
	private IUserService userService;
	@Resource
	private RedisUtil redisClient;
	@Value("${app.wxAuth.authReturnUrl}")
	private String authReturnUrl;
	@Value("${app.wxpay.appId}")
	private String appId;
	@Value("${app.wxAuth.appSecret}")
	private String appSecret;
	
	/**
	 * 静默授权
	 * base
	 */
	@RequestMapping("/default")
	public void getUserBase(@RequestParam(required=false,value="code")String code,HttpServletRequest request,HttpServletResponse response,@RequestParam(required=false,value="state")String state){
		JSONObject jsonObject =null;
		String openid=""; //微信用户openid
		String result =""; //接口返回字符
		String requesUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APP_ID&secret=APP_SECRET&code=USE_CODE&grant_type=authorization_code";//获取用户openid地址
		
		try {
			if(!"".equals(code)){
					requesUrl = requesUrl.replace("APP_ID",appId).replace("APP_SECRET",appSecret).replace("USE_CODE", code);
					result = HttpsUtil.httpsRequest(requesUrl, "GET", null);
				if(!"".equals(result)&&result.indexOf("openid")>0){
					jsonObject =JSONObject.parseObject(result);
					//获取到openid
					openid=jsonObject.get("openid").toString();
					
					//授权后跳入的页面(调到微信支付)
//					System.out.println(code+";"+state+":"+openid);
//					response.sendRedirect("http://localhost:4865/page/online_pay.html?openid="+openid+"&orderNumber="+state);
//					adUserService.
					String token=state;
					User user=(User) redisClient.get(token);
					user.setOpenId(openid);
					userService.updateByPrimaryKeySelective(user);
					
					redisClient.set(token,user);//
					response.sendRedirect(authReturnUrl);
				}
			}else{
				//获取code失败 用户不同意授权
				log.error("获取静默code失败...."+code);
			}
		}catch (Exception e) {
			log.error("授权失败",e);
	}
		
	}
}
