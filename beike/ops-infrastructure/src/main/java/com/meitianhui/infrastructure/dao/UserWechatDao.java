package com.meitianhui.infrastructure.dao;

import java.util.Map;

import com.meitianhui.infrastructure.entity.UserWechat;

public interface UserWechatDao {
	
	UserWechat getUserWechat(Map<String, Object> map) throws Exception;
	
	Integer  insert(UserWechat userWechat) throws Exception;
}
