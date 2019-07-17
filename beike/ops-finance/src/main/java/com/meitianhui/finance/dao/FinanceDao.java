package com.meitianhui.finance.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.meitianhui.finance.entity.*;

public interface FinanceDao {

	/**
	 * 新增交易信息
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void insertFDTransactions(FDTransactions fDTransactions) throws Exception;

	/**
	 * 新增交易结果信息
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void insertFDTransactionsResult(FDTransactionsResult fDTransactionsResult) throws Exception;

	/**
	 * 新增会员资产信息
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void insertFDMemberAsset(FDMemberAsset fDMemberAsset) throws Exception;

	/**
	 * 新增门店收银流水
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public void insertFDStoresCashier(FDStoresCashier fDStoresCashier) throws Exception;

	/**
	 * 新增会员优惠券信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public void insertFdMemberAssetCoupon(FdMemberAssetCoupon fdMemberAssetCoupon) throws Exception;

	
	
	/**
	 * 	新增会员返佣日志
	 * @param fdMemberRebateLog
	 * @throws Exception
	 */
	public  void  insertFdMemberRebateLog(FDMemberRebateLog  fdMemberRebateLog) throws Exception;
	
	
	/**
	 * 添加会员积分日志
	 * 
	 * @param tempMap
	 * @throws Exception
	 */
	public void insertFdMemberPointLog(Map<String, Object> tempMap) throws Exception;

	/**
	 * 查询交易信息
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public FDTransactions selectFDTransactions(Map<String, Object> paramMap) throws Exception;
	/**
	 * 查询交易信息(返回对象集合)
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<FDTransactions> selectListFDTransactions(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询交易结果
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<FDTransactionsResult> selectFDTransactionsResult(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询消费者礼券交易流水
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<FDVoucherDailyAccountMember> selectFDVoucherDailyAccountConsumer(Map<String, Object> paramMap)
			throws Exception;

	/**
	 * 查询会员现金变更日志
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> selectFDMemberCashLog(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询会员现金变更日志(新接口)
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	List<FDMemberCashLog> selectFDMemberCashLogNew(Map<String, Object> paramMap) throws Exception;
	/**
	 * 查询会员金币变更日志
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<FDMemberGoldLog> selectFDMemberGoldLog(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询会员积分变更日志
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<FDMemberPointLog> selectFDMemberPointLog(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 查询会员礼券变更日志
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<FDMemberVoucherLog> selectFDMemberVoucherLog(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询门店收银交易统计
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectStoreCashierCount(Map<String, Object> paramMap) throws Exception;

	/**
	 * 门店礼券交易统计
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectFDVoucherDailyAccountStoreCount(Map<String, Object> paramMap) throws Exception;

	/**
	 * 门店赠送礼券人数统计
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectFDStoreVoucherRewardAccountCount(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询门店礼券交易流水
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectFDVoucherAmountCount(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询会员资产
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public FDMemberAsset selectFDMemberAsset(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询会员贝壳是否大于2
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public FDMemberAsset selectFDMemberAssetByMemberIdAndShell(Map<String, Object> paramMap) throws Exception;

	
	/**
	 * 查询会员列表
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectFDMemberAssetPageList(Map<String, Object> paramMap) throws Exception;
	/**
	 * 查询会员资产列表
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<FDMemberAsset> selectFDMemberAssetList(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询礼券信息
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public FDVoucher selectFDVoucher(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询门店的收银流水
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<FDStoresCashier> selectFDStoresCashier(Map<String, Object> map) throws Exception;

	/**
	 * 查询会员银行卡信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<FdMemberAssetCoupon> selectFdMemberAssetCoupon(Map<String, Object> map) throws Exception;

	/**
	 * 会员优惠券统计
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectFdMemberAssetCouponCount(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询和店东有交易的消费者信息列表
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectTradeConsumerListForStores(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询门店礼券交易流水
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<FDVoucherDailyAccountMember> selectFDVoucherDailyAccountStore(Map<String, Object> paramMap)
			throws Exception;

	/**
	 * 记录会员资产信息
	 * 
	 * @param map
	 * @throws Exception
	 */
	public Integer updateFDMemberAsset(Map<String, Object> map) throws Exception;
	
	/**
	 * 更新会员贝壳数量 和 红包
	 * 
	 * @param map
	 * @throws Exception
	 */
	public Integer updateMemberShell(Map<String, Object> map) throws Exception;

	/**
	 * 更新礼券信息
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void updateFDVoucher(Map<String, Object> map) throws Exception;

	/**
	 * 更新会员优惠券信息
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void updateFdMemberAssetCoupon(Map<String, Object> map) throws Exception;

	/**
	 * 会员过期的优惠券设置为作废
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void updateDisabledCouponAssetStatus(Map<String, Object> map) throws Exception;

	/**
	 * 查询消费者现金交易流水
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public List<FDCashDailyAccountMember> selectFDCashDailyAccountConsumer(Map<String, Object> paramMap)
			throws Exception;

	/**
	 * 查询门店现金交易流水
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public List<FDCashDailyAccountMember> selectFDCashDailyAccountStore(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询门店金币交易流水
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public List<FDGoldDailyAccountMember> selectFDGoldDailyAccountStore(Map<String, Object> paramMap) throws Exception;

	/**
	 * 查询消费者金币交易流水
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public List<FDGoldDailyAccountMember> selectFDGoldDailyAccountConsumer(Map<String, Object> paramMap)
			throws Exception;

	/**
	 * 查询交易对账日志
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectBillCheckLog(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 查询门店佣金
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectStoresCashCommission(Map<String, Object> map) throws Exception;
	
	/**
	 * 	添加门店佣金
	 * 
	 * @param fDTransactions
	 * @throws Exception
	 */
	public void insertStoresCashCommission(FDCashCommissionStore fdCashCommissionStore) throws Exception;
	
	/**
	 * 修改门店佣金
	 * 
	 * @param fdCashCommissionStore
	 * @throws Exception
	 */
	public void updateStoresCashCommission(Map<String, Object> map) throws Exception;
	
	/**
	 * 查询门店佣金详细列表（日志表）
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectStoresCashCommissionLogList(Map<String, Object> map) throws Exception;
	
	/**
	 * 	添加门店佣金日志
	 * 
	 * @param fDTransactions
	 * @throws Exception
	 */
	public Integer insertStoresCashCommissionLog(Map<String, Object> map) throws Exception;
	

	public Map<String, Object> selectTransactionsnoByOuttradno(Map<String, Object> map) throws Exception;

	/**
	 * 插入多条数据到交易结果表
	 */
	public void insertListFDTransactionsResult(List<FDTransactionsResult> listFDTransactionsResult) throws Exception;

	/**
	 * 查询贝壳资产日志
	 */
	public List<FDMemberShellLog> selectFDMemberShellLog(Map<String, Object> paramsMap)throws Exception;
	
	/**
	 * 插入会员礼券关系表数据
	 */
	public Integer insertFdMemberCoupons(List<FDMemberCoupons> list) throws Exception;
	/**
	 * 批量修改贝壳余额
	 */
	void updateFDMemberAssetShell(List<FDMemberAsset> memberAssetList) throws Exception;

	void updateFdMemberCouponsRepairCount(List<FDMemberCoupons> list) throws Exception;
	void updateFdMemberCouponsRepairCount02(FDMemberCoupons s) throws Exception;

	/**
	 * 查询会员的礼券
	 */
	List<FDMemberCoupons> selectFdMemberCoupons(Map<String, Object> paramsMap);

	List<Map<String, Object>> selectCouponsLSN(Map<String, Object> paramsMap);

	/**
	 * 查询会员的礼券列表
	 */
	List<Map<String,Object>> selectMemberCouponsList(Map<String, Object> paramsMap);
	
	/**
	 * 查询会员的礼券
	 */
	List<Map<String,Object>> selectMemberCoupons(Map<String, Object> paramsMap);
	
	

	List<FDMemberRebateLog> selectFDMemberRebateLog(Map<String, Object> paramMap) throws Exception;

	/**
	 * 修改会员礼券表
	 */
	Integer updateMemberCoupons(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 修改会员礼券表数量
	 */
	Integer  updateMemberCouByMemberId(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 查找会员红包和提现明细
	 */
	List<Map<String,Object>> selectMemberMoneyLog(Map<String, Object> paramsMap);
	
	/**
	 * 只查询直邀 和间邀
	 */
	List<Map<String,Object>> selectMemberRateLog(Map<String, Object> paramsMap);
	
	/**
	 *  查找会员当月是否充过话费
	 */
	public  List<String>  selectTelephoneOrderMonth(Map<String, Object> tempMap);

	/**
	 *  修改礼券的key(将未激活的礼券改成尖货礼券)
	 */
	int updateFdMemberCouponsCouponsKey(Map<String, Object> paramsMap) throws Exception;

	int updateFdMemberCoupons(Map<String, Object> paramsMap) throws Exception;
	
	
	Map<String, Object> selectFdMemberCouponsCouponsKey(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 修改礼券数量
	 */
	int updateGiftCouponMun(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 *   查找掌柜累计返佣
	 */
	BigDecimal getMemberManagerRateCount(Map<String, Object> tempMap);
	
	/**
	 *   查找掌柜上个月累计返佣
	 */
	BigDecimal getMemberManagerRateMonCount(Map<String, Object> tempMap);

	/**
	 * 激活时间最前的一张礼券
	 */
	int updateFdMemberCouponsIsActivate(Map<String, Object> tempMap) throws Exception;



	int insertFDMemberCouponsLog(Map<String, Object> sqlMap);

}
