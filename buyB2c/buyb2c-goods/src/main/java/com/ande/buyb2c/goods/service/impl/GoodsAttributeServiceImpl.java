package com.ande.buyb2c.goods.service.impl;



import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.goods.dao.GoodsAttributeMapper;
import com.ande.buyb2c.goods.entity.GoodsAttribute;
import com.ande.buyb2c.goods.service.IGoodsAttributeService;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:49:28
 */
@Service
public class GoodsAttributeServiceImpl extends BaseServiceImpl<GoodsAttribute> implements
		IGoodsAttributeService {
@Resource
private GoodsAttributeMapper goodsAttributeMapper;
	@Override
	protected IBaseDao<GoodsAttribute> getMapper() {
		return goodsAttributeMapper;
	}
}
