package com.ande.buyb2c.column.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.column.entity.ColumnGoods;
import com.ande.buyb2c.common.util.IBaseDao;

public interface ColumnGoodsMapper extends IBaseDao<ColumnGoods>{
	public Integer addGoodsToColumn(List<ColumnGoods> list);
	public Integer  delColumnGoods(@Param("columnGoodsIds")String columnGoodsIds)throws Exception;
	public Integer deleteByColumnId(Integer columnId)throws Exception;
}