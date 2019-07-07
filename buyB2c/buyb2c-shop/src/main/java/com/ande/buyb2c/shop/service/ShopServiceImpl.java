package com.ande.buyb2c.shop.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.shop.dao.ShopMapper;
import com.ande.buyb2c.shop.entity.Shop;

/**
 * @author chengzb
 * @date 2018年1月27日下午2:26:45
 */
@Service
public class ShopServiceImpl extends BaseServiceImpl<Shop> implements
		IShopService {
@Resource
private ShopMapper shopMapper;
	@Override
	protected IBaseDao<Shop> getMapper() {
		return shopMapper;
	}



}
