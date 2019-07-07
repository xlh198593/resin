package com.ande.buyb2c.goods.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.goods.entity.GoodsAttribute;

public interface GoodsAttributeMapper extends IBaseDao<GoodsAttribute>{
	public Integer addBatch(List<GoodsAttribute> list);
	public Integer delGoodsAttribute(@Param("ids")String ids);
}