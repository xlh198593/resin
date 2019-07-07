package com.ande.buyb2c.shopcart.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.shopcart.dao.ShopCartAttributeMapper;
import com.ande.buyb2c.shopcart.dao.ShopCartMapper;
import com.ande.buyb2c.shopcart.entity.ShopCart;
import com.ande.buyb2c.shopcart.entity.ShopCartAttribute;

/**
 * @author chengzb
 * @date 2018年2月2日下午1:57:44
 */
@Service
public class ShopCartServiceImpl extends BaseServiceImpl<ShopCart> implements IShopCartService{
@Resource
private ShopCartMapper shopCartMapper;
@Resource
private ShopCartAttributeMapper shopCartAttributeMapper;
	@Override
	protected IBaseDao<ShopCart> getMapper() {
		return shopCartMapper;
	}
	@Override
	public ShopCart getShopCart(Integer customerId, Integer goodsId
			) {
		return shopCartMapper.getShopCart(customerId,goodsId);
	}
	@Override
	public Integer delShopCart(String shopCartIds) {
		return shopCartMapper.delShopCart(shopCartIds);
	}
	@Override
	@Transactional
	public int insertSelective(ShopCart shopCart) throws Exception {
		shopCartMapper.insertSelective(shopCart);
		List<ShopCartAttribute> shopCartAttributeList = shopCart.getShopCartAttributeList();
		for(ShopCartAttribute attribute:shopCartAttributeList){
			attribute.setShopCartId(shopCart.getShopCartId());
			attribute.setGoodsId(shopCart.getGoodsId());
			attribute.setCreateTime(new Date());
		}
		shopCartAttributeMapper.addBatch(shopCartAttributeList);
		return 1;
	}
	@Override
	@Transactional
	public int deleteByPrimaryKey(int id)throws Exception {
		 shopCartMapper.deleteByPrimaryKey(id);
		 shopCartAttributeMapper.delShopCartAttribute(id);
		 return 1;
	 }
}
