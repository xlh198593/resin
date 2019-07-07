package com.ande.buyb2c.shopcart.service;

import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.shopcart.entity.ShopCart;

/**
 * @author chengzb
 * @date 2018年2月2日下午1:58:25
 */
public interface IShopCartService extends IBaseService<ShopCart> {
	/**
	 * 添加商品到购物车时 判断该商品是否已存在
	 */
public ShopCart getShopCart(Integer customerId,Integer goodsId);

public Integer delShopCart(String shopCartIds);
}
