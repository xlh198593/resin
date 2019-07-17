package com.meitianhui.member.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.member.entity.MDConsumer;
import com.meitianhui.member.entity.MDConsumerAddress;
import com.meitianhui.member.entity.MDMemberBenefits;
import com.meitianhui.member.entity.MDMemberRecommend;

public interface ConsumerDao {
	

	/**
	 * 消费者信息
	 * 
	 * @param map
	 * @throws Exception
	 */
	void insertMDConsumer(MDConsumer mDConsumer) throws Exception;

	/**
	 * 消费者信息日志
	 * 
	 * @param map
	 * @throws Exception
	 */
	void insertMDConsumerLog(Map<String, Object> map) throws Exception;
	

	/**
	 * 消费者收货地址创建
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	void insertMDConsumerAddress(MDConsumerAddress mDConsumerAddress) throws Exception;


	/**
	 * 消费者定位信息
	 * 
	 * @param map
	 * @throws Exception
	 */
	void insertMDConsumerLocation(Map<String, Object> map) throws Exception;
	

	/**
	 * 查询消费者地址信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectMDConsumerAddress(Map<String, Object> map) throws Exception;

	
	/**
	 * 查询消费者会员信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<MDConsumer> selectMDConsumer(Map<String, Object> map) throws Exception;
	
	/**
	 * 查询消费者会员信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public MDConsumer selectMDConsumerById(Map<String, Object> map) throws Exception;
	
	/**
	 * 查询消费者会员列表信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<MDConsumer> selectMDConsumerList(Map<String, Object> map) throws Exception;

	/**
	 * 查询消费者会员信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public MDConsumer selectMDConsumerBaseInfo(Map<String, Object> map) throws Exception;
	
	
	/**
	 * 更新消费者信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	Integer updateMDConsumer(Map<String, Object> map) throws Exception;

	
	/**
	 * 更新消费者地址信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int updateMDConsumerAddress(Map<String, Object> map) throws Exception;
	

	/**
	 * 删除消费者收货地址
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	void deleteMDConsumerAddress(Map<String, Object> map) throws Exception;
	
	/**
	 * 添加消费者对应的推荐人的关系
	 * 
	 * @param map
	 * @throws Exception
	 */
	void insertUserRecommend(Map<String, Object> map) throws Exception;
	
	/**
	 * 查询消费者对应的推荐人的关系信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectUserRecommend(Map<String, Object> map) throws Exception;
/**
	 * 查找会员结束时间小于当天时间，而且成长值大于0  的会员id
	 * 
	 * */
	public  List<Map<String, Object>> memberOutVipEndTimeList()  throws  Exception;
/**
	 * (定时任务)查找与当前时间对比未过期的会员
	 */
	public List<MDMemberBenefits> selectMDConsumerForVipEndTime() throws Exception;
	/**
	 * (定时任务)查找与当前时间对比未过期的会员,并加上今日的成长值
	 */
	Integer updateMDConsumerGrowthValue(Map<String, Object> map) throws Exception;

	/**
	 * 查询会员等级
	 */
	List<Map<String, Object>> selectMDConsumerLevel(Map<String, Object> paramsMap);

	/**
	 * 获取所有用户手机号
	 */
	List<String> getAllConsumerMobile()throws Exception;

	/***
	 *获取用户总数
	 */
	Long getAllConsumerCount()throws Exception;

	/**
	 * 查询消费者会员列表信息
	 *
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<MDConsumer> queryMDConsumerList(Map<String, Object> map) throws Exception;
}
