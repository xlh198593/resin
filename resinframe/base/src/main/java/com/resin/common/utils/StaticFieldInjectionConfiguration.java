package com.resin.common.utils;

import javax.annotation.PostConstruct;

import com.resin.daos.ConfigDao;
import com.resin.jms.JMSTool;
import com.resin.jpa.JPAListener;
import com.resin.tool.ConfigUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

/**
 * 工具类的注入
 */
@Configuration
public class StaticFieldInjectionConfiguration {

    @Autowired
    MessageSource resources;

    @Autowired
    ConfigDao configDao;

    @Autowired
    JMSTool jmsTool;

    @PostConstruct
    private void init() {
        System.out.println("\n\n-----StaticFieldInjectionConfiguration----\n\n");

        CheckUtil.setResources(resources);
        ConfigUtil.setConfigDao(configDao);

        JPAListener.setJmsTool(jmsTool);
    }
}