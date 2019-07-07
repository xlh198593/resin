package com.ande.buyb2c.goods.service;



import java.util.Date;
import java.util.List;

import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.goods.entity.Goods;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:47:54
 */
public interface IGoodsService extends IBaseService<Goods> {
	/**
	 * 删除分类时 查询分类是否在商品中已使用
	 */
	public List<Goods> ifCanDel(String level,Integer goodTypeId);
	/**
	 * 栏目关联商品时 查询商品所用(移除已关联的)
	 */
	 public PageResult<Goods> getGoodsPageByColumn(PageResult<Goods> page,Goods goods);
	 /**
		 * 后台编辑商品时 查询商品详情
		 */
	 public Goods getGoodsById(Integer goodsId);
	 /**
	  * 
	  * @param 通过商品分类id查商品个数
	  * @return
	  */
	 public Integer getGoodsByTypeId(Integer typeId);
	 /**
	  * 
	  * @param 清除商品的类型属性
	  * @return
	  */
	public Integer updateByTypeId(Goods goods) throws Exception;
	 
	 public Integer updateState(String ids,String saleState,String delState,Date date)throws Exception;
	 /**
		 * web端通过栏目查商品列表
		 */
	 public PageResult<Goods> getGoodsPageByColumnId(PageResult<Goods> page,String goodsName,Integer columnId);
	 
	 /**
		 *复制时查询所用
		 */
		public Goods getGoodsByCopy(Integer goodsId);
}
