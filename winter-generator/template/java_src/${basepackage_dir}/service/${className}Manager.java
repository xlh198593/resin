<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ${basepackage}.entity.${className};
import ${basepackage}.dao.${className}Dao;

import com.winterframework.modules.page.Page;
import com.winterframework.modules.page.PageRequest;
import com.winterframework.orm.dal.ibatis3.BaseDao;
import com.winterframework.orm.dal.ibatis3.BaseManager;

<#include "/java_imports.include">
@Component
@Transactional(rollbackFor = Exception.class)
public class ${className}Manager extends BaseManager<${className}Dao,${className}>{

@Override
@Autowired
public void setEntityDao(${className}Dao ${classNameLower}Dao ) {
	this.entityDao=${classNameLower}Dao;
}

}
