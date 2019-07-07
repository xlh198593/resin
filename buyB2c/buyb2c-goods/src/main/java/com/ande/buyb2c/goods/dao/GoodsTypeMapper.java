package com.ande.buyb2c.goods.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.goods.entity.GoodsType;
public interface GoodsTypeMapper extends IBaseDao<GoodsType>{
	public List<GoodsType> getGoodsTypeList(Integer goodsTypeId);
	public Integer delByParentId(@Param("level")String level,@Param("goodsTypeId")Integer goodsTypeId);
	public Integer updateByParentId(@Param("goodsTypeId")Integer goodsTypeId,@Param("level")String level,@Param("goodsTypeName")String goodsTypeName);
}