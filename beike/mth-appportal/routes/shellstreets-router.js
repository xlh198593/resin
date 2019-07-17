/*
 * 贝壳街市 相关接口
 * @param user_token 用户令牌
 * @param service 服务名称
 * @param params 业务参数集，json格式的字符串
 * @param sign 签名
 */
var express = require('express');
var router = express.Router();
var url = require('url');
var request = require('request');
var ipware = require('ipware')();
var EventProxy = require('eventproxy');
var parseString = require('xml2js').parseString;
var logger = require('../lib/log4js').getLogger('order-router');
var appConfig = require('../app-config');
var errHandler = require('../lib/err-handler');
var bizUtils = require('../lib/biz-utils');


/*
 * 贝壳街市 店铺接口地址
 * @param user_token 用户令牌
 * @param app_token 手机令牌
 * @param service 服务名称
 * @param params 业务参数集，json格式的字符串
 * @param sign 签名
 */
router.all('/stores', function (req, res, next) {
    try {
        // 请求参数
        var requestParams = bizUtils.extend(req.query, req.body);

        // 验证公共参数是否完整
        if ((typeof requestParams.app_token == 'undefined' && typeof requestParams.user_token == 'undefined') || typeof requestParams.service == 'undefined'
            || typeof requestParams.params == 'undefined' || typeof requestParams.sign == 'undefined') {
            errHandler.incompleteParams(res);
            return;
        }
        if (requestParams.user_token) {
            bizUtils.validUserToken(req, res, requestParams.user_token, function (tokenValidData) {
                // 根据返回的security_code校验签名
                if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
                    errHandler.invalidSign(res);
                    return;
                }
                try {
                    var formData = {
                        service: requestParams.service,
                        params: requestParams.params
                    };
                    if (requestParams.page) {	// 若请求有传递分页参数
                        formData.page = requestParams.page;
                    }
                    request.post(appConfig.storesmemberPath, { form: formData }, function (error, response, body) {
                        if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
                            res.send(JSON.parse(body));
                        } else {
                            errHandler.systemError(res, error);
                            return;
                        }
                    });
                } catch (e) {
                    errHandler.systemError(res, e);
                    return;
                }
            })
        } else if (requestParams.app_token) {
            // 校验app_token
            bizUtils.validAppToken(req, res, requestParams.app_token, function (tokenValidData) {
                // 根据返回的security_code校验签名
                if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
                    errHandler.invalidSign(res);
                    return;
                }
                //转发接口
                try {
                    var formData = {
                        service: requestParams.service,
                        params: requestParams.params
                    };
                    if (requestParams.page) {	// 若请求有传递分页参数
                        formData.page = requestParams.page;
                    }
                    request.post(appConfig.storesmemberPath, { form: formData }, function (error, response, body) {
                        if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
                            res.send(JSON.parse(body));
                        } else {
                            errHandler.systemError(res, error);
                            return;
                        }
                    });
                } catch (e) {
                    errHandler.systemError(res, e);
                    return;
                }
            })
        }

    } catch (e) {
        errHandler.systemError(res, e);
        return;
    }
})

/*
 * 贝壳街市 首页接口
 * @param user_token 用户令牌
 * @param app_token 手机令牌
 * @param service 服务名称
 * @param params 业务参数集，json格式的字符串
 * @param sign 签名
 */

router.all('/bkcqproducts', function (req, res, next) {
    try {
        // 请求参数
        var requestParams = bizUtils.extend(req.query, req.body);

        // 验证公共参数是否完整
        if ((typeof requestParams.app_token == 'undefined' && typeof requestParams.user_token == 'undefined') || typeof requestParams.service == 'undefined'
            || typeof requestParams.params == 'undefined' || typeof requestParams.sign == 'undefined') {
            errHandler.incompleteParams(res);
            return;
        }
        if (requestParams.user_token) {
            bizUtils.validUserToken(req, res, requestParams.user_token, function (tokenValidData) {
                // 根据返回的security_code校验签名
                if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
                    errHandler.invalidSign(res);
                    return;
                }
                try {
                    var formData = {
                        service: requestParams.service,
                        params: requestParams.params
                    };
                    if (requestParams.page) {	// 若请求有传递分页参数
                        formData.page = requestParams.page;
                    }
                    request.post(appConfig.bkcqProductsPath, { form: formData }, function (error, response, body) {
                        if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
                            res.send(JSON.parse(body));
                        } else {
                            errHandler.systemError(res, error);
                            return;
                        }
                    });
                } catch (e) {
                    errHandler.systemError(res, e);
                    return;
                }
            })
        } else if (requestParams.app_token) {
            // 校验app_token
            bizUtils.validAppToken(req, res, requestParams.app_token, function (tokenValidData) {
                // 根据返回的security_code校验签名
                if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
                    errHandler.invalidSign(res);
                    return;
                }
                //转发接口
                try {
                    var formData = {
                        service: requestParams.service,
                        params: requestParams.params
                    };
                    if (requestParams.page) {	// 若请求有传递分页参数
                        formData.page = requestParams.page;
                    }
                    request.post(appConfig.bkcqProductsPath, { form: formData }, function (error, response, body) {
                        if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
                            res.send(JSON.parse(body));
                        } else {
                            errHandler.systemError(res, error);
                            return;
                        }
                    });
                } catch (e) {
                    errHandler.systemError(res, e);
                    return;
                }
            })
        }

    } catch (e) {
        errHandler.systemError(res, e);
        return;
    }
})
/*
 * 贝壳街市下单回调 (支付宝回调)
 */
router.all('/aliPayNotify', function (req, res, next) {
    try {
        // 请求参数
        var requestParams = bizUtils.extend(req.query, req.body);
        logger.info("####贝壳街市支付宝下单回调:"+requestParams);
        request.post(appConfig.aliPayNotifyPath, { form: requestParams }, function (error, response, body) {
            logger.info("####贝壳街市支付宝下单回调请求成功:");
            try {
                if (!error && response.statusCode == 200) {
                    res.send(JSON.parse(body));
                } else {
                    errHandler.systemError(res, error);
                    return;
                }
            } catch (e) {
                errHandler.systemError(res, e);
                return;
            }
        });
    } catch (e) {
        errHandler.systemError(res, e);
        return;
    }
})

/*
 * 贝壳街市下单回调 (微信回调)
 */
router.all('/wxPayNotify', function (req, res, next) {
    try {
        // 微信传递的xml
		var xml = '';
		req.on('data', function(chunk) {
			xml += chunk;
        });
		req.on('end', function() {
			// 将xml转换成json
			parseString(xml, { explicitArray : false, ignoreAttrs : true }, function (err, result) {
                logger.info("####贝壳街市微信下单回调:");
				var formData = 'data=' + JSON.stringify(result.xml);
				// 进行交易相关业务
				request.post(appConfig.wxPayNotifyPath, {form: formData}, function(error, response, body) {
					if(!error && response.statusCode == 200) {	// 请求成功后直接返回结果
						res.send(body);
					} else {
						logger.info('error:' + error);
						errHandler.systemError(res, error);
						return;
					}
				});
			});
		});

        
    } catch (e) {
        errHandler.systemError(res, e);
        return;
    }
})

/*
 * 贝壳街市订单支付接口
 */
router.all('/streetPay', function (req, res, next) {
    try {
        // 请求参数
        var requestParams = bizUtils.extend(req.query, req.body);

        // 验证公共参数是否完整
        if ( typeof requestParams.user_token == 'undefined' || typeof requestParams.service == 'undefined'
            || typeof requestParams.params == 'undefined' || typeof requestParams.sign == 'undefined') {
            errHandler.incompleteParams(res);
            return;
        }
        if (requestParams.user_token) {
            bizUtils.validUserToken(req, res, requestParams.user_token, function (tokenValidData) {
                // 根据返回的security_code校验签名
                if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
                    errHandler.invalidSign(res);
                    return;
                }
                try {
                    var formData = {
                        service: requestParams.service,
                        params: requestParams.params
                    };
                    if (requestParams.page) {	// 若请求有传递分页参数
                        formData.page = requestParams.page;
                    }
                    request.post(appConfig.streetPayPath, { form: formData }, function (error, response, body) {
                        if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
                            res.send(JSON.parse(body));
                        } else {
                            errHandler.systemError(res, error);
                            return;
                        }
                    });
                } catch (e) {
                    errHandler.systemError(res, e);
                    return;
                }
            })
        }

    } catch (e) {
        errHandler.systemError(res, e);
        return;
    }
})

/*  
 * 代理商下单回调 (微信回调)
 */
router.all('/agent/wxPayNotify', function (req, res, next) {
    try {
        // 微信传递的xml
		var xml = '';
		req.on('data', function(chunk) {
			xml += chunk;
        });
		req.on('end', function() {
			// 将xml转换成json
			parseString(xml, { explicitArray : false, ignoreAttrs : true }, function (err, result) {
                logger.info("####代理商下单回调微信下单回调:");
				var formData = 'data=' + JSON.stringify(result.xml);
				// 进行交易相关业务
				request.post(appConfig.agentwxPayNotifyPath, {form: formData}, function(error, response, body) {
					if(!error && response.statusCode == 200) {	// 请求成功后直接返回结果
						res.send(body);
					} else {
						logger.info('error:' + error);
						errHandler.systemError(res, error);
						return;
					}
				});
			});
		});

        
    } catch (e) {
        errHandler.systemError(res, e);
        return;
    }
})
module.exports = router;