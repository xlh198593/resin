var express = require('express');
var router = express.Router();
var bizUtils = require('../lib/biz-utils');
var request = require('request');
var appConfig = require('../app-config');
var errHandler = require('../lib/err-handler');


router.all("/",function(req,res,next) {
	var requestParams = bizUtils.extend(req.query, req.body);
	  
    //如果手机号缺失参数错误
    if(typeof requestParams.mobile == 'undefined') {
			errHandler.incompleteParams(res);
			return;
		}
		res.end(appConfig.memberPath+"?service=consumer.consumer.imageCheckCode&params={%22mobile%22:"+requestParams.mobile+"}")
	//跳转到图片地址
	// res.redirect(appConfig.memberPath+"?service=consumer.consumer.imageCheckCode&params={%22mobile%22:"+requestParams.mobile+"}")
    
});


module.exports = router;