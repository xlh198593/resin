var express = require('express');
const app = express();
var router = express.Router();
var url = require('url');
var request = require('request');
var ipware = require('ipware')();
var parseString = require('xml2js').parseString;
var md5 = require('md5');

var logger = require('../../lib/log4js').getLogger('djk-router');
var appConfig = require('../../app-config');
var errHandler = require('../../lib/err-handler');
var bizUtils = require('../../lib/biz-utils');

//进入代金券
// router.all('/index', function(req, res, next) {
// 	try {
// 		const requestParams = bizUtils.extend(req.query, req.body);
// 		// 请求参数
// 		var hostname = appConfig.appBaseName;
// 		requestParams.pageHost = hostname;
		
// 		//参数缺失
// 		if (typeof requestParams.CREDTENTIAL == 'undefined' || typeof requestParams.SIGN_DATA == 'undefined') {
// 			errHandler.incompleteParams(res);
// 			return;
// 		};
// 		var FormData = {
// 			service: 'consumer.hebaoUserInfoFind',
// 			params: JSON.stringify({
// 				credtential: requestParams.CREDTENTIAL,
// 				signData: requestParams.SIGN_DATA
// 			})
// 		};
// 		request.post(appConfig.memberPath, {form:FormData}, function(error, response, body) {
// 			if(!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
// 				res.render('b/coupon/index', {
// 					requestParams:requestParams,
// 				});
// 			} else {
// 				errHandler.systemError(res, error);
// 				return;
// 			}
// 		});

		
// 	} catch(e) {
// 		errHandler.systemError(res, e);
// 		return;
// 	}
// });

router.all('/index', function(req, res, next) {
	try {
		const requestParams = bizUtils.extend(req.query, req.body);
		console.log("和包进入requestParams::::::",requestParams)
		var accessToken = "";
		// 请求参数
		var hostname = appConfig.appBaseName;
		requestParams.pageHost = hostname;
		
		//参数缺失
		if (typeof requestParams.account == 'undefined') {//如果手机号为空
			if (typeof requestParams.code == 'undefined') {//code也为空
				console.log("code为undefined")
				res.redirect("https://www.cmpay.com/open/auth/service/extreq?response_type=code&client_id=10020113&redirect_uri=https://appportal.meitianhui.com/cmpay/lyh/index&scope=getAuthUsr:1.0&PAGE_TYP=1");
			} else {
				console.log("code不为undefined:::code:::",requestParams.code)
				request.get("https://www.cmpay.com/open/auth/service/token?grant_type=authorization_code&code="+requestParams.code+"&redirect_uri=https://appportal.meitianhui.com/cmpay/lyh/index&client_secret=0512413061", function(error, response, body) {
					console.log("jinlaile::body:::",body)
					var body = JSON.parse(body);
					if (body.access_token != "undefined") {
						function dataStr() {
							var date = new Date();
                             Y = date.getFullYear();
                             M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1);
                             D = date.getDate();
 	 						 h = date.getHours()<10?"0"+date.getHours():date.getHours();
							 m = date.getMinutes();
							 s = date.getSeconds(); 
							 return Y+M+D+h+m+s;
						}
						var operationtime = dataStr();
						//由于和包那边的md5加密里面封装了一下 所以想我们后台请求一次有java有对应的util
                        var formData = {
							service:"consumer.hebaoMd5Sign",
							params : JSON.stringify({
								accesstoken:body.access_token,
								appid:"10020113",
								format:"json",
								operationtime:operationtime,
								signaturemethod:"MD5",
								appsecret:"cb917908caee354b54f573ca837befe2"
							})
						};
						accessToken = body.access_token,
						console.log("request::::",formData)
						request.post(appConfig.memberPath, {form: formData}, function(error, response, body) {
							try {
								if(!error && response.statusCode == 200) {
									  var body = JSON.parse(body);
									  console.log("java::::",body)
									  if(body.rsp_code == 'succ') {
										var FormData ={
											accesstoken:accessToken,
											appid:"10020113",
											operationtime:operationtime,
											format:"json",
											signaturemethod:"MD5",
											signaturevalue:body.data,};
									console.log("signaturevalueObj:::::",FormData)
									var url = "https://www.cmpay.com/open/mpop/7/getAuthUsr/1.0";
									request.post(url,{form:FormData}, function(error, response, body) {
										console.log("手机号:::",body)
										var body = JSON.parse(body);
                                                                                var mobile = body.body.mbl_no;
                                                                                   console.log(mobile);
										if (mobile) {
                                                                                                                                      
											var FormData = {
												service: 'consumer.hebaoUserInfoFind',
												params: JSON.stringify({
													mobile:mobile
												})
											};
											request.post(appConfig.memberPath, {form:FormData}, function(error, response, body) {
												if(!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
												//	res.render('b/coupon/index', {
												//		requestParams:requestParams,
												//	});

                                                                                                res.redirect("https://appportal.meitianhui.com/cmpay/lyh/index?account="+mobile+"&requestParams="+requestParams);
												} else {
													errHandler.systemError(res, error);
													return;
												}
											}); 
										}
									});
									  } else {

									  }
								} else {
									errHandler.systemError(res, error);
									return;
								}
							} catch(e) {
								errHandler.systemError(res, e);
								return;
							}
						})

					} else {
                      
					}
				});
			}
		} else {


console.log(requestParams);


var FormData = {
                        service: 'consumer.hebaoUserInfoFind',
                        params: JSON.stringify({
                            //account:requestParams.account
                            mobile:requestParams.account
                        })
                                };

                    request.post(appConfig.memberPath, {form:FormData}, function(error, response, body) {
                        if(!error && response.statusCode == 200) {  // 请求成功后直接返回查询结果
                            res.render('b/coupon/index', {
                                requestParams:requestParams,
                            });
                        } else {
                            errHandler.systemError(res, error);
                            return;
                        }
                });
 //                          const requestParams = bizUtils.extend(req.query, req.body);
//
//			request.post(appConfig.memberPath, {form:FormData}, function(error, response, body) {
//				if (body.mbl_no) {
//					var FormData = {
//						service: 'consumer.hebaoUserInfoFind',
//						params: JSON.stringify({
//							//account:requestParams.account
//							mobile:requestParams.account
//						})
//					};
//					request.post(appConfig.memberPath, {form:FormData}, function(error, response, body) {
//						if(!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
//							res.render('b/coupon/index', {
//								requestParams:requestParams,
//							});
//						} else {
//							errHandler.systemError(res, error);
//							return;
//						}
//					}); 
//				}
//			});
		}
    } catch(e) {
		errHandler.systemError(res, e);
		return;
	}
});
//保存代金券的详情
router.all('/save', function(req, res, next) {
	const requestParams = bizUtils.extend(req.query, req.body);
                   console.log(requestParams);
	try {
		var FormData = {
			service: 'consumer.hebaoGetCashCoupon',
			params: JSON.stringify({
				//credtential: requestParams.credtential,
				mobile: requestParams.mobile,
				cashCouponCode:requestParams.cashCouponCode,
				cashcouponValue:requestParams.cashcouponValue,
			})
		};
		request.post(appConfig.memberPath, {form:FormData}, function(error, response, body) {
			if(!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
               res.send(body)
			} else {
				errHandler.systemError(res, error);
				return;
			}
		});

		
	} catch(e) {
		errHandler.systemError(res, e);
		return;
	}
});

//进入代金券详情页面
router.all('/detail', function(req, res, next) {
	try {
		// 请求参数
		const requestParams = bizUtils.extend(req.query, req.body);
                 console.log(requestParams);
		var hostname = appConfig.appBaseName;
		requestParams.pageHost = hostname;
		res.render('b/coupon/detail', {
			requestParams:requestParams
		});
	} catch(e) {
		errHandler.systemError(res, e);
		return;
	}
});

//进入代金券详情页面
router.all('/list', function(req, res, next) {
           
	const requestParams = bizUtils.extend(req.query, req.body);
             console.log(requestParams.mobile);
	try {
		var FormData = {
			service: 'consumer.hebaoCashCouponListFind',
			params: JSON.stringify({
				mobile: requestParams.mobile,
			})
		};
		request.post(appConfig.memberPath, {form:FormData}, function(error, response, body) {
			if(!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
               res.send(body)
			} else {
				errHandler.systemError(res, error);
				return;
			}



		});
	} catch(e) {
		errHandler.systemError(res, e);
		return;
	}

});

//进入二维码详情页面
router.all('/qrcode', function(req, res, next) {
	try {
		// 请求参数
		const requestParams = bizUtils.extend(req.query, req.body);
		res.render('b/coupon/qrcode', {
			requestParams:requestParams
		});
	} catch(e) {
		errHandler.systemError(res, e);
		return;
	}
});





module.exports = router;
