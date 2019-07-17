package com.meitianhui.finance.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface FinanceService {

	
	/**
	 * 查询交易数据
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void transactionsnoByOuttradnoFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 初始化会员资产
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleInitMemberAsset(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员资产查询
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberAssetFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员资产列表查询
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberAssetListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员可用现金余额查询
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberUsableCashBalanceFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 店东收银
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storeCashierPromotion(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员积分查询
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException  
	 * @throws SystemException  
	 */
	public void memberPointFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 会员积分创建  
	 * 
	 * @param paramMap    
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberPointEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	
	/**
	 * 会员积分操作日志
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberPointLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 会员现金操作日志
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCashLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 会员现金操作日志(新接口)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCashLogListFindNew(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员金币操作日志
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberGoldLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员礼券操作日志
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberVoucherLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 加盟店现金交易统计
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storeCashCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 门店礼券交易统计
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storeVoucherBillCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 门店赠送礼券人数统计
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storeVoucherRewardAccountCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 加盟店收银记录新增
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesCashierCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 加盟店收银记录查询
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesCashierFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员优惠券创建
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCouponCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员优惠券信息查询
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCouponFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员优惠券信息查询
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberIdBySkuCodeFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员优惠券统计
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCouponCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员优惠券状态编辑
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCouponStatusEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 过期的的优惠券状态更新
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void disabledCouponStatusUpdate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 和店东发生交易的消费者列表
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void tradeConsumerListForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 和店东发生交易的消费者列表(会员类型)
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void tradeConsumerListForMemberList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者礼券交易流水
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerVoucherBill(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 门店礼券交易流水
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storeVoucherBill(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 店东为消费者解冻余额
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleStoresUnfreezeBalanceForConsumer(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	
	/**
	 * 交易异常数据
	 * 
	 * @Title: billCheckLogListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void billCheckLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 门店的店东的佣金明细列表查询
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void storesCashCommissionLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	/**
	 *  分页查询用户积分列表查询
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void menberBonusListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 新增门店的店东佣金
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void storesCashCommissionCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 修改门店的店东佣金
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void storesCashCommissionEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 自动转佣金到店东零钱账户
	 */
	void transmatic(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查询会员信息 
	 */
	void memberAssetsInformation(Map<String, Object> paramsMap, ResultData result) throws Exception;

    /**
     * 和包用户领取现金券
     * @param paramsMap
     * @param result
     */
	void handleHebaoGetCashCoupon(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;


	/**
	 * 获取和包用户已领现金券列表
	 * @param paramsMap
	 * @param result
	 */
	void hebaoCashCouponListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

    /**
     * 和包用户核销现金券
     * @param paramsMap
     * @param result
     */
	void hebaoUseCashCoupon(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 每月给相对应等级的会员送礼券
	 */
	void monthlyGiftCoupon(Map<String, Object> paramsMap, ResultData result)throws Exception;

	/**
	 * 赠送生日礼券
	 */
	void birthdayGiftCoupon(Map<String, Object> paramsMap, ResultData result)throws Exception;

	/**
	 * 查找用户礼券
	 */
	void findGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 查找用户礼券列表
	 */
	void findGiftCouponList(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 查找用户礼券列表(新版)
	 */
	void findGiftCouponList_v2(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 充值时关联礼卷表
	 */
	void  editMemberCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 	用户返佣
	 */
	void  handleMemberReceiveMoney(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception;

	/**
	 * 用户使用礼券
	 */
	void updateGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 *  退回免邮券
	 */
	void updateMemberGift(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查找用户礼券列表
	 */
	void findGiftCouponList_v1(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 激活礼券
	 */
	void activationGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 补签
	 */
	void repairGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception;


	/**
	 * 查找礼券签到的信息
	 */
	void findGiftCouponSignInfo(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 *  礼券转换(399未激活的礼券,转换成尖货礼券)
	 */
	void findGiftCouponTransform(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 修改礼券数量
	 */
	void updateGiftCouponMun(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 礼券类型
	 */
	void  findGiftCouponType(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 把掌柜次邀返佣 移到余额
	 */
	void  manageRateToBalance(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 测试把掌柜次邀返佣 移到余额
	 */
	void  testManageRateToBalance(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 激活时间最前的一张礼券
	 */
	void activationFormerOneGiftCoupon(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查询上个月的签到信息
	 */
	void findGiftCouponSignInfoForLastMonth(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳换补签次数
	 */
    void shellSwitchToCount(Map<String, Object> paramsMap, ResultData result) throws Exception;


    void buyByShell(Map<String, Object> paramsMap, ResultData result) throws Exception;


	/**
	 * 获取会员资产
	 */
	void findMemberAssetByMobile(Map<String, Object> paramsMap, ResultData result) throws Exception;



}
