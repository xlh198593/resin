package com.meitianhui.member.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.member.entity.MDMemberDistrbutionInfo;

public interface MemberDistrbutionInfoDao {
	
	Integer  insert(MDMemberDistrbutionInfo  memberDistrbutionInfo) throws  Exception;
	
	MDMemberDistrbutionInfo  findMemberDistrInfo(Map<String,Object>  paramMap);
	MDMemberDistrbutionInfo  findMemberDistrInfoById(Map<String,Object>  paramMap);
//	Map  findMemberDistr(Map<String,Object>  paramMap);

	List<MDMemberDistrbutionInfo>  getMemberDistrData(Map<String,Object>  paramMap);
	
	Integer  update(MDMemberDistrbutionInfo  memberDistrbutionInfo)  throws  Exception;
	
}
