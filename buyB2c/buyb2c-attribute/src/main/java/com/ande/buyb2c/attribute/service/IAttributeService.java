package com.ande.buyb2c.attribute.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ande.buyb2c.attribute.entity.Attribute;
import com.ande.buyb2c.common.util.IBaseService;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:46:27
 */
public interface IAttributeService extends IBaseService<Attribute> {
	public int addAttribute(HttpServletRequest request,Attribute attribute) throws Exception;
	public int updateAttribute(HttpServletRequest request, Attribute attribute)throws Exception;
	
	public int delAttribute(Integer attributeId) throws Exception;
	public List<Attribute> getAttributeList(Integer attributeTypeId);
}
