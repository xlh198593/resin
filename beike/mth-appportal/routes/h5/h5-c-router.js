var express = require('express');
var router = express.Router();
var url = require('url');
var request = require('request');
var ipware = require('ipware')();
var uuidv1 = require('uuid/v1');
var parseString = require('xml2js').parseString;
var eventproxy = require('eventproxy');

var logger = require('../../lib/log4js').getLogger('h5-c-router');
var appConfig = require('../../app-config');
var errHandler = require('../../lib/err-handler');
var bizUtils = require('../../lib/biz-utils');
var goldExchangeBrandList = require('../../lib/gold-exchange-brand-list');

/*
 * 进入特卖页面
 */
// router.get('/onsale', function(req, res, next) {
// 	res.redirect('http://h.meitianhui.com');
// });


/*
 * 进入代办页面
 */
// router.get('/agency', function(req, res, next) {
// 	res.render('c/agency/index');
// });
/**
 * 年货活动页
 * @params 
 */
router.all("/stocking", function (req, res, next) {
	//查询vip推荐商品的参数
	let goodsFormData = {
		service: 'psGoods.consumer.newYearGoodsFind',
		params:JSON.stringify({})
	};
    logger.info('年货商品查询开始 %::%', JSON.stringify(goodsFormData));
	//factoryPromise
	bizUtils.factoryPromise(appConfig.goodsPath, goodsFormData)
		.then(function (value) {
			logger.info('年货商品查询结束 %::%', JSON.stringify(value));
			console.log(value.data.list)
			if (value.data) {
				res.render('c/activity/stocking',{
					yingCunList:value.data.yingCunList,
					jianGuoList:value.data.jianGuoList,
					ziBuList:value.data.ziBuList

				});
			} else {
				res.send(JSON.stringify(value));
			}
		
		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})
	
    
})

/**
 *查看是不是会员
 * @params 
 */
router.all("/ismember", function (req, res, next) {
	try {
		var requestParams = bizUtils.extend(req.query, req.body);
	// 验证请求参数是否完整
	if (typeof requestParams.params == 'undefined' || typeof requestParams.app_token == 'undefined' || typeof requestParams.sign == "undefined") {
		errHandler.incompleteParams(res);
		return;
	}
	
	bizUtils.validAppToken(req, res, requestParams.app_token, function (tokenValidData) {
		// 根据返回的security_code校验签名
		if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
			errHandler.invalidSign(res);
			return;
		}
		var formData = {
			service: 'member.consumerIsVip',
			params: requestParams.params
		};
		request.post(appConfig.memberPath, { form: formData }, function (error, response, body) {
			try {
				if (!error && response.statusCode == 200) {	//请求成功
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
      })
	} catch (error) {
		errHandler.systemError(res, e);
		return;
	}
})

/**
 * 进入充值页面 
 * @params member_id
 */
router.all("/member", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	var member = {
		member_id: requestParams.member_id,
		appBaseName: appConfig.appBaseName,
		login_status: requestParams.app_token ? "0" : "1",
		user_token: requestParams.user_token,
		end_time: "",
		username: "",
		imagePath: "",
		type: requestParams.type == undefined ? "0" : requestParams.type,
	}
	//屏蔽ios上面的会员购买
	// if(isiOS) {
	// 	res.render("c/user/iosmember", member);
	// } else {
	// 	res.render("c/user/member1", member);
	// }
	logger.info("会员中心查询开始 %::%", requestParams);
	//查询用户信息需要的参数
	let userMessage = {
		service: 'member.consumerFind',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	}
	//查询vip推荐商品的参数
	let vipshop = {
		service: 'psGoods.consumer.vipGoodsFind',
		params: JSON.stringify({})
	};

	//factoryPromise
	bizUtils.factoryPromise(appConfig.memberPath, userMessage)
		.then(function (value) {
			logger.info('用户信息查询结束 %::%', JSON.stringify(value));
			if (value.data) {
				member.username = value.data.nick_name;
				member.end_time = value.data.vip_end_time == '' ? null : value.data.vip_end_time;
				member.imagePath = value.data.doc_url[value.data.head_pic_path];
				// member.member = value.data.vip_end_time == '' ? "0" : "1";
				member.member = "1";
			}
			return bizUtils.factoryPromise(appConfig.goodsPath, vipshop)
		}).then(function (value) {
			logger.info('vip推荐商品查询结束 %::%', JSON.stringify(value));
			let list = handleImgUrlAndArea(value.data.hongBaoList, value.data.doc_url);
			member.list = list;
			if (isiOS) {
				res.render("c/user/iosmember", member);
				// res.render("c/user/member1", member);
			} else {
				res.render("c/user/member1", member);
			}

		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})
});
/**
 * 短信验证码
 * @prams mobile 手机号码
 */
router.all("/send_sms_code_lyh", function (req, res, next) {
	let requestParams = bizUtils.extend(req.query, req.body);
	let sendSMSCodeFormData = 'service=notification.sendCheckCode&params=' + JSON.stringify({ sms_source: requestParams.sms_source, mobile: requestParams.mobile, type: requestParams.type });
	request.post(appConfig.notificationPath, { form: sendSMSCodeFormData }, function (error, response, body) {
		if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
			res.send(JSON.parse(body));
		} else {
			errHandler.systemError(res, error);
			return;
		}
	});
});


/**
 * 开学季活动
 * 
 **/
router.all("/schoolopens",function(req,res,next) {
	let requestparams = bizUtils.extend(req.query,req.body);
	res.render("c/activity/schoolopens");
})

/**
 * 女神节活动
 * 
 **/
router.all("/nvs",function(req,res,next) {
	let requestparams = bizUtils.extend(req.query,req.body);
	res.render("c/activity/nvs");
})

/**
 * 春节活动
 * 
 **/
router.all("/cjactivit",function(req,res,next) {
	let requestparams = bizUtils.extend(req.query,req.body);
	res.render("c/activity/beike_active");
})
/**
 * 春节活动
 * 
 **/
router.all("/cjactivit_b",function(req,res,next) {
	let requestparams = bizUtils.extend(req.query,req.body);
	res.render("c/activity/beike_active_b");
})


router.all('/cjactivit_c', function (req, res, next) {
	res.render('c/activity/beike_active_c')
})

//抢红包活动
router.all("/ropredpacket", function (req, res, next) {
	//查询红包数量及抽奖次数
    var requestParams = bizUtils.extend(req.query, req.body);
	let ropredpackedFormData = {
			service: 'member.hongbaoActivityInfo',
			params: JSON.stringify({
				parent_id: requestParams.member_id
			})
		}
    logger.info('红包信息查询 %::%', JSON.stringify(ropredpackedFormData));
	//factoryPromise
	bizUtils.factoryPromise(appConfig.memberPath, ropredpackedFormData)
		.then(function (value) {
			logger.info('红包信息查询结束 %::%', JSON.stringify(value));
			if (value.rsp_code == "succ") {
			    res.render('c/activity/rop',{
					"amountSum": value.data.amountSum? value.data.amountSum:0,
                    "hongbaoCount": value.data.hongbaoCount,
					"memberCount": value.data.memberCount,
					"member_id":requestParams.member_id
				})
			} else {
				res.send(JSON.stringify(value));
			}
		
		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})
	
    
})

//拆红包接口
router.all("/pulldown",function(req,res,next) {
    var requestParams = bizUtils.extend(req.query, req.body);
	let ropredpackedFormData = {
			service: 'member.drawHongbao',
			params: JSON.stringify({
				parent_id: requestParams.member_id
			})
		}
    logger.info('抽取红包开始 %::%', JSON.stringify(ropredpackedFormData));
	//factoryPromise
	bizUtils.factoryPromise(appConfig.memberPath, ropredpackedFormData)
		.then(function (value) {
			logger.info('抽取红包结束 %::%', JSON.stringify(value));
			if (value.rsp_code == "succ") {
				res.send(value)
			} else {
				res.send(value);
			}
		
		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})
})


//进入充值页面 重新修改页面
router.all("/member1", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	let Message,userMessage;
	let current_version=["2.8.1"];
	let notchange_version=["2.7.0","2.7.2","2.7.3","2.7.4","2.8.0"];
	let memberMessagechange_version = ["v1"];//
	var member = {
		member_id: requestParams.member_id,
		appBaseName: appConfig.appBaseName,
		login_status: requestParams.user_token != undefined ? "1" : "0",
		user_token: requestParams.user_token,
		end_time: "",
		username: "",
		imagePath: "",
		type: requestParams.type == undefined ? "0" : requestParams.type,
		entry: requestParams.entry,
		isios: isiOS ? "0" : "1",
		need_signIn:0,
		level:1,
		is_signIn:"Y",
		growth_value:100,
		member_coupons_id:'1',
		have_next:"N",
		is_activate:"N"
	}
	logger.info("会员中心查询开始 %::%", member);
	//如果member_id不存在  直接渲染页面
	if(requestParams.member_id == null) {
		if(bizUtils.searchIndexOf(current_version,requestParams.version)) {
			// res.render("c/user/iosmember", member);
            res.render("c/user/memberv2", member);
		} else if(bizUtils.searchIndexOf(notchange_version,requestParams.version)) {
            res.render("c/user/memberv2", member);
		} else if(bizUtils.searchIndexOf(memberMessagechange_version,requestParams.version)) {
			res.render("c/user/memberv1", member);
		} else {
			res.render("c/user/member2", member);
		}
		
		return;
	}
	if(bizUtils.searchIndexOf(current_version,requestParams.version)||bizUtils.searchIndexOf(notchange_version,requestParams.version)|| bizUtils.searchIndexOf(memberMessagechange_version,requestParams.version)) {
		//个人信息查询
		userMessage = {
			service: 'member.consumerFind_v1',
			params: JSON.stringify({
				member_id: requestParams.member_id
			})
		}
		//签到信息
	    Message = {
			service: 'finance.consumer.findGiftCouponSignInfo',
			params: JSON.stringify({
			  member_id: requestParams.member_id
			})
		 };
	} else {
		userMessage = {
			service: 'member.consumerFind',
			params: JSON.stringify({
				member_id: requestParams.member_id
			})
		}
	}
	
	//查询礼券信息
	let liquan = {
		service: 'finance.consumer.findGiftCoupon',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	};
	//节省多少钱
	let jiesheng = {
		service: 'order.consumer.beikeMallOrderShell',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	};
	//推荐商品查询
	let shopmessage = {
		service: 'goods.beikeMallGoodsDetailFindNew',
			params: JSON.stringify({
				goods_id: "2036",
			})
	}
	bizUtils.factoryPromise(appConfig.memberPath, userMessage)
		.then(function (value) {
			logger.info('用户信息查询结束 %::%', JSON.stringify(value));
			if(value.data.type == 0 ||value.data.type == 1 ||value.data.type == 2) {
				member.member = "1";
			} else {
				member.member = "0";
			}
			if (value.data) {
				member.end_time = value.data.vip_end_time == '' ? null : value.data.vip_end_time;
				member.nick_name = value.data.nick_name == '' ? "您还没有昵称" : value.data.nick_name;
				member.imagePath = value.data.doc_url[value.data.head_pic_path];
				member.level = value.data.level ? value.data.level : 0;
				member.growth_value = value.data.growth_value ? value.data.growth_value : 0;
				member.is_closedBeta = value.data.is_closedBeta;
			}
			return bizUtils.factoryPromise(appConfig.financePath, liquan)
		}).then(function (value) {
			logger.info('礼券信息查询结束 %::%', JSON.stringify(value));
			if (value.data) {
				member.mianyou = value.data.mianyou;
				member.youhui = value.data.youhui;
			}
			return bizUtils.factoryPromise(appConfig.opsorderPath, jiesheng)

		}).then(function (value) {
			logger.info('节省查询结束 %::%', JSON.stringify(value));
			if (value.data) {
				member.beike_credit = value.data.beike_credit;
			}
			if(bizUtils.searchIndexOf(current_version,requestParams.version)|| bizUtils.searchIndexOf(notchange_version,requestParams.version) ||bizUtils.searchIndexOf(memberMessagechange_version,requestParams.version)) {
				return bizUtils.factoryPromise(appConfig.financePath, Message);
			} else {
				res.render("c/user/member2", member);
				return;
			}
		}).then(function(value) {
			logger.info('签到信息查询结束 %::%', JSON.stringify(value));
			if(value != undefined) {
				member.is_signIn = value.data.is_signIn;
				member.member_coupons_id=value.data.member_coupons_id;
				member.have_next = value.data.have_next;
				member.need_signIn = value.data.need_signIn;
				member.is_activate = value.data.is_activate;
			}
			return bizUtils.factoryPromise(appConfig.goodsPath, shopmessage)
		}).then(function(value) {
			member.sale_qty = value.data.list[0].sale_qty;
			if(value != undefined) {
				if(bizUtils.searchIndexOf(current_version,requestParams.version)) {
					res.render("c/user/memberv2", member);
					// res.render("c/user/iosmember", member);
				} else if(bizUtils.searchIndexOf(notchange_version,requestParams.version)) {
                    res.render("c/user/memberv2", member);
				} else if(bizUtils.searchIndexOf(memberMessagechange_version,requestParams.version)) {
					res.render("c/user/memberv1", member);
				} else {
					res.render("c/user/member2", member);
				}
			}
		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})
});
//会员页广告
router.all("/member1/advertising",function(req,res,next) {
	try {
		// 请求参数
		// var requestParams = bizUtils.extend(req.query, req.body);
		var formData = {
			service: 'goodsAd.app.member.ad',
			params: '{}'
		};
		
		request.post(appConfig.goodsPath, { form: formData }, function (error, response, body) {
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

//掌柜登录
router.all("/manager/login",function(req,res,next) {
	res.render("c/user/manager",{appBaseName: appConfig.appBaseName,});
})
//掌柜提成查询
router.all("/gotoenvelope",function(req,res,next) {
	//掌柜入口关闭
	res.send(false);

	// logger.info("掌柜红包总额查询开始 %::%",requestParams)
	// var requestParams = bizUtils.extend(req.query, req.body);
	// let Message = {
	// 	service: 'consumer.consumerFdMemberAsset',
	// 	params: JSON.stringify({
	// 		member_id: requestParams.member_id
	// 	})
	// }
	// bizUtils.factoryPromise(appConfig.financeshellpropertyPath, Message)
	// 		.then(function (value) {
	// 			logger.info('掌柜红包总额查询结束 %::%', JSON.stringify(value));
	// 			if(value.rsp_code == 'succ') {
	// 				res.render("c/user/gotoenvelope",
	// 				{rateCount:value.data.invite_balance,
	// 				 type:requestParams.type,
	// 				 appBaseName: appConfig.appBaseName,
	// 				 member_id:requestParams.member_id
					
	// 				});
	// 			} else {
	// 				res.send(value);
	// 			}
				
				
	// 		}).catch(function (res, e) {
	// 			errHandler.renderErrorPage(res, e);
	// 		})
})
/**
 * 次邀粉丝
 * @params member_id 会员id 
 * @params type 非必填
 */
router.all("/timetoinvitefans",function(req,res,next) {
	//掌柜入口关闭
	res.send(false);
	// let requestParams = bizUtils.extend(req.query, req.body);
	// let member_id = requestParams.member_id;
	// res.render("c/user/timetoinvitefans",{member_id:member_id,type:requestParams.type});
})
/**
 * 次邀粉丝列表查询
 * @params member_id  掌柜id 
 */
router.all("/timetoinvitefans/list",function(req,res,next) {
	//掌柜入口关闭
	res.send(false);
	// var requestParams = bizUtils.extend(req.query, req.body);
	// let formData = {
	// 	'service' : requestParams.service,
	// 	'params' : requestParams.params,
	// 	'page' : requestParams.page,
	// }
    // logger.info("次邀粉丝列表查询开始::::",requestParams)

	// request.post(appConfig.memberPath, {form: formData}, function(error, response, body) {
	// 	try {
	// 		if(!error && response.statusCode == 200) {
	// 			logger.info("次邀粉丝列表查询::::",body)
	// 			res.send(JSON.parse(body));
	// 		} else {
	// 			errHandler.systemError(res, error);
	// 		}
			
	// 	} catch(e) {
	// 		errHandler.systemError(res, e);
	// 	}
	// })
})
/**
 * 掌柜红包明细
 * @params member_id 会员id
 */

router.all("/detailaccount",function(req,res,next) {
	//掌柜入口关闭
	res.send(false);
	// logger.info("掌柜红包总额查询开始 %::%",requestParams)
	// var requestParams = bizUtils.extend(req.query, req.body);
	// let Message = {
	// 	service: 'consumer.memberManagerRabateCount',
	// 	params: JSON.stringify({
	// 		member_id: requestParams.member_id,
	// 	})
	// }

	// bizUtils.factoryPromise(appConfig.financeshellpropertyPath, Message)
	// 		.then(function (value) {
	// 			logger.info('掌柜红包总额查询结束 %::%', JSON.stringify(value));
	// 			if(value.rsp_code == "succ") {
	// 				res.render("c/user/detailaccount",{
	// 					rateCount:value.data.rateCount,
	// 					type:requestParams.type,
	// 					member_id:requestParams.member_id,
	// 				});
	// 			} else {
	// 				res.send(value);
	// 			}
				
	// 		}).catch(function (res, e) {
	// 			errHandler.renderErrorPage(res, e);
	// 		})
	
})
/**
 * 掌柜红包明细列表查询
 * @params params {"member_id:""} member_id掌柜id
 * @service
 * 
 */
router.all("/manager/list",function(req,res,next) {
		//掌柜入口关闭
		res.send(false);
	// var requestParams = bizUtils.extend(req.query, req.body);
	// if( typeof requestParams.params == 'undefined'|| typeof requestParams.service == 'undefined') {
	//    errHandler.incompleteParams(res);
	//    return;
	// };
	// logger.info("掌柜红包明细查询开始::::",requestParams)
	// var formData = {
	// 	'service' : requestParams.service,
	// 	'params' : requestParams.params,
	// 	'page' : requestParams.page,
	// }
	// request.post(appConfig.financeshellpropertyPath, {form: formData}, function(error, response, body) {
	// 	try {
	// 		if(!error && response.statusCode == 200) {
	// 			logger.info("掌柜红包明细查询开始::::",body)
	// 			res.send(JSON.parse(body));
	// 		} else {
	// 			errHandler.systemError(res, error);
	// 		}
			
	// 	} catch(e) {
	// 		errHandler.systemError(res, e);
	// 	}
	// });
})

/**
 * 签到
 * @params member_coupons_id 会员id
 */
router.all("/signin", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	let Message = {
		service: 'finance.consumer.activationGiftCoupon',
		params: JSON.stringify({
			member_coupons_id: requestParams.member_coupons_id
		})
	}

	bizUtils.factoryPromise(appConfig.financePath, Message)
			.then(function (value) {
				logger.info('签到接口 %::%', JSON.stringify(value));
			    res.send(value)
				
			}).catch(function (res, e) {
				errHandler.renderErrorPage(res, e);
			})

});

/**
 * 补签次数购买
 * @params member_coupons_id 会员id
 */
router.all("/calender/paynum",async function (req, res, next) {
	logger.info("00000000000000000000000000");
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	// //参数校验
	// if(!requestParams.member_id || !requestParams.user_token) {
	// 	errHandler.incompleteParams(res);
	// 	return;
	// }
	// console.log("111111111111111111111111");
	// // 验证user_token
	// bizUtils.validUserToken(req, res, requestParams.user_token,async function(tokenValidData) {
	// 	logger.info("222222222222222222222222");
	// 	logger.info(tokenValidData);
	// 	// 根据返回的security_code校验签名
	// 	if(!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
	// 		errHandler.invalidSign(res);
	// 		return;
	// 	}
	// 	logger.info("3333333333333333333333");
		// 进行会员相关业务
		let Message = {
			service: 'finance.consumer.shellSwitchToCount',
			params: JSON.stringify({
				member_id: requestParams.member_id
			})
		}
		try {
			let value = await bizUtils.factoryPromise(appConfig.financePath, Message,res);
			console.log(value);
			res.send(value);
		} catch (error) {
			errHandler.renderErrorPage(res, e);
		}
	// })
});

/**
 * 补签
 * @params member_coupons_id 会员id
 */
router.all("/retroactive", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	let Message = {
		service: 'finance.consumer.repairGiftCoupon',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	}

	bizUtils.factoryPromise(appConfig.financePath, Message,res)
		.then(function (value) {
			logger.info('补签签到接口 %::%', JSON.stringify(value));
			res.send(value)
			
		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})
});
/**
 * 签到日历
 * @params member_id 会员id
 */
router.all("/calender", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	let Message = {
		service: 'finance.consumer.findGiftCouponSignInfo',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	}

	bizUtils.factoryPromise(appConfig.financePath, Message,res)
			.then(function (value) {
				logger.info('签到信息查询 %::%', JSON.stringify(value));
				if (value.data) {
					res.render("c/user/calender", {
						starttime:value.data.coupons_validity_start,
						endtime:value.data.coupons_validity,
						signData:value.data.sign_data,
						signDataRepair:value.data.sign_data_repair,
						repairCount:value.data.repair_count || 0,
						need_signIn:value.data.need_signIn,
						shell_count:value.data.shell_count || 0,
						is_signIn:value.data.is_signIn,
						member_coupons_id:value.data.member_coupons_id
					});
				}
				
			}).catch(function (res, e) {
				errHandler.renderErrorPage(res, e);
			})

});

/**
 * 签到日历 上个月签到记录
 * @params member_id 会员id
 */
router.all("/calender/lastmonth", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	let Message = {
		service: 'finance.consumer.findGiftCouponSignInfoForLastMonth',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	}   
	bizUtils.factoryPromise(appConfig.financePath, Message,res)
		.then(function (value) {
			logger.info('上个月签到信息查询 %::%', JSON.stringify(value));
			if (value.data) {
				let val = {"rsp_code":value.rsp_code};
				val.data ={
					starttime:value.data.coupons_validity_start,
					endtime:value.data.coupons_validity,
					signData:value.data.sign_data,
					signDataRepair:value.data.sign_data_repair,
					need_signIn:value.data.need_signIn,
					is_signIn:value.data.is_signIn,
					member_coupons_id:value.data.member_coupons_id
				};
				res.send(val);
			}else{
			   res.send(value);
			}
		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})

});

/**
 * 会员权益
 * @params member_id 会员id
 */
router.all("/member/qy", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);

	let userMessage = {
		service: 'member.consumerFind',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	}

	let obj = {
		type: requestParams.type == "0" ? 7 : requestParams.type,
		loginstatus: requestParams.user_token ? "1" : "0",
		entry: requestParams.entry,
	}
	if (requestParams.user_token) {
		bizUtils.factoryPromise(appConfig.memberPath, userMessage)
			.then(function (value) {
				logger.info('用户信息查询结束 %::%', JSON.stringify(value));
				if (value.data) {
					obj.is_vip = value.data.is_vip;
					obj.level = value.data.level;
				}
				res.render("c/user/memberqy", obj);
			}).catch(function (res, e) {
				errHandler.renderErrorPage(res, e);
			})
	} else {
		res.render("c/user/memberqy", obj);
	}


});
//h5登录
router.all("/login", function (req, res, next) {
	let requestParams = bizUtils.extend(req.query, req.body);
	let obj = {
		user_id: uuidv1().replace(/-/g, ''),
		mobile: requestParams.mobile,
		password: requestParams.password?requestParams.password:uuidv1().replace(/-/g, ''),
		request_info: requestParams.request_info?requestParams.request_info:ipware.get_ip(req).clientIp,
		check_code: requestParams.check_code,
		member_type_key: "consumer",
		type: requestParams.type,
		data_source: "meitianhui",
		member_id: requestParams.member_id,
		lq_02: requestParams.lq_02,
		lq_03: requestParams.lq_03
	}
	logger.info("h5登录功能开始::::::",JSON.stringify(obj))
	let loginFormData = 'service=infrastructure.mobileLogin&params=' + JSON.stringify(obj);
	let registerFormData = 'service=infrastructure.consumerUserRegister&params=' + JSON.stringify(obj);
	let checkCodeFormData = 'service=notification.validateCheckCode&params=' + JSON.stringify({ mobile: requestParams.mobile, check_code: requestParams.check_code });

	bizUtils.factoryPromise(appConfig.notificationPath, checkCodeFormData)
		.then(function (value) {
			logger.info("h5校验验证码结束:::",JSON.stringify(value));
			if (value.rsp_code == "fail") {
				res.send(value);
			} else {
				return bizUtils.factoryPromise(appConfig.userPath, loginFormData)
			}
		}).then(function (value) {
			logger.info("h5登录结束:::",JSON.stringify(value));
			if (value == undefined) {
				return;
			}
			if (value.error_code == "user_not_exist") {
				obj.type = "account";
				return bizUtils.factoryPromise(appConfig.userPath, registerFormData)
			} else {
				res.send(value);
			}
		}).then(function (value) {
			logger.info("h5登录注册结束:::",JSON.stringify(value));
			res.send(value)

		}).catch(function (res, e) {
			errHandler.systemError(res, e);
			return;
		})
});
//h5微信支付
router.all("/payh5", function (req, res, next) {
	try {
		function get_ip(req) {
			var ip = req.headers['x-real-ip'] || 
					 req.headers['x-forwarded-for'] ||
					 req.socket.remoteAddress || '';
			if(ip.split(',').length>0){
				ip = ip.split(',')[0];
			}
			return ip;
		};
		function getClientIP(req) {
			return req.headers['x-forwarded-for'] || // 判断是否有反向代理 IP
				req.connection.remoteAddress || // 判断 connection 的远程 IP
				req.socket.remoteAddress || // 判断后端的 socket 的 IP
				req.connection.socket.remoteAddress;
		};
		var requestParams = bizUtils.extend(req.query, req.body);
		function RndNum(n){
			var rnd="";
			for(var i=0;i<n;i++)
				rnd+=Math.floor(Math.random()*10);
			return rnd;
		}

		logger.info("h5支付接口 requestParams %:::%", requestParams)
		// 验证请求参数是否完整
		if (typeof requestParams.user_token == 'undefined'
			|| typeof requestParams.service == 'undefined' || typeof requestParams.params == 'undefined'
			|| typeof requestParams.sign == 'undefined') {
			errHandler.incompleteParams(res);
			return;
		}
		if (typeof requestParams.user_token != 'undefined') {
			bizUtils.validUserToken(req, res, requestParams.user_token, function (tokenValidData) {
				// 根据返回的security_code校验签名
				if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
					errHandler.invalidSign(res);
					return;
				};
				let params = JSON.parse(requestParams.params);
				logger.info("node层getClientIP()ip:::",getClientIP(req))
				logger.info("node层get_ip()ip:::",get_ip(req))
				logger.info("h5ip::::",params.ip)
				var voucherRewardFormData = {
					service: 'orderPay.shellRecharge',
					params: JSON.stringify({
						member_id: params.consumer_id,
						data_source: 'SJLY_01',
						payment_way_key: 'ZFFS_10',
						amount: "399",
						member_type_key: "consumer",
						// order_no:order_no,
						currency_code: "人民币",
						order_type_key: "DDLX_06",
						ip: params.ip,
						trade_type_key:"JYLX_01",
						out_trade_no:RndNum(5)
					})
				};
				logger.info("h5支付开始::: requestparams", JSON.stringify(voucherRewardFormData));
				request.post(appConfig.financeshellPath, { form: voucherRewardFormData }, function (error, response, body) {
					if (!error && response.statusCode == 200) {	// 请求成功后直接返回查询结果
						logger.info("h5支付结束::::", JSON.stringify(body));
						res.send(JSON.parse(body));
					} else {
						errHandler.systemError(res, error);
					}
				});
				// }

				// } else {
				// 	errHandler.systemError(res, error);
				// 	return;
				// }
				// });
			})
		}
	} catch (e) {

	}
});



/**
 * 成长值
 * @member_id  会员id
 */
router.all("/member/growthvalue", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	var member = {
		member_id: requestParams.member_id,
	}
	logger.info("会员中心查询开始 %::%", member);
	//查询用户信息需要的参数
	let userMessage = {
		service: 'member.consumerFind',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	}
	//factoryPromise
	bizUtils.factoryPromise(appConfig.memberPath, userMessage)
		.then(function (value) {
			logger.info('用户信息查询结束 %::%', JSON.stringify(value));
			if (value.data) {
				member.level = value.data.level;
				member.growth_value = value.data.growth_value;
				member.imagePath = value.data.doc_url[value.data.head_pic_path];
			}
			res.render("c/user/growthvalue", member)
		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})

});
//进入充值页面vip推荐商品
router.all("/member/vip", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	logger.info("vip推荐商品查询开始 %::%", requestParams);
	//验证请求参数是否完整
	if (typeof requestParams.user_token == 'undefined' || typeof requestParams.sign == 'undefined') {
		errHandler.incompleteParams(res);
		return;
	};
	bizUtils.validAppToken(req, res, requestParams.app_token, function (tokenValidData) {
		// 根据返回的security_code校验签名
		if (!bizUtils.validSign(bizUtils.clone(requestParams), tokenValidData.data.security_code)) {
			errHandler.invalidSign(res);
			return;
		}
		//查询vip推荐商品的参数
		let vipshop = {
			service: 'psGoods.consumer.vipGoodsFind',
			params: JSON.stringify({})
		};

		//factoryPromise
		bizUtils.factoryPromise(appConfig.goodsPath, vipshop)
			.then(function (value) {
				logger.info('用户信息查询结束 %::%', JSON.stringify(value));
				res.send(value);
			}).catch(function (res, e) {
				errHandler.renderErrorPage(res, e);
			})
	})

});
//会员订单页
router.all("/memberOrder", function (req, res, next) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	var member = {
		appBaseName: appConfig.appBaseName,
		isios: isiOS ? "0" : "1",
		end_time: "",
		username: "",
		imagePath: "",
		memberlevel: requestParams.memberlevel == undefined ? "168" : requestParams.memberlevel,
	}
	// res.render("c/user/memberorder", member);
	logger.info("会员中心查询开始 %::%", requestParams);
	//查询用户信息需要的参数
	let userMessage = {
		service: 'member.consumerFind_v1',
		params: JSON.stringify({
			member_id: requestParams.member_id
		})
	}

	//factoryPromise
	bizUtils.factoryPromise(appConfig.memberPath, userMessage)
		.then(function (value) {
			logger.info('订单用户信息查询结束 %::%', JSON.stringify(value));
			if(value.data.type == "0" || value.data.type == "1" ||value.data.type == "2"||value.data.type == "4") {
				member.is_vip = "Y"
			} else  {
				member.is_vip = "N"
			} 
			if (value.data) {
				member.type = requestParams.type == "1" ? "1" : "0";
				member.mobile = value.data.mobile;
				member.level = value.data.vip_end_time == '' ? 0 : value.data.vip_end_time;
				member.imagePath = value.data.doc_url[value.data.head_pic_path];
			}
			res.render("c/user/memberorder", member);
			// if(requestParams.version == "2.8.1") {
            //     res.render("c/user/iosmemberorder", member);
			// } else {
			// 	res.render("c/user/memberorder", member);
			// }
			
			// if(isiOS) {
			//     res.render("c/user/iosmemberorder", member);
			// } else {
			//     res.render("c/user/memberorder", member);
			// }

		}).catch(function (res, e) {
			errHandler.renderErrorPage(res, e);
		})
});
//常见问题
router.all("/questions", function (req, res, next) {
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	res.render("c/user/questions",{isios:isiOS == true ? "1":"0"});
})
//提现的版本控制
router.all("/withdrawcash",function(req,res,next) {
	var requestParams = bizUtils.extend(req.query, req.body);
	if(requestParams.version == "2.8.4") {
		errHandler.depstionhidden(res)
	} else {
		errHandler.depstionshow(res)
	}
})

//进入会员协议页面
router.all("/userdeal", function (req, res, next) {
	res.render("c/user/deal");
})
//会员中心
// router.all("/memberdetail",function(req,res,next) {
// 	res.render("c/user/member");
// })

//充值记录页面
//     router.all("/recharge",function(req,res,next) {
// 	   // 请求参数
// 	   var requestParams = bizUtils.extend(req.query, req.body);
// 	   //验证请求参数是否完整
// 	   if(typeof requestParams.user_token == 'undefined' || typeof requestParams.sign == 'undefined'|| typeof requestParams.member_id == 'undefined') {
// 		   errHandler.incompleteParams(res);
// 		   return;
// 	     };
//         res.render("c/user/recharge",{
// 			member_id:requestParams.member_id,
// 			user_token:requestParams.user_token,
// 			sign:requestParams.sign.sign,
// 			imageUrl:requestParams.imageUrl
// 	    });

// })

//h5注册接口 
router.all('/register', function (req, res) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	requestParams.params = JSON.parse(requestParams.params);
	requestParams.params.user_id = uuidv1().replace(/-/g, '');
	requestParams.params.data_source = "meitianhui";
	var formData = {
		service: "infrastructure.consumerUserRegister",
		params: JSON.stringify(requestParams.params)
	};
	bizUtils.factoryPromise(appConfig.userPath, formData)
		.then(function (value) {
			logger.info('登录功能返回结果 %::%', JSON.stringify(value))
			res.send(value);
		}).catch(function (err) {
			errHandler.systemError(res, err);
		})
})

//红包兑详情页面
router.all('/redenvelope', function (req, res) {
	try {
		// 请求参数
		
		var requestParams = bizUtils.extend(req.query, req.body);
		logger.info("红包兑详情查询开始 %::%", requestParams);
		var formData = {
			service: 'goods.hongbaoGoodsDetailFind',
			params: JSON.stringify({
				goods_id: requestParams.goods_id,
			})
		};

		request.post(appConfig.goodsPath, { form: formData }, function (error, response, body) {
			try {
				if (!error && response.statusCode == 200) {
					var jsonData = JSON.parse(body);
					logger.info('红包兑查询结束 %::%', JSON.stringify(jsonData));
					goodslist = handleImgUrlAndArea(jsonData.data.list, jsonData.data.doc_url);
					res.render('c/redenvelope/index', {
						goods: goodslist[0],
						member_id: requestParams.member_id,
						type: requestParams.type,
						entry: requestParams.entry,
						mobile: requestParams.mobile,
						appBaseName: appConfig.appBaseName,
					});
				} else {
					errHandler.renderErrorPage(res, error);
				}
			} catch (e) {
				errHandler.renderErrorPage(res, e);
			}
		})

	} catch (e) {
		errHandler.renderErrorPage(res, e);
	}


})
//店铺里面商品的详情页
router.all('/shop_detail', function (req, res) {
	// 请求参数
	var requestParams = bizUtils.extend(req.query, req.body);
	logger.info("查询店铺商品详情开始 %::%", requestParams)
	var formData = {
		service: 'goods.itemStoreDetailFind',
		params: JSON.stringify({
			item_store_id: requestParams.item_store_id
		})
	};
	request.post(appConfig.goodsPath, { form: formData }, function (error, response, body) {
		try {
			if (!error && response.statusCode == 200) {
				var jsonData = JSON.parse(body).data;
				var picUrlArr = [jsonData.doc_url[jsonData.detail.image_info]];
				logger.info("查询店铺商品详情结束 %::%", jsonData)
				res.render('c/redenvelope/shop_detail', {
					market_price: jsonData.detail.market_price,
					item_name: jsonData.detail.item_name,
					desc1: jsonData.detail.desc1,
					vip_price: jsonData.detail.vip_price,
					pic_info_url: picUrlArr[0],
					beike_price: jsonData.detail.beike_price,
					picUrlArr: picUrlArr,
					item_store_id: jsonData.detail.item_store_id,
					store_id: jsonData.detail.store_id
				});


				
			} else {
				errHandler.renderErrorPage(res, error);
			}
		} catch (e) {
			errHandler.renderErrorPage(res, e);
		}
	})
})

// 处理图片url和地区
function handleImgUrlAndArea(goodsList, map) {
	var picUrlArr, pic_info, i, j, picDetailArr, m;
	for (var i = 0; i < goodsList.length; i++) {
		// if(goodsList[i].data_source ==='hsrj'){ //新版 取path_id
		// 	 picUrlArr = [];
		// 	 pic_info = JSON.parse(goodsList[i].pic_info);
		// 	for( j=0; j<pic_info.length; j++) {
		// 		picUrlArr.push(pic_info[j].path_id);
		// 	}
		// 	goodsList[i].picUrlArr = picUrlArr;
		//
		// 	 picDetailArr = [];
		// 	goodsList[i].pic_detail_info = JSON.parse(goodsList[i].pic_detail_info);
		// 	for( m=0; m<goodsList[i].pic_detail_info.length; m++) {
		// 		picDetailArr.push({
		// 			title : goodsList[i].pic_detail_info[m].title || '',
		// 			url : goodsList[i].pic_detail_info[m].path_id
		// 		});
		// 	}
		// 	goodsList[i].picDetailArr = picDetailArr;
		//
		// }else{
		// 处理图片
		if (map && goodsList[i].pic_info) {
			picUrlArr = [];
			pic_info = JSON.parse(goodsList[i].pic_info);
			for (j = 0; j < pic_info.length; j++) {
				picUrlArr.push(map[pic_info[j].path_id]);
			}
			goodsList[i].picUrlArr = picUrlArr;
		}

		// 处理详情图片
		if (map && goodsList[i].pic_detail_info && goodsList[i].pic_detail_info.length != 0) {	// 如果有详情图片
			picDetailArr = [];
			goodsList[i].pic_detail_info = JSON.parse(goodsList[i].pic_detail_info);
			for (m = 0; m < goodsList[i].pic_detail_info.length; m++) {
				picDetailArr.push({
					title: goodsList[i].pic_detail_info[m].title || '',
					url: map[goodsList[i].pic_detail_info[m].path_id]
				});
			}
			goodsList[i].picDetailArr = picDetailArr;
		}

		// }// end  if(goodsList[i].data_source ==='hsrj'){ //新版 取path_id

		// 处理头像
		if (map && goodsList[i].neighbor_pic_path) {
			goodsList[i].neighbor_pic_path = map[goodsList[i].neighbor_pic_path];
		}

		//处理地区
		if (goodsList[i].delivery_desc) {
			var areaArr = goodsList[i].delivery_desc.split('|');
			for (var k = 0; k < areaArr.length; k++) {
				areaArr[k] = areaArr[k].replace('中国,', '').replace(/,/g, '').replace('中国', '全国');
			}
			goodsList[i].delivery_desc = areaArr.join('，');
		}

		// 处理开卖时间
		if (goodsList[i].valid_thru) {
			var buyDate = new Date(goodsList[i].valid_thru);
			var now = new Date();
			var buyday = buyDate.getDay();
			var nowday = now.getDay();
			var leftMilliseconds = buyDate - now;
			if (!isNaN(leftMilliseconds)) {
				goodsList[i].leftSeconds = parseInt(leftMilliseconds / 1000);	// 距离开卖时间还剩多少秒
			} else {
				goodsList[i].leftSeconds = -1;	// 若没有取到距离开卖时间则默认可以直接购买
			}
			//新增一个字段是不是今天的商品
			if (buyday != nowday) {
				goodsList[i].istoday = true;
			} else {
				goodsList[i].istoday = false;
			}
			var saleTimeStr = goodsList[i].valid_thru;
			goodsList[i].saleDate = parseInt(saleTimeStr.substr(5, 2)) + '月' + parseInt(saleTimeStr.substr(8, 2)) + '日';
			goodsList[i].saleTime = saleTimeStr.substr(11, 5) || '00:00';
			// goodsList[i].istoday = 
			if (buyDate.getYear() == now.getYear() && buyDate.getMonth() == now.getMonth()) {
				if (buyDate.getDate() == now.getDate()) goodsList[i].saleDate = '今天';
				if (buyDate.getDate() - now.getDate() == 1) goodsList[i].saleDate = '明天';
			}
		}

		// 处理距离
		if (goodsList[i].distance) {
			if (goodsList[i].distance < 1000) {
				goodsList[i].distance = parseInt(goodsList[i].distance) + 'm';
			} else {
				goodsList[i].distance = (parseInt(goodsList[i].distance) / 1000).toFixed(1) + 'km';
			}
		}

		// 处理标签
		if (goodsList[i].label) {
			var labelArr = goodsList[i].label.split(',');
			goodsList[i].labelArr = labelArr;
		}
	}
	return goodsList;
}



/*
 * 进入“贝壳商城” 商品详情页面
 * @param goods_id  商品id
 */
router.get('/payback/ziying-detail', function (req, res, next) {
	try {

		var requestParams = bizUtils.extend(req.query, req.body);
		console.log(requestParams)
		let userMessage = {
			service: 'member.consumerFind',
			params: JSON.stringify({
				member_id: requestParams.member_id
			})
		}
		var shopData = {
			service: 'goods.beikeMallGoodsDetailFindNew',
			params: JSON.stringify({
				goods_id: requestParams.goods_id,
				member_id: requestParams.member_id ? requestParams.member_id : ""
			})
		};
		let is_vip;
		//factoryPromise
		bizUtils.factoryPromise(appConfig.memberPath, userMessage)
			.then(function (value) {
				logger.info('用户信息查询结束 %::%', JSON.stringify(value));
				if (value.data) {
					is_vip = value.data.is_vip;
				}
				return bizUtils.factoryPromise(appConfig.goodsPath, shopData)
			}).then(function (value) {
				logger.info('商品查询%::%', JSON.stringify(value));
				var goodsList = handleImgUrlAndArea(value.data.list, value.data.doc_url);
				goods = goodsList[0];
				let rules = value.data.skuList;
				//这个是为了对商品的的库存进行排序
				for (let i = 0; i < rules.length; i++) {
					if (rules[i].fValue != undefined) {
						for (let j = 0; j < rules[i].fValue.length; j++) {
							rules[i].fValue.sort((a, b) => {
								return a.stock < b.stock;
							})
						}
					} else {
						rules.sort((a, b) => {
							return a.stock < b.stock;
						})
					}
				}

				res.render('c/payback/ziying-detail', {
					goods: goods,
					rules: rules,
					requestParams: requestParams,
					is_vip: is_vip
				});


			}).catch(function (res, e) {
				errHandler.renderErrorPage(res, e);
			})
	} catch (e) {
		errHandler.renderErrorPage(res, e);
	}
});

//获取规则和颜色

router.post("/order", function (req, res, next) {

	try {
		var requestParams = bizUtils.extend(req.query, req.body);
		var formData = {
			service: 'goods.selectPsGoodsSkuid',
			// params : requestParams,
			params: JSON.stringify({
				goods_code: requestParams.goods_code,
				attr_zvalue: requestParams.attr_zvalue,
			})
		};
		request.post(appConfig.goodsPath, { form: formData }, function (error, response, body) {
			var data = JSON.parse(body);
			res.send(data)
		})
	} catch (e) {

	}
})




/*
 * 分享地址
 */
// router.get('/share', function(req, res, next) {
// 	res.redirect('http://www.meitianhui.com/app');
// });
/*
 * 邀请码分享
 */
router.get('/share', function(req, res, next) {
	const requestParams = bizUtils.extend(req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	var formData = {
		'service' : "member.findInvitationCode",
		'params' : JSON.stringify({member_id:requestParams.member_id})
	}
	if(requestParams.member_id == undefined) {
		errHandler.incompleteParams(res);
		return;
	}
	logger.info('用户查询邀请码开始 %::%', JSON.stringify(formData));
	bizUtils.factoryPromise(appConfig.memberPath, formData)
			.then(function (value) {
				logger.info('用户查询邀请码结束 %::%', JSON.stringify(value));
				if (value.rsp_code == "succ") {
					res.render('c/h5share/Invitationcode', {
						isios: isiOS ? "0" : "1",
						appBaseName: appConfig.appBaseName,
				        invite_code:value.data.invite_code,
					});
				} else {
					res.send(value);
				}
			}).catch(function (res, e) {
				errHandler.renderErrorPage(res, e);
			})
	
});
/*
 * 分享亲情卡
 */
// router.get('/share/prepaycard', function(req, res, next) {
// 	var requestParams = req.query;
// 	res.render('c/share/prepaycard', {
// 		card_no : requestParams.card_no || ''
// 	});
// });

/*
 * 朋友圈
 */
// router.get('/community', function(req, res, next) {
// 	req.url = req.url.replace('community', '');
// 	res.redirect(appConfig.communityHost + req.url);
// });

/*
 * 朋友圈--我的
 */
// router.get('/community/mine', function(req, res, next) {
// 	req.url = req.url.replace('community/', '');
// 	res.redirect(appConfig.communityHost + req.url);
// });

/*
 * 今日头条
 */
// router.get('/dailynews', function(req, res, next) {
// 	var requestParams = req.query;
// 	res.render('c/dailynews/index');
// });

/*
 * 生活服务
 */
// router.get('/lifeservice', function(req, res, next) {
// 	var requestParams = req.query;
// 	res.render('c/lifeservice/index', {
// 		member_id: requestParams.member_id
// 	});
// });

/*
 * 合作商家
 */
// router.get('/businessPartner', function(req, res, next) {
// 	res.redirect('https://cms.meitianhui.com/?p=2329');
// });

// 展示页面
// //根据商品标签查询商品列表
//1。从首页轮播banner图片点进去的，有图片的
//2.从新人攻略点进去的
// http://localhost:3001/openapi/h5/c/tag
router.get('/tag', function (req, res, next) {
	// 请求参数
	var requestParams = Object.assign({}, req.query, req.body);
	res.render('c/tag/list', {
		requestParams: requestParams
	});
});

// router.get('/tag1',function(req,res,next){
// 	// 请求参数
// 		var requestParams = Object.assign({},req.query, req.body);
// 		res.render('c/tag1/list', {
// 			requestParams: requestParams
// 		});
// });



// router.get('/tag2',function(req,res,next){
// 	// 请求参数
// 		var requestParams = Object.assign({},req.query, req.body);
// 		console.log("tag2 %::%",requestParams);
// 		var now = new Date();
// 		var hours = now.getHours();
// 		var tabSelect = null;
// 		if (hours > 0 && hours < 10) {
// 			hours = 10;
// 			tabSelect =10;
// 		} else if (hours >= 10 && hours < 12 ){
// 			hours = 10;
// 			tabSelect =10;
// 		} else if (hours >= 12 && hours < 16 ){
// 			hours = 12;
// 			tabSelect =12;
// 		} else if(hours >=16 && hours < 20){
// 			hours = 16;
// 			tabSelect =16;
// 		} else {
// 			hours = 20;
// 			tabSelect =20;
// 		}
// 	    if (requestParams.time == undefined) {
// 			requestParams.time = tabSelect;
// 		} 
// 		requestParams.Hours = now.getHours(); 
//         requestParams.hours = hours; 
// 		res.render('c/tag2/list', {
// 			requestParams: requestParams
// 		});
// });


router.get('/activity/newperson', function (req, res, next) {
	var requestParams = Object.assign({}, req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	res.render('c/activity/newpersonwx', {
		member_id: requestParams.member_id,
		isiOS: isiOS ? "0" : "1"
	});
});
router.get('/activity/download', function (req, res, next) {
	var requestParams = Object.assign({}, req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	res.render('c/activity/downapp', {
		appBaseName: appConfig.appBaseName,
		member_id: requestParams.member_id,
		isiOS: isiOS ? "0" : "1"
	});
});

router.get('/activity/invoic',async function (req, res, next) {
	var requestParams = Object.assign({}, req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	if(requestParams.member_id){
		var formData = {
			'service' : "member.consumerFind_v1",
			params: JSON.stringify({
				member_id: requestParams.member_id
			})
		}
		logger.info('用户查询邀请码开始 %::%', JSON.stringify(formData));
		try {
			var value = await bizUtils.factoryPromise(appConfig.memberPath, formData);
			if (value.rsp_code != "succ") {
				res.send(value);
			}else{
				res.render('c/activity/lianxi', {
					appBaseName: appConfig.appBaseName,
					member_id: requestParams.member_id,
					isiOS: isiOS ? "0" : "1",
					dowanapp: requestParams.dowanapp,
					gotobuy: requestParams.gotobuy,
					mobile: requestParams.mobile,
					payfaile:requestParams.payfaile,
					nick_name:value.data.nick_name || value.data.mobile.substring(0, 3) + "****" + value.data.mobile.substring(7, value.data.mobile.length),
					doc_url:value.data.doc_url[value.data.head_pic_path] || ''
				});
			}
		} catch (error) {
			errHandler.renderErrorPage(res, error);
		}
	}else{
		res.render('c/activity/lianxi', {
			appBaseName: appConfig.appBaseName,
			member_id: requestParams.member_id,
			isiOS: isiOS ? "0" : "1",
			dowanapp: requestParams.dowanapp,
			gotobuy: requestParams.gotobuy,
			mobile: requestParams.mobile,
			payfaile:requestParams.payfaile,
			nick_name:'',
			doc_url:''
		});
	}


});
router.get('/activity/order', function (req, res, next) {
	var requestParams = Object.assign({}, req.query, req.body);
	const UserAgent = req.headers["user-agent"];
	const isiOS = !!UserAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	res.render('c/activity/order', {
		appBaseName: appConfig.appBaseName,
		member_id: requestParams.member_id,
		mobile: requestParams.mobile,
		goods_id: requestParams.goods_id,
		isiOS: isiOS ? "0" : "1"
	});
});

/*
 * 获取0元购商品列表(新增)
 * @param display_area	商品类别
 * @param area_id		地区id
 */
// router.post('/payback/zero/getList', function(req, res, next) {
// 	try {
// 		// 请求参数
// 		var requestParams = bizUtils.extend(req.query, req.body);
// 		var formData = {
// 			service: requestParams.service,
// 			params: requestParams.params,
// 			page: requestParams.page
// 		};
// 		request.post(appConfig.goodsPath, {form: formData}, function(error, response, body) {
// 			try {
// 				if(!error && response.statusCode == 200) {
// 					var jsonData = JSON.parse(body);
// 					var goodsList = jsonData.rsp_code == 'succ' ? jsonData.data.list : [];
// 					goodsList = handleImgUrlAndArea(goodsList);
// 					res.send({
// 						goodsList : goodsList,
// 						page : jsonData.data ? jsonData.data.page : {}
// 					});
// 				} else {
// 					errHandler.systemError(res, error);
// 				}
// 			} catch(e) {
// 				errHandler.systemError(res, e);
// 			}
// 		});
// 	} catch(e) {
// 		errHandler.systemError(res, e);
// 	}
// });

/*
 * 进入零元购详情页面(新增)
 * @param goods_id  商品id
 */

// router.get('/payback/zero/detail', function(req, res, next) {
// 	try {
// 		// 请求参数
// 		var requestParams = bizUtils.extend(req.query, req.body);
// 		requestParams.list=[
// 			JSON.parse(requestParams.list)[0],
// 			JSON.parse(requestParams.list)[1]
// 		];

//         var formData = {
// 			// service : 'goods.psGoodsDetailFind',//这个是淘淘领的service
// 			service : 'goods.gdFreeGetGoodsDetailFind',
// 			params : JSON.stringify({
// 				goods_id : requestParams.goods_id
// 			})
// 		};
// 		console.log("0元购formdata:",formData)
// 		request.post(appConfig.goodsPath, {form: formData}, function(error, response, body) {
// 			try {
// 				if(!error && response.statusCode == 200) {
// 					var data = JSON.parse(body);
// 					console.log('0元购body:',body);
// 					var goods = {};
// 					if(data.data && data.data.list.length != 0) {
// 						var goodsList = handleImgUrlAndArea(data.data.list, data.data.doc_url);
// 						goods = goodsList[0];
// 					}
// 					console.log('0元购goods:',JSON.stringify(goods) );
// 					if(requestParams.mobile) {	// 如果有传手机号则直接判断该用户是否能领取该商品
// 						var validFormData = {
// 							service: 'order.freeGetValidate',
// 							params: JSON.stringify({
// 								goods_code: goods.goods_code,
// 								mobile: requestParams.mobile
// 							})
// 						};
// 						request.post(appConfig.opsorderPath, {form: validFormData}, function(error, response, body) {
// 							try {
// 								if(!error && response.statusCode == 200) {
// 									var validResult = JSON.parse(body);
// 									res.render('c/payback/zero-detail', {
// 										goods: goods,
// 										alreadyGot: validResult.data ? validResult.data.already_got : 'false',		// 默认没有领过
// 										inBlacklist: validResult.data ? validResult.data.in_blacklist : 'false',	// 默认不在黑名单
// 										requestParams: requestParams
// 									});
// 								} else {
// 									errHandler.renderErrorPage(res, error);
// 								}
// 							} catch(e) {
// 								errHandler.renderErrorPage(res, e);
// 							}
// 						});
// 					} else {
// 						console.log("传过来的推荐商品列表",requestParams)
// 						res.render('c/payback/zero-detail', {
// 							goods: goods,
// 							alreadyGot: 'false',
// 							inBlacklist: 'false',
// 							requestParams: requestParams
// 						});
// 					}
// 				} else {
// 					errHandler.renderErrorPage(res, error);
// 				}
// 			} catch(e) {
// 				errHandler.renderErrorPage(res, e);
// 			}
// 		});
// 	} catch(e) {
// 		errHandler.renderErrorPage(res, e);
// 	}
// });

module.exports = router;
