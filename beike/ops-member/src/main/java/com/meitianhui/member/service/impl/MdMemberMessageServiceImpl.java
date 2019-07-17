package com.meitianhui.member.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.member.dao.MemberMessageDao;
import com.meitianhui.member.entity.MdMemberMessage;
import com.meitianhui.member.service.MdMemberMessageService;
import com.meitianhui.member.util.push.PushUtil;
import com.meitianhui.member.util.push.ios.IOSCustomizedcast;

/**
 * 消息推送服务层
 */
@Service
public class MdMemberMessageServiceImpl implements MdMemberMessageService {
	
	@Autowired
	private MemberMessageDao memberMessageDao;
	
	@Autowired
	private DocUtil docUtil;

	@Override
	public void sendMessage(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "message_category" });
		String message_category = StringUtil.formatStr(paramsMap.get("message_category"));
		if ("place_order".equals(message_category)) {
			MdMemberMessage memberMessage = new MdMemberMessage();
			Date date = new Date();
			memberMessage.setMessage_id(IDUtil.getUUID());
			memberMessage.setMessage_type(StringUtil.formatStr(paramsMap.get("message_type")));
			memberMessage.setMember_id(StringUtil.formatStr(paramsMap.get("member_id")));
			memberMessage.setMessage_category(message_category);
			memberMessage.setMessage_title("下单成功");
			memberMessage.setMessage_subtitle("下单成功通知");
			String goods_title = StringUtil.formatStr(paramsMap.get("goods_title"));
			memberMessage.setMessage_text("您已成功购买" + goods_title + "，我们会尽快为您安排发货！");
			memberMessage.setMessage_pic(StringUtil.formatStr(paramsMap.get("message_pic")));
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("order_no", paramsMap.get("order_no"));
			memberMessage.setMessage_param(FastJsonUtil.toJson(tempMap));
			memberMessage.setCreated_date(date);
			memberMessage.setModified_date(date);
			memberMessage.setIs_read("N");
			memberMessage.setIs_show("Y");
			memberMessageDao.insertMemberMessage(memberMessage);

			Map<String, Object> map = new HashMap<>();
			map.put("message_title", memberMessage.getMessage_title());
			map.put("member_id", memberMessage.getMember_id());
			map.put("message_subtitle", memberMessage.getMessage_subtitle());
			map.put("message_text", memberMessage.getMessage_text());
			tempMap.put("message_category", message_category);
			map.put("tempMap", tempMap);
			PushUtil.sendIOSCustomizedcast(map);
			PushUtil.sendAndroidCustomizedcast(map);
		} else if ("vip_order".equals(message_category)) {
			MdMemberMessage memberMessage = new MdMemberMessage();
			Date date = new Date();
			memberMessage.setMessage_id(IDUtil.getUUID());
			memberMessage.setMessage_type(StringUtil.formatStr(paramsMap.get("message_type")));
			memberMessage.setMember_id(StringUtil.formatStr(paramsMap.get("member_id")));
			memberMessage.setMessage_category(message_category);
			memberMessage.setMessage_title("成功购买超级会员");
			memberMessage.setMessage_subtitle("购买会员成功通知");
			memberMessage.setMessage_text("恭喜您成为超级会员，会员有效期一年，同时获得12个大礼包及1314个贝壳，开启您的特权之路吧！");
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("order_no", paramsMap.get("order_no"));
			memberMessage.setMessage_param(FastJsonUtil.toJson(tempMap));
			memberMessage.setCreated_date(date);
			memberMessage.setModified_date(date);
			memberMessage.setIs_read("N");
			memberMessage.setIs_show("Y");
			memberMessageDao.insertMemberMessage(memberMessage);

			Map<String, Object> map = new HashMap<>();
			map.put("message_title", memberMessage.getMessage_title());
			map.put("member_id", memberMessage.getMember_id());
			map.put("message_subtitle", memberMessage.getMessage_subtitle());
			map.put("message_text", memberMessage.getMessage_text());
			tempMap.put("message_category", message_category);
			map.put("tempMap", tempMap);
			PushUtil.sendIOSCustomizedcast(map);
			PushUtil.sendAndroidCustomizedcast(map);
		} else if ("direct_inform".equals(message_category)) {
			MdMemberMessage memberMessage = new MdMemberMessage();
			Date date = new Date();
			memberMessage.setMessage_id(IDUtil.getUUID());
			memberMessage.setMessage_type(StringUtil.formatStr(paramsMap.get("message_type")));
			memberMessage.setMember_id(StringUtil.formatStr(paramsMap.get("member_id")));
			memberMessage.setMessage_category(message_category);
			memberMessage.setMessage_title("直邀奖励入账");
			memberMessage.setMessage_subtitle("收到直邀红包通知");
			memberMessage.setMessage_text("您已成功邀请一名用户成为超级会员，120元红包奖励已经到账，请查收>>>");
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("member_id", paramsMap.get("member_id"));
			memberMessage.setMessage_param(FastJsonUtil.toJson(tempMap));
			memberMessage.setCreated_date(date);
			memberMessage.setModified_date(date);
			memberMessage.setIs_read("N");
			memberMessage.setIs_show("Y");
			memberMessageDao.insertMemberMessage(memberMessage);

			Map<String, Object> map = new HashMap<>();
			map.put("message_title", memberMessage.getMessage_title());
			map.put("member_id", memberMessage.getMember_id());
			map.put("message_subtitle", memberMessage.getMessage_subtitle());
			map.put("message_text", memberMessage.getMessage_text());
			tempMap.put("message_category", message_category);
			map.put("tempMap", tempMap);
			PushUtil.sendIOSCustomizedcast(map);
			PushUtil.sendAndroidCustomizedcast(map);
		} else if ("indirect_inform".equals(message_category)) {
			MdMemberMessage memberMessage = new MdMemberMessage();
			Date date = new Date();
			memberMessage.setMessage_id(IDUtil.getUUID());
			memberMessage.setMessage_type(StringUtil.formatStr(paramsMap.get("message_type")));
			memberMessage.setMember_id(StringUtil.formatStr(paramsMap.get("member_id")));
			memberMessage.setMessage_category(message_category);
			memberMessage.setMessage_title("间邀奖励入账");
			memberMessage.setMessage_subtitle("收到间邀红包通知");
			memberMessage.setMessage_text("60元间邀红包奖励已到账，请查收>>>");
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("member_id", paramsMap.get("member_id"));
			memberMessage.setMessage_param(FastJsonUtil.toJson(tempMap));
			memberMessage.setCreated_date(date);
			memberMessage.setModified_date(date);
			memberMessage.setIs_read("N");
			memberMessage.setIs_show("Y");
			memberMessageDao.insertMemberMessage(memberMessage);

			Map<String, Object> map = new HashMap<>();
			map.put("message_title", memberMessage.getMessage_title());
			map.put("member_id", memberMessage.getMember_id());
			map.put("message_subtitle", memberMessage.getMessage_subtitle());
			map.put("message_text", memberMessage.getMessage_text());
			tempMap.put("message_category", message_category);
			map.put("tempMap", tempMap);
			PushUtil.sendIOSCustomizedcast(map);
			PushUtil.sendAndroidCustomizedcast(map);
		}
	}

	@Override
	public void findMessage(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "message_type", "member_id" });
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("message_type", paramsMap.get("message_type"));
		tempMap.put("member_id", paramsMap.get("member_id"));
		tempMap.put("is_show", "Y");
		List<MdMemberMessage> memberMessage = memberMessageDao.findMemberMessage(tempMap);
		tempMap.clear();
		List<String> doc_ids = new ArrayList<>();
		for (MdMemberMessage mdMemberMessage : memberMessage) {
			String pic_info = StringUtil.formatStr(mdMemberMessage.getMessage_pic());
			if (StringUtil.isNotEmpty(pic_info)) {
				Map<String, Object> m = FastJsonUtil.jsonToMap(pic_info);
				if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
					doc_ids.add(m.get("path_id") + "");
				}
			}
		}
		tempMap.put("list", memberMessage);
		tempMap.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(tempMap);
	}

	@Override
	public void readMessage(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "message_id" });
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("message_id", paramsMap.get("message_id"));
		tempMap.put("is_read", "Y");
		memberMessageDao.updateMemberMessage(tempMap);
	}

	@Override
	public void messageCenterHomePage(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("member_id", paramsMap.get("member_id"));
		tempMap.put("is_read", "N");
		List<Map<String, Object>> messageMap = memberMessageDao.findMessageTypeCount(tempMap);
		result.setResultData(messageMap);
	}

	@Override
	public void deleteMessage(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "message_ids" });
		String message_ids = StringUtil.formatStr(paramsMap.get("message_ids"));
		String[] message_id_in = message_ids.split(",");
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put("message_id_in", message_id_in);
		tempMap.put("is_show", "N");
		tempMap.put("is_read", "Y");
		memberMessageDao.updateMemberMessage(tempMap);
	}

}
