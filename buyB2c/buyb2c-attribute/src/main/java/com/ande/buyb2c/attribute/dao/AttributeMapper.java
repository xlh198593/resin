package com.ande.buyb2c.attribute.dao;

import java.util.List;

import com.ande.buyb2c.attribute.entity.Attribute;
import com.ande.buyb2c.common.util.IBaseDao;

public interface AttributeMapper extends IBaseDao<Attribute>{
	public List<Attribute> getAttributeList(Integer attributeTypeId);
}