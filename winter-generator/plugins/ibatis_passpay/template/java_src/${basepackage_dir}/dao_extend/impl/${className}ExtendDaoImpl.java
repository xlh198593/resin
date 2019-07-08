<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
package com.ande.passpay.dao.impl;

import org.springframework.stereotype.Repository;

import com.ande.passpay.common.dao.impl.${className}DaoImpl;
import com.ande.passpay.common.entity.${className};
import com.ande.passpay.dao.I${className}ExtendDao;


@Repository("${classNameLower}ExtendDaoImpl")
public class ${className}ExtendDaoImpl extends ${className}DaoImpl<${className}> implements I${className}ExtendDao {

}

