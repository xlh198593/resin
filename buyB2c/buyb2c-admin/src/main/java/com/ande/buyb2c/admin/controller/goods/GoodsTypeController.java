package com.ande.buyb2c.admin.controller.goods;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.goods.entity.Goods;
import com.ande.buyb2c.goods.entity.GoodsType;
import com.ande.buyb2c.goods.service.IGoodsService;
import com.ande.buyb2c.goods.service.IGoodsTypeService;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:59:55
 * 商品分类
 */
@RestController
@RequestMapping("/admin/goodsType")
public class GoodsTypeController extends AbstractController{
@Resource
private IGoodsTypeService goodsTypeService;
@Resource
private IGoodsService goodsService;
@Resource
private SessionUtil<AdminUser> sessionUtil;
@RequestMapping("/addOrUpdateGoodsType")
public JsonResponse<String> addOrUpdateGoodsType(HttpServletRequest request,GoodsType goodsType){
	JsonResponse<String> json=new JsonResponse<String>();
	try {
		goodsType.setUpdateTime(new Date());
		if(goodsType.getGoodsTypeId()==null){
			//新增
			goodsType.setCreateTime(new Date());
			goodsType.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
			goodsTypeService.insertSelective(goodsType);
			//新增分类时，将该分类的父类下关联的商品  的商品分类属性清除
			if(!"1".equals(goodsType.getGoodsTypeLevel())){
				//不是一级分类
				if(goodsService.getGoodsByTypeId(goodsType.getGoodsTypeId())!=0){
					//该分类的父类下有商品
					Goods goods=new Goods();
					goods.setGoodTypeId(goodsType.getGoodsTypeId());
					goodsService.updateByTypeId(goods);
				}
			}
			
		}else{
			//编辑
			goodsTypeService.updateByPrimaryKeySelective(goodsType);
		}
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		if(e.getMessage().indexOf("for key 'sort'")!=-1){
			json.setRes(SystemCode.FIELD_REPETITION.getCode());
			json.setResult("排序字段重复");
		}
		logError("新增或编辑商品分类异常", e);
	}
	return json;
}
/**
 * 
 * @param page
 * @param 
 * @return  商品分类 树形结构(包括列表页数据)
 */
@RequestMapping("/getGoodsTypeList")
public JsonResponse<GoodsType> getGoodsTypeList(){
	JsonResponse<GoodsType> json=new JsonResponse<GoodsType>();
	json.set(SystemCode.SUCCESS.getCode(), 
			SystemCode.SUCCESS.getMsg());
	json.setList(goodsTypeService.getGoodsTypeList());
	return json;
	
}
/**
 * 
 * @param page
 * @param 
 * @return  商品分类 级联查询
 */
@RequestMapping("/getGoodsTypeByParentId")
public JsonResponse<GoodsType> getGoodsTypeByParentId(Integer parentId){
	JsonResponse<GoodsType> json=new JsonResponse<GoodsType>();
	List<GoodsType> list = goodsTypeService.getGoodsTypeByParentId(parentId);
	if(list!=null&&list.size()!=0){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setList(list);
	}
	return json;
}
@RequestMapping("/delGoodsType")
public JsonResponse<String> delGoodsType(String goodsTypeLevel,Integer goodsTypeId){
	JsonResponse<String> json=new JsonResponse<String>();
	if(StringUtils.isEmpty(goodsTypeLevel)||goodsTypeId==null){
		json.setResult("goodsTypeLevel且goodsTypeId不能为空");
		return json;
	}
	List<Goods> list=goodsService.ifCanDel(goodsTypeLevel, goodsTypeId);
	if(null!=list&&list.size()!=0){
		//当前分类 有商品关联不允许删除
		json.setRes(SystemCode.NOT_ALLOW_OPERATION.getCode());
		json.setResult("当前分类有商品关联不允许删除");
		return json;
	}
	//删除 当前分类以及子分类
	try {
		goodsTypeService.delGoodsType(goodsTypeLevel,goodsTypeId);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("删除商品分类异常", e);
	}
	return json;
}
@RequestMapping("/getGoodsType")
public JsonResponse<GoodsType> getGoodsType(Integer goodsTypeId){
	JsonResponse<GoodsType> json=new JsonResponse<GoodsType>();
	if(goodsTypeId==null){
		json.setResult("goodsTypeId不能为空");
		return json;
	}
	json.set(SystemCode.SUCCESS.getCode(), 
			SystemCode.SUCCESS.getMsg());
	json.setObj(goodsTypeService.selectByPrimaryKey(goodsTypeId));
	return json;
}
}
