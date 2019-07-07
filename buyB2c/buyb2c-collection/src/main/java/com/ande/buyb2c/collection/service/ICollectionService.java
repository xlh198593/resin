package com.ande.buyb2c.collection.service;

import com.ande.buyb2c.collection.entity.Collection;
import com.ande.buyb2c.common.util.IBaseService;

/**
 * @author chengzb
 * @date 2018年2月2日下午2:04:53
 */
public interface ICollectionService extends IBaseService<Collection> {
	/**
	 * 添加商品到收藏夹时 判断该商品是否已存在
	 */
public Collection getCollection(Integer customerId,Integer goodsId);
/**
 * 取消收藏
 */
public Integer cancelCollection(Integer customerId,Integer goodsId) throws Exception;;
}
