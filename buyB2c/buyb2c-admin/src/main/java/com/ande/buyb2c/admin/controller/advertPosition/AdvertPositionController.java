package com.ande.buyb2c.admin.controller.advertPosition;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.advert.entity.AdvertPosition;
import com.ande.buyb2c.advert.entity.AdvertPositionDetail;
import com.ande.buyb2c.advert.service.IAdvertPositionService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.entity.AdminUser;
import com.fasterxml.jackson.databind.JsonSerializer;

/**
 * @author chengzb
 * @date 2018年2月2日下午6:26:47
 */
@RestController
@RequestMapping("/admin/advert")
public class AdvertPositionController extends AbstractController{
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	@Resource
	private IAdvertPositionService advertPositionService;
	@RequestMapping("/getAdvertPosition")
	public JsonResponse<AdvertPosition> getAdvertPosition(Integer advertPositionId){
		JsonResponse<AdvertPosition> json=new JsonResponse<AdvertPosition>();
		if(advertPositionId==null){
			json.setResult("advertPositionId不能为空");
			return json;
		}
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(advertPositionService.selectByPrimaryKey(advertPositionId));
		return json;
	}
	@RequestMapping("/addAdvertPosition")
	public JsonResponse<String> addAdvertPosition(@RequestBody AdvertPosition advertPosition,HttpServletRequest request){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			advertPositionService.add(advertPosition, request);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("添加广告位异常", e);
		}
		return json;
	}
	@RequestMapping("/updateAdvertPosition")
	public JsonResponse<String> updateAdvertPosition(@RequestBody AdvertPosition advertPosition){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			advertPositionService.updateByPrimaryKeySelective(advertPosition);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("编辑广告位异常", e);
		}
		return json;
	}
}
