package com.meitianhui.finance.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.finance.dao.TelephoneChargeLogDao;
import com.meitianhui.finance.entity.FDTelephoneChargeLog;

@Controller
public class TelephoneChargeController {
	
	@Autowired
	public  TelephoneChargeLogDao  telephoneChargeLogDao;
	
	private static Logger logger = Logger.getLogger(WechatNotifyControler.class);
	
	@RequestMapping(value = "/telephoneChargeNotify")
	public void consumerWechatNotity(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String reqStreamId =  StringUtil.formatStr(request.getParameter("reqStreamId"));
		String   state =  (String) request.getParameter("state");  
		logger.info("充值话费回调; reqStreamId:"+reqStreamId+";state:"+state);
		Integer  status  =  Integer.valueOf(state);
		Map<String,Object>  reqMap = new  HashMap<String,Object>();
		if(StringUtil.isNotEmpty(reqStreamId)) {
			reqMap.put("reqStreamId", reqStreamId);
			FDTelephoneChargeLog   entity =telephoneChargeLogDao.selectTelephoneCharge(reqMap);
			if(null == entity) {
				logger.info("没有查询到充值订单");
			}else {
				if(status ==0) {
					entity.setTradeStatus("1000");
					entity.setRemark("充值成功");
					this.telephoneChargeSendMsg(entity.getMobile());
				}else {
					entity.setTradeStatus("1001");
					entity.setRemark("充值失败");
				}
				telephoneChargeLogDao.update(entity);
			}
		}
	}
	
	/*
	 * 	话费充值 ，发送短信
	 * */
	public void telephoneChargeSendMsg(String mobile) {
		try {
			String url = PropertiesConfigUtil.getProperty("notification_service_url");;
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "notification.SMSSend");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobiles",mobile);
			params.put("sms_source", "SJLY_03");
			params.put("sendMsgType", "充值话费");
			params.put("msg", "充值话费");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
