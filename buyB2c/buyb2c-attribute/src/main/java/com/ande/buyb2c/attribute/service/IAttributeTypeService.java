package com.ande.buyb2c.attribute.service;

import java.util.List;

import com.ande.buyb2c.attribute.entity.AttributeType;
import com.ande.buyb2c.common.util.IBaseService;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:48:16
 */
public interface IAttributeTypeService extends IBaseService<AttributeType>{
public List<AttributeType> getAttributeTypeList();
public Integer delAttributeType(Integer attributeTypeId,String level)throws Exception ;
/**
 * 级联查询
 */
public List<AttributeType> getAttributeTypeListByParentId(Integer parentId);

}
