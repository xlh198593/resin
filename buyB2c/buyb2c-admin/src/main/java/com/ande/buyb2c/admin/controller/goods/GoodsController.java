package com.ande.buyb2c.admin.controller.goods;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.admin.vo.goods.GoodsVo;
import com.ande.buyb2c.attribute.service.IAttributeTypeAttributeService;
import com.ande.buyb2c.attribute.vo.AttributeValVo;
import com.ande.buyb2c.attribute.vo.AttributeVo;
import com.ande.buyb2c.common.generateOrderNo.ISerialNumberService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.PinyinTool;
import com.ande.buyb2c.common.util.PinyinTool.Type;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.goods.entity.Goods;
import com.ande.buyb2c.goods.entity.GoodsAttribute;
import com.ande.buyb2c.goods.entity.GoodsAttributeVal;
import com.ande.buyb2c.goods.service.IGoodsService;
import com.ande.buyb2c.goods.vo.RequestGoodsVo;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月30日上午11:29:17
 */
@RestController
@RequestMapping("/admin/goods")
public class GoodsController extends AbstractController {
	@Resource
	private IGoodsService goodsService;
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	@Resource
	private IAttributeTypeAttributeService attributeTypeAttributeService;
	@Resource
	private ISerialNumberService serialNumberService;
	/**
	 * @return 新增商品
	 */
	@RequestMapping("/addGoods")
	public JsonResponse<String> addOrUpdateGoodsType(HttpServletRequest request,@RequestBody Goods goods){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
				goods.setUpdateTime(new Date());
				goods.setCreateTime(new Date());
				if("1".equals(goods.getSaleState())){
					goods.setUpSaleTime(new Date());
				}
				goods.setPinyinGoodsName(PinyinTool.toPinYin(goods.getGoodsName(),"",Type.LOWERCASE));
				goods.setGoodsNo("G"+serialNumberService.getOrderNO("buyb2c:goods")+""+sessionUtil.getAdminUser(request).getAdminId());
				goods.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
				goodsService.insertSelective(goods);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("新增商品异常", e);
		}
		return json;
	}
	/**
	 * @return 复制商品
	 */
	@RequestMapping("/copyGoods")
	public JsonResponse<String> copyGoods(HttpServletRequest request,Integer goodsId){
		JsonResponse<String> json=new JsonResponse<String>();
		Goods goods=goodsService.getGoodsByCopy(goodsId);
		goods.setGoodsId(null);
		goods.setUpdateTime(new Date());
		goods.setCreateTime(new Date());
		goods.setGoodsNo("G"+serialNumberService.getOrderNO("buyb2c:goods")+""+sessionUtil.getAdminUser(request).getAdminId());
		try {
			goodsService.insertSelective(goods);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("复制商品异常", e);
		}
		return json;
	}
	/**
	 * @return 查询商品详情
	 */
	@RequestMapping("/getGoodsById")
	public JsonResponse<GoodsVo> addOrUpdateGoodsType(Integer goodsId){
		JsonResponse<GoodsVo> json=new JsonResponse<GoodsVo>();
		if(goodsId==null){
			json.setResult("goodsId 不能为空");
			return json;
		}
		Goods goods=goodsService.getGoodsById(goodsId);
		List<AttributeVo> attributeVoList = attributeTypeAttributeService.getAttributeTypeAttributeList(goods.getPlatformTypeId());
		check(attributeVoList, goods.getGoodsAttributeList());
		
		GoodsVo vo=new GoodsVo();
		vo.setAttributeVo(attributeVoList);
		goods.setGoodsAttributeList(null);
		vo.setGoods(goods);
		
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(vo);
		return json;
	}
	/**
	 * @return  查询列表
	 */
	@RequestMapping("/getGoodsPage")
	public JsonResponse<PageResult<Goods>> getGoodsPage(PageResult<Goods> page,Goods column){
		JsonResponse<PageResult<Goods>> json=new JsonResponse<PageResult<Goods>>();
		goodsService.queryByPage(page, column);
		if(page.getTotal()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(page);
		}
		return json;
	}
	/**
	 * @return 编辑商品
	 */
	@RequestMapping("/updateGoods")
	public JsonResponse<String> updateGoods(HttpServletRequest request,@RequestBody RequestGoodsVo goods){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
				goods.setUpdateTime(new Date());
				goods.setPinyinGoodsName(PinyinTool.toPinYin(goods.getGoodsName(),"",Type.LOWERCASE));
				goodsService.updateByPrimaryKeySelective(goods);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("编辑商品异常", e);
		}
		return json;
	}
	/**
	 * @return 上下架 删除商品
	 */
	@RequestMapping("/updateSaleState")
	public JsonResponse<String> updateSaleState(String goodsIds,String saleState,String delState){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			Date date=null;
			if("1".equals(saleState)){
				//上架
				date=new Date();
			}
			goodsService.updateState(goodsIds,saleState,delState,date);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("删除商品或上下架异常", e);
		}
		return json;
	}
	/**
	 * 
	 * 将商品属性和当前分类属性值做对比
	 * 相等的则作标记 val.setChoice(true);
	 * 并将商品属性中的值映射到分类属性中，返回前端
	 */
	public void check(List<AttributeVo> attributeVoList,List<GoodsAttribute> goodsAttributeList){
		for(GoodsAttribute goodsAttribute:goodsAttributeList){
			for(AttributeVo vo:attributeVoList){
					for(GoodsAttributeVal attVal:goodsAttribute.getGoodsAttributeValList()){
						for(AttributeValVo val:vo.getAttributeValList()){
								if(val.getAttributeValId().equals(attVal.getAttributeValId())){
									//1 设置商品属性值表的id
									val.setGoodsAttributeValId(attVal.getGoodsAttributeValId());
									//2 设置商品属性值选中
									val.setChoice(true);
									break;
								}
						}
					}
					if(vo.getAttributeId().equals(goodsAttribute.getAttributeId())){
						//3设置商品属性id
						vo.setGoodsAttributeId(goodsAttribute.getGoodsAttributeId());
						if("2".equals(goodsAttribute.getGoodsAttributeType())){
							//4 设置商品属性值
							vo.setGoodsAttributeVal(goodsAttribute.getGoodsAttributeVal());
						}
						break;
					}
				}
			}
	}
}
