package com.ande;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.ande.buyb2c.**.dao")
public class SpringbootApplication extends SpringBootServletInitializer{
    @Override
protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
           return application.sources(SpringbootApplication.class);
       }
   public static void main(String[] args) {
       SpringApplication.run(SpringbootApplication.class, args);
   }
}
