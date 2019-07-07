package com.ande.buyb2c.web.contorller.advertPosition;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.advert.entity.AdvertPosition;
import com.ande.buyb2c.advert.service.IAdvertPositionService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;

/**
 * @author chengzb
 * @date 2018年2月2日下午6:26:47
 */
@RestController
@RequestMapping("/advert")
public class AdvertPositionController extends AbstractController{
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
}
