package com.ande.buyb2c.goods.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.goods.entity.Goods;

public interface GoodsMapper extends IBaseDao<Goods>{
	public List<Goods> ifCanDel(@Param("level")String level,@Param("goodTypeId")Integer goodTypeId);
	
	   public List<Goods> getGoodsPageByColumn(Goods goods);
	   
	   public Goods getGoodsById(Integer goodsId);
	   public Integer updateState(@Param("ids") String ids
			   ,@Param("saleState")String saleState
			   ,@Param("delState") String delState
			   ,@Param("date") Date date) throws Exception;
	   public List<Goods> getGoodsPageByColumnId(@Param("goodsName")String goodsName,@Param("columnId")Integer columnId);
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
	public Integer updateByTypeId(Goods goods);
	
	//复制时查询所用
	public Goods getGoodsByCopy(Integer goodsId);
	
}