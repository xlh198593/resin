package com.ande.buyb2c.web.contorller.goods;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.goods.entity.Goods;
import com.ande.buyb2c.goods.service.IGoodsService;

/**
 * @author chengzb
 * @date 2018年2月2日上午9:55:54
 * 商品
 */
@RestController
@RequestMapping("/goods")
public class GoodsController extends AbstractController{
@Resource
private IGoodsService goodsService;
/**
 * 
 * @param page
 * @return  查询该栏目下关联的商品列表  传goodsName过滤查询
 */
@RequestMapping("/getGoodsPageByColumnId")
public JsonResponse<PageResult<Goods>> getGoodsPageByColumnId(PageResult<Goods> page,String goodsName,Integer columnId){
	JsonResponse<PageResult<Goods>> json=new JsonResponse<PageResult<Goods>>();
	if(columnId==null){
		json.setResult("columnId不能为空");
		return json;
	}
	goodsService.getGoodsPageByColumnId(page, goodsName, columnId);
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
 * @return  通过商品类型查商品列表  传goodsName过滤查询
 */
@RequestMapping("/getGoodsPageByTypeId")
public JsonResponse<PageResult<Goods>> getGoodsPageByTypeId(PageResult<Goods> page,Goods goods){
	JsonResponse<PageResult<Goods>> json=new JsonResponse<PageResult<Goods>>();
	if(goods.getGoodTypeId()==null){
		json.setResult("typeId不能为空");
		return json;
	}
	goodsService.queryByPageFront(page,goods);
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
 * @return  通过商品类型查商品列表  传goodsName过滤查询
 */
@RequestMapping("/getGoodsPage")
public JsonResponse<PageResult<Goods>> getGoodsPage(PageResult<Goods> page,Goods goods){
	JsonResponse<PageResult<Goods>> json=new JsonResponse<PageResult<Goods>>();
	goodsService.queryByPageFront(page,goods);
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
 * @return  商品详情
 */
@RequestMapping("/getGoodsById")
public JsonResponse<Goods> getGoodsById(Integer goodsId){
	JsonResponse<Goods> json=new JsonResponse<Goods>();
	if(goodsId==null){
		json.setResult("goodsId不能为空");
		return json;
	}
	json.set(SystemCode.SUCCESS.getCode(), 
			SystemCode.SUCCESS.getMsg());
	json.setObj(goodsService.getGoodsById(goodsId));
	return json;
}
}
