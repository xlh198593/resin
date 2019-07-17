package com.meitianhui.finance.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.finance.entity.FDMemberCashCoupon;
import com.meitianhui.finance.entity.FDMemberCashCouponLog;

public interface MemberCashCouponDao {

	/**
	 * 新增现金券
	 * @return
	 * @throws Exception
	 */
	int insertFDMemberCashCoupon(FDMemberCashCoupon fdMemberCashCoupon) throws Exception;
	/**
	 * 更新现金券状态
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int updateFDMemberCashCouponStatus(Map<String,Object> map) throws Exception;
	
	List<FDMemberCashCoupon> selectFDMemberCashCouponBy(Map<String,Object> map) throws Exception;
	
	int insertFDMemberCashCouponLog(FDMemberCashCouponLog fdMemberCashCouponLog) throws Exception;
	
	
}
