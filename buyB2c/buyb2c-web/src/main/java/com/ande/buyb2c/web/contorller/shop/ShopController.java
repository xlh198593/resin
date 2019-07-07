package com.ande.buyb2c.web.contorller.shop;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.shop.entity.Shop;
import com.ande.buyb2c.shop.service.IShopService;

/**
 * @author chengzb
 * @date 2018年2月2日下午4:01:40
 * 网店信息
 */
@RestController
@RequestMapping("/shop")
public class ShopController{
@Resource
private IShopService shopService;
@RequestMapping("/getShopById")
public JsonResponse<Shop> getShopById(Integer id){
	JsonResponse<Shop> json=new JsonResponse<Shop>();
	json.set(SystemCode.SUCCESS.getCode(), 
			SystemCode.SUCCESS.getMsg());
	json.setObj(shopService.selectByPrimaryKey(id));
	return json;
}
}
