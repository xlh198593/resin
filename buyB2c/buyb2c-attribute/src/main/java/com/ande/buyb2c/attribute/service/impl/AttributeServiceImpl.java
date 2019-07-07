package com.ande.buyb2c.attribute.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ande.buyb2c.attribute.dao.AttributeMapper;
import com.ande.buyb2c.attribute.dao.AttributeValMapper;
import com.ande.buyb2c.attribute.entity.Attribute;
import com.ande.buyb2c.attribute.entity.AttributeVal;
import com.ande.buyb2c.attribute.service.IAttributeService;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:46:48
 */
@Service
public class AttributeServiceImpl extends BaseServiceImpl<Attribute>
	implements IAttributeService{
	@Resource
	private AttributeMapper attributeMapper;
	@Resource
	private AttributeValMapper attributeValMapper;
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	@Override
	protected IBaseDao<Attribute> getMapper() {
		return attributeMapper;
	}  
	@Transactional
	public int addAttribute(HttpServletRequest request,Attribute attribute) throws Exception {
		attributeMapper.insertSelective(attribute);
		List<AttributeVal> attributeValList=attribute.getAttributeValList();
		for(AttributeVal val:attributeValList){
			val.setAttributeId(attribute.getAttributeId());
			val.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
		}
		attributeValMapper.addBatch(attributeValList);
		return 1;
	}
	@Transactional
	@Override
	public int updateAttribute(HttpServletRequest request, Attribute attribute) throws Exception {
		attributeMapper.updateByPrimaryKeySelective(attribute);
		List<AttributeVal> attributeValList=attribute.getAttributeValList();
		List<AttributeVal> newAttributeValList=new ArrayList<AttributeVal>();
		List<AttributeVal> updateAttributeValList=new ArrayList<AttributeVal>();
		StringBuffer buffer=new StringBuffer();
		for(AttributeVal val:attributeValList){
			if(val.getAttributeValId()==null){
				//新增
				val.setAttributeId(attribute.getAttributeId());
				val.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
				newAttributeValList.add(val);
			}else if("".equals(val.getAttributeVal())
					||null==val.getAttributeVal()){
				//删除
				buffer.append(val.getAttributeValId()+",");
			}else{
				//修改
				updateAttributeValList.add(val);
			}
		}
		if(newAttributeValList.size()!=0){
			attributeValMapper.addBatch(newAttributeValList);
		}
		if(updateAttributeValList.size()!=0){
			attributeValMapper.updateBatch(updateAttributeValList);
		}
		if(buffer.length()!=0){
			String str=buffer.toString();
			attributeValMapper.delAttribute(str.substring(0,str.length()-1));
		}
		return 1;
	}
	@Override
	public int delAttribute(Integer attributeId) throws Exception {
		attributeMapper.deleteByPrimaryKey(attributeId);
		attributeValMapper.deleteByPrimaryKey(attributeId);
		return 1;
	}
	@Override
	public List<Attribute> getAttributeList(Integer attributeTypeId) {
		return attributeMapper.getAttributeList(attributeTypeId);
	}
}
