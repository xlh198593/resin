package com.meitianhui.member.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.member.entity.MDFavoriteStore;
import com.meitianhui.member.entity.MDFavoriteStoreLog;

/**
 * 消费者收藏门店
 * 
 * @ClassName: FavoriteStoreDao
 * @author tiny
 * @date 2017年5月15日 下午2:16:22
 *
 */
public interface FavoriteStoreDao {

	/**
	 * 消费者收藏门店列表
	 * 
	 * @param map
	 * @throws Exception
	 */
	void insertMDFavoriteStore(MDFavoriteStore favoriteStore) throws Exception;

	/**
	 * 会员收藏店东信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	MDFavoriteStore selectMDFavoriteStore(Map<String, Object> map) throws Exception;

	/**
	 * 会员收藏店东列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectMDFavoriteStoreList(Map<String, Object> map) throws Exception;

	/**
	 * 会员收藏店东列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectMDFavoriteStoreListPage(Map<String, Object> map) throws Exception;
	
	/**
	 * 取消关注
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int deleteMDFavoriteStore(Map<String, Object> map) throws Exception;

	/**
	 * 更新收藏信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int updateMDFavoriteStore(Map<String, Object> map) throws Exception;

	/**
	 * 查询消费者是否有默认门店
	 */
	List<Map<String, Object>> selectMDFavoriteStoreByIsLlmStores(Map<String, Object> paramsMap);
	
	
	/**
	 * 新增绑定店东日志
	 * 
	 * @param map
	 * @throws Exception
	 */
	void insertBingStoreLog(MDFavoriteStoreLog favoriteStoreLog) throws Exception;
	
	/**
	 * 查询绑定日志查询列表
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectBingStoreLogListPage(Map<String, Object> map) throws Exception;

}
