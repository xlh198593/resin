<#include "/macro.include"/> <#include "/custom.include"/> <#assign
className = table.className> <#assign classNameLower
=className?uncap_first> <#assign actionExtension = "do"> <#assign
jspClassName = table.jspClassName> <#macro mapperEl
value>${r"${"}${value}}</#macro> <#macro mapperJQ
value>${r"$("}${value})</#macro>
<%@page import="${basepackage}.entity.${className}"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title><%=${className}.TABLE_ALIAS%>新增</title>
	<%@ include file="/common/meta.jsp"%>
	<link href="<@mapperEl 'ctx'/>/css/yui.css" type="text/css" rel="stylesheet" />
	<link href="<@mapperEl 'ctx'/>/css/style.css" type="text/css" rel="stylesheet" />
	<link href="<@mapperEl 'ctx'/>/js/jquery/themes/base/jquery.ui.all.css" type="text/css"	rel="stylesheet" />
	<link href="<@mapperEl 'ctx'/>/js/jquery/validate/jquery.validate.css"	type="text/css" rel="stylesheet" />
	<script src="<@mapperEl 'ctx'/>/js/jquery/jquery-1.4.2.js" type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/jquery/ui/jquery.ui.core.js"	type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/jquery/ui/jquery.ui.widget.js"	type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/jquery/ui/jquery.ui.datepicker.js"	type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/jquery/validate/jquery.validate.js"	type="text/javascript"></script>
	<script src="<@mapperEl 'ctx'/>/js/jquery/validate/messages_cn.js" type="text/javascript"></script>
	<script>
	$(document).ready(function() {
		$(".date-pick").datepicker();
		//聚焦第一个输入框
		$("#username").focus();
		//为inputForm注册validate函数
		$("#inputForm").validate({
			rules: {
				username: "required"
		 },
		 messages:{
			}
		});
	});
	</script>
</head>

<body>
<div id="doc">
	<%@ include file="/common/header.jsp"%>
	<div id="bd">
		<div id="yui-main">
			<div class="yui-b">
					<h2><s:if test="id == null">创建</s:if><s:else>修改</s:else>${className}</h2>
					<form id="inputForm" action="${jspClassName}!save.action" method="post">
						<input type="hidden" name="id" value="<@mapperEl 'id'/>" />
						<table  class="noborder">
							<!-- ONGL access static field: @package.class@field or @vs@field -->
							<#list table.notDefaultColumns as column>
							<#if !column.htmlHidden> 
							<#if column.isDateTimeColumn>
							<tr>
								<td><label for="${column.columnNameLower}String"><%=${className}.ALIAS_${column.constantName}%></label></td>
								<td><input type="text" id="${column.columnNameLower}String"	name="${column.columnNameLower}String" maxlength="0" value="<@jspEl 'model.'+column.columnNameLower+'String'/>"	class="date-pick" /> </td>
							</tr>
							<#elseif column.isBooleanColumn>
							<tr>
								<td><label for="${column.columnNameLower}String"><%=${className}.ALIAS_${column.constantName}%></label></td>
								<td><input type="text" id="${column.columnNameLower}String"	name="${column.columnNameLower}String" maxlength="0" value="<@jspEl 'model.'+column.columnNameLower+'String'/>"	class="date-pick" /> </td>
							</tr>
							<#else>
							<tr>
								<td><label for="${column.columnNameLower}"><%=${className}.ALIAS_${column.constantName}%></label></td>
								<td><input type="text" id="${column.columnNameLower}"	name="${column.columnNameLower}" maxlength="${column.size}" value="<@jspEl 'model.'+column.columnNameLower/>"/> </td>
							</tr>
							</#if> 
							</#if>
							</#list>
							<tr>
								<td colspan="2"><input class="button" type="submit" value="提交" />&nbsp;
								<input class="button" type="button" value="返回"
									onclick="history.back()" /></td>
							</tr>
						</table>
					</form>
			</div>
		</div>
	</div>
</div>
</body>
</html>