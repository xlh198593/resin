package com.ande.buyb2c.common.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 
 * @author chengzb
 *@date 2016年9月12日 下午4:01:39
 * Copyright: Copyright (c) 2016
 	公共controller
 */
public abstract class AbstractController {
	/** 日志 */
	protected   final Logger logger = LoggerFactory.getLogger(this.getClass());  
	/**打印日志*/
	protected void logInfo(HttpServletRequest request,String msg){
		logger.info(msg);
	}
	protected void logInfo(String msg){
		logger.info(msg);
	}
	protected void logError(HttpServletRequest request,String msg,Throwable e){
		logger.error(msg,e);
	}
	protected void logError(String msg,Throwable e){
		logger.error(msg,e);
	}
}
