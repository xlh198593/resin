package com.ande;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author chengzb
 * @date 2018年2月24日上午10:03:36
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.ande.buyb2c.**.dao")
public class JobManagementApplication extends SpringBootServletInitializer{

     @Override
protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
            return application.sources(JobManagementApplication.class);
        }
    public static void main(String[] args) {
        SpringApplication.run(JobManagementApplication.class, args);
    }
}
