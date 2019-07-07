package com.ande.buyb2c.shopcart.dao;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.shopcart.entity.ShopCart;

public interface ShopCartMapper extends IBaseDao<ShopCart>{
	/**
	 * 添加商品到购物车时 判断该商品是否已存在
	 */
	ShopCart getShopCart(@Param("customerId")Integer customerId, 
			@Param("goodsId")Integer goodsId
			);
	
	public Integer delShopCart(@Param("shopCartIds") String shopCartIds);
}