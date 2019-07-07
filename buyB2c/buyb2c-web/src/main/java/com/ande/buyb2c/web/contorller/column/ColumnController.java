package com.ande.buyb2c.web.contorller.column;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.column.entity.Column;
import com.ande.buyb2c.column.service.IColumnGoodsService;
import com.ande.buyb2c.column.service.IColumnService;
import com.ande.buyb2c.column.vo.FrontColumnVo;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;

/**
 * @author chengzb
 * @date 2018年2月1日下午3:19:25
 */
@RestController
@RequestMapping("/column")
public class ColumnController extends AbstractController{
@Resource
private IColumnService columnService;
/**
 * 
 * @param page
 * @return  
 * 首页显示推荐的栏目图标
 */
@RequestMapping("/getColumnPage")
public JsonResponse<PageResult<Column>> getColumnPage(PageResult<Column> page,Column column){
	 JsonResponse<PageResult<Column>> json=new  JsonResponse<PageResult<Column>>();
	 columnService.queryByPageFront(page,column);
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
 * @return  
 * 首页显示推荐的栏目 及商品
 */
@RequestMapping("/getColumnGoodsPage")
public JsonResponse<PageResult<FrontColumnVo>> getColumnGoodsPage(PageResult<FrontColumnVo> page){
	 JsonResponse<PageResult<FrontColumnVo>> json=new  JsonResponse<PageResult<FrontColumnVo>>();
	 columnService.getColumnByPage(page);
	 for(FrontColumnVo vo:page.getDataList()){
		 String sort="";
		 String desc="";
		 if("1".equals(vo.getGoodsSort())){
			 sort="up_sale_time";
			 desc="desc";
		 }else if ("2".equals(vo.getGoodsSort())) {
			 sort="goods_price";
			 desc="desc";
		}else if ("3".equals(vo.getGoodsSort())) {
			 sort="goods_price";
			 desc="asc";
		}
		 vo.setGoodsList(columnService.getGolumnGoodsList(sort,vo.getShowGoodsNum(),vo.getColumnId(),desc));
	 }
		 if(page.getTotal()!=0){
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
				json.setObj(page);
			}
	return json;
}
}
