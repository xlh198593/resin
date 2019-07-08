<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
package com.ande.passpay.common.dao;

<#include "/java_imports.include">

import com.ande.passpay.core.base.IBaseDao;
import com.ande.passpay.common.entity.${className};

public interface I${className}Dao extends IBaseDao<${className}>{	
	
	<#list table.columns as column>
	<#if column.unique && !column.pk>
	public ${className} getBy${column.columnName}(${column.javaType} v) ;
	</#if>
	</#list>

}
