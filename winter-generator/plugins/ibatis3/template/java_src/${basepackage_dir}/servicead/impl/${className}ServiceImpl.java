<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
package com.ande.passpay.common.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ande.passpay.core.base.BaseServiceImpl;
import com.ande.passpay.common.service.I${className}Service;
import javax.annotation.Resource;
import com.ande.passpay.common.dao.I${className}Dao;
import com.ande.passpay.common.entity.${className};

@Service("${classNameLower}ServiceImpl")
@Transactional(rollbackFor=Exception.class)
public class ${className}ServiceImpl extends  BaseServiceImpl<I${className}Dao,${className}> implements I${className}Service
{

    @Resource(name="${classNameLower}DaoImpl")
    I${className}Dao ${classNameLower}DaoImpl;

	@Override
	protected I${className}Dao getEntityDao() {
		// TODO Auto-generated method stub
		return ${classNameLower}DaoImpl;
	}
}