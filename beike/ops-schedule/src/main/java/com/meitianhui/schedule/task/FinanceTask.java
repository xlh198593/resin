package com.meitianhui.schedule.task;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface FinanceTask {

	/**
	 * 支付宝对账单导入
	 * 
	 * @Title: alipayBillImport
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void alipayBillImport() throws BusinessException, SystemException, Exception;

	/**
	 * 微信对账单导入
	 * 
	 * @Title: wechatBillImport
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void wechatBillImport() throws BusinessException, SystemException, Exception;

	/**
	 * 交易对账
	 * 
	 * @Title: billCheck
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void billCheck() throws BusinessException, SystemException, Exception;

	/**
	 * 会员每月赠送礼券
	 */
	void monthlyGiftCoupon() throws Exception;

	/**
	 * 会员生日赠送礼券
	 */
	void birthdayGiftCoupon() throws Exception;

	/**
	 * 掌柜每月1号定时将次邀金额转到红包金额里面
	 */
	void manageRateToBalance() throws Exception;

}
