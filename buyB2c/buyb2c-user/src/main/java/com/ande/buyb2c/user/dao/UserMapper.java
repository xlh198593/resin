package com.ande.buyb2c.user.dao;

import java.util.List;
import java.util.Map;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.user.entity.User;

public interface UserMapper extends IBaseDao<User>{
public User toLogin(String phone);

public int getUserCountToday();

public int getUserCount();

public List<Map<String,Object>> getUserCountByYear();
}