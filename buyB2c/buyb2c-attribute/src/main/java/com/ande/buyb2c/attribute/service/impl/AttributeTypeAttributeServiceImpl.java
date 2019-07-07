package com.ande.buyb2c.attribute.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ande.buyb2c.attribute.dao.AttributeTypeAttributeMapper;
import com.ande.buyb2c.attribute.entity.AttributeTypeAttribute;
import com.ande.buyb2c.attribute.service.IAttributeTypeAttributeService;
import com.ande.buyb2c.attribute.vo.AttributeVo;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月30日下午2:24:17
 */
@Service
public class AttributeTypeAttributeServiceImpl extends
		BaseServiceImpl<AttributeTypeAttribute> implements
		IAttributeTypeAttributeService {
@Resource
private AttributeTypeAttributeMapper attributeTypeAttributeMapper;
@Resource
private SessionUtil<AdminUser> sessionUtil;
	@Override
	protected IBaseDao<AttributeTypeAttribute> getMapper() {
		return attributeTypeAttributeMapper;
	}
	@Override
	public List<AttributeTypeAttribute> ifCanDel(String level, Integer attributeTypeId) {
		return attributeTypeAttributeMapper.ifCanDel(level, attributeTypeId);
	}
	@Override
	public List<AttributeTypeAttribute> getAttributeListRight(
			Integer attributeTypeId) {
		return attributeTypeAttributeMapper.getAttributeListRight(attributeTypeId);
	}
	@Transactional
	@Override
	public Integer addAttributeTypeAttribute(HttpServletRequest request,List<AttributeTypeAttribute> list) throws Exception{
		StringBuffer buffer=new StringBuffer();
		List<AttributeTypeAttribute> newList=new ArrayList<AttributeTypeAttribute>();
		for(AttributeTypeAttribute ata:list){
			if(ata.getAttributeTypeAttributeId()!=null){
				//删除
				if(StringUtils.isEmpty(ata.getAttributeName())){
					buffer.append(ata.getAttributeTypeAttributeId()+",");
				}
			}else{
				ata.setCreateTime(new Date());
				ata.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
				newList.add(ata);
			}
		}
		if(newList.size()!=0){
			attributeTypeAttributeMapper.addBatch(newList);
		}
		if(buffer.length()!=0){
			String str=buffer.toString();
			attributeTypeAttributeMapper.delBatch(str.substring(0,str.length()-1));
		}
		return 1;
	}
	@Override
	public List<AttributeVo> getAttributeTypeAttributeList(
			Integer attributeTypeId) {
		return attributeTypeAttributeMapper.getAttributeTypeAttributeList(attributeTypeId);
	}

}
