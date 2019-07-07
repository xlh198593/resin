package com.resin.controllers;

import com.resin.tool.MailTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resin.common.beans.ResultBean;

@RestController
@RequestMapping("/mail")
public class MailControlller {

	@Autowired
    MailTool mailTool;

	/**
	 * JUNIT里面测试由于jms的关系会失败，所以启动应用来测试
	 */
	@GetMapping("/test")
	public ResultBean<String> test() {
		String to = "1304471323@qq.com";
		mailTool.send("测试发送标题", "这是正文\n没有html", to);
		return new ResultBean<String>("send mail to: " + to);
	}

}
