package com.ande.buyb2c.admin.controller.shop;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.shop.entity.Shop;
import com.ande.buyb2c.shop.service.IShopService;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb(已测)
 * @date 2018年1月27日下午2:28:10
 */
@RestController
@RequestMapping("/admin/shop")
public class ShopController extends AbstractController{
	@Resource
	private IShopService shopService;
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  查询网店详情
	 */
	@RequestMapping("/getShopById")
	public JsonResponse<Shop> getLogisticsById(Integer shopId){
		JsonResponse<Shop> json=new JsonResponse<Shop>();
		Shop shop = shopService.selectByPrimaryKey(shopId);
		if(shop!=null){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(shop);
		}
		return json;
	}
	
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  新增或更新或删除 
	 */
	@RequestMapping("/addOrUpdateOrDelShop")
	public JsonResponse<Shop> addOrUpdateShop(HttpServletRequest request,Shop shop){
		JsonResponse<Shop> json=new JsonResponse<Shop>();
		if(StringUtils.isEmpty(shop.getShopId())){
			//新增
			try {
				shop.setCreateTime(new Date());
				shop.setUpdateTime(new Date());
				shop.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
				shopService.insertSelective(shop);
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
			} catch (Exception e) {
				logError("新增网店异常", e);
			}
		}else{
			//编辑  
			shop.setUpdateTime(new Date());
			try {
				shopService.updateByPrimaryKeySelective(shop);
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
			} catch (Exception e) {
				logError("编辑网店异常", e);
			}
		}
		return json;
	}
}
