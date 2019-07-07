package com.ande.buyb2c.admin.controller.column;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.attribute.entity.AttributeTypeAttribute;
import com.ande.buyb2c.column.entity.Column;
import com.ande.buyb2c.column.entity.ColumnGoods;
import com.ande.buyb2c.column.service.IColumnGoodsService;
import com.ande.buyb2c.column.service.IColumnService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.content.entity.Content;
import com.ande.buyb2c.content.service.IContentService;
import com.ande.buyb2c.goods.entity.Goods;
import com.ande.buyb2c.goods.service.IGoodsService;
import com.ande.buyb2c.logistics.entity.Logistics;
import com.ande.buyb2c.shop.entity.Shop;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb(已测)
 * @date 2018年1月27日下午4:02:56
 */
@RestController
@RequestMapping("/admin/column")
public class ColumnController  extends AbstractController{
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	@Resource
	private IColumnService columnService;
	@Resource
	private IColumnGoodsService columnGoodsService;
	@Resource
	private IGoodsService goodsService;
	/**
	 * 
	 * @param page
	 * @param 
	 * @return  查询详情
	 */
	
	
	@RequestMapping("/getColumnById")
	public JsonResponse<Column> getLogisticsById(Integer columnId){
		JsonResponse<Column> json=new JsonResponse<Column>();
		Column content = columnService.selectByPrimaryKey(columnId);
		if(content!=null){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(content);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  查询列表
	 */
	@RequestMapping("/getColumnPage")
	public JsonResponse<PageResult<Column>> getColumnPage(PageResult<Column> page,Column column){
		JsonResponse<PageResult<Column>> json=new JsonResponse<PageResult<Column>>();
		columnService.queryByPage(page, column);
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
	 * @param logistics
	 * @return  删除栏目
	 */
	public JsonResponse<String> delColumn(Integer columnId){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			//编辑  
				columnService.deleteByPrimaryKey(columnId);
				columnGoodsService.deleteByColumnId(columnId);
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
		}catch (Exception e) {
			logError("删除栏目异常", e);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  上下架 或删除
	 */
	@RequestMapping("/update")
	public JsonResponse<String> update(HttpServletRequest request,@RequestBody Column column){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			//编辑  
			if(!StringUtils.isEmpty(column.getDelState())){
				//将逻辑删除改为物理删除 避免前端改动，直接在当前接口改动
				json=delColumn(column.getColumnId());
				return json;
			}
				column.setUpdateTime(new Date());	
				columnService.updateByPrimaryKeySelective(column);
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
		}catch (Exception e) {
			logError("新增或更新栏目异常", e);
		}
		return json;
	}
	@RequestMapping("/addOrUpdateOrDeleteColumn")
	public JsonResponse<String> addOrUpdateOrDeleteColumn(HttpServletRequest request,@RequestBody Column column){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
		if(StringUtils.isEmpty(column.getColumnId())){
			//新增
			
				column.setCreateTime(new Date());
				column.setUpdateTime(new Date());
				column.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
				columnService.insertSelective(column);
				List<ColumnGoods> list=column.getList();
				for(ColumnGoods ata:list){
					ata.setColumnId(column.getColumnId());
				}
				columnGoodsService.addGoodsToColumn(request, list);
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
		}else{
			//编辑  
				column.setUpdateTime(new Date());
				columnService.updateByPrimaryKeySelective(column);
				List<ColumnGoods> list=column.getList();
				for(ColumnGoods ata:list){
					ata.setColumnId(column.getColumnId());
				}
				columnGoodsService.addGoodsToColumn(request, column.getList());
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
			}
		}catch (Exception e) {
			logError("新增或更新栏目异常", e);
			if(e.getMessage().indexOf("for key 'sort'")!=-1){
				json.setRes(SystemCode.FIELD_REPETITION.getCode());
				json.setResult("排序字段重复");
			}
		}
		return json;
	}
	/******************************栏目添加商品**************************************/
	/**
	 * 栏目添加商品
	 * @return  json格式数据 
	 * [
	 * {"columnGoodsId":1},
		{"columnId":1,"goodsId":1,"goodsNo":"wx23423","goodsName":" 商品名称"},
		{"columnId":1,"goodsId":2,"goodsNo":"wx2222","goodsName":" 商品名222称"},
		{"columnId":1,"goodsId":3,"goodsNo":"wx3333","goodsName":" 商品名称333"},
		{"columnId":1,"goodsId":4,"goodsNo":"wx4444","goodsName":" 商品名称444"}
		]
	 */
	@RequestMapping("/addColumnGoods")
	public JsonResponse<String> addColumnGoods(HttpServletRequest request,@RequestBody List<ColumnGoods> list){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			columnGoodsService.addGoodsToColumn(request, list);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("栏目添加商品失败", e);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @return  查询商品  传goodsName过滤查询
	 */
	@RequestMapping("/getColumnGoodsPageLeft")
	public JsonResponse<PageResult<Goods>> getColumnGoodsPageLeft(PageResult<Goods> page,Goods goods){
		JsonResponse<PageResult<Goods>> json=new JsonResponse<PageResult<Goods>>();
		goodsService.getGoodsPageByColumn(page, goods);
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
	 * @return  查询该栏目下关联的商品  传goodsName过滤查询
	 */
	@RequestMapping("/getColumnGoodsPageRight")
	public JsonResponse<PageResult<ColumnGoods>> getColumnGoodsPageRight(PageResult<ColumnGoods> page,ColumnGoods columnGoods){
		JsonResponse<PageResult<ColumnGoods>> json=new JsonResponse<PageResult<ColumnGoods>>();
		columnGoodsService.queryByPage(page, columnGoods);
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
	 *   移除栏目里的商品
	 */
	/*@RequestMapping("/delColumnGoods")
	public JsonResponse<String> delColumnGoods(String columnGoodsIds){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			columnGoodsService.delColumnGoods(columnGoodsIds);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("栏目移除商品失败", e);
		}
		return json;
	}*/
}
