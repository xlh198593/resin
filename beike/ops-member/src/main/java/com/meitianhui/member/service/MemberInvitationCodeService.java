package com.meitianhui.member.service;

import java.util.Map;


import com.meitianhui.common.constant.ResultData;

public interface MemberInvitationCodeService {

	/**
	 * 查找用户贝壳号
	 */
	void findInvitationCode(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 通过验证码查找用户信息
	 */
	void findMemberInfoByInvitationCode(Map<String, Object> paramsMap, ResultData result) throws Exception;

}
