package com.ande.buyb2c.user.dao;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.user.entity.AdminUser;

public interface AdminUserMapper extends IBaseDao<AdminUser>{
	public AdminUser toLogin(String userName);
}