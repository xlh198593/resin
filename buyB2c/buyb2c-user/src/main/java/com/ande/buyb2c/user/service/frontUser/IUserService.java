package com.ande.buyb2c.user.service.frontUser;

import java.util.List;
import java.util.Map;

import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.user.entity.User;

/**
 * @author chengzb
 * @date 2018年1月26日下午2:07:58
 */
public interface IUserService extends IBaseService<User>{
/**
 * 登陆 成功返回user对象 失败返回 null 异常说明用户注册失败
*/
public User toLogin(String phone,String code) throws Exception;

public int getUserCount();
public int getUserCountToday();
public List<Map<String,Object>> getUserCountByYear();
}
