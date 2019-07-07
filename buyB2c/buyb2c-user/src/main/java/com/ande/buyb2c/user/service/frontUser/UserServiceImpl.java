package com.ande.buyb2c.user.service.frontUser;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.MD5Util;
import com.ande.buyb2c.common.util.RedisUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.dao.UserMapper;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.web.service.sms.ISmsAuthenCodeService;

/**
 * @author chengzb
 * @date 2018年1月26日下午2:12:40
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User>implements IUserService {
@Resource
private UserMapper userDao;
@Resource
private RedisUtil redisUtil;
@Resource ISmsAuthenCodeService smsAuthenCode;
	@Override
	public User toLogin(String phone,String code) throws Exception {
		User user=null;
		//短信验证码登录
		if(SystemCode.SMS_OK.getCode()==smsAuthenCode.checkCode(phone,code).getRes()){
			user=userDao.toLogin(phone);
			if(user==null){
				//用户不存在，注册
				user=new User();
				user.setPhone(phone);
				user.setCreateTime(new Date());
				user.setUpdateTime(new Date());
				userDao.insertSelective(user);
			}
			String md5Phone=MD5Util.MD5Encode(phone);
			String token=md5Phone+":"+System.currentTimeMillis();
			user.setToken(token);
			
			//踢出之前登录的用户
			redisUtil.updatePattern(md5Phone+"*");
			redisUtil.set(token,user);
		}
		return user;
	}
	@Override
	protected IBaseDao<User> getMapper() {
		return userDao;
	}
	@Override
	 public int updateByPrimaryKeySelective(User user) throws Exception{
		user.setUpdateTime(new Date());
		userDao.updateByPrimaryKeySelective(user);
		redisUtil.set(user.getToken(), user);
		return 1;
	 }
	public static void main(String[] args) {
		System.out.println(MD5Util.MD5Encode("18757127055"));
	}
	@Override
	public int getUserCount() {
		return userDao.getUserCount();
	}
	@Override
	public int getUserCountToday() {
		return userDao.getUserCountToday();
	}
	@Override
	public List<Map<String, Object>> getUserCountByYear() {
		return userDao.getUserCountByYear();
	}
}
