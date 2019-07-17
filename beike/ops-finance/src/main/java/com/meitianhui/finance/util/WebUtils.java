package com.meitianhui.finance.util;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * 将参数组装成html页面
 * @author Tiny
 *
 */
public abstract class WebUtils {

    public static String buildForm(String baseUrl, Map<String, String> parameters) {
        java.lang.StringBuffer sb = new StringBuffer();
        sb.append("<form name=\"punchout_form\" method=\"post\" action=\"");
        sb.append(baseUrl);
        sb.append("\">\n");
        sb.append(buildHiddenFields(parameters));

        sb.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n");
        sb.append("</form>\n");
        sb.append("<script>document.forms[0].submit();</script>");
        java.lang.String form = sb.toString();
        return form;
    }
    

    private static String buildHiddenFields(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        java.lang.StringBuffer sb = new StringBuffer();
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            String value = parameters.get(key);
            // 除去参数中的空值
            if (key == null || value == null) {
                continue;
            }
            sb.append(buildHiddenField(key, value));
        }
        java.lang.String result = sb.toString();
        return result;
    }

    private static String buildHiddenField(String key, String value) {
        java.lang.StringBuffer sb = new StringBuffer();
        sb.append("<input type=\"hidden\" name=\"");
        sb.append(key);

        sb.append("\" value=\"");
        //转义双引号
        String a = value.replace("\"", "&quot;");
        sb.append(a).append("\">\n");
        return sb.toString();
    }
    
    
    public static String getRealIp(HttpServletRequest  request) {
    	String ip = request.getHeader("x-forwarded-for");
    	if(ip ==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)) {
    		ip =  request.getHeader("Proxy-Client-IP");
    	}
    	if(ip ==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)) {
    		ip =  request.getHeader("WL-Proxy-Client-IP");
    	}
    	if(ip ==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)) {
    		ip =  request.getRemoteAddr();
    	}
    	return ip;
    }
    
}
