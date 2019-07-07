package com.ande.buyb2c.goods.service.impl;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.goods.dao.GoodsTypeMapper;
import com.ande.buyb2c.goods.entity.GoodsType;
import com.ande.buyb2c.goods.service.IGoodsTypeService;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:52:00
 */
@Service
public class GoodsTypeServiceImpl extends BaseServiceImpl<GoodsType> implements
		IGoodsTypeService {
@Resource
private GoodsTypeMapper goodsTypeMapper;
	@Override
	protected IBaseDao<GoodsType> getMapper() {
		return goodsTypeMapper;
	}
	@Override
	public List<GoodsType> getGoodsTypeList() {
		//查一级分类
				List<GoodsType> list=goodsTypeMapper.getGoodsTypeList(0);
				getChildGoodsTypeList(list);
				return list;
	}
	public void getChildGoodsTypeList(List<GoodsType> list){
		if(list.size()!=0){
			for(GoodsType type:list){
				List<GoodsType> childList =goodsTypeMapper.getGoodsTypeList(type.getGoodsTypeId());
				type.setChildList(childList);
				getChildGoodsTypeList(childList);
			}
		}
	}
	@Override
	public Integer delGoodsType(String level,Integer goodsTypeId)
			throws Exception {
		if("3".equals(level)){
			goodsTypeMapper.deleteByPrimaryKey(goodsTypeId);
		}else{
			goodsTypeMapper.deleteByPrimaryKey(goodsTypeId);
			goodsTypeMapper.delByParentId(level,goodsTypeId);
		}
		return 1;
	}
	public int updateByPrimaryKeySelective(GoodsType goodsType) throws Exception{
		goodsTypeMapper.updateByPrimaryKeySelective(goodsType);
		String level=goodsType.getGoodsTypeLevel();
		//更新冗余数据
		if("1".equals(level)){
			goodsTypeMapper.updateByParentId(goodsType.getGoodsTypeId(),"1",
					goodsType.getGoodsType());
			
			goodsTypeMapper.updateByParentId(goodsType.getGoodsTypeId(),"2",
					goodsType.getGoodsType());
		}else if("2".equals(level)){
			goodsTypeMapper.updateByParentId(goodsType.getGoodsTypeId(),"2",
					goodsType.getGoodsType());
		}
		return 1;
	}
	@Override
	public List<GoodsType> getGoodsTypeByParentId(Integer parentId) {
		return goodsTypeMapper.getGoodsTypeList(parentId);
	}
}
