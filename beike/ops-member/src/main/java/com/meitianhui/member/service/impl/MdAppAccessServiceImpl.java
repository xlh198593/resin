package com.meitianhui.member.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.member.dao.MdAppAccessRecordDao;
import com.meitianhui.member.entity.MdAppAccessRecord;
import com.meitianhui.member.service.MdAppAccessService;

@SuppressWarnings("unchecked")
@Service
public class MdAppAccessServiceImpl implements MdAppAccessService {

	@Autowired
	private MdAppAccessRecordDao mdAppAccessRecordDao;
	@Override
	public int insertAppAccessRecord(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		
		MdAppAccessRecord record =new MdAppAccessRecord();
		record.setApp_type(paramsMap.get("app_type").toString());
		record.setApp_type_detail(paramsMap.get("app_type_detail").toString());
		record.setApp_version(paramsMap.get("app_version").toString());
		record.setAccess_method(paramsMap.get("access_method").toString());
		record.setMember_id(paramsMap.get("member_id") !=null ? paramsMap.get("member_id").toString():null);
		return mdAppAccessRecordDao.insertAppAccessRecord(record);
	}

}
