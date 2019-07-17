package com.meitianhui.member.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.member.entity.MdMemberMessage;

public interface MemberMessageDao {

	int insertMemberMessage(MdMemberMessage memberMessage) throws Exception;

	List<MdMemberMessage> findMemberMessage(Map<String, Object> tempMap) throws Exception;

	int updateMemberMessage(Map<String, Object> tempMap) throws Exception;

	List<Map<String, Object>> findMessageTypeCount(Map<String, Object> tempMap) throws Exception;

}
