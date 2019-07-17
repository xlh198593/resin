var express = require('express');
var router = express.Router();
var url = require('url');
var request = require('request');
var ipware = require('ipware')();

var logger = require('../lib/log4js').getLogger('goods-router');
var appConfig = require('../app-config');
var errHandler = require('../lib/err-handler');
var bizUtils = require('../lib/biz-utils');
var wypCategory = require('../lib/wyp-category');
var pplBrandList = require('../lib/ppl-brand-list');

/*
 * 商品类接口
 * @param app_token / user_token 令牌（运营管理系统调用传递app_token，APP调用传递user_token）
 * @param service 服务名称
 * @param params 业务参数集，json格式的字符串
 * @param page 分页参数集，json格式字符串（非必填）
 * @param sign 签名
 */
router.all('/', function (req, res, next) {
	try {
		// 请求参数
		var requestParams = bizUtils.extend(req.query, req.body);
		console.log(requestParams)

		// 验证请求参数是否完整
		// if((!requestParams.app_token && !requestParams.user_token) 
		// 	|| !requestParams.service || !requestParams.params || !requestParams.sign) {
		// 	errHandler.incompleteParams(res);
		// 	return;
		// }
		console.log(requestParams.app_token)
		console.log(requestParams.params)
		console.log(requestParams.service)
		logger.info('商品接口查询开始 %::%', requestParams);
		if (requestParams.app_token) {
			// 验证app_token
			// bizUtils.validAppToken(req, res, requestParams.app_token, function (tokenValidData) {
			// 	// 根据返回的security_code校验签名
			// 	if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
			// 		errHandler.invalidSign(res);
			// 		return;
			// 	}
				// 进行商品相关业务
				var formData = {
					service: requestParams.service,
					params: requestParams.params
				};
				if (requestParams.page) {	// 若请求有传递分页参数
					formData.page = requestParams.page;
				}
				request.post(appConfig.goodsPath, { form: formData }, function (error, response, body) {
					if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
						logger.info('商品接口查询结束 %::%', body);
						res.send(JSON.parse(body));
					} else {
						errHandler.systemError(res, error);
						return;
					}
				});

			// });
		} else {
			// 验证user_token
			bizUtils.validUserToken(req, res, requestParams.user_token, function (tokenValidData) {
				// 根据返回的security_code校验签名
				if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
					errHandler.invalidSign(res);
					return;
				}
				// 进行商品相关业务
				var formData = {
					service: requestParams.service,
					params: requestParams.params
				};
				if (requestParams.page) {	// 若请求有传递分页参数
					formData.page = requestParams.page;
				}
				request.post(appConfig.goodsPath, { form: formData }, function (error, response, body) {
					if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
						logger.info('商品接口查询结束 %::%', body);
						res.send(JSON.parse(body));
					} else {
						errHandler.systemError(res, error);
						return;
					}
				});
			});
		}
	} catch (e) {
		errHandler.systemError(res, e);
		return;
	}
});

module.exports = router;