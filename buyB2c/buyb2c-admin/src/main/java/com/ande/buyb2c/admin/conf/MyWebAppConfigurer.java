package com.ande.buyb2c.admin.conf;
/**
 * @author chengzb
 * @date 2018年1月31日下午6:08:45
 */

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.ande.buyb2c.admin.interceptor.WebInterceptor;

@Configuration
public class MyWebAppConfigurer 
        extends WebMvcConfigurerAdapter {
@Resource
private WebInterceptor webInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(webInterceptor).addPathPatterns("/admin/**");
        super.addInterceptors(registry);
    }
}

