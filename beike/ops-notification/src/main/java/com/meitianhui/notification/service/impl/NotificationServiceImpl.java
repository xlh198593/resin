package com.meitianhui.notification.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meitianhui.base.controller.BaseController;
import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.notification.constant.Constant;
import com.meitianhui.notification.constant.RspCode;
import com.meitianhui.notification.dao.NotificationDao;
import com.meitianhui.notification.entity.IdMessageQueue;
import com.meitianhui.notification.service.JpushService;
import com.meitianhui.notification.service.MessagesService;
import com.meitianhui.notification.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
	
	private static final Logger logger = Logger.getLogger(NotificationServiceImpl.class);

	@Autowired
	public NotificationDao notificationDao;

	@Autowired
	public RedisUtil redisUtil;

	@Resource(name = "aliyunService")
	public MessagesService aliyunService;

	@Autowired
	public JpushService jpushService;

	
	@Override
	public void sendCheckCode(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "sms_source", "mobile"});
			logger.info("发送短信验证码；sms_source:"+(String) paramsMap.get("sms_source")+"======mobile:"+(String) paramsMap.get("mobile") +
					"++++++==type:"+(String) paramsMap.get("type"));
			//加入安全验证码验证 对应的验证码是在接口： infrastructure.securityVerify 加入到redis的
			//根据当前的日期生成时间戳(2017-09-08) 来获取钥匙KEY
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//	        Date date = simpleDateFormat.parse(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
//	        String timeStamp = String.valueOf(date.getTime());
//	        String lockValue = redisUtil.getStr("[safetyCodeVerify]_meitianhui"+timeStamp);
//	        if(!lockValue.equals(paramsMap.get("lock_value").toString())){
//	        	throw new BusinessException(RspCode.CHECK_CODE_DISABLED, RspCode.MSG.get(RspCode.CHECK_CODE_DISABLED));
//	        }
	        //查询数据库，判断用户是否注册过
			
			String mobile = (String) paramsMap.get("mobile");
			if(StringUtils.isEmpty(mobile)) {
				throw new BusinessException("mobile_is_null", "手机号为空");
			}
			
			//判断类型
			String type = StringUtil.formatStr(paramsMap.get("type"));
			
			
			Map<String, Object> userMap = new HashMap<String, Object>();
			String user_service_url = PropertiesConfigUtil.getProperty("user_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("service", "infrastructure.userFindByMobile");
			userMap.put("mobile", mobile);
			reqParams.put("params", FastJsonUtil.toJson(userMap));
			String resultStr = HttpClientUtil.post(user_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			String rsp_code = (String) resultMap.get("rsp_code");
			if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
			String userId =  (String) dateMap.get("user_id");
			if(StringUtils.equals(type, "regist") || StringUtils.equals(type, "editPhone")) {
				if(StringUtils.isNotEmpty(userId)) {
					throw new BusinessException(RspCode.USER_EXIST, RspCode.MSG.get(RspCode.USER_EXIST));
				}
			}
			
			if(StringUtils.equals(type, "reset") || StringUtils.equals(type, "login")) {
				if(StringUtils.isEmpty(userId)) {
					throw new BusinessException(RspCode.USER_NOT_EXIST, RspCode.MSG.get(RspCode.USER_NOT_EXIST));
				}
			}
			
			String userStatus = null;
			if(StringUtils.equals(type, "binding")  ) {	
				//这个手机号码如果已经绑定了微信就不用，就不要再发送短信
				Map<String, Object> memberMap = new HashMap<String, Object>();
				String member_service_url = PropertiesConfigUtil.getProperty("user_service_url");
				reqParams.clear();
				reqParams.put("service", "infrastructure.userWechatFindByMobile");
				memberMap.put("mobile", mobile);
				reqParams.put("params", FastJsonUtil.toJson(memberMap));
				String resultData = HttpClientUtil.post(member_service_url, reqParams);
				Map<String, Object> resultDataMap = FastJsonUtil.jsonToMap(resultData);
				String rspCode = (String) resultDataMap.get("rsp_code");
				if (!rspCode.equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
				}
				Map<String, Object> memberDataMap = (Map<String, Object>) resultDataMap.get("data");
				String  openid  = (String) memberDataMap.get("openid");
				if(StringUtils.isNotEmpty(openid)) {
					throw new BusinessException(RspCode.MOBILE_WECHAT_EXIST, RspCode.MSG.get(RspCode.MOBILE_WECHAT_EXIST));
				}
				
				if(StringUtils.isNotEmpty(userId)) {
					userStatus ="user_exist";
				}else {
					userStatus ="user_not_exist";
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userStatus", userStatus);
				result.setResultData(map);
			}
			
			//先判断是否有这个KEY
			String key = mobile+"incr";
			boolean   flag =redisUtil.exists(key);
			if(flag == false) {
				redisUtil.setStr(key, "1");
			}else {
				String incrValue =  redisUtil.getStr(key);
				if(Integer.valueOf(incrValue) >500) {
					throw new BusinessException(RspCode.MOBILE_USE_BUSY, RspCode.MSG.get(RspCode.MOBILE_USE_BUSY));
				}else {
					redisUtil.incr(key);
					String incrNum =  redisUtil.getStr(key);
					//如果为 5 就设置有效时间。
					if(Integer.valueOf(incrNum) == 500) {
						String  diffSecound =  this.getTimeDiffSecound();
						redisUtil.expire(key, diffSecound);
					}
				}
			}
			
			String check_code = IDUtil.random(4);
			redisUtil.setStr((String) paramsMap.get("mobile") + "_" + check_code, check_code, 120);
			logger.info("发送验证码，check_code："+check_code+" key:"+(String) paramsMap.get("mobile") + "_" + check_code);
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("sms_source", (String) paramsMap.get("sms_source"));
			tempMap.put("mobiles", (String) paramsMap.get("mobile"));
			tempMap.put("code", check_code);
			aliyunService.sendCheckCode(tempMap, result);
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 *  取现在时间距离今天结束 还有多少秒
	 * @return
	 */
	public  String getTimeDiffSecound() {
			Date  dateStr =  new Date();
		   String dateStr1= DateUtil.date2Str(dateStr, "yyyy-MM-dd HH:mm:ss");
	       String dateStr2= DateUtil.date2Str(dateStr, "yyyy-MM-dd");
	       String dateStr3 =  dateStr2+" 23:59:59";
	       String diffSecond = DateUtil.differSecond(dateStr1, dateStr3);
		return diffSecond;
	}
	
	@Override
	public void sendCheckCode_new(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "sms_source", "mobile"});
			
			//加入安全验证码验证 对应的验证码是在接口： infrastructure.securityVerify 加入到redis的
			//根据当前的日期生成时间戳(2017-09-08) 来获取钥匙KEY
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        Date date = simpleDateFormat.parse(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
	        String timeStamp = String.valueOf(date.getTime());
	        String lockValue = redisUtil.getStr("[safetyCodeVerify]_meitianhui"+timeStamp);
//	        if(!lockValue.equals(paramsMap.get("lock_value").toString())){
//	        	throw new BusinessException(RspCode.CHECK_CODE_DISABLED, RspCode.MSG.get(RspCode.CHECK_CODE_DISABLED));
//	        }
	        
	        String mobileValue=redisUtil.getStr("exist_"+paramsMap.get("mobile"));
	        if(mobileValue!=null && !"".equals(mobileValue)){
	        	 String mobileCode=redisUtil.getStr("imageCode_"+paramsMap.get("mobile").toString());
	        	 logger.info("手机号码:"+paramsMap.get("mobile").toString()+"获取到的手机验证码:"+mobileCode);
	 	        if(mobileCode!=null && !"null".equals(mobileCode) && StringUtils.isNotEmpty(mobileCode)){
	 	        	Object image_code=paramsMap.get("image_code");
	 	        	if(image_code==null  || !mobileCode.equalsIgnoreCase(image_code.toString())){
	 	        		//throw new  BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "图片验证码错误");
	 	        		JSONObject json=new JSONObject();
		 	        	json.put("error_code", CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR);
		 	        	json.put("error_msg", "图片验证码错误");
		 	        	result.setResultData(json);
		 	        	return ;
	 	        	}
	 	        }else{
	 	        	//throw new  BusinessException(CommonRspCode.SYSTEM_NO_IMAGE_CODE_ERROR, "没有图片验证码，请输入图片验证码");
	 	        	JSONObject json=new JSONObject();
	 	        	json.put("error_code", CommonRspCode.SYSTEM_NO_IMAGE_CODE_ERROR);
	 	        	json.put("error_msg", "没有图片验证码，请输入图片验证码");
	 	        	result.setResultData(json);
	 	        	return ;
	 	        }
	        }else{
	        	redisUtil.setStr("exist_"+paramsMap.get("mobile"), "1", 1800);
	        }
	        
			String check_code = IDUtil.random(4);
			redisUtil.setStr((String) paramsMap.get("mobile") + "_" + check_code, check_code, 120);
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("sms_source", (String) paramsMap.get("sms_source"));
			tempMap.put("mobiles", (String) paramsMap.get("mobile"));
			tempMap.put("code", check_code);
			aliyunService.sendCheckCode(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 发送信息接口
	 * 
	 * @param mobiles
	 * @param msg
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void sendMsg(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			aliyunService.sendMsg(paramsMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void validateCheckCode(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "mobile", "check_code" });
			String mobile = (String) paramsMap.get("mobile");
			String check_code = (String) paramsMap.get("check_code");
			
				String key = mobile + "_" + check_code;
				String redis_check_code = redisUtil.getStr(key);
				logger.info("校验短信验证码，mobile：" + mobile + "===check_code：" + check_code + "++redis_check_code:"
						+ redis_check_code + "----key:" + key);
				if (StringUtil.isBlank(redis_check_code)) {
					throw new BusinessException(RspCode.CHECK_CODE_DISABLED, RspCode.MSG.get(RspCode.CHECK_CODE_DISABLED));
				}
				//redisUtil.del(key);
		
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void appMsgNotify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "receiver", "message" });

			IdMessageQueue idMessageQueue = new IdMessageQueue();
			BeanConvertUtil.mapToBean(idMessageQueue, paramsMap);
			idMessageQueue.setQueue_id(IDUtil.getUUID());
			idMessageQueue.setSend_date(new Date());
			notificationDao.insertIdMessageQueue(idMessageQueue);

			String member_id = (String) paramsMap.get("receiver");
			String key = "app_notify_" + member_id;
			redisUtil.setList(key, (String) paramsMap.get("message"));
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void pushStoresNotification(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "title", "alert", "extrasparam" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("title", paramsMap.get("title"));
			tempMap.put("alert", paramsMap.get("alert"));
			tempMap.put("extrasparam", paramsMap.get("extrasparam"));
			jpushService.pushStoresNotification(paramsMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void pushConsumerNotification(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "title", "alert", "extrasparam" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("title", paramsMap.get("title"));
			tempMap.put("alert", paramsMap.get("alert"));
			tempMap.put("extrasparam", paramsMap.get("extrasparam"));
			jpushService.pushConsumerNotification(paramsMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void pushNotificationByAlias(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "title", "alert", "extrasparam" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("member_id", paramsMap.get("member_id"));
			List<Map<String, Object>> memberLoginLogList = notificationDao.selectMemberLoginLog(tempMap);
			for (Map<String, Object> memberLoginLog : memberLoginLogList) {
				// 会员类型
				String member_type_key = memberLoginLog.get("member_type_key") + "";
				// 注册id
				if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
					// 本地生活
					tempMap.clear();
					tempMap.put("alias", memberLoginLog.get("alias"));
					tempMap.put("device_type", memberLoginLog.get("device_type"));
					tempMap.put("title", paramsMap.get("title"));
					tempMap.put("alert", paramsMap.get("alert"));
					tempMap.put("extrasparam", paramsMap.get("extrasparam"));
					jpushService.pushConsumerNotificationByAlias(tempMap, result);
				} else if (member_type_key.equals(Constant.MEMBER_TYPE_STORES)) {
					// 店东助手
					tempMap.clear();
					tempMap.put("alias", memberLoginLog.get("alias"));
					tempMap.put("device_type", memberLoginLog.get("device_type"));
					tempMap.put("title", paramsMap.get("title"));
					tempMap.put("alert", paramsMap.get("alert"));
					tempMap.put("extrasparam", paramsMap.get("extrasparam"));
					jpushService.pushStoresNotificationByAlias(tempMap, result);
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
	public void pushNotificationByTag(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "tag", "title", "alert", "extrasparam" });
			String tag = paramsMap.get("tag") + "";
			if (tag.equals("stores")) {
				jpushService.pushStoresNotificationByTag(paramsMap, result);
			} else if (tag.equals("consumer")) {
				jpushService.pushConsumerNotificationByTag(paramsMap, result);
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
	public void pushMessage(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "member_id", "msg_title", "msg_content", "extrasparam" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("member_id", paramsMap.get("member_id"));
			List<Map<String, Object>> memberLoginLogList = notificationDao.selectMemberLoginLog(tempMap);
			for (Map<String, Object> memberLoginLog : memberLoginLogList) {
				// 会员类型
				String member_type_key = memberLoginLog.get("member_type_key") + "";
				// 注册id
				if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
					// 本地生活
					tempMap.clear();
					tempMap.put("alias", memberLoginLog.get("alias"));
					tempMap.put("device_type", memberLoginLog.get("device_type"));
					tempMap.put("msg_title", paramsMap.get("msg_title"));
					tempMap.put("msg_content", paramsMap.get("msg_content"));
					tempMap.put("extrasparam", paramsMap.get("extrasparam"));
					jpushService.pushConsumerMessage(tempMap, result);
				} else if (member_type_key.equals(Constant.MEMBER_TYPE_STORES)) {
					// 店东助手
					tempMap.clear();
					tempMap.put("alias", memberLoginLog.get("alias"));
					tempMap.put("device_type", memberLoginLog.get("device_type"));
					tempMap.put("msg_title", paramsMap.get("msg_title"));
					tempMap.put("msg_content", paramsMap.get("msg_content"));
					tempMap.put("extrasparam", paramsMap.get("extrasparam"));
					jpushService.pushStoresMessage(tempMap, result);
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


}
