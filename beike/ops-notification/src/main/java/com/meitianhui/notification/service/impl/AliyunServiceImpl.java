package com.meitianhui.notification.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
//import com.aliyuncs.sms.model.v20160927.SingleSendSmsRequest;
//import com.aliyuncs.sms.model.v20160927.SingleSendSmsResponse;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.constant.SendMsgEnum;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.RegexpValidateUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.notification.constant.RspCode;
import com.meitianhui.notification.controller.NotificationController;
import com.meitianhui.notification.dao.NotificationDao;
import com.meitianhui.notification.entity.IdSmsStatistics;
import com.meitianhui.notification.service.MessagesService;

//import cn.jiguang.common.utils.StringUtils;

@Service("aliyunService")
public class AliyunServiceImpl implements MessagesService {

	private static final Logger logger = Logger.getLogger(AliyunServiceImpl.class);

	@Autowired
	public NotificationDao notificationDao;

	@Autowired
	public RedisUtil redisUtil;

	public final static String ACCESSKEY = PropertiesConfigUtil.getProperty("aliyuncs_accessKey");

	public final static String ACCESSSECRET = PropertiesConfigUtil.getProperty("aliyuncs_accessSecret");

	/**
	 * 阿里云短信接口
	 * 
	 * @param mobiles
	 * @param msg
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void sendCheckCode(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "sms_source", "mobiles", "code" });
			final String sms_source = (String) paramsMap.get("sms_source");
			final String mobiles = (String) paramsMap.get("mobiles");
			final String code = (String) paramsMap.get("code");
			NotificationController.threadExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String[] mobilesStr = mobiles.split(",");
						for (String phone : mobilesStr) {
							if (!RegexpValidateUtil.isPhone(phone)) {
								throw new BusinessException(RspCode.PHONE_ERROR, RspCode.MSG.get(RspCode.PHONE_ERROR));
							}
						}
						
						//设置超时时间-可自行调整
						System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
						System.setProperty("sun.net.client.defaultReadTimeout", "10000");
						//初始化ascClient需要的几个参数
						final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
						final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
						
						//初始化ascClient,暂时不支持多region（请勿修改）
						IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESSKEY, ACCESSSECRET);
						DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
						IAcsClient acsClient  = new DefaultAcsClient(profile);
						//组装请求对象
						SendSmsRequest  request = new SendSmsRequest();
						//使用post提交
						request.setMethod(MethodType.POST);
						//必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
						request.setPhoneNumbers(mobiles);
						//必填:短信签名-可在短信控制台中找到
						request.setSignName("贝壳传奇");
						//必填:短信模板-可在短信控制台中找到
						request.setTemplateCode("SMS_142470269");
						//可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
						//友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
						//request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
						Map<String, String> paramString = new HashMap<String, String>();
						paramString.put("code", code);
						request.setTemplateParam(FastJsonUtil.toJson(paramString));
						//可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
						//request.setSmsUpExtendCode("90997");
						//可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
						//request.setOutId("yourOutId");
						//请求失败这里会抛ClientException异常
						SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
						if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
						//请求成功
							// 成功则记录日志
							List<IdSmsStatistics> smsStatisticsList = new ArrayList<IdSmsStatistics>();
							for (String mobile : mobilesStr) {
								Date tracked_date = new Date();
								IdSmsStatistics idSmsStatistics = new IdSmsStatistics();
								idSmsStatistics.setStatistics_id(IDUtil.getUUID());
								idSmsStatistics.setSms_source(sms_source);
								idSmsStatistics.setMobile(mobile);
								idSmsStatistics.setSms("短信验证码:" + code + "【RequestId:" + sendSmsResponse.getRequestId() + "】");
								idSmsStatistics.setTracked_date(tracked_date);
								smsStatisticsList.add(idSmsStatistics);
							}
							// 发送短信的记录需要记录到数据
							notificationDao.insertIdSmsStatistics(smsStatisticsList);
						}
					} catch (Exception e) {
						logger.error("发送验证短信异常;mobile:" + mobiles, e);
					}
				}
			});
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 发送信息
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
			ValidateUtil.validateParams(paramsMap, new String[] { "sms_source", "mobiles", "msg" });
			final String mobiles = (String) paramsMap.get("mobiles");
			final String msg = (String) paramsMap.get("msg");
			final String sms_source = (String) paramsMap.get("sms_source");
			final String sendMsgType = (String) paramsMap.get("sendMsgType");
			NotificationController.threadExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String[] mobilesStr = mobiles.split(",");
						for (String phone : mobilesStr) {
							if (!RegexpValidateUtil.isPhone(phone)) {
								throw new BusinessException(RspCode.PHONE_ERROR, RspCode.MSG.get(RspCode.PHONE_ERROR));
							}
						}
						if (mobilesStr.length > 100) {
							throw new BusinessException(RspCode.PHONES_TOO_MUCH,
									RspCode.MSG.get(RspCode.PHONES_TOO_MUCH));
						}
						
						//设置超时时间-可自行调整
						System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
						System.setProperty("sun.net.client.defaultReadTimeout", "10000");
						//初始化ascClient需要的几个参数
						final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
						final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
						
						//初始化ascClient,暂时不支持多region（请勿修改）
						IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESSKEY, ACCESSSECRET);
						DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
						IAcsClient acsClient  = new DefaultAcsClient(profile);
						//组装请求对象
						SendSmsRequest  request = new SendSmsRequest();
						//使用post提交
						request.setMethod(MethodType.POST);
						//必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
						request.setPhoneNumbers(mobiles);
						//必填:短信签名-可在短信控制台中找到
						//request.setSignName("惠点科技");
						request.setSignName("贝壳传奇");
						
						//必填:短信模板-可在短信控制台中找到    
						//request.setTemplateCode("SMS_47145164");
						
						//根据比较字符串，好维护
						if(StringUtils.equals(sendMsgType, SendMsgEnum.deliverGoods.getName())) {
							request.setTemplateCode("SMS_145295587");
						}
						if(StringUtils.equals(sendMsgType, SendMsgEnum.memberRenew.getName())) {
							request.setTemplateCode("SMS_145295584");
						}
						if(StringUtils.equals(sendMsgType, SendMsgEnum.dredgeMember.getName())) {
							request.setTemplateCode("SMS_145295581");
						}
						if(StringUtils.equals(sendMsgType, SendMsgEnum.giftPastDue.getName())) {
							request.setTemplateCode("SMS_144854102");
						}
						if(StringUtils.equals(sendMsgType, SendMsgEnum.memberPastDue.getName())) {
							request.setTemplateCode("SMS_144943457");
						}
						if(StringUtils.equals(sendMsgType, SendMsgEnum.memberExpire.getName())) {
							request.setTemplateCode("SMS_144943449");
						}
						if(StringUtils.equals(sendMsgType, SendMsgEnum.arrivalNotice.getName())) {
							request.setTemplateCode("SMS_151768952");
						}
						if(StringUtils.equals(sendMsgType, SendMsgEnum.telephoneCharge.getName())) {
							request.setTemplateCode("SMS_153725119");
						}
						//可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
						//友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
						//request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
						Map<String, String> paramString = new HashMap<String, String>();
						paramString.put("msg", msg);
						request.setTemplateParam(FastJsonUtil.toJson(paramString));
						//可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
						//request.setSmsUpExtendCode("90997");
						//可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
						//request.setOutId("yourOutId");
						//请求失败这里会抛ClientException异常
						SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
						if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
						//请求成功
							// 成功则记录日志
							List<IdSmsStatistics> smsStatisticsList = new ArrayList<IdSmsStatistics>();
							for (String mobile : mobilesStr) {
								Date tracked_date = new Date();
								IdSmsStatistics idSmsStatistics = new IdSmsStatistics();
								idSmsStatistics.setStatistics_id(IDUtil.getUUID());
								idSmsStatistics.setSms_source(sms_source);
								idSmsStatistics.setMobile(mobile);
								idSmsStatistics.setSms(msg + "【RequestId:" + sendSmsResponse.getRequestId() + "】");
								idSmsStatistics.setTracked_date(tracked_date);
								smsStatisticsList.add(idSmsStatistics);
							}
							// 发送短信的记录需要记录到数据
							notificationDao.insertIdSmsStatistics(smsStatisticsList);
						}
					} catch (Exception e) {
						logger.error("发送短信信息异常;mobile:" + mobiles, e);
					}
				}
			});
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
//	/**
//	 * 移动梦网短信接口
//	 * 
//	 * @param mobiles
//	 * @param msg
//	 * @throws BusinessException
//	 * @throws SystemException
//	 */
//	@Override
//	public void sendCheckCode(Map<String, Object> paramsMap, ResultData result)
//			throws BusinessException, SystemException, Exception {
//		try {
//			ValidateUtil.validateParams(paramsMap, new String[] { "sms_source", "mobiles", "code" });
//			final String sms_source = (String) paramsMap.get("sms_source");
//			final String mobiles = (String) paramsMap.get("mobiles");
//			final String code = (String) paramsMap.get("code");
//			NotificationController.threadExecutor.execute(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						String[] mobilesStr = mobiles.split(",");
//						for (String phone : mobilesStr) {
//							if (!RegexpValidateUtil.isPhone(phone)) {
//								throw new BusinessException(RspCode.PHONE_ERROR, RspCode.MSG.get(RspCode.PHONE_ERROR));
//							}
//						}
//
//						IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESSKEY, ACCESSSECRET);
//						DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Sms", "sms.aliyuncs.com");
//						IAcsClient client = new DefaultAcsClient(profile);
//						SingleSendSmsRequest request = new SingleSendSmsRequest();
//						// 控制台创建的签名名称
//						request.setSignName("每天惠");
//						// 控制台创建的模板CODE
//						request.setTemplateCode("SMS_47520040");
//						// 短信模板中的变量；数字需要转换为字符串；个人用户每个变量长度必须小于15个字符。"
//						Map<String, String> paramString = new HashMap<String, String>();
//						paramString.put("code", code);
//						request.setParamString(FastJsonUtil.toJson(paramString));
//						// 接收号码
//						request.setRecNum(mobiles);
//						SingleSendSmsResponse httpResponse = client.getAcsResponse(request);
//						// 成功则记录日志
//						List<IdSmsStatistics> smsStatisticsList = new ArrayList<IdSmsStatistics>();
//						for (String mobile : mobilesStr) {
//							Date tracked_date = new Date();
//							IdSmsStatistics idSmsStatistics = new IdSmsStatistics();
//							idSmsStatistics.setStatistics_id(IDUtil.getUUID());
//							idSmsStatistics.setSms_source(sms_source);
//							idSmsStatistics.setMobile(mobile);
//							idSmsStatistics.setSms("短信验证码:" + code + "【RequestId:" + httpResponse.getRequestId() + "】");
//							idSmsStatistics.setTracked_date(tracked_date);
//							smsStatisticsList.add(idSmsStatistics);
//						}
//						// 发送短信的记录需要记录到数据
//						notificationDao.insertIdSmsStatistics(smsStatisticsList);
//					} catch (Exception e) {
//						logger.error("发送验证短信异常;mobile:" + mobiles, e);
//					}
//				}
//			});
//		} catch (BusinessException e) {
//			throw e;
//		} catch (SystemException e) {
//			throw e;
//		} catch (Exception e) {
//			throw e;
//		}
//	}

//	/**
//	 * 发送信息
//	 * 
//	 * @param mobiles
//	 * @param msg
//	 * @throws BusinessException
//	 * @throws SystemException
//	 */
//	@Override
//	public void sendMsg(Map<String, Object> paramsMap, ResultData result)
//			throws BusinessException, SystemException, Exception {
//		try {
//			ValidateUtil.validateParams(paramsMap, new String[] { "sms_source", "mobiles", "msg" });
//			final String mobiles = (String) paramsMap.get("mobiles");
//			final String msg = (String) paramsMap.get("msg");
//			final String sms_source = (String) paramsMap.get("sms_source");
//			NotificationController.threadExecutor.execute(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						String[] mobilesStr = mobiles.split(",");
//						for (String phone : mobilesStr) {
//							if (!RegexpValidateUtil.isPhone(phone)) {
//								throw new BusinessException(RspCode.PHONE_ERROR, RspCode.MSG.get(RspCode.PHONE_ERROR));
//							}
//						}
//						if (mobilesStr.length > 100) {
//							throw new BusinessException(RspCode.PHONES_TOO_MUCH,
//									RspCode.MSG.get(RspCode.PHONES_TOO_MUCH));
//						}
//
//						IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESSKEY, ACCESSSECRET);
//						DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Sms", "sms.aliyuncs.com");
//						IAcsClient client = new DefaultAcsClient(profile);
//						SingleSendSmsRequest request = new SingleSendSmsRequest();
//						// 控制台创建的签名名称
//						request.setSignName("每天惠");
//						// 控制台创建的模板CODE
//						request.setTemplateCode("SMS_47145164");
//						// 短信模板中的变量；数字需要转换为字符串；个人用户每个变量长度必须小于15个字符。"
//						Map<String, String> paramString = new HashMap<String, String>();
//						paramString.put("msg", msg);
//						request.setParamString(FastJsonUtil.toJson(paramString));
//						// 接收号码
//						request.setRecNum(mobiles);
//						SingleSendSmsResponse httpResponse = client.getAcsResponse(request);
//						// 成功则记录日志
//						List<IdSmsStatistics> smsStatisticsList = new ArrayList<IdSmsStatistics>();
//						for (String mobile : mobilesStr) {
//							Date tracked_date = new Date();
//							IdSmsStatistics idSmsStatistics = new IdSmsStatistics();
//							idSmsStatistics.setStatistics_id(IDUtil.getUUID());
//							idSmsStatistics.setSms_source(sms_source);
//							idSmsStatistics.setMobile(mobile);
//							idSmsStatistics.setSms(msg + "【RequestId:" + httpResponse.getRequestId() + "】");
//							idSmsStatistics.setTracked_date(tracked_date);
//							smsStatisticsList.add(idSmsStatistics);
//						}
//						// 发送短信的记录需要记录到数据
//						notificationDao.insertIdSmsStatistics(smsStatisticsList);
//					} catch (Exception e) {
//						logger.error("发送短信信息异常;mobile:" + mobiles, e);
//					}
//				}
//			});
//		} catch (BusinessException e) {
//			throw e;
//		} catch (SystemException e) {
//			throw e;
//		} catch (Exception e) {
//			throw e;
//		}
//	}

}
