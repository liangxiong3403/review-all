package org.liangxiong.review.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-04-02 20:57
 * @description 配置不是必须地;主要用于微服务情况不同模块的服务分组
 **/
@Configuration
@EnableSwagger2
public class Knife4jConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .description("# swagger-bootstrap-ui-demo RESTful APIs")
                        .contact(new Contact("liangxiong", "http://blog.liangxiong.org", "1071608617@qq.com"))
                        .version("1.0")
                        .build())
                //分组名称
                .groupName(applicationName)
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("rg.liangxiong.review.client.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
}
