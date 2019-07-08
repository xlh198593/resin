<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
package ${basepackage}.dao.unit;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;
import static junit.framework.Assert.*;

<#include "/java_imports.include">

public class ${className}DaoTest extends BaseDaoTestCase{
	@Autowired
	private ${className}Dao ${classNameLower}Dao;
	
	@Override
	protected String[] getDbUnitDataFiles() {
		return new String[]{"classpath:common_testdata.xml","classpath:${className}_testdata.xml"};
	}
	
	@Test
	public void findByPageRequest() {
		int pageNumber = 1;
		int pageSize = 10;
		${className} obj = new ${className}();
		
		<#list table.columns as column>
	  		<#if column.isNotIdOrVersionField>
	  			<#if column.isDateTimeColumn>
	  	obj.set${column.columnName}(new ${column.javaType}(System.currentTimeMillis()));
	  			<#else>
	  	obj.set${column.columnName}(new ${column.javaType}("1"));
	  			</#if>
			</#if>
		</#list>
		
		PageRequest<${className}> pageRequest = new PageRequest<${className}>(obj);
		pageRequest.setPageNumber(pageNumber);
		pageRequest.setPageSize(pageSize);
		pageRequest.setSortColumns(null);
		
		
		
		Page page = ${classNameLower}Dao.getAllByPage(pageRequest);
		
		assertEquals(pageNumber,page.getPageNo());
		assertEquals(pageSize,page.getPageSize());
		List resultList = (List)page.getResult();
		assertNotNull(resultList);		
	}
	
}
