package com.meitianhui.infrastructure.service.impl;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.DesCbcSecurity;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.MD5Util;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.RegexpValidateUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.infrastructure.constant.Constant;
import com.meitianhui.infrastructure.constant.RspCode;
import com.meitianhui.infrastructure.controller.UserController;
import com.meitianhui.infrastructure.dao.PaymentSecurityDao;
import com.meitianhui.infrastructure.dao.UserDao;
import com.meitianhui.infrastructure.dao.UserWechatDao;
import com.meitianhui.infrastructure.entity.PaymentSecurity;
import com.meitianhui.infrastructure.entity.User;
import com.meitianhui.infrastructure.entity.UserLog;
import com.meitianhui.infrastructure.entity.UserWechat;
import com.meitianhui.infrastructure.service.UserService;

@SuppressWarnings("unchecked")
@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private PaymentSecurityDao paymentSecurityDao;
	
	@Autowired
	private  UserWechatDao  userWechatDao;

	@Override
	public void consumerLoginForOAuth(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 直接调用兑换中心接口
			Map<String, Object> tempMap = new HashMap<String, Object>();
			ValidateUtil.validateParams(paramsMap,
					new String[] { "user_account", "password", "member_type_key", "request_info" });
			String user_account = (String) paramsMap.get("user_account");
			String password = (String) paramsMap.get("password");
			String request_info = (String) paramsMap.get("request_info");
			// token存内存数据库
			Map<String, String> reqParams = new HashMap<String, String>();
			String php_consumer_service_url = PropertiesConfigUtil.getProperty("php_consumer_service_url");
			reqParams.put("appid", "mth");
			reqParams.put("secret", "mth123");
			logger.info("获取兑换中心apptoken->request:" + reqParams.toString());
			String resultStr = HttpClientUtil.post(php_consumer_service_url + "/token.html", reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			logger.info("获取兑换中心apptoken->response:" + resultMap.toString());
			String errmsg = (String) resultMap.get("errmsg");
			if (errmsg != null) {
				userLogAdd(user_account, "登陆失败", "消费者" + user_account + "兑换中心登陆失败->兑换中心返回信息:" + errmsg);
				throw new BusinessException(RspCode.PASSWORD_ERROR, RspCode.MSG.get(RspCode.PASSWORD_ERROR));
			}
			Map<String, Object> resultDate = (Map<String, Object>) resultMap.get("data");
			reqParams.clear();
			resultMap.clear();
			reqParams.put("method", "user_passport.login");
			reqParams.put("access_token", (String) resultDate.get("access_token"));
			reqParams.put("login_account", user_account);
			reqParams.put("login_password", password);
			reqParams.put("password_type", "md5");
			logger.info("兑换中心登陆->request:" + reqParams.toString());
			resultStr = HttpClientUtil.post(php_consumer_service_url, reqParams);
			resultMap = FastJsonUtil.jsonToMap(resultStr);
			logger.info("兑换中心登陆->response:" + resultMap.toString());
			if ((String) resultMap.get("errmsg") != null) {
				userLogAdd(user_account, "登陆失败", "消费者" + user_account + "兑换中心登陆失败->兑换中心返回信息:" + errmsg);
				throw new BusinessException(RspCode.PASSWORD_ERROR, RspCode.MSG.get(RspCode.PASSWORD_ERROR));
			}
			resultDate = (Map<String, Object>) resultMap.get("data");
			String user_token = (String) resultDate.get("access_token");
			String user_id = (Integer) resultDate.get("user_id") + "";
			String security_code = IDUtil.getShortUUID();
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("user_id", user_id);
			userMap.put("user_token", user_token);
			userMap.put("request_info", request_info);
			userMap.put("security_code", security_code);
			redisUtil.setObj(user_token, userMap);
			result.setResultData(userMap);
			userLogAdd(user_id, "登陆成功", "消费者" + user_account + "惠顾家登陆成功;request_info:" + request_info);
			tempMap.clear();
			tempMap = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void consumerInfoForOAuthFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 直接调用兑换中心接口
			Map<String, Object> tempMap = new HashMap<String, Object>();
			ValidateUtil.validateParams(paramsMap, new String[] { "user_id" });
			String user_id = (String) paramsMap.get("user_id");
			// token存内存数据库
			Map<String, String> reqParams = new HashMap<String, String>();
			String php_consumer_service_url = PropertiesConfigUtil.getProperty("php_consumer_service_url");
			reqParams.put("method", "temporary.getAccountById");
			reqParams.put("app_token", PropertiesConfigUtil.getProperty("php_consumer_service_app_token"));
			reqParams.put("user_id", user_id);
			logger.info("兑换中心获取用户信息->request:" + reqParams.toString());
			String resultStr = HttpClientUtil.post(php_consumer_service_url + "/temporary.html", reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			logger.info("兑换中心获取用户信息->response:" + resultMap.toString());
			if ((String) resultMap.get("errmsg") != null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, resultMap.get("errmsg") + "");
			}
			Map<String, Object> resultDate = (Map<String, Object>) resultMap.get("data");
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("login_account", StringUtil.formatStr(resultDate.get("login_account")));
			userMap.put("mobile", StringUtil.formatStr(resultDate.get("mobile")));
			userMap.put("email", StringUtil.formatStr(resultDate.get("email")));
			result.setResultData(userMap);
			tempMap.clear();
			tempMap = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberLoginLogUpdate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "member_id", "member_type_key", "device_type", "alias", "device_model" });
			String data_source = StringUtil.formatStr(paramsMap.get("data_source"));
			if (StringUtils.isEmpty(data_source)) {
				String member_type_key = StringUtil.formatStr(paramsMap.get("member_type_key"));
				if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
					data_source = Constant.DATA_SOURCE_SJLY_01;
				} else {
					data_source = Constant.DATA_SOURCE_SJLY_02;
				}
				paramsMap.put("data_source", data_source);
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			// 删除旧数据
			tempMap.put("member_id", paramsMap.get("member_id"));
			tempMap.put("member_type_key", paramsMap.get("member_type_key"));
			tempMap.put("data_source", data_source);
			userDao.deleteMemberLoginInfo(tempMap);
			// 新增记录
			tempMap.clear();
			tempMap.put("device_model", paramsMap.get("device_model"));
			paramsMap.put("remark", FastJsonUtil.toJson(tempMap));
			userDao.insertMemberLoginLog(paramsMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void mobileLogin(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			ValidateUtil.validateParams(paramsMap,
					new String[] { "mobile", "member_type_key", "request_info", "data_source" });
			String mobile = (String) paramsMap.get("mobile");
			String data_source = (String) paramsMap.get("data_source");
			String memberId = StringUtil.formatStr(paramsMap.get("member_id")) ;
			tempMap.put("mobile", mobile);
			User user = userDao.selectUser(tempMap);
			if (null == user) {
				userLogAdd("", "登陆失败", "会员类型->" + paramsMap.get("member_type_key") + ",登陆失败->" + mobile
						+ "不存在,device_model:" + paramsMap.get("device_model"));
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			String status =  StringUtil.formatStr(user.getStatus());
			if(!StringUtil.equals(status, "normal")) {
				throw new BusinessException(RspCode.USER_STATUS_ERROR, RspCode.MSG.get(RspCode.USER_STATUS_ERROR));
			}
			Map<String, String> reqParams = new HashMap<String, String>();
			// 获取用户对于的会员id
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			reqParams.clear();
			reqParams.put("service", "member.memberIdFind");
			tempMap.clear();
//			if(StringUtil.isNotBlank(memberId)) {
//				tempMap.put("parentId", memberId);
//			}
			tempMap.put("user_id", user.getUser_id());
			tempMap.put("member_type_key", (String) paramsMap.get("member_type_key"));
			tempMap.put("type", StringUtil.formatStr(paramsMap.get("type")));
			reqParams.put("params", FastJsonUtil.toJson(tempMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				userLogAdd(user.getUser_id(), "登陆失败", "会员类型->" + paramsMap.get("member_type_key") + ",登陆失败->"
						+ (String) resultMap.get("error_msg") + ",device_model:" + paramsMap.get("device_model"));
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			String member_id = (String) dateMap.get("member_id");
			String is_manager = StringUtil.formatStr(dateMap.get("is_manager"));
			// 删除原token信息
			String user_token = redisUtil.getStr(user.getUser_id() + member_id + data_source);
			if (user_token != null) {
				redisUtil.del(user.getUser_id() + member_id + data_source);
				redisUtil.del(user_token);
			}
			String security_code = IDUtil.getShortUUID();
			user_token = IDUtil.generateToken(IDUtil.getUUID());
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("data_source", data_source);
			userMap.put("user_id", user.getUser_id());
			userMap.put("security_code", security_code);
			userMap.put("mobile", mobile);
			userMap.put("member_id", member_id);
			userMap.put("member_type_key", paramsMap.get("member_type_key"));
			userMap.put("request_info", paramsMap.get("request_info"));
			// 存储新token信息
			redisUtil.setObj(user_token, userMap);
			redisUtil.setStr(user.getUser_id() + member_id + data_source, user_token);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", member_id);
			map.put("user_token", StringUtil.formatStr(user_token));
			map.put("security_code", security_code);
			map.put("mobile", user.getMobile());
			map.put("is_manager",is_manager);
			// 查询支付密码选项信息
			this.paymentSecuritySet(tempMap, map);
			map.put("user_id", user.getUser_id());
			result.setResultData(map);
			logger.info("登陆信息->user_id:" + user.getUser_id() + ",token:" + user_token);
			userLogAdd(user.getUser_id(), "登陆成功", "会员类型->" + paramsMap.get("member_type_key") + ",request_info:"
					+ paramsMap.get("request_info") + ", device_model:" + paramsMap.get("device_model"));
			user = null;
			tempMap.clear();
			tempMap = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void consumerLogin(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile","password", "request_info", "data_source" });
			String mobile = (String) paramsMap.get("mobile");
			String password = (String) paramsMap.get("password");
			String data_source = (String) paramsMap.get("data_source");
			String memberId = (String) paramsMap.get("member_id");
			String invite_code = StringUtil.formatStr(paramsMap.get("invite_code"));
			tempMap.put("mobile", mobile);
			User user = userDao.selectUser(tempMap);
			if (null == user) {
				userLogAdd("", "登陆失败", "消费者登陆失败->" + mobile + "不存在,device_model:" + paramsMap.get("device_model"));
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			if(!StringUtils.equals("h5", data_source)) {
				if(!StringUtils.equals(password,user.getPassword())) {
					throw new BusinessException(RspCode.PASSWORD_ERROR, RspCode.MSG.get(RspCode.PASSWORD_ERROR));
				}
			}
			String status =  StringUtil.formatStr(user.getStatus());
			if(!StringUtil.equals(status, "normal")) {
				throw new BusinessException(RspCode.USER_STATUS_ERROR, RspCode.MSG.get(RspCode.USER_STATUS_ERROR));
			}
			
			Map<String, String> reqParams = new HashMap<String, String>();
			// 获取用户对于的会员id
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			reqParams.clear();
			tempMap.clear();
			reqParams.put("service", "member.consumerLoginValidate");
			tempMap.put("user_id", user.getUser_id());
			if(StringUtils.isNotEmpty(memberId)) {
				tempMap.put("memberId", memberId);
			}
			if(StringUtils.isNotEmpty(invite_code)) {
				tempMap.put("invite_code", invite_code);
			}
			tempMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
			reqParams.put("params", FastJsonUtil.toJson(tempMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				userLogAdd(user.getUser_id(), "登陆失败", "消费者登陆失败->" + (String) resultMap.get("error_msg")
						+ ",device_model:" + paramsMap.get("device_model"));
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			
			String member_id = (String) dateMap.get("member_id");
			String is_manager = StringUtil.formatStr(dateMap.get("is_manager"));
			// 删除原token信息
			String user_token = redisUtil.getStr(user.getUser_id() + member_id + data_source);
			if (user_token != null) {
				redisUtil.del(user.getUser_id() + member_id + data_source);
				redisUtil.del(user_token);
			}
			String security_code = IDUtil.getShortUUID();
			user_token = IDUtil.generateToken(IDUtil.getUUID());
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("data_source", data_source);
			userMap.put("user_id", user.getUser_id());
			userMap.put("security_code", security_code);
			userMap.put("mobile", mobile);
			userMap.put("member_id", member_id);
			userMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
			userMap.put("request_info", paramsMap.get("request_info"));
			// 存储新token信息
			redisUtil.setObj(user_token, userMap);
			redisUtil.setStr(user.getUser_id() + member_id + data_source, user_token);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", member_id);
			map.put("user_token", StringUtil.formatStr(user_token));
			map.put("security_code", security_code);
			map.put("mobile", user.getMobile());
			// 查询支付密码选项信息
			this.paymentSecuritySet(tempMap, map);
			map.put("user_id", user.getUser_id());
			map.put("is_manager",is_manager);
			result.setResultData(map);
			logger.info("登陆信息->user_id:" + user.getUser_id() + ",token:" + user_token);
			userLogAdd(user.getUser_id(), "登陆成功", "消费者登陆成功,request_info:" + paramsMap.get("request_info")
					+ ", device_model:" + paramsMap.get("device_model"));
			user = null;
			tempMap.clear();
			tempMap = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	//微信登录
	@Override
	public void consumerWeChatLogin(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
	
		Map<String, Object> tempMap = new HashMap<String, Object>();
		ValidateUtil.validateParams(paramsMap, new String[] { "openid", "data_source","device_model"});
		String openId =  (String) paramsMap.get("openid");
		String data_source = (String) paramsMap.get("data_source");
		tempMap.put("openId", openId);
		UserWechat userWechat =  userWechatDao.getUserWechat(tempMap);
		if(null == userWechat) {
			//前端跳转到绑定手机号
			userLogAdd("", "登陆失败", "消费者微信登陆失败-> openId:" + openId + "不存在,device_model:" + paramsMap.get("device_model"));
			throw new BusinessException(RspCode.WECHAT_NOT_EXIST, RspCode.MSG.get(RspCode.WECHAT_NOT_EXIST));
		}
		//怎么判断是第一次。进行查询会员，有就用会员信息，没有就用微信信息
		Map<String, Object> tempMap1 = new HashMap<String, Object>();
		tempMap1.put("mobile", userWechat.getMobile());
		User user = userDao.selectUser(tempMap1);
		if (null == user) {
			userLogAdd("", "登陆失败", "消费者登陆失败->" + userWechat.getMobile() + "不存在,device_model:" + paramsMap.get("device_model"));
			throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
		}
		Map<String, String> reqParams = new HashMap<String, String>();
		// 获取用户对于的会员id
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		reqParams.clear();
		tempMap.clear();
		reqParams.put("service", "member.consumerLoginValidate");
		tempMap.put("user_id", user.getUser_id());
		tempMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
		reqParams.put("params", FastJsonUtil.toJson(tempMap));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			userLogAdd(user.getUser_id(), "登陆失败", "消费者登陆失败->" + (String) resultMap.get("error_msg")
					+ ",device_model:" + paramsMap.get("device_model"));
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
		String member_id = (String) dateMap.get("member_id");
		// 删除原token信息
		String user_token = redisUtil.getStr(user.getUser_id() + member_id + data_source);
		if (user_token != null) {
			redisUtil.del(user.getUser_id() + member_id + data_source);
			redisUtil.del(user_token);
		}
		String security_code = IDUtil.getShortUUID();
		user_token = IDUtil.generateToken(IDUtil.getUUID());
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("data_source", data_source);
		userMap.put("user_id", user.getUser_id());
		userMap.put("security_code", security_code);
		userMap.put("mobile", userWechat.getMobile());
		userMap.put("member_id", member_id);
		userMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
		userMap.put("request_info", paramsMap.get("request_info"));
		// 存储新token信息
		redisUtil.setObj(user_token, userMap);
		redisUtil.setStr(user.getUser_id() + member_id + data_source, user_token);
		
		//通过会员id 进行查询会员信息
		Map<String, Object>  memberMap =  this.getMemberInfo(member_id);
		Map<String, Object> map = new HashMap<String, Object>();
		if(!memberMap.isEmpty()) {
			if(StringUtils.isEmpty((String) memberMap.get("nick_name"))) {
				map.put("nick_name", userWechat.getNickName());
			}else {
				map.put("nick_name", (String) memberMap.get("nick_name"));
			}
			
			if(StringUtils.isEmpty((String) memberMap.get("sex_key"))) {
				map.put("sex_key", userWechat.getSex());
			}else {
				map.put("sex_key", (String) memberMap.get("sex_key"));
			}
			Map<String, Object> docUrlMap = (Map<String, Object>) memberMap.get("doc_url");
			if(StringUtils.isEmpty((String) memberMap.get("head_pic_path")) &&  docUrlMap.isEmpty()  ) {
				map.put("head_pic_path", userWechat.getHeadImgUrl());
			}else {
				map.put("head_pic_path", (String) memberMap.get("head_pic_path"));
				map.put("doc_url", docUrlMap);
			}
		}
		map.put("member_id", member_id);
		map.put("user_token", StringUtil.formatStr(user_token));
		map.put("security_code", security_code);
		map.put("mobile", user.getMobile());
		// 查询支付密码选项信息
		this.paymentSecuritySet(tempMap, map);
		map.put("user_id", user.getUser_id());
		result.setResultData(map);
		logger.info("登陆信息->user_id:" + user.getUser_id() + ",token:" + user_token);
		userLogAdd(user.getUser_id(), "登录成功", "消费者登陆成功,request_info:" + paramsMap.get("request_info")
				+ ", device_model:" + paramsMap.get("device_model"));
		user = null;
		tempMap.clear();
		tempMap = null;
	}
	
	/**
	 * 通过memberId查找会员信息
	 * @param memberId
	 * @return
	 */
	public Map<String,Object> getMemberInfo(String memberId){
		try {
			Map<String, String> reqParams = new HashMap<String, String>();
			// 获取用户对于的会员id
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, Object> tempMap = new HashMap<String, Object>();
			reqParams.put("service", "member.consumerFind");
			tempMap.put("member_id", memberId);
			reqParams.put("params", FastJsonUtil.toJson(tempMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			return dateMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 通过手机号查找会员信息
	 * @param memberId
	 * @return
	 */
	public Map<String,Object> getMemberByMobile(String mobile){
		try {
			Map<String, String> reqParams = new HashMap<String, String>();
			// 获取用户对于的会员id
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, Object> tempMap = new HashMap<String, Object>();
			reqParams.put("service", "member.consumerFindByPhone");
			tempMap.put("mobile", mobile);
			reqParams.put("params", FastJsonUtil.toJson(tempMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			return dateMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void salesmanLogin(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			ValidateUtil.validateParams(paramsMap, new String[] {"mobile", "request_info", "device_model", "data_source" });
			String mobile = (String) paramsMap.get("mobile");
			String data_source = (String) paramsMap.get("data_source");
			tempMap.put("mobile", mobile);
			User user = userDao.selectUser(tempMap);
			if (null == user) {
				userLogAdd("", "登陆失败", "消费者登陆失败->" + mobile + "不存在,device_model:" + paramsMap.get("device_model"));
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			
			Map<String, String> reqParams = new HashMap<String, String>();
			// 获取用户对于的会员id
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			reqParams.clear();
			tempMap.clear();
			reqParams.put("service", "salesman.operate.salesmanLoginValidate");
			tempMap.put("user_id", user.getUser_id());
			tempMap.put("member_type_key", Constant.MEMBER_TYPE_SALESMAN);
			reqParams.put("params", FastJsonUtil.toJson(tempMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				userLogAdd(user.getUser_id(), "登陆失败", "消费者登陆失败->" + (String) resultMap.get("error_msg")
						+ ",device_model:" + paramsMap.get("device_model"));
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			String member_id = (String) dateMap.get("member_id");
			// 删除原token信息
			String user_token = redisUtil.getStr(user.getUser_id() + member_id + data_source);
			if (user_token != null) {
				redisUtil.del(user.getUser_id() + member_id + data_source);
				redisUtil.del(user_token);
			}
			String security_code = IDUtil.getShortUUID();
			user_token = IDUtil.generateToken(IDUtil.getUUID());
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("data_source", data_source);
			userMap.put("user_id", user.getUser_id());
			userMap.put("security_code", security_code);
			userMap.put("mobile", mobile);
			userMap.put("member_id", member_id);
			userMap.put("member_type_key", Constant.MEMBER_TYPE_SALESMAN);
			userMap.put("request_info", paramsMap.get("request_info"));
			// 存储新token信息
			redisUtil.setObj(user_token, userMap);
			redisUtil.setStr(user.getUser_id() + member_id + data_source, user_token);
			Map<String, Object> map = new HashMap<String, Object>();
			map.putAll(dateMap);
			map.put("user_token", StringUtil.formatStr(user_token));
			map.put("security_code", security_code);
			map.put("mobile", user.getMobile());
			// 查询支付密码选项信息
			this.paymentSecuritySet(tempMap, map);
			map.put("user_id", user.getUser_id());
			result.setResultData(map);
			logger.info("登陆信息->user_id:" + user.getUser_id() + ",token:" + user_token);
			userLogAdd(user.getUser_id(), "登陆成功", "消费者登陆成功,request_info:" + paramsMap.get("request_info")
					+ ", device_model:" + paramsMap.get("device_model"));
			user = null;
			tempMap.clear();
			tempMap = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}	
	}
	
	@Override
	public void storeLogin(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_account", "request_info", "data_source" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			String request_info = (String) paramsMap.get("request_info");
			String data_source = (String) paramsMap.get("data_source");
			String mobile = (String) paramsMap.get("user_account");
			User user = null;
			if (!RegexpValidateUtil.isPhone(mobile)) {
				userLogAdd("", "登陆失败", "门店登陆失败->手机号" + mobile + "格式错误,device_model:" + paramsMap.get("device_model"));
				throw new BusinessException(RspCode.PHONE_ERROR, RspCode.MSG.get(RspCode.PHONE_ERROR));
			}
			tempMap.put("mobile", mobile);
			user = userDao.selectUser(tempMap);
			if (user == null) {
				userLogAdd("", "登陆失败", "门店登陆失败->" + mobile + "不存在,device_model:" + paramsMap.get("device_model"));
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			// 获取用户对于的会员id
			Map<String, String> reqParams = new HashMap<String, String>();
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			reqParams.put("service", "member.storeLoginValidate");
			tempMap.clear();
			tempMap.put("user_id", user.getUser_id());
			reqParams.put("params", FastJsonUtil.toJson(tempMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				userLogAdd(user.getUser_id(), "登陆失败", "门店登陆失败->" + (String) resultMap.get("error_msg")
						+ ",device_model:" + paramsMap.get("device_model"));
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			String member_id = (String) dateMap.get("member_id");
			String is_admin = (String) dateMap.get("is_admin");
			String area_id = (String) dateMap.get("area_id");
			String business_type_key = (String) dateMap.get("business_type_key");
			String stores_type_key = (String) dateMap.get("stores_type_key");
			// 删除原token信息
			String user_token = redisUtil.getStr(user.getUser_id() + member_id + data_source);
			if (user_token != null) {
				redisUtil.del(user.getUser_id() + member_id + data_source);
				redisUtil.del(user_token);
			}
			String security_code = IDUtil.getShortUUID();
			user_token = IDUtil.generateToken(IDUtil.getUUID());
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("data_source", data_source);
			userMap.put("user_id", user.getUser_id());
			userMap.put("request_info", request_info);
			userMap.put("security_code", security_code);
			userMap.put("mobile", user.getMobile());
			userMap.put("area_id", area_id);
			userMap.put("member_id", member_id);
			userMap.put("member_type_key", Constant.MEMBER_TYPE_STORES);
			// 存储新token信息
			redisUtil.setObj(user_token, userMap);
			redisUtil.setStr(user.getUser_id() + member_id + data_source, user_token);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", (String) dateMap.get("member_id"));
			map.put("user_token", StringUtil.formatStr(user_token));
			map.put("security_code", StringUtil.formatStr(security_code));
			map.put("mobile", user.getMobile());
			// 查询支付密码选项信息
			this.paymentSecuritySet(tempMap, map);
			map.put("user_id", user.getUser_id());
			map.put("is_admin", is_admin);
			map.put("business_type_key", business_type_key);
			map.put("stores_type_key", stores_type_key);
			result.setResultData(map);

			logger.info("便利店登陆信息->user_id:" + user.getUser_id() + ",token:" + user_token);
			userLogAdd(user.getUser_id(), "登陆成功", "门店登陆成功,request_info:" + paramsMap.get("request_info")
					+ ",device_model:" + paramsMap.get("device_model"));
			user = null;
			tempMap.clear();
			tempMap = null;
			reqParams.clear();
			reqParams = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void supplierLogin(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile", "request_info", "data_source" });
			String request_info = (String) paramsMap.get("request_info");
			String data_source = (String) paramsMap.get("data_source");
			String mobile = (String) paramsMap.get("mobile");
			User user = null;
			if (!RegexpValidateUtil.isPhone(mobile)) {
				userLogAdd("", "登陆失败", "供应商登陆失败->手机号" + mobile + "格式错误,device_model:" + paramsMap.get("device_model"));
				throw new BusinessException(RspCode.PHONE_ERROR, RspCode.MSG.get(RspCode.PHONE_ERROR));
			}
			tempMap.put("mobile", mobile);
			user = userDao.selectUser(tempMap);
			if (user == null) {
				userLogAdd("", "登陆失败", "供应商登陆失败->" + mobile + "不存在,device_model:" + paramsMap.get("device_model"));
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			// 获取用户对于的会员id
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, String> bizParams = new HashMap<String, String>();
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			reqParams.put("service", "member.supplierLoginValidate");
			bizParams.put("user_id", user.getUser_id());
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				userLogAdd(user.getUser_id(), "登陆失败", "供应商登陆失败->" + (String) resultMap.get("error_msg")
						+ ",device_model:" + paramsMap.get("device_model"));
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			String member_id = (String) dateMap.get("member_id");
			String is_admin = (String) dateMap.get("is_admin");
			String area_id = (String) dateMap.get("area_id");
			String supplier_no = (String) dateMap.get("supplier_no");
			String supplier_name = (String) dateMap.get("supplier_name");

			// 删除原token信息
			String user_token = redisUtil.getStr(user.getUser_id() + member_id + data_source);
			if (user_token != null) {
				redisUtil.del(user.getUser_id() + member_id + data_source);
				redisUtil.del(user_token);
			}
			String security_code = IDUtil.getShortUUID();
			user_token = IDUtil.generateToken(IDUtil.getUUID());
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("data_source", data_source);
			userMap.put("user_id", user.getUser_id());
			userMap.put("request_info", request_info);
			userMap.put("security_code", security_code);
			userMap.put("mobile", user.getMobile());
			userMap.put("area_id", area_id);
			userMap.put("member_id", member_id);
			// 存储新token信息
			redisUtil.setObj(user_token, userMap);
			redisUtil.setStr(user.getUser_id() + member_id + data_source, user_token);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", (String) dateMap.get("member_id"));
			map.put("user_token", StringUtil.formatStr(user_token));
			map.put("security_code", StringUtil.formatStr(security_code));
			map.put("mobile", user.getMobile());
			// 查询支付密码选项信息
			this.paymentSecuritySet(tempMap, map);
			map.put("user_id", user.getUser_id());
			map.put("is_admin", is_admin);
			map.put("supplier_no", supplier_no);
			map.put("supplier_name", supplier_name);
			result.setResultData(map);

			logger.info("供应商登陆信息->user_id:" + user.getUser_id() + ",token:" + user_token);
			userLogAdd(user.getUser_id(), "登陆成功", "供应商登陆成功,request_info:" + paramsMap.get("request_info"));
			user = null;
			tempMap.clear();
			tempMap = null;
			reqParams.clear();
			reqParams = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 返回支付密码选项信息
	 * 
	 * @param paramsMap
	 * @param map
	 * @throws SystemException
	 */
	private void paymentSecuritySet(Map<String, Object> paramsMap, Map<String, Object> map) throws Exception {
		List<PaymentSecurity> paymentSecurityList;
		try {
			paymentSecurityList = paymentSecurityDao.selectPaymentSecurity(paramsMap);
			if (paymentSecurityList != null && paymentSecurityList.size() > 0) {
				PaymentSecurity paymentSecurity = paymentSecurityList.get(0);
				String payment_password = paymentSecurity.getPayment_password();
				String small_direct = paymentSecurity.getSmall_direct();
				String sms_notify = paymentSecurity.getSms_notify();
				map.put("small_direct_flag", small_direct);
				map.put("sms_notify_flag", sms_notify);
				if (StringUtils.isNotEmpty(payment_password)) {
					map.put("payment_password_flag", "Y");
				} else {
					map.put("payment_password_flag", "N");
				}
			} else {
				map.put("small_direct_flag", "N");
				map.put("sms_notify_flag", "N");
				map.put("payment_password_flag", "N");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void userLogout(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_token", "request_info" });
			String user_token = (String) paramsMap.get("user_token");
			Map<String, Object> redisMap = (Map<String, Object>) redisUtil.getObj(user_token);
			if (redisMap == null) {
				throw new BusinessException(RspCode.USER_TOKEN_ERROR, RspCode.MSG.get(RspCode.USER_TOKEN_ERROR));
			}
			String member_type_key = redisMap.get("member_type_key") + "";
			String data_source = StringUtil.formatStr(redisMap.get("data_source"));
			Map<String, Object> tempMap = new HashMap<String, Object>();
			if (StringUtils.isEmpty(data_source)) {
				if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
					data_source = Constant.DATA_SOURCE_SJLY_01;
				} else {
					data_source = Constant.DATA_SOURCE_SJLY_02;
				}
				tempMap.put("data_source", data_source);
			}
			tempMap.put("member_id", redisMap.get("member_id"));
			tempMap.put("member_type_key", redisMap.get("member_type_key"));
			userDao.deleteMemberLoginInfo(tempMap);
			redisUtil.del(user_token);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void userTokenClear(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_id", "member_id" });
			String user_id = (String) paramsMap.get("user_id");
			String member_id = (String) paramsMap.get("member_id");
			List<String> list = new ArrayList<String>();
			list.add("meitianhui");
			list.add("local_life");
			list.add("store_assistant");
			list.add("shume");
			for (String data_source : list) {
				String user_token = redisUtil.getStr(user_id + member_id + data_source);
				if (user_token != null) {
					redisUtil.del(user_id + member_id + data_source);
					redisUtil.del(user_token);
				}
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void userValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_token", "request_info" });
			Map<String, Object> redisMap = (Map<String, Object>) redisUtil.getObj((String) paramsMap.get("user_token"));
			if (redisMap == null) {
				throw new BusinessException(RspCode.USER_TOKEN_ERROR, RspCode.MSG.get(RspCode.USER_TOKEN_ERROR));
			}
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("user_id", StringUtil.formatStr(redisMap.get("user_id")));
			resultMap.put("security_code", StringUtil.formatStr(redisMap.get("security_code")));
			resultMap.put("member_id", StringUtil.formatStr(redisMap.get("member_id")));
			resultMap.put("mobile", StringUtil.formatStr(redisMap.get("mobile")));
			resultMap.put("area_id", StringUtil.formatStr(redisMap.get("area_id")));
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void userValidateNoRequestInfo(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_token" });
			String user_token = (String) paramsMap.get("user_token");
			Map<String, Object> redisMap = (Map<String, Object>) redisUtil.getObj(user_token);
			if (redisMap == null) {
				throw new BusinessException(RspCode.USER_TOKEN_ERROR, RspCode.MSG.get(RspCode.USER_TOKEN_ERROR));
			}
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("user_id", StringUtil.formatStr(redisMap.get("user_id")));
			resultMap.put("security_code", StringUtil.formatStr(redisMap.get("security_code")));
			resultMap.put("member_id", StringUtil.formatStr(redisMap.get("member_id")));
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void userFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_id" });
			User user = userDao.selectUser(paramsMap);
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("user_account", user.getUser_account());
			resultMap.put("mobile", StringUtil.formatStr(user.getMobile()));
			resultMap.put("user_name", StringUtil.formatStr(user.getUser_name()));
			resultMap.put("email", StringUtil.formatStr(user.getEmail()));
			resultMap.put("registered_date", DateUtil.date2Str(user.getRegistered_date(), DateUtil.fmt_yyyyMMddHHmmss));
			result.setResultData(resultMap);
			user = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberTypeFindByMobile(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			String user_id = "";
			String member_type = "";
			String mobile = (String) paramsMap.get("mobile");
			tempMap.put("mobile", mobile);
			User user = userDao.selectUser(tempMap);
			if (null != user) {
				user_id = user.getUser_id();
				String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
				Map<String, String> reqParams = new HashMap<String, String>();
				tempMap.clear();
				reqParams.put("service", "member.memberTypeFind");
				tempMap.put("user_id", user.getUser_id());
				reqParams.put("params", FastJsonUtil.toJson(tempMap));
				String resultStr = HttpClientUtil.post(member_service_url, reqParams);
				tempMap.clear();
				tempMap = FastJsonUtil.jsonToMap(resultStr);
				if (((String) tempMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					Map<String, Object> dateMap = (Map<String, Object>) tempMap.get("data");
					member_type = (String) dateMap.get("member_type");
				}
			}
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("member_type", member_type);
			resultMap.put("user_id", user_id);
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberTypeValidateByMobile(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile", "member_type_key" });
			Map<String, Object> resultDateMap = new HashMap<String, Object>();
			resultDateMap.put("member_type", "not_exist");
			resultDateMap.put("user", "not_exist");
			String mobile = (String) paramsMap.get("mobile");
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("mobile", mobile);
			User user = userDao.selectUser(tempMap);
			if (null != user) {
				resultDateMap.put("user", "exist");
				resultDateMap.put("user_id", user.getUser_id());
				String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
				Map<String, String> reqParams = new HashMap<String, String>();
				Map<String, Object> paramMap = new HashMap<String, Object>();
				reqParams.put("service", "member.memberTypeFind");
				paramMap.put("user_id", user.getUser_id());
				reqParams.put("params", FastJsonUtil.toJson(paramMap));
				String resultStr = HttpClientUtil.post(member_service_url, reqParams);
				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"),
							(String) resultMap.get("error_msg"));
				}
				Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
				String member_type = (String) dateMap.get("member_type");
				String member_type_key = (String) paramsMap.get("member_type_key");
				if (member_type.contains(member_type_key)) {
					resultDateMap.put("member_type", "exist");
				}
			}
			result.setResultData(resultDateMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void userFindByMobile(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("mobile", paramsMap.get("mobile"));
			User user = userDao.selectUser(tempMap);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("user_id", user == null ? "" : user.getUser_id());
			if(user == null || StringUtil.equals("0", user.getPassword()) || StringUtil.equals("123456", user.getPassword())) {
				if (user == null) {
					resultMap.put("password","");
					resultMap.put("is_exist", "N");
				} else {
					resultMap.put("password","Y");
					resultMap.put("is_exist", "Y");
				}
			}else {
				resultMap.put("password","N");
				resultMap.put("is_exist", "Y");
			}
			result.setResultData(resultMap);
			user = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberFindByMobile(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile", "member_type_key" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("mobile", paramsMap.get("mobile"));
			User user = userDao.selectUser(tempMap);
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("service", "member.memberIdFind");
			tempMap.clear();
			tempMap.put("user_id", user.getUser_id());
			tempMap.put("member_type_key", paramsMap.get("member_type_key"));
			reqParams.put("params", FastJsonUtil.toJson(tempMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			String rsp_code = (String) resultMap.get("rsp_code");
			if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			resultMap.clear();
			resultMap.put("member_id", dateMap.get("member_id"));
			result.setResultData(resultMap);
			reqParams.clear();
			tempMap.clear();
			reqParams = null;
			tempMap = null;
			user = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	 
	/*
	 * 提供给第三方的接口
	 * */
	@Override
	public void handleConsumerUserRegister(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_id", "mobile", "password" });
			logger.info("注册日志,mobile:" + (String) paramsMap.get("mobile") + ";type:"
					+ StringUtil.formatStr(paramsMap.get("type")));
			String mobile = (String) paramsMap.get("mobile");
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("mobile", paramsMap.get("mobile"));
			User user = userDao.selectUser(tempMap);
			String member_id = paramsMap.get("user_id") + "";
			String user_id = null;
			// 如果用户已经存在,说明用户可能已其他身份已经注册,此时只要建立用户和消费者用户的关系即可
			if (null == user) {
				// 注册用户
				String password = (String) paramsMap.get("password");
				user_id = IDUtil.getUUID();
				logger.info("手机号" + paramsMap.get("mobile") + "未注册,注册新用户,会员id:" + member_id);
				user = new User();
				user.setStatus(Constant.USER_STATUS_NORMAL);
				user.setUser_id(user_id);
				user.setMobile((String) paramsMap.get("mobile"));
				user.setRegistered_date(new Date());
				user.setPassword(password);
				userDao.insertUser(user);
				// 如果是微信登录时，绑定用户和微信
				userLogAdd(user_id, "用户注册", (String) paramsMap.get("mobile") + "注册成功");
			} else {
				user_id = user.getUser_id();
				// 查找 member 的mdconsumer 的 type
				Map<String, Object> map = this.getMemberByMobile(mobile);
				if (null != map) {
					Integer type = (Integer) map.get("type");
					if (null != type && type != 3) {
						throw new BusinessException(RspCode.USER_EXIST, RspCode.MSG.get(RspCode.USER_EXIST));
					}
					paramsMap.put("user_id", map.get("consumer_id"));
				}
			}
			// 注册
			this.userRegisterSimple(user_id, member_id, mobile);
			// 初始化会员资产信息 增加1314贝壳
			this.syncFinanceMemberAsset(paramsMap, 1);
			tempMap.clear();
			tempMap = null;
			logger.info("消费者注册完成");
		} catch (BusinessException e) {
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	//外部用户注册
	public  void userRegisterSimple(String user_id,String member_id,String mobile) throws SystemException, Exception {
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		reqParams.put("service", "member.consumerRegistSimple");
		tempMap.clear();
		tempMap.put("user_id", user_id);
		tempMap.put("member_id", member_id);
		tempMap.put("mobile", mobile);
		tempMap.put("registered_date", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		tempMap.put("status", Constant.USER_STATUS_NORMAL);
		reqParams.put("params", FastJsonUtil.toJson(tempMap));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		String rsp_code = (String) resultMap.get("rsp_code");
		if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
	}
	
	
	
	@Override
	public void handleUserMemberRegister( Map<String, Object> paramsMap,    ResultData result)
			throws BusinessException, SystemException, Exception {
				try {
					ValidateUtil.validateParams(paramsMap, new String[] { "user_id", "mobile", "check_code","data_source"});
					logger.info("注册日志,mobile:"+(String) paramsMap.get("mobile")+";type:"+StringUtil.formatStr(paramsMap.get("type"))+";member_id:"+
							StringUtil.formatStr(paramsMap.get("member_id")));
					String mobile = (String) paramsMap.get("mobile");
					String data_source = (String) paramsMap.get("data_source");
					String type = StringUtil.formatStr(paramsMap.get("type"));
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("mobile", paramsMap.get("mobile"));
					User user = userDao.selectUser(tempMap);
					String member_id = paramsMap.get("user_id") + "";
					String user_id = null;
					// 如果用户已经存在,说明用户可能已其他身份已经注册,此时只要建立用户和消费者用户的关系即可
					if (null == user) {
						// 注册用户
							String password = StringUtil.formatStr(paramsMap.get("password"));
							if(StringUtil.isBlank(password)) {
								password = "123456";
							}
							user_id = IDUtil.getUUID();
							logger.info("手机号" + paramsMap.get("mobile") + "未注册,注册新用户,会员id:" + member_id);
							user = new User();
							user.setStatus(Constant.USER_STATUS_NORMAL);
							user.setUser_id(user_id);
							user.setMobile((String) paramsMap.get("mobile"));
							user.setRegistered_date(new Date());
							String slat = generateSlat(6);
							user.setSlat(slat);
							if(StringUtil.equals(type, "h5")) {
								user.setPassword("0");
							}else {
								user.setPassword(password);
							}
							
							int flag =	userDao.insertUser(user);
							if(flag ==1) {
								//写入md_consumer及相关的逻辑
								int memberType = this.syncUserMemberSave(paramsMap, user_id);
								this.syncFinanceMemberAsset(paramsMap, memberType);
//								this.buildingWeChatSave(paramsMap);
								userLogAdd(user_id, "用户注册", (String) paramsMap.get("mobile") + "注册成功");
							}else {
								throw new BusinessException(RspCode.SYSTEM_ERROR, "保存用户信息出错");
							}
							/**
							 * 返回登录的信息
							 * */
							// 删除原token信息
							String user_token = redisUtil.getStr(user.getUser_id() + member_id + data_source);
							if (user_token != null) {
								redisUtil.del(user.getUser_id() + member_id + data_source);
								redisUtil.del(user_token);
							}
							String security_code = IDUtil.getShortUUID();
							user_token = IDUtil.generateToken(IDUtil.getUUID());
							Map<String, Object> userMap = new HashMap<String, Object>();
							userMap.put("data_source", data_source);
							userMap.put("user_id", user.getUser_id());
							userMap.put("security_code", security_code);
							userMap.put("mobile", mobile);
							userMap.put("member_id", member_id);
							userMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
							userMap.put("request_info", paramsMap.get("request_info"));
							// 存储新token信息
							redisUtil.setObj(user_token, userMap);
							redisUtil.setStr(user.getUser_id() + member_id + data_source, user_token);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("member_id", member_id);
							map.put("user_token", StringUtil.formatStr(user_token));
							map.put("security_code", security_code);
							map.put("mobile", user.getMobile());
							// 查询支付密码选项信息
//							this.paymentSecuritySet(tempMap, map);
							map.put("user_id", user.getUser_id());
							map.put("regist", "regist");
							result.setResultData(map);
							tempMap.clear();
							tempMap = null;
							logger.info("消费者注册完成");

//							try {
							//注册一个云通讯账号
//							Map<String,String> requestParams = new HashMap<>();
//							String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
//							requestParams.put("service","tim.operate.accountImport");
//							Map<String,String> accountMap = new HashMap<>();
//							accountMap.put("identifier",(String) paramsMap.get("mobile"));
//
//							requestParams.put("params", FastJsonUtil.toJson(accountMap));
//							String resultStr = HttpClientUtil.postShort(member_service_url, requestParams);
//							if(org.apache.commons.lang.StringUtils.isNotEmpty(resultStr)){
//
//								Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
//
//								if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
//									logger.error("云通讯单个账号注册成功!");
//								}else{
//									logger.error("云通讯单个账号注册失败");
//								}
//							}

//							}catch (Exception e){
//								logger.error("云通讯单个账号注册系统错误!,或日志同步错误"+paramsMap.toString());
//							e.printStackTrace();
//						}


					} else {
						throw new BusinessException(RspCode.USER_EXIST, RspCode.MSG.get(RspCode.USER_EXIST));
					}
				} catch (BusinessException e) {
					throw e;
				} catch (NoSuchAlgorithmException e) {
					throw e;
				} catch (Exception e) {
					throw e;
				}
	}
	
	
	
	
	/**
	 *   写入用户的会员相关信息
	 * @param paramsMap
	 * @param user_id
	 * @return
	 * @throws SystemException
	 * @throws Exception
	 */
	public  int  syncUserMemberSave(Map<String, Object> paramsMap,String user_id) throws SystemException, Exception {
		Map<String, Object> tempMap =  new HashMap<String, Object>();
		String member_id = paramsMap.get("user_id") + "";
		String type = StringUtil.formatStr(paramsMap.get("type"));
		// 注册消费者会员信息
		String distributionId = StringUtil.formatStr(paramsMap.get("member_id")) ;
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("service", "member.consumerSync");
		tempMap.clear();
		tempMap.put("user_id", user_id);
		tempMap.put("member_id", member_id);
		tempMap.put("mobile", paramsMap.get("mobile"));
		tempMap.put("type", type);
		if(StringUtil.equals(type, "h5")) {
			if(StringUtil.isEmpty(distributionId)){
				throw new BusinessException(RspCode.DISTRBUTION_ERROR, RspCode.MSG.get(RspCode.DISTRBUTION_ERROR));
			}
			tempMap.put("distributionId", distributionId);
		}
		if(StringUtil.equals(type, "app") || StringUtil.equals(type, "account")) {
			String invite_code = StringUtil.formatStr(paramsMap.get("invite_code"));
			if(StringUtil.isNotEmpty(invite_code)){
				tempMap.put("invite_code", invite_code);
//				throw new BusinessException(RspCode.INVIT_CODE_ERROR, RspCode.MSG.get(RspCode.INVIT_CODE_ERROR));
			}
			
		}
		tempMap.put("registered_date", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		tempMap.put("status", Constant.USER_STATUS_NORMAL);
		reqParams.put("params", FastJsonUtil.toJson(tempMap));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		String rsp_code = (String) resultMap.get("rsp_code");
		if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
		Integer  memberType = (Integer) dataMap.get("type");	
		if(null !=memberType ) {
			return memberType;
		}else {
			return 0;
		}
	}
	
	/**
	 *   写入会员资产
	 * @throws Exception 
	 * @throws SystemException 
	 */
	public void  syncFinanceMemberAsset(Map<String, Object> paramsMap,int memberType) throws SystemException, Exception {
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		String member_id = paramsMap.get("user_id") + "";
		// 初始化会员资产信息
		reqParams.clear();
		tempMap.clear();
		String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
		reqParams.put("service", "finance.memberAssetInit");
		tempMap.put("member_id", member_id);
		tempMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
		if(memberType ==1) {
			tempMap.put("type", 1);
		}
		reqParams.put("params", FastJsonUtil.toJson(tempMap));
		String resultStr = HttpClientUtil.post(finance_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		String rsp_code = (String) resultMap.get("rsp_code");
		if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
	}
	
	
	
	/**
	 * 	注册时候绑定微信
	 * @param paramsMap
	 * @throws Exception
	 */
	public  void  buildingWeChatSave(Map<String, Object> paramsMap) throws Exception {
		String type = StringUtil.formatStr(paramsMap.get("type"));
		String mobile = (String) paramsMap.get("mobile");
		if(StringUtils.equals(type, "wechat")) {
			String openId =  (String) paramsMap.get("openid"); 
			String nickname =  (String) paramsMap.get("nickname");
			Integer sex = null;
			if(paramsMap.get("sex") instanceof Integer) {
				sex =  (Integer) paramsMap.get("sex");
			}
			if(paramsMap.get("sex") instanceof String) {
				sex = Integer.valueOf((String) paramsMap.get("sex"));
			}
			String city =  (String) paramsMap.get("city");
			String province =  (String) paramsMap.get("province");
			String country =  (String) paramsMap.get("country");
			String headimgurl =  (String) paramsMap.get("headimgurl");
			String unionid =  (String) paramsMap.get("unionid");
			String  id = IDUtil.getUUID();
			Map<String, Object> userWechatMap = new HashMap<String, Object>();
			userWechatMap.put("openId", openId);
			UserWechat userWechatInfo =  userWechatDao.getUserWechat(userWechatMap);
			if(null != userWechatInfo) {
				throw new BusinessException(RspCode.USER_WECHAT_EXIST, RspCode.MSG.get(RspCode.USER_WECHAT_EXIST));
			}else {
				UserWechat  userWechat =  new UserWechat();
				userWechat.setUserWechatId(id);
				userWechat.setOpenId(openId);
				userWechat.setMobile(mobile);
				userWechat.setNickName(nickname); 
				userWechat.setSex(sex);
				userWechat.setCity(city);
				userWechat.setProvince(province);
				userWechat.setCountry(country);
				userWechat.setHeadImgUrl(headimgurl);
				userWechat.setUnionId(unionid);
				userWechat.setCreateTime(new Date());
				userWechat.setStatus(1);
				userWechatDao.insert(userWechat);
			}
			
		}

	}
	
	
	
	
	
	
	
	
	@Override
	public void handleSyncUserRegister(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile", "user_name" });
			// 是否需要注册新用户 true/false 是/否
			String user_id = IDUtil.getUUID();
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("mobile", (String) paramsMap.get("mobile"));
			User user = userDao.selectUser(tempMap);
			if (null != user) {
				logger.info("用户" + (String) paramsMap.get("mobile") + "已存在,获取用户id");
				user_id = user.getUser_id();
				String user_name_temp = (String) paramsMap.get("user_name");
				String user_name = StringUtil.formatStr(user.getUser_name());
				if (!user_name.equals(user_name_temp)) {
					tempMap.clear();
					tempMap.put("user_id", user_id);
					tempMap.put("user_name", user_name_temp);
					userDao.updateUser(tempMap);
				}
			} else {
				logger.info("用户" + (String) paramsMap.get("mobile") + "不存在,创建用户");
				user = new User();
				BeanConvertUtil.mapToBean(user, paramsMap);
				String slat = generateSlat(6);
				String password = MD5Util.MD5Encode((String) paramsMap.get("mobile"));
				String encrptPassword = (DesCbcSecurity.encode(password.toLowerCase() + DesCbcSecurity.md5(slat)))
						.toUpperCase();
				user.setStatus(Constant.USER_STATUS_NORMAL);
				user.setUser_id(user_id);
				user.setUser_name((String) paramsMap.get("user_name"));
				user.setSlat(slat);
				user.setPassword(encrptPassword);
				user.setRegistered_date(new Date());
				userDao.insertUser(user);
				userLogAdd(user_id, "用户同步", user.getMobile() + "注册成功");
			}
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("user_id", user_id);
			result.setResultData(resultMap);
			tempMap.clear();
			tempMap = null;
			user = null;
		} catch (BusinessException e) {
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改用户密码
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void handleUserPasswordChange(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// 更新用户信息
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_id", "old_password", "new_password" });
			User user = userDao.selectUser(paramsMap);
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			String oldPassword = (String) paramsMap.get("old_password");
			String newPassword = (String) paramsMap.get("new_password");
			// 验证旧密码加密后与数据库是否一致
			if(StringUtils.equals(oldPassword, user.getPassword())) {
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("user_id", (String) paramsMap.get("user_id"));
				userMap.put("password", newPassword);
				userDao.updateUser(userMap);
				userLogAdd((String) paramsMap.get("user_id"), "资料变更", "修改密码");
				userMap.clear();
				userMap = null;
				
			}else {
				throw new BusinessException(RspCode.OLD_PASSWORD_ERROR, RspCode.MSG.get(RspCode.OLD_PASSWORD_ERROR));
			}
			
			
			// 验证旧密码加密后与数据库是否一致
			/*if (this.checkPassword(oldPassword, user.getPassword(), user.getSlat())) {
				String encrptPassword = (DesCbcSecurity
						.encode(newPassword.toLowerCase() + DesCbcSecurity.md5(user.getSlat()))).toUpperCase();
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("user_id", (String) paramsMap.get("user_id"));
				userMap.put("password", encrptPassword);
				userDao.updateUser(userMap);
				userLogAdd((String) paramsMap.get("user_id"), "资料变更", "修改密码");
				userMap.clear();
				userMap = null;
			} else {
				throw new BusinessException(RspCode.OLD_PASSWORD_ERROR, RspCode.MSG.get(RspCode.OLD_PASSWORD_ERROR));
			}*/
			user = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 重置用户密码
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void handleUserPasswordReset(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// 更新用户信息
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile", "password" });
			User user = userDao.selectUser(paramsMap);
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("user_id", user.getUser_id());
			String password = (String) paramsMap.get("password");
			/*String encyptPasswd = (DesCbcSecurity.encode(password.toLowerCase() + DesCbcSecurity.md5(user.getSlat())))
					.toUpperCase();*/
			userMap.put("password", password);
			userDao.updateUser(userMap);
			userMap.clear();
			userMap = null;
			user = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 用户信息编辑
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void userEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// 更新用户信息
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_id" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("user_id", (String) paramsMap.get("user_id"));
			User user = userDao.selectUser(tempMap);
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			userDao.updateUser(paramsMap);
			userLogAdd((String) paramsMap.get("user_id"), "资料变更", FastJsonUtil.toJson(paramsMap));
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 用户登陆手机号变更
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void handleLoginMobileChange(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// 更新用户信息
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_id", "mobile" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("user_id", (String) paramsMap.get("user_id"));
			User user = userDao.selectUser(tempMap);
			String beforeMobile =  user.getMobile();
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			// 校验修改的手机号是否被其他用户使用
			tempMap.clear();
			tempMap.put("mobile", (String) paramsMap.get("mobile"));
			user = userDao.selectUser(tempMap);
			if (null != user) {
				throw new BusinessException(RspCode.PHONE_EXIST, RspCode.MSG.get(RspCode.PHONE_EXIST));
			}
			userDao.updateUserMobile(paramsMap);
			//修改mdconsumer
			this.consumerPhoneUpdate(paramsMap);
			userLogAdd((String) paramsMap.get("user_id"), "资料变更", beforeMobile+"登陆手机号变更为" + (String) paramsMap.get("mobile"));
			tempMap.clear();
			tempMap = null;
			user = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public  void consumerPhoneUpdate(Map<String, Object> params) throws SystemException, Exception {
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> requestData = new HashMap<String, String>();
		requestData.put("service", "member.consumerPhoneUpdate");
		requestData.put("params", FastJsonUtil.toJson(params));
		String resultData = HttpClientUtil.post(member_service_url, requestData);
	} 

	private String generateSlat(int length) {
		char[] chars = new char[62];
		chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
		int arrayLength = chars.length - 1;
		String slat = "";
		for (int i = 0; i < length; i++) {
			int position = (int) (Math.random() * arrayLength);
			slat = slat + chars[position];
		}
		return slat;
	}

	/**
	 * 验证该账号密码
	 * 
	 * @param password
	 * @param loginPassword
	 * @param slat
	 */
	private boolean checkPassword(String password, String loginPassword, String slat) throws Exception {
		// 验证加密密码 DigestUtils.sha1Hex(password)
		boolean flag = false;
		try {
			String encyptPasswd = (DesCbcSecurity.encode(password.toLowerCase() + DesCbcSecurity.md5(slat)))
					.toUpperCase();
			if (encyptPasswd.equals(loginPassword)) {
				flag = true;
			} else {
				logger.info("密码:" + loginPassword + "输入的密码:" + encyptPasswd + "slat:" + slat);
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 设置支付密码
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handlePayPasswordSetting(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "user_id", "payment_password" });

			// 验证用户是否存在
			User user = userDao.selectUser(paramsMap);
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}

			// 查询支付密码是否已经存在
			List<PaymentSecurity> paymentSecurityList = paymentSecurityDao.selectPaymentSecurity(paramsMap);
			if (paymentSecurityList != null && paymentSecurityList.size() > 0) {
				paymentSecurityDao.updatePaymentSecurity(paramsMap);
			} else {
				PaymentSecurity paymentSecurity = new PaymentSecurity();
				paymentSecurity.setUser_id((String) paramsMap.get("user_id"));
				paymentSecurity.setPayment_password((String) paramsMap.get("payment_password"));
				String small_direct = (String) paramsMap.get("small_direct");
				// 设置小额免密默认为否
				if (StringUtils.isNotEmpty(small_direct)) {
					paymentSecurity.setSmall_direct(small_direct);
				} else {
					paymentSecurity.setSmall_direct("N");
				}
				// 设置默认为100
				paymentSecurity.setSmall_direct_amount(new BigDecimal(100));
				// 设置短信通知，可选值：Y（是）N（否）默认为N
				String sms_notify = (String) paramsMap.get("sms_notify");
				if (StringUtils.isNotEmpty(sms_notify)) {
					paymentSecurity.setSms_notify(sms_notify);
				} else {
					paymentSecurity.setSms_notify("N");
				}
				paymentSecurityDao.insertPaymentSecurity(paymentSecurity);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 支付选项设置
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handlePaySecurityOptions(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "user_id", "small_direct", "sms_notify" });
		try {
			// 验证用户是否存在
			User user = userDao.selectUser(paramsMap);
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			// 查询支付密码是否已经存在
			List<PaymentSecurity> paymentSecurityList = paymentSecurityDao.selectPaymentSecurity(paramsMap);
			if (paymentSecurityList != null && paymentSecurityList.size() > 0) {
				paymentSecurityDao.updatePaymentSecurity(paramsMap);
			} else {
				PaymentSecurity paymentSecurity = new PaymentSecurity();
				paymentSecurity.setUser_id((String) paramsMap.get("user_id"));
				String small_direct = (String) paramsMap.get("small_direct");
				paymentSecurity.setSmall_direct(small_direct);
				// 设置默认为100
				paymentSecurity.setSmall_direct_amount(new BigDecimal(100));
				// 设置短信通知
				String sms_notify = (String) paramsMap.get("sms_notify");
				paymentSecurity.setSms_notify(sms_notify);
				paymentSecurityDao.insertPaymentSecurity(paymentSecurity);
			}
			user = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void payPasswordValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "user_id", "payment_password" });
		try {
			// 验证用户是否存在
			User user = userDao.selectUser(paramsMap);
			if (user == null) {
				throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
			}
			// 查询支付密码是否已经存在
			List<PaymentSecurity> paymentSecurityList = paymentSecurityDao.selectPaymentSecurity(paramsMap);
			if (paymentSecurityList != null && paymentSecurityList.size() > 0) {
				PaymentSecurity paymentSecurity = paymentSecurityList.get(0);
				String payment_password = paymentSecurity.getPayment_password();
				String input_payment_password = (String) paramsMap.get("payment_password");
				if (!payment_password.equals(input_payment_password)) {
					// 支付密码错误
					throw new BusinessException(RspCode.PAYMENT_PASSWORD_ERROR,
							RspCode.MSG.get(RspCode.PAYMENT_PASSWORD_ERROR));
				}
			} else {
				// 支付密码不存在
				throw new BusinessException(RspCode.PAYMENT_PASSWORD_NOT_EXIST,
						RspCode.MSG.get(RspCode.PAYMENT_PASSWORD_NOT_EXIST));
			}
			user = null;
			paymentSecurityList.clear();
			paymentSecurityList = null;
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	private void userLogAdd(String user_id, String category, String event)
			throws BusinessException, SystemException, Exception {
		try {
			UserLog userLog = new UserLog();
			userLog.setLog_id(IDUtil.getUUID());
			userLog.setUser_id(user_id);
			userLog.setCategory(category);
			userLog.setEvent(event);
			userLog.setTracked_date(new Date());
			userDao.insertUserLog(userLog);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}


	@Override
	public void userWechatFindByMobile(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile" });
			UserWechat userWechat = userWechatDao.getUserWechat(paramsMap);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			if (null != userWechat) {
				resultMap.put("openid", userWechat.getOpenId());
			} else {
				resultMap.put("openid", "");
			}
			result.setResultData(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void userBindingWechat(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "mobile","openid" });
		String mobile =  (String) paramsMap.get("mobile"); 
		String openId =  (String) paramsMap.get("openid"); 
		String nickname =  (String) paramsMap.get("nickname");
		Integer sex = null;
		if(paramsMap.get("sex") instanceof Integer) {
			sex =  (Integer) paramsMap.get("sex");
		}
		if(paramsMap.get("sex") instanceof String) {
			sex = Integer.valueOf((String) paramsMap.get("sex"));
		}
		String city =  (String) paramsMap.get("city");
		String province =  (String) paramsMap.get("province");
		String country =  (String) paramsMap.get("country");
		String headimgurl =  (String) paramsMap.get("headimgurl");
		String unionid =  (String) paramsMap.get("unionid");
		String  id = IDUtil.getUUID();
		
		//判断手机号码，是否注册过
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("mobile", mobile);
		User user = userDao.selectUser(tempMap);
		if(null == user) {
			throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
		}
		
		Map<String, Object> userWechatMap = new HashMap<String, Object>();
		userWechatMap.put("openId", openId);
		UserWechat userWechatInfo =  userWechatDao.getUserWechat(userWechatMap);
		if(null != userWechatInfo) {
			throw new BusinessException(RspCode.USER_WECHAT_EXIST, RspCode.MSG.get(RspCode.USER_WECHAT_EXIST));
		}else {
			UserWechat  userWechat =  new UserWechat();
			userWechat.setUserWechatId(id);
			userWechat.setOpenId(openId);
			userWechat.setMobile(mobile);
			userWechat.setNickName(nickname); 
			userWechat.setSex(sex);
			userWechat.setCity(city);
			userWechat.setProvince(province);
			userWechat.setCountry(country);
			userWechat.setHeadImgUrl(headimgurl);
			userWechat.setUnionId(unionid);
			userWechat.setCreateTime(new Date());
			userWechat.setStatus(1);
			int flag  =userWechatDao.insert(userWechat);
			if(flag !=1) {
				throw new BusinessException(RspCode.BINDING_WECHAT_ERROR, RspCode.MSG.get(RspCode.BINDING_WECHAT_ERROR));
			}
			
			//  给会员增加成长值
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.userGrowthValue");
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", user.getUser_id());
			params.put("growth_value", "10");
			requestData.put("params", FastJsonUtil.toJson(params));
			String resultStr = HttpClientUtil.post(member_service_url, requestData);
		}
	}
}
