package com.ande.buyb2c.admin.controller.logistics;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.logistics.entity.Logistics;
import com.ande.buyb2c.logistics.service.ILogisticsService;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月27日上午9:17:35
 * 物流（已测）
 */
@RestController
@RequestMapping("/admin/logistics")
public class LogisticsController extends AbstractController{
@Resource
private ILogisticsService logisticsService;
@Resource
private SessionUtil<AdminUser> sessionUtil;
/**
 * 
 * @param page
 * @param logistics
 * @return  查询物流列表
 */
@RequestMapping("/getLogisticsPage")
public JsonResponse<PageResult<Logistics>> getLogisticsPage(PageResult<Logistics> page,Logistics logistics){
	JsonResponse<PageResult<Logistics>> json=new JsonResponse<PageResult<Logistics>>();
	logisticsService.queryByPage(page, logistics);
	if(page.getTotal()!=0){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(page);
	}
	return json;
}
/**
 * 
 * @param page
 * @param logistics
 * @return  查询物流详情
 */
@RequestMapping("/getLogisticsById")
public JsonResponse<Logistics> getLogisticsById(Integer logisticsId){
	JsonResponse<Logistics> json=new JsonResponse<Logistics>();
	Logistics logistics = logisticsService.selectByPrimaryKey(logisticsId);
	if(logistics!=null){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(logistics);
	}
	return json;
}
/**
 * 
 * @param page
 * @param logistics
 * @return  新增物流
 */
@RequestMapping("/addLogistics")
public JsonResponse<String> addLogistics(HttpServletRequest request,Logistics logistics){
	JsonResponse<String> json=new JsonResponse<String>();
	try {
		logistics.setCreateTime(new Date());
		logistics.setUpdateTime(new Date());
		logistics.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
		logisticsService.insertSelective(logistics);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("插入物流模板异常", e);
	}
	return json;
}
/**
 * 
 * @param page
 * @param logistics
 * @return  更新 删除物流   删除只需传delState=1
 * 
 */
@RequestMapping("/updateOrDeleteLogisticsById")
public JsonResponse<String> updateOrDeleteLogisticsById(Logistics logistics){
	JsonResponse<String> json=new JsonResponse<String>();
	try {
		logistics.setUpdateTime(new Date());
		logisticsService.updateByPrimaryKeySelective(logistics);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("更新物流模板异常", e);
	}
	return json;
}
}
