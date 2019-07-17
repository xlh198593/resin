package com.meitianhui.member.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface ConsumerService {

	/**
	 * 查询消费者信息
	 */
	void findConsumerById(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 消费者登陆校验
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerLoginValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者状态更新
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerStatusEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者信息完善
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者信息查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者信息同步
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleConsumerSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者信息查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerBaseInfoFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者信息列表查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者收货地址创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerAddressCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者地址编辑
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerAddressEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者地址查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerAddressFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者地址删除
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleConsumerAddressRemove(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者等级查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerLevelFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者签到
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerSign(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者查询指定默认门店
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void defaultStore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者收藏门店
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void favoriteStore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费收藏门店编辑
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void favoriteStoreEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者取消收藏门店
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void favoriteStoreCancel(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者收藏门店列表
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void favoriteStoreList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者收藏门店列表
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void favoriteStoreListPage(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 添加消费者对应的推荐人
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void userRecommendCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询消费者对应的推荐人
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void userRecommendFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 修改用户临时绑定的邀请码
	 */
	void modifyInvitationCode(Map<String, Object> paramsMap, ResultData result)
			throws Exception;

	/**
	 * 绑定店东
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void handleBingStore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 绑定店东操作日志
	 * 
	 * @param paramsMap
	 * @param result
	 */
	public void bingStoreLogListPage(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;;

	/**
	 * 查询领有惠app版本
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void appVersionFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 分页查询app版本列表
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void appversionPageListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 新增app版本
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void saveAppversion(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 更新版本状态
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void updateApversionStatus(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 根据手机号码查询店东
	 * 
	 * @param paramsMap
	 * @param result
	 */
	public void selectMDStoresListByContactTel(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 获取和包用户信息
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void hebaoUserInfoFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 和包用户领取现金券
	 * 
	 * @param paramsMap
	 * @param result
	 */
	void hebaoGetCashCoupon(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 和包用户核销现金券
	 * 
	 * @param paramsMap
	 * @param result
	 */
	void hebaoUseCashCoupon(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 获取和包用户已领现金券列表
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void hebaoCashCouponListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	void hebaoMd5Sign(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 修改消费者VIP时长
	 */
	void mdConsumerVipTimeUpdate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询消费者会员是否过期
	 */
	void consumerVipTime(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 修改用户成长值
	 */
	void updateConsumerGrowthValue(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查询消费者VIP等级
	 */
	void consumerVipLevel(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 设置会员成长值，传固定的值
	 */
	void handleMemberGrowthValue(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 传入userId，修改会员成长值
	 */
	void handleConsumerGrowthValue(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 插入member_id，判断是否是会员
	 */
	void isMemberInfo(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 测试分销逻辑
	 */
	void buildingMemberDistrbution(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 粉丝管理
	 */
	void distrbutionFansManage(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 掌柜粉丝管理
	 */
	void managerMemberFans(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者信息查询_v1
	 */
	void consumerFind_v1(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 修改用户手机号
	 */
	void consumerPhoneUpdate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	void memberDistributionByRecharge(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 获取掌柜id
	 */
	void memberDistributionManagerId(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 通过手机号进行查找
	 */
	void consumerFindByPhone(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 外部用户注册
	 */
	void consumerRegistSimple(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询用户是否是vip
	 */
	void consumerIsVip(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询会员分销信息parent_id
	 */
	void memberDistributionInfoById(Map<String, Object> paramsMap, ResultData result)throws Exception;
}
