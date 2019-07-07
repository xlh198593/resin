package com.ande.buyb2c.web.contorller.logistics;


import javax.annotation.Resource;

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
@RequestMapping("/front/logistics")
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
	logisticsService.queryByPageFront(page, logistics);
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

}
