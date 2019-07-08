<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
package com.ande.passpay.common.dao.impl;


import org.springframework.stereotype.Repository;

import com.ande.passpay.core.base.BaseDaoImpl;
import com.ande.passpay.common.dao.I${className}Dao;
import com.ande.passpay.common.entity.${className};

@Repository("${classNameLower}DaoImpl")
public class ${className}DaoImpl<E extends ${className}> extends BaseDaoImpl<${className}> implements I${className}Dao{

	<#list table.columns as column>
	<#if column.unique && !column.pk>
	public ${className} getBy${column.columnName}(${column.javaType} v) {
		return (${className})sqlSessionTemplate.selectOne("${className}.getBy${column.columnName}",v);
	}	
	</#if>
	</#list>

}
