package com.ande.buyb2c.user.service.adminUser;


import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.MD5Util;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.dao.AdminUserMapper;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月26日下午5:40:47
 */
@Service
public class AdminUserServiceImpl extends BaseServiceImpl<AdminUser> implements
		IAdminUserService {
	@Resource
	private AdminUserMapper adminUserMapper;
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	@Override
	protected IBaseDao<AdminUser> getMapper() {
		return adminUserMapper;
	}
	@Override
	public JsonResponse<AdminUser> toLogin(HttpServletRequest request,String userName,String password) {
		JsonResponse<AdminUser> json=new JsonResponse<AdminUser>(SystemCode.USER_NOT_EXISTS.getCode(),SystemCode.USER_NOT_EXISTS.getMsg());
		AdminUser model=adminUserMapper.toLogin(userName);
		if(model!=null){
			 password=MD5Util.MD5EncodeBySalt(password,model.getRand());
			if(password.equals(model.getPassword())){
				//登陆成功
				sessionUtil.setAdminUser(request,model);
				model.setPassword("");
				model.setRand("");
				json.setObj(model);
				json.set(SystemCode.SUCCESS.getCode(),SystemCode.SUCCESS.getMsg());
				
			}
		}
		//登录失败
		return json;
	}
	@Override
	public JsonResponse<String> updatePassword(HttpServletRequest request,String oldPassword, String newPassword) throws Exception{
		JsonResponse<String> json=new JsonResponse<String>();
		AdminUser userModel=sessionUtil.getAdminUser(request);
		userModel=adminUserMapper.selectByPrimaryKey(userModel.getAdminId());
		String pass=MD5Util.MD5EncodeBySalt(oldPassword,userModel.getRand());
		if(pass.equals(userModel.getPassword())){
			//可以重置
			String rand=MD5Util.getRand();
			String password=MD5Util.MD5EncodeBySalt(newPassword,rand);
			userModel.setRand(rand);
			userModel.setPassword(password);
			userModel.setUpdateTime(new Date());
			adminUserMapper.updateByPrimaryKeySelective(userModel);
			json.set(SystemCode.SUCCESS.getCode(),SystemCode.SUCCESS.getMsg());
		}else{
			json.set(SystemCode.WRONG_PASSWORD.getCode(),SystemCode.WRONG_PASSWORD.getMsg());
		}
		return json;
	}
	public static void main(String[] args) {
		System.out.println(MD5Util.MD5EncodeBySalt("123456","123456"));
	}
}
