package com.ande.buyb2c.collection.dao;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.collection.entity.Collection;
import com.ande.buyb2c.common.util.IBaseDao;

public interface CollectionMapper extends IBaseDao<Collection>{
	/**
	 * 添加商品到收藏夹时 判断该商品是否已存在
	 */
	Collection getCollection(@Param("customerId")Integer customerId, 
			@Param("goodsId")Integer goodsId
			);

	Integer cancelCollection(@Param("customerId")Integer customerId, 
			@Param("goodsId")Integer goodsId) throws Exception;
}