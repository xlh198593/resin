package com.ande.buyb2c.web.contorller.shopcart;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.collection.entity.Collection;
import com.ande.buyb2c.collection.service.ICollectionService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.shopcart.entity.ShopCart;
import com.ande.buyb2c.shopcart.entity.ShopCartAttribute;
import com.ande.buyb2c.shopcart.service.IShopCartService;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.web.UserJsonConvertUtil;

/**
 * @author chengzb
 * @date 2018年2月2日下午2:08:10
 */
@RestController
@RequestMapping("/front/shopcart")
public class ShopCartController extends AbstractController{
@Resource
private UserJsonConvertUtil userJsonConvertUtil;
@Resource
private IShopCartService shopCartService;
@Resource
private ICollectionService collectionService;
/**
 * 查询购物车中的商品列表
 */
@RequestMapping("/getGoodsOfShopCartPage")
public JsonResponse<PageResult<ShopCart>> addGoodsToShopCart(HttpServletRequest request,PageResult<ShopCart> page,ShopCart shopCart){
	JsonResponse<PageResult<ShopCart>> json=new JsonResponse<PageResult<ShopCart>>();
	User user = userJsonConvertUtil.getUser(request);
	shopCart.setCustomerId(user.getUserId());
	shopCartService.queryByPageFront(page, shopCart);
	if(page.getTotal()!=0){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(page);
	}
	return json;
}
/**
 * 添加购物车
 */
@RequestMapping("/addGoodsToShopCart")
public JsonResponse<String> addGoodsToShopCart(HttpServletRequest request,@RequestBody ShopCart shopCart){
	 User user = userJsonConvertUtil.getUser(request);
	JsonResponse<String> json=new JsonResponse<String>();
	ShopCart shop= shopCartService.getShopCart(user.getUserId(), shopCart.getGoodsId());
	try {
		if(shop!=null&&check(shopCart.getShopCartAttributeList(),shop.getGoodsAttributeValIds())){
			//有该商品  并属性相同 直接更新
			Integer nowNum=shop.getGoodsNum()+shopCart.getGoodsNum();
			shopCart=new ShopCart();
			shopCart.setShopCartId(shop.getShopCartId());
			shopCart.setGoodsNum(nowNum.byteValue());
			shopCart.setUpdateTime(new Date());
			shopCartService.updateByPrimaryKeySelective(shopCart);
		}else{
			//没有该商品或 有该商品  属性不相同  直接添加
			shopCart.setCreateTime(new Date());
			shopCart.setUpdateTime(new Date());
			shopCart.setCustomerId(user.getUserId());
			shopCartService.insertSelective(shopCart);
		}
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("添加购物车异常", e);
	}
	return json;
}
/**
 * 更新购物车商品数量
 */
@RequestMapping("/updateGoodsNumToShopCart")
public JsonResponse<String> updateGoodsNumToShopCart(HttpServletRequest request,@RequestBody ShopCart shopCart){
	JsonResponse<String> json=new JsonResponse<String>();
	try {
			//有该商品  更新商品件数
			shopCart.setUpdateTime(new Date());
			shopCartService.updateByPrimaryKeySelective(shopCart);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("更新购物车商品数量异常", e);
	}
	return json;
}
/**
 * 删除购物车商品
 */
@RequestMapping("/delGoodsOfShopCart")
public JsonResponse<String> delGoodsOfShopCart(HttpServletRequest request,Integer shopCartId){
	JsonResponse<String> json=new JsonResponse<String>();
	try {
			shopCartService.deleteByPrimaryKey(shopCartId);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("删除购物车商品异常", e);
	}
	return json;
}
/**
 * 移入收藏夹
 * 
 */
@RequestMapping("/moveGoodsToCollection")
public JsonResponse<String> moveGoodsToCollection(HttpServletRequest request,Collection collection,Integer shopCartId){
	JsonResponse<String> json=new JsonResponse<String>();
	User user = userJsonConvertUtil.getUser(request);
	try {
		if(collectionService.getCollection(user.getUserId(), collection.getGoodsId())==null){
			//收藏夹不存在  添加
			collection.setCreateTime(new Date());
			collection.setUpdateTime(new Date());
			collection.setCustomerId(user.getUserId());
			collectionService.insertSelective(collection);
		}
		shopCartService.deleteByPrimaryKey(shopCartId);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("移入收藏夹商品异常", e);
	}
	return json;
}
/**
 * 
 * @param sourceList
 * @param goodsAttributeValIds
 * @return true 属性相同返回true
 */
public boolean check(List<ShopCartAttribute> sourceList,String goodsAttributeValIds){
	String[] str=goodsAttributeValIds.split(",");
	String[] sourceStr=new String[sourceList.size()];
	for(int i=0;i<sourceList.size();i++){
		sourceStr[i]=String.valueOf(sourceList.get(i).getGoodsAttributeValId());
	}
	 Arrays.sort(str);  
	 Arrays.sort(sourceStr);  
	return Arrays.equals(str, sourceStr);
}
}
