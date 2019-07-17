package com.meitianhui.member.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.meitianhui.member.entity.MdAppVersion;

public interface MdAppVersionDao {

	/**
	 * 插入记录
	 * @param mdAppVersion
	 * @return
	 * @throws Exception
	 */
	int insertMdAppVersion(MdAppVersion mdAppVersion) throws Exception ;
	/**
	 * 选择最新的一条版本更新记录
	 * @param map
	 * @return
	 * @throws Exception
	 */
	MdAppVersion selectLasterMdAppVersion(Map<String,Object> map) throws Exception;
	/**
	 * 根据版本类型和版本号查询记录
	 * @param app_type
	 * @param version_no
	 * @return
	 * @throws Exception
	 */
	MdAppVersion selectMdAppVersionBy(@Param("app_type") Integer app_type, @Param("version_no") String version_no) throws Exception; 
	
	/**
	 * 分页查询版本记录列表
	 * @param map
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> selectMdAppVersionPageList(Map<String,Object> map) throws Exception; 
	
	/**
	 * 更新订单状态
	 * @param update_status
	 * @param id
	 * @return
	 * @throws Exception
	 */
	int updateUpdateStatusById(@Param("update_status") Integer update_status, @Param("id") Integer id) throws Exception; 
	
}
