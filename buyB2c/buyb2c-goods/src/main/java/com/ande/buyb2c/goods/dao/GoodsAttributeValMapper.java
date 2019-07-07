package com.ande.buyb2c.goods.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.goods.entity.GoodsAttributeVal;

public interface GoodsAttributeValMapper extends IBaseDao<GoodsAttributeVal>{
	public Integer addBatch(List<GoodsAttributeVal> list);
	public Integer delGoodsAttributeVal(@Param("ids")String ids);
	public Integer delGoodsAttributeValById(@Param("ids")String ids);
}