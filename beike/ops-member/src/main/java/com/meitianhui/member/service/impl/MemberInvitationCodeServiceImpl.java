package com.meitianhui.member.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.member.dao.MemberInvitationCodeDao;
import com.meitianhui.member.entity.MDConsumer;
import com.meitianhui.member.entity.MDMemberInvitationCode;
import com.meitianhui.member.service.MemberInvitationCodeService;

@Service
public class MemberInvitationCodeServiceImpl implements MemberInvitationCodeService {

	@Autowired
	public MemberInvitationCodeDao memberInvitationCodeDao;
	@Autowired
	private DocUtil docUtil;
	
	@Override
	public void findInvitationCode(Map<String, Object> paramsMap, ResultData result)
			throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		MDMemberInvitationCode InviteCode = memberInvitationCodeDao.findMemberInviteCode(paramsMap);
		if(InviteCode == null){
			throw new BusinessException("未查询到贝壳号", "您还没有贝壳号，请先购买会员");
		}
		Map<String, Object> resultMap = new HashMap<>(); 
		resultMap.put("invite_code", InviteCode.getInviteCode());
		resultMap.put("member_id", InviteCode.getMemberId());
		result.setResultData(resultMap);
	}

	@Override
	public void findMemberInfoByInvitationCode(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "invite_code" });
		MDConsumer consumer =  memberInvitationCodeDao.findMemberInfo(paramsMap);
		if(consumer == null){
			throw new BusinessException("贝壳号失效", "贝壳号不存在");
		}
		String head_pic_path = consumer.getHead_pic_path();
		Map<String, Object> resultMap = new HashMap<>(); 
		resultMap.put("head_pic_path",head_pic_path);
		List<String> url_list = new ArrayList<String>();
		url_list.add(head_pic_path);
		resultMap.put("doc_url", docUtil.imageUrlFind(url_list));
		resultMap.put("nick_name", consumer.getNick_name());
		resultMap.put("mobile", consumer.getMobile());
		result.setResultData(resultMap);
	}

}
