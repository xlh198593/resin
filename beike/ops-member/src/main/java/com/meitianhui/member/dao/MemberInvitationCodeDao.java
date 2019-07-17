package com.meitianhui.member.dao;

import java.util.Map;

import com.meitianhui.member.entity.MDConsumer;
import com.meitianhui.member.entity.MDMemberInvitationCode;

public interface MemberInvitationCodeDao {

	public  MDMemberInvitationCode  findMemberInviteCode(Map<String,Object> paramMap);

	public void addMemberInvitationCode(MDMemberInvitationCode invitationCode) throws Exception;

	/**
	 * 查找会员信息
	 */
	public MDConsumer findMemberInfo(Map<String, Object> paramsMap) throws Exception;
}
