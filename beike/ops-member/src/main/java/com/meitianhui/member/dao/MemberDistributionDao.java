package com.meitianhui.member.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.member.entity.MDMemberDistribution;

public interface MemberDistributionDao {
	
	Integer  insert(MDMemberDistribution  mdMemberDistribution) throws Exception;
	
	Integer update(MDMemberDistribution  mdMemberDistribution) throws Exception;
	
	MDMemberDistribution   getMemberDistributionInfo(Map<String,Object>  map);
	
	List<Map<String,Object>>  getSimpleMemberdistr(Map<String, Object> paramsMap);
	/**
	 * 	查询满足条件的分销memberId
	 * @param paramsMap
	 * @return
	 */
	List<String>  getDistrMemberId();
	
	/**
	 * 	达到条件的会员的下面所有的memberId(除孙级和子级)
	 * @param paramsMap
	 * @return
	 */
	List<String>  getDistrNextMemberId(Map<String,Object>  map);
	
	List<MDMemberDistribution>  getSimpleMemberdistrByParentId(Map<String,Object>  map);
	
	/**
	 * 查询临时粉丝
	 * @param paramsMap
	 * @return
	 */
	List<Map<String,Object>>  selectTempFans(Map<String,Object>  map);
	
	/**
	 * 查询直邀粉丝
	 * @param paramsMap
	 * @return
	 */
	List<Map<String,Object>>  selectMemberDirectFans(Map<String, Object> paramsMap);
	/**
	 * 查询间邀粉丝
	 * @param paramsMap
	 * @return
	 */
	List<Map<String,Object>>  selectMemberIndirectFans(Map<String, Object> paramsMap);
	
	/**
	 * 查询全部粉丝
	 * @param paramsMap
	 * @return
	 */
	List<Map<String,Object>>  selectMemberFans(Map<String, Object> paramsMap);
	
	Integer  findMemberDistrCount(Map<String,Object>  paramMap);
	
	/**
	 *  查询上级的手机号
	 * @param paramsMap
	 * @return
	 */
	String  selectConsumerMobile(Map<String,Object>  paramMap);
	
	/**
	 *  查询掌柜memberId
	 * @param paramsMap
	 * @return
	 */
	List<String>  getMemberManagerId();
	
	
	/**
	 *  查询掌柜下面的memberId
	 * @param paramsMap
	 * @return
	 */
	List<Map<String,Object>>  getdistrMemberInfo(Map<String,Object>  paramMap);
}
