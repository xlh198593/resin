package com.ande.buyb2c.shopcart.dao;

import java.util.List;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.shopcart.entity.ShopCartAttribute;

public interface ShopCartAttributeMapper extends IBaseDao<ShopCartAttribute>{
	public int addBatch(List<ShopCartAttribute> list)throws Exception;

	public Integer delShopCartAttribute(Integer shopCartId);
}