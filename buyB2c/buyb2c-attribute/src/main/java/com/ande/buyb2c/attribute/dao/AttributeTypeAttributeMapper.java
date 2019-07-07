package com.ande.buyb2c.attribute.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.attribute.entity.AttributeTypeAttribute;
import com.ande.buyb2c.attribute.vo.AttributeVo;
import com.ande.buyb2c.common.util.IBaseDao;

public interface AttributeTypeAttributeMapper extends IBaseDao<AttributeTypeAttribute>{
	public List<AttributeTypeAttribute> ifCanDel(@Param("level")String level,@Param("attributeTypeId")Integer attributeTypeId);
	public List<AttributeTypeAttribute> getAttributeListRight(
			Integer attributeTypeId);
	
	public Integer addBatch(List<AttributeTypeAttribute> list)throws Exception;
	public Integer delBatch(@Param("attributeTypeAttributeIds")String attributeTypeAttributeIds)throws Exception;
	
	public List<AttributeVo> getAttributeTypeAttributeList(Integer attributeTypeId);
}