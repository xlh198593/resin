var express = require('express');
var router = express.Router();
var url = require('url');
var request = require('request');
var ipware = require('ipware')();

var logger = require('../lib/log4js').getLogger('member-router');
var appConfig = require('../app-config');
var errHandler = require('../lib/err-handler');
var bizUtils = require('../lib/biz-utils');

/*
 * 会员类接口
 * @param app_token / user_token
 * @param service 服务名称
 * @param params 业务参数集，json格式的字符串
 * @param sign 签名
 */
router.all('/', function(req, res, next) {
	try {
		// 请求参数
		var requestParams = bizUtils.extend(req.query, req.body);

		// 验证请求参数是否完整
		if((!requestParams.app_token && !requestParams.user_token) 
			|| !requestParams.service || !requestParams.params || !requestParams.sign) {
			errHandler.incompleteParams(res);
			return;
		}

		if(requestParams.app_token) {
			// 验证app_token
			bizUtils.validAppToken(req, res, requestParams.app_token, function(tokenValidData) {
				// 根据返回的security_code校验签名
				if(!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
					errHandler.invalidSign(res);
					return;
				}
				// 进行会员相关业务
				var formData = {
					'service' : requestParams.service,
					'params' : requestParams.params
				}
				if(requestParams.page) {	// 若请求有传递分页参数
					formData.page = requestParams.page;
				}
				request.post(appConfig.memberPath, {form: formData}, function(error, response, body) {
					if(!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
						var result = JSON.parse(body);
						// 因为安卓领有惠把距离的单位搞成了km，所以这边暂时先处理成km
						if(result.rsp_code == 'succ' && requestParams.service == 'member.nearbyLDStoreFind') {
							for(var i=0; i<result.data.list.length; i++) {
								result.data.list[i].original_distance = result.data.list[i].distance;
								result.data.list[i].distance = (parseFloat(result.data.list[i].distance) / 1000).toFixed(2);
							}
						}
						res.send(result);
					} else {
						errHandler.systemError(res, error);
						return;
					}
				});
			});
		} else {
			// 验证user_token
			bizUtils.validUserToken(req, res, requestParams.user_token, function(tokenValidData) {
				// 根据返回的security_code校验签名
				if(!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
					errHandler.invalidSign(res);
					return;
				}
				// 进行会员相关业务
				var formData = {
					'service' : requestParams.service,
					'params' : requestParams.params
				}
				if(requestParams.page) {	// 若请求有传递分页参数
					formData.page = requestParams.page;
				}
				request.post(appConfig.memberPath, {form: formData}, function(error, response, body) {
					if(!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
						var result = JSON.parse(body);
						// 因为安卓领有惠把距离的单位搞成了km，所以这边暂时先处理成km
						if(result.rsp_code == 'succ' && requestParams.service == 'member.nearbyLDStoreFind') {
							for(var i=0; i<result.data.list.length; i++) {
								result.data.list[i].original_distance = result.data.list[i].distance;
								result.data.list[i].distance = (parseFloat(result.data.list[i].distance) / 1000).toFixed(2);
							}
						}
						res.send(result);
					} else {
						errHandler.systemError(res, error);
						return;
					}
				});
			});
		}
	} catch(e) {
		errHandler.systemError(res, e);
		return;
	}
});
//充值记录
router.all("/rechargelist",function(req,res,next) {
	logger.info("查询充值记录开始");
	
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	logger.info('requestParams %::%',requestParams);
		var FormData = {
			service : 'order.consumer.rcOrderPageListFind',
			params : JSON.stringify({
			member_id : requestParams.member_id,
			})
		};
	request.post(appConfig.opsorderPath, {form: FormData}, function(error, response, validBody) {
		validBody = JSON.parse(validBody);
		try {
			if(!error && validBody.rsp_code == 'succ') {	// 请求成功后直接返回查询结果
				logger.info('查询充值记录结束')
				logger.info('查询的充值的data:',validBody.data.list)
				res.send(validBody.data.list);
			} else {
				errHandler.systemError(res, error);
			}
		} catch(e) {
			errHandler.systemError(res, e);
		}

      });
	
})
//会员扫码支付
router.all("/company",function(req,res,next) {
	logger.info('会员扫码支付开始');
	var requestParams = bizUtils.extend(req.query, req.body);
	//验证请求参数是否完整
	if(typeof requestParams.user_token == 'undefined'
	|| typeof requestParams.sign == 'undefined' || typeof requestParams.params == 'undefined'
    || typeof requestParams.service == 'undefined') {
	   errHandler.incompleteParams(res);
	   return;
	};
	if (requestParams.user_token) {
         //验证user_token
		 bizUtils.validUserToken(req, res, requestParams.user_token, function(tokenValidData) {
			// 根据返回的security_code校验签名
			if(!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
				errHandler.invalidSign(res);
				return;
			}
			var formData = {
				'service' : requestParams.service,
				'params' : requestParams.params,
			}
			request.post(appConfig.companyMemberPath, {form: formData}, function(error, response, body) {
				try {
					if(!error && response.statusCode == 200) {
						res.send(JSON.parse(body));
					} else {
						errHandler.systemError(res, error);
					}
					
				} catch(e) {
					errHandler.systemError(res, e);
				}
			});
			
		  })
	}
	

})

//代码整合  资产查询 积分查询 贝壳查询
router.all("/assetquery",function(req,res,next) {
	logger.info('会员资产查询开始');
	var requestParams = bizUtils.extend(req.query, req.body);
	//验证请求参数是否完整
	if((typeof requestParams.app_token == 'undefined' && typeof requestParams.user_token == 'undefined') 
	|| typeof requestParams.sign == 'undefined' || typeof requestParams.params == 'undefined'
    || typeof requestParams.service == 'undefined') {
	   errHandler.incompleteParams(res);
	   return;
	};
	logger.info('requestParams %::%',requestParams);
	//service  "consumer.consumerFdMemberAsset" 会员资产查询
	//         "consumer.consumerFdMemberAssetShellLogFind" 贝壳查询
	//         "consumer.consumerFdMemberAssetPointLogFind" 积分查询

	if (requestParams.app_token) {
		//验证app_token
	bizUtils.validAppToken(req, res, requestParams.app_token, function(tokenValidData) {
		// 根据返回的security_code校验签名
		if(!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
			errHandler.invalidSign(res);
			return;
		}

		var formData = {
			'service' : requestParams.service,
			'params' : requestParams.params,
			'page':requestParams.page,
		}
		request.post(appConfig.financeshellpropertyPath, {form: formData}, function(error, response, body) {
			try {
				if(!error && response.statusCode == 200) {
					logger.info('资产查询结束')
					res.send(JSON.parse(body));
				} else {
					errHandler.systemError(res, error);
				}
				
			} catch(e) {
				errHandler.systemError(res, e);
			}
		});
	    
      })
	} else {
         //验证user_token
	bizUtils.validUserToken(req, res, requestParams.user_token, function(tokenValidData) {
		// 根据返回的security_code校验签名
		if(!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
			errHandler.invalidSign(res);
			return;
		}

		var formData = {
			'service' : requestParams.service,
			'params' : requestParams.params,
			'page' : requestParams.page,
		}
		request.post(appConfig.financeshellpropertyPath, {form: formData}, function(error, response, body) {
			try {
				if(!error && response.statusCode == 200) {
					logger.info('资产查询结束')
					res.send(JSON.parse(body));
				} else {
					errHandler.systemError(res, error);
				}
				
			} catch(e) {
				errHandler.systemError(res, e);
			}
		});
	    
      })
	}

	
	
});


module.exports = router;