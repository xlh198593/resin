package com.ande.buyb2c.attribute.service;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ande.buyb2c.attribute.entity.AttributeTypeAttribute;
import com.ande.buyb2c.attribute.vo.AttributeVo;
import com.ande.buyb2c.common.util.IBaseService;

/**
 * @author chengzb
 * @date 2018年1月30日下午2:23:21
 */
public interface IAttributeTypeAttributeService extends IBaseService<AttributeTypeAttribute>{
	public List<AttributeTypeAttribute> ifCanDel(String level,Integer attributeTypeId);
	public List<AttributeTypeAttribute> getAttributeListRight(Integer attributeTypeId);
	
	public Integer addAttributeTypeAttribute(HttpServletRequest request,List<AttributeTypeAttribute> list)throws Exception;
	/**
	 * 添加商品时 选中属性分类查询属性
	 */
	public List<AttributeVo> getAttributeTypeAttributeList(Integer attributeTypeId);
}
