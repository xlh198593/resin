<#include "/macro.include"/> 
<#include "/custom.include"/> 
<#assign className = table.className>   
<#assign jspClassName = table.jspClassName>  
<#assign classNameLower = className?uncap_first>
<%@ page import="${basepackage}.entity.${className}"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<#macro mapperEl value>${r"${"}${value}}</#macro>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title><%=${className}.TABLE_ALIAS%>信息</title>
	<%@ include file="/common/meta.jsp"%>
	<link href="<@mapperEl 'ctx'/>/css/yui.css" type="text/css" rel="stylesheet" />
	<link href="<@mapperEl 'ctx'/>/css/style.css" type="text/css" rel="stylesheet" />
	<link href="<@mapperEl 'ctx'/>/js/jquery/themes/base/jquery.ui.all.css" type="text/css"	rel="stylesheet" />
	<link href="<@mapperEl 'ctx'/>/js/jquery/validate/jquery.validate.css"	type="text/css" rel="stylesheet" />
	<script src="<@mapperEl 'ctx'/>/js/jquery/jquery-1.4.2.js" type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/jquery/jquery-ui.min.js"	type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/jquery/validate/jquery.validate.js"	type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/jquery/validate/messages_cn.js" type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/table.js" type="text/javascript"></script>
</head>

<body>
<div id="doc">
	<%@ include file="/common/header.jsp"%>
	<div id="bd">
		<div id="yui-main">
			<div class="yui-b">
				<form id="mainForm" action="${jspClassName}.action" method="get">
					<input type="hidden" name="page.pageNo"  id="pageNo"	value="<@mapperEl 'page.pageNo'/>" /> 
					<input type="hidden" name="page.orderBy" id="orderBy" value="<@mapperEl 'page.orderBy'/>" /> 
					<input type="hidden" name="page.order" id="order" value="<@mapperEl 'page.order'/>" />
					
					<div id="message"><s:actionmessage theme="custom"	cssClass="success" /></div>
					<div id="filter">
					<#list table.notDefaultColumns as column> <#if !column.htmlHidden>
					<#if	column.isDateTimeColumn>
						<label for="pageRequest.searchDo.${column.columnNameLower}String"><%=${className}.ALIAS_${column.constantName}%> :</label>		
						<input type="text" id="${column.columnNameLower}String"	name="pageRequest.searchDo.${column.columnNameLower}String"  maxlength="0"	value="<@jspEl 'pageRequest.searchDo.'+column.columnNameLower+'String'/>" class="date-pick" />
					<#else>
						<label for="pageRequest.searchDo.${column.columnNameLower}"><%=${className}.ALIAS_${column.constantName}%>:</label>
						<input type="text"	name="pageRequest.searchDo.${column.columnNameLower}" value="<@jspEl 'pageRequest.searchDo.'+column.columnNameLower/>"/>
					</#if> 
					</#if> <br/></#list>
						<input type="button" value="搜索" onclick="search();"/>
					</div>
					<div id="content">
						<table id="contentTable">
							<tr>
							<#list table.columns as column> <#if !column.htmlHidden>
								<th><a href="javascript:sort('${column.columnNameLower}','asc')"><#if column.isDateTimeColumn><@jspEl 'item.'+column.columnNameLower+"String"/></#if><%=${className}.ALIAS_${column.constantName}%></a></th>
									</#if> </#list>
								<th>操作</th>
							</tr>
						
							<s:iterator value="page.result">
								<tr>
								<#list table.columns as column> 
									<#if !column.htmlHidden>
										<#if column.isDateTimeColumn>
										<td><@mapperEl column.columnNameLower+'String'/>&nbsp;</td>
										<#else> 
										<td><@mapperEl column.columnNameLower/>&nbsp;</td>
										</#if> 
									 </#if> 
							    </#list>
									<td><a href="${jspClassName}!input.action?id=<@mapperEl 'id'/>">查看</a>&nbsp;
									    <a href="${jspClassName}!input.action?id=<@mapperEl 'id'/>">修改</a>&nbsp; 
									    <a href="${jspClassName}!delete.action?id=<@mapperEl 'id'/>" onclick="return confirm('确定要删除吗？')">删除</a></td>
								</tr>
							</s:iterator>
						</table>
					</div>
					<div>
						第<@mapperEl 'page.pageNo'/>页, 共<@mapperEl 'page.totalPages'/>页
						 <a	href="javascript:jumpPage(1)">首页</a> 
						 <s:if test="page.hasPre">
						 <a href="javascript:jumpPage(<@mapperEl 'page.prePage'/>)">上一页</a></s:if> 
						 <s:if test="page.hasNext">
						<a href="javascript:jumpPage(<@mapperEl 'page.nextPage'/>)">下一页</a></s:if> 
						<a href="javascript:jumpPage(<@mapperEl 'page.totalPages'/>)">末页</a> 
						<a href="${jspClassName}!input.action">增加${className}</a>
					</div>
				</form>
			</div>
		</div>
	</div>
	<%@ include file="/common/footer.jsp"%>
</div>
</body>
</html>