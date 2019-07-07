package com.ande.buyb2c.attribute.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.attribute.entity.AttributeType;
import com.ande.buyb2c.common.util.IBaseDao;

public interface AttributeTypeMapper extends IBaseDao<AttributeType>{
	public List<AttributeType> getAttributeTypeList(Integer attributeTypeId);
	public Integer delByParentId(@Param("attributeTypeId")Integer attributeTypeId,@Param("level")String level);
	public Integer updateByParentId(@Param("attributeTypeId")Integer attributeTypeId,@Param("level")String level,@Param("attributeTypeName")String attributeTypeName);
}