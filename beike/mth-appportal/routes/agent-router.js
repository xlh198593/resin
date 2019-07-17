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
 * 代理商接口 商户,合伙人列表及详情
 * @param user_token
 * @param service 服务名称
 * @param params 业务参数集，json格式的字符串
 */
router.all('/member', function(req, res, next) {
    try {
        // 请求参数
        var requestParams = bizUtils.extend(req.query, req.body);
        var formData = {
            'service' : requestParams.service,
            'params' : requestParams.params,
        }
        if(requestParams.page) {	// 若请求有传递分页参数
            formData.page = requestParams.page;
        }
        request.post(appConfig.memberPath, {form: formData}, function(error, response, body) {
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
    } catch (error) {
        errHandler.systemError(res, error);
		return;
    }
})

/*
 * 代理商接口 财务类接口
 * @param user_token
 * @param service 服务名称
 * @param params 业务参数集，json格式的字符串
 */
router.all('/finance', function(req, res, next) {
    try {
        // 请求参数
        var requestParams = bizUtils.extend(req.query, req.body);
        var formData = {
            'service' : requestParams.service,
            'params' : requestParams.params,
        }
        if(requestParams.page) {	// 若请求有传递分页参数
            formData.page = requestParams.page;
        }
        request.post(appConfig.financePath, {form: formData}, function(error, response, body) {
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
    } catch (error) {
        errHandler.systemError(res, error);
		return;
    }
})

/*
 * 代理商接口 登录
 * @param user_token
 * @param service 服务名称
 * @param params 业务参数集，json格式的字符串
 */
router.all('/companyAccount', function(req, res, next) {
    try {
        // 请求参数
        var requestParams = bizUtils.extend(req.query, req.body);
        var formData = {
            'service' : requestParams.service,
            'params' : requestParams.params,
        }
        if(requestParams.page) {	// 若请求有传递分页参数
            formData.page = requestParams.page;
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
    } catch (error) {
        errHandler.systemError(res, error);
		return;
    }
})

module.exports = router;