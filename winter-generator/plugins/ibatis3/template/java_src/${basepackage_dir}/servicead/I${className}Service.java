<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
package com.ande.passpay.common.service;

import com.ande.passpay.core.base.IBaseService;
import com.ande.passpay.common.entity.${className};

<#include "/java_imports.include">


public interface I${className}Service extends IBaseService<${className}>
{

}