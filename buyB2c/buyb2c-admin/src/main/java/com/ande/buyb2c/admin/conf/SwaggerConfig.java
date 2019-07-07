package com.ande.buyb2c.admin.conf;

import io.swagger.annotations.ApiOperation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author chengzb
 * @date 2018年3月14日下午2:41:48
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig{

    @Bean
    public Docket createRestApi() {
    	/*new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
      .apis(RequestHandlerSelectors.basePackage("com.ande.buyb2c.admin.controller"))
       .paths(PathSelectors.any())
      .build();*/
        
        
         Docket docket= new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
         .select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).build();
         return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful API")
                .description("rest api 文档构建利器")
                .termsOfServiceUrl("http://blog.csdn.net/itguangit")
                .contact("itguang")
                .version("1.0")
                .build();
    }

}
