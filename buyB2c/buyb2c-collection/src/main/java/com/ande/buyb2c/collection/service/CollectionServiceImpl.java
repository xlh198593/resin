package com.ande.buyb2c.collection.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.collection.dao.CollectionMapper;
import com.ande.buyb2c.collection.entity.Collection;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;

/**
 * @author chengzb
 * @date 2018年2月2日下午2:05:26
 */
@Service
public class CollectionServiceImpl extends BaseServiceImpl<Collection>
		implements ICollectionService {
@Resource
private CollectionMapper collectionMapper;
	@Override
	protected IBaseDao<Collection> getMapper() {
		return collectionMapper;
	}
	@Override
	public Collection getCollection(Integer customerId, Integer goodsId) {
		return collectionMapper.getCollection(customerId, goodsId);
	}
	@Override
	public Integer cancelCollection(Integer customerId, Integer goodsId
			) throws Exception {
		return collectionMapper.cancelCollection(customerId,goodsId);
	}
}
