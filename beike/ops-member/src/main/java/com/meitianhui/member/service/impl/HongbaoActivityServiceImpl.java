package com.meitianhui.member.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.member.constant.RspCode;
import com.meitianhui.member.dao.ConsumerDao;
import com.meitianhui.member.dao.HongbaoActivityDao;
import com.meitianhui.member.entity.MDConsumer;
import com.meitianhui.member.entity.MdHongbaoActivity;
import com.meitianhui.member.service.HongbaoActivityService;

@Service
public class HongbaoActivityServiceImpl implements HongbaoActivityService {

	private static final Logger logger = Logger.getLogger(HongbaoActivityServiceImpl.class);

	@Autowired
	public HongbaoActivityDao hongbaoActivityDao;
	@Autowired
	public ConsumerDao consumerDao;

	@Override
	public void hongbaoActivityInfo(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "parent_id" });
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("parent_id", paramsMap.get("parent_id"));// 邀请人id
		Map<String, Object> infoMap = hongbaoActivityDao.findHongbaoActivityInfo(tempMap);
		result.setResultData(infoMap);
	}

	@Override
	public void drawHongbao(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "parent_id" });
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("parent_id", paramsMap.get("parent_id"));// 邀请人id
		tempMap.put("is_use", "N");// 未抽取过红包的状态
		List<MdHongbaoActivity> hongbaoList = hongbaoActivityDao.findHongbaoActivity(tempMap);
		if (null == hongbaoList || hongbaoList.size() == 0) {
			throw new BusinessException("抽取红包失败", "您暂时还没有可以抽取的红包");
		}
		Random random = new Random();
		int r = random.nextInt(2001) + 1000;
		//直接保留两位小数
		BigDecimal decimal = BigDecimal.valueOf((float) r / 100).setScale(2, BigDecimal.ROUND_DOWN);

		Map<String, Object> tempMdMap = new HashMap<String, Object>();
		MdHongbaoActivity mdHongbaoActivity = hongbaoList.get(0);
		tempMdMap.put("consumer_id", mdHongbaoActivity.getMember_id());
		MDConsumer memberConsumer = consumerDao.selectMDConsumerBaseInfo(tempMdMap);
		tempMdMap.clear();
		tempMdMap.put("consumer_id", mdHongbaoActivity.getParent_id());
		MDConsumer parentConsumer = consumerDao.selectMDConsumerBaseInfo(tempMdMap);
		handlerConsumerReceiveMoney(mdHongbaoActivity.getParent_id(), decimal, memberConsumer.getMobile(),
				parentConsumer.getMobile(), "lottery", "活动奖励");
		tempMap.clear();
		tempMap.put("activity_id", mdHongbaoActivity.getActivity_id());
		tempMap.put("is_use", "Y");
		tempMap.put("amount", decimal);
		hongbaoActivityDao.hongbaoActivityEdit(tempMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("amount", decimal);
		result.setResultData(resultMap);
	}

	/**
	 * 抽取红包插入日志表
	 * 
	 * @param memberId     用户id
	 * @param receiveMoney 金额
	 * @param mobile       被邀请人手机号
	 * @param inviteMobile 邀请人手机号
	 * @param type         数据类型
	 * @param remark       备注
	 */
	public void handlerConsumerReceiveMoney(String memberId, BigDecimal receiveMoney, String mobile,
			String inviteMobile, String type, String remark) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("member_id", memberId);
		paramsMap.put("receiveMoney", receiveMoney.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
		paramsMap.put("mobile", mobile);
		paramsMap.put("inviteMobile", inviteMobile);
		paramsMap.put("type", type);
		paramsMap.put("remark", remark);
		String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("service", "finance.memberReceiveMoney");
		reqParams.put("params", FastJsonUtil.toJson(paramsMap));
		String resultStr = HttpClientUtil.postShort(finance_service_url, reqParams);
		Map<String, Object> resultMapData = FastJsonUtil.jsonToMap(resultStr);
		String rsp_code = (String) resultMapData.get("rsp_code");
		if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMapData.get("error_code"), (String) resultMapData.get("error_msg"));
		}
	}
}
