package com.ande.buyb2c.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.RedisUtil;
import com.ande.buyb2c.user.entity.User;

/**
 * @author chengzb
 * @date 2018年2月2日下午2:18:49
 */
@Service
public class UserJsonConvertUtil {
	@Resource
	private RedisUtil redisUtil;
public User getUser(HttpServletRequest request){
	return (User) redisUtil.get(request.getParameter("token"));
}
}
