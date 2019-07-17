package com.meitianhui.member.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.ImageUtil;
import com.meitianhui.common.util.RedisUtil;
@Controller
@RequestMapping("/imageCode")
public class ImageCodeController {

	@Autowired
	private ImageUtil imageUtil;
	
	@Autowired
	public RedisUtil redisUtil;
	
	@RequestMapping("/sendImageCode")
	public void sendImageCode(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap) 
			throws ServletException, IOException, BusinessException{
		//保存用户访问日志
		imageUtil.service2(request, response,paramsMap,redisUtil);
	}
}
