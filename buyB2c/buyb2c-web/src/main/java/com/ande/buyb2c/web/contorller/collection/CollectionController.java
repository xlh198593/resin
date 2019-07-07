package com.ande.buyb2c.web.contorller.collection;

import java.util.Date;

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
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.web.UserJsonConvertUtil;

/**
 * @author chengzb
 * @date 2018年2月2日下午4:08:13
 * 收藏夹
 */
@RestController
@RequestMapping("/front/collection")
public class CollectionController extends AbstractController{
	@Resource
	private UserJsonConvertUtil userJsonConvertUtil;
	@Resource
	private ICollectionService collectionService;
	/**
	 * @param goodsId
	 * @param goodsAttribute
	 * @return 判断该商品是否被当前用户收藏 1 是 0不是
	 */
	@RequestMapping("/isCollect")
	public JsonResponse<String> isCollect(HttpServletRequest request,Integer goodsId){
		JsonResponse<String> json=new JsonResponse<String>();
		Collection collection= collectionService.getCollection(userJsonConvertUtil.getUser(request).getUserId(),goodsId);
		if(collection!=null){
			json.setRes(SystemCode.SUCCESS.getCode());
		}
		return json;
	}
	/**
	 * @param goodsId
	 * @param goodsAttribute
	 * @return 取消收藏
	 */
	@RequestMapping("/cancelCollect")
	public JsonResponse<String> cancelCollect(HttpServletRequest request,Integer goodsId){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			collectionService.cancelCollection(userJsonConvertUtil.getUser(request).getUserId(),goodsId);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("取消收藏", e);
		}
		return json;
	}
	/**
	 * 查询收藏夹的商品列表
	 */
	@RequestMapping("/getCollectionPage")
	public JsonResponse<PageResult<Collection>> getCollectionPage(HttpServletRequest request,PageResult<Collection> page,Collection collection){
		JsonResponse<PageResult<Collection>> json=new JsonResponse<PageResult<Collection>>();
		User user = userJsonConvertUtil.getUser(request);
		collection.setCustomerId(user.getUserId());
		collectionService.queryByPageFront(page, collection);
		if(page.getTotal()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(page);
		}
		return json;
	}
	/**
	 * 添加收藏夹
	 */
	@RequestMapping("/addGoodsToCollection")
	public JsonResponse<String> addGoodsToCollection(HttpServletRequest request,@RequestBody Collection collection){
		 User user = userJsonConvertUtil.getUser(request);
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			collection.setCreateTime(new Date());
			collection.setUpdateTime(new Date());
			collection.setCustomerId(user.getUserId());
			collectionService.insertSelective(collection);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("添加收藏夹异常", e);
		}
		return json;
	}
	/**
	 * 删除收藏夹商品
	 */
	@RequestMapping("/delGoodsOfCollection")
	public JsonResponse<String> delGoodsOfCollection(HttpServletRequest request,Integer collectionId){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			collectionService.deleteByPrimaryKey(collectionId);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("删除收藏夹商品异常", e);
		}
		return json;
	}
}
