package com.ande.buyb2c.attribute.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.attribute.dao.AttributeTypeMapper;
import com.ande.buyb2c.attribute.entity.AttributeType;
import com.ande.buyb2c.attribute.service.IAttributeTypeService;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:48:45
 */
@Service
public class AttributeTypeServiceImpl extends BaseServiceImpl<AttributeType> implements IAttributeTypeService{
@Resource
private AttributeTypeMapper attributeTypeMapper;
	@Override
	protected IBaseDao<AttributeType> getMapper() {
		return attributeTypeMapper;
	}
	@Override
	public List<AttributeType> getAttributeTypeList() {
		//查一级分类
		List<AttributeType> list=attributeTypeMapper.getAttributeTypeList(0);
		getChildAttributeTypeList(list);
		return list;
	}
	public void getChildAttributeTypeList(List<AttributeType> list){
		if(list.size()!=0){
			for(AttributeType type:list){
				List<AttributeType> childList =attributeTypeMapper.getAttributeTypeList(type.getAttributeTypeId());
				type.setChildList(childList);
				getChildAttributeTypeList(childList);
			}
		}
	}
	@Override
	public Integer delAttributeType(Integer attributeTypeId,String level) throws Exception {
		if("3".equals(level)){
			attributeTypeMapper.deleteByPrimaryKey(attributeTypeId);
		}else{
			attributeTypeMapper.deleteByPrimaryKey(attributeTypeId);
			attributeTypeMapper.delByParentId(attributeTypeId, level);
		}
		return 1;
	}
	public int updateByPrimaryKeySelective(AttributeType attributeType) throws Exception{
		attributeTypeMapper.updateByPrimaryKeySelective(attributeType);
		String level=attributeType.getAttributeTypeLevel();
		//更新冗余数据
		if("1".equals(level)){
			attributeTypeMapper.updateByParentId(attributeType.getAttributeTypeId(),"1",
					attributeType.getAttributeTypeName());
			attributeTypeMapper.updateByParentId(attributeType.getAttributeTypeId(),"2",
					attributeType.getAttributeTypeName());
		}else if("2".equals(level)){
			attributeTypeMapper.updateByParentId(attributeType.getAttributeTypeId(),"2",
					attributeType.getAttributeTypeName());
		}
		return 1;
	}
	@Override
	public List<AttributeType> getAttributeTypeListByParentId(
			Integer parentId) {
		return attributeTypeMapper.getAttributeTypeList(parentId);
	}
}
