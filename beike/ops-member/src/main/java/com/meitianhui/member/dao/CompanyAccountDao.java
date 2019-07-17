package com.meitianhui.member.dao;

import com.meitianhui.member.entity.*;

import java.util.List;
import java.util.Map;

public interface CompanyAccountDao {


	Map<String, Object> findByPhone(Map<String, Object> param);

	String findMemberInvitationCode(Map<String, Object> memberSqlMap);

	Map<String, Object> findMemberDistribution(Map<String, Object> memberSqlMap);

    int createCompanyAccount(Map<String, Object> tempSqlMap);

	int createMemberDistributionInfo(Map<String, Object> tempSqlMap);

}
