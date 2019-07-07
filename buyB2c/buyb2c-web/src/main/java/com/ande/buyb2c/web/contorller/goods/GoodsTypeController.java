package com.ande.buyb2c.web.contorller.goods;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.goods.dao.GoodsTypeMapper;
import com.ande.buyb2c.goods.entity.GoodsType;
import com.ande.buyb2c.goods.service.IGoodsTypeService;

/**
 * @author chengzb
 * @date 2018年2月1日下午6:11:22
 */
@RestController
@RequestMapping("/goodsType")
public class GoodsTypeController extends AbstractController{
	@Resource
	private IGoodsTypeService goodsTypeService;
	@Resource
	private GoodsTypeMapper goodsTypeMapper;
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
	/**
	 * 
	 * @param page
	 * @param 
	 * @return  商品分类 级联查询
	 */
	@RequestMapping("/getGoodsTypeList")
	public JsonResponse<GoodsType> getGoodsTypeList(Integer parentId){
		JsonResponse<GoodsType> json=new JsonResponse<GoodsType>();
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		List<GoodsType> list =goodsTypeService.getGoodsTypeByParentId(parentId);
		getChildGoodsTypeList(list);
		json.setList(list);
		return json;
	}
	public void getChildGoodsTypeList(List<GoodsType> list){
		if(list.size()!=0){
			for(GoodsType type:list){
				List<GoodsType> childList =goodsTypeMapper.getGoodsTypeList(type.getGoodsTypeId());
				type.setChildList(childList);
				getChildGoodsTypeList(childList);
			}
		}
	}
}
