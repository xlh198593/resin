package com.meitianhui.schedule.task.impl;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.schedule.constant.RspCode;
import com.meitianhui.schedule.task.MemberTask;

@Component
public class MemberTaskImpl implements MemberTask {

	private static final Logger logger = Logger.getLogger(MemberTaskImpl.class);

	// 每月1号12点到13点每10分账执行一次执行
	//@Scheduled(cron = "0 0/20 12-13 1 * ?")
	@Override
	public void freezeServiceFree() {
		try {
			logger.info("执行冻结联盟店会员费");
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			// 更新商品库存
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			reqParams.put("service", "member.freezeServiceFree");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
			resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
		} catch (BusinessException e) {
			logger.warn("冻结会员服务费失败," + e.getMsg());
		} catch (SystemException e) {
			logger.error("冻结会员服务费异常", e);
		} catch (Exception e) {
			logger.error("冻结会员服务费异常", e);
		}
	}

	// 每月1号12点到13点每20分账执行一次执行
	//@Scheduled(cron = "0 0/20 12-13 28 * ?")
	@Override
	public void proServiceFree() {
		try {
			logger.info("执行会员费结算");
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			// 更新商品库存
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			reqParams.put("service", "member.proServiceFree");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
			resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
		} catch (BusinessException e) {
			logger.warn("处理会员服务费失败", e);
		} catch (SystemException e) {
			logger.error("处理会员服务费异常", e);
		} catch (Exception e) {
			logger.error("处理会员服务费异常", e);
		}
	}
	
	// 每隔一个小时执行一次
	//@Scheduled(cron = "0 0 */1 * * ?")
	@Override
	public void assistantServiceFree() {
		try {
			logger.info("锁定门店对应助教申请单修改变为驳回状态");
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			// 更新商品库存
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			reqParams.put("service", "member.assistantServiceFree");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
			resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
		} catch (BusinessException e) {
			logger.warn("修改锁定门店对应助教申请单状态失败", e);
		} catch (SystemException e) {
			logger.error("修改锁定门店对应助教申请单状态异常", e);
		} catch (Exception e) {
			logger.error("修改锁定门店对应助教申请单状态异常", e);
		}
	}
	
	//每天半夜12点 
	@Scheduled(cron = "0 0 0 * * ?")
	@Override
	public void memberServiceFree() {
		try {
			logger.info("会员期内,每日获得10点成长值");
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			// 更新商品库存
			Map<String, String> reqParams = new HashMap<>();
			Map<String, Object> bizParams = new HashMap<>();
			Map<String, Object> resultMap = new HashMap<>();
			reqParams.put("service", "member.memberServiceFree");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
			resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
		} catch (Exception e) {
			logger.error("获取每日成长值异常", e);
		}
	}

	//每天半夜12点 
//	@Scheduled(cron = "0 0 0 * * ?")
	@Override
	public void memberDistribtionEdit() {
		try {
			logger.info("每天凌晨，查询分销会员是否达标，成为掌柜");
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			// 更新商品库存
			Map<String, String> reqParams = new HashMap<>();
			Map<String, Object> bizParams = new HashMap<>();
			Map<String, Object> resultMap = new HashMap<>();
//			reqParams.put("service", "member.memberDistribtion");
//			reqParams.put("params", FastJsonUtil.toJson(bizParams));
//			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
//			resultMap = FastJsonUtil.jsonToMap(resultStr);
//			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
//				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
//			}
		} catch (Exception e) {
			logger.error("每天凌晨，查询分销会员是否达标，成为掌柜异常", e);
		}
	}
	

}
