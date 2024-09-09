package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author xyzZero3
 * @date 2024/9/8 23:22
 */
@Configuration
public class Knife4jConfiguration {

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
    }

    @Bean
    public Docket adminApiConfig() {
        Docket adminConfig = new Docket(DocumentationType.SWAGGER_2)
                .groupName("后台管理接口")
                .apiInfo(apiInfo())
                .select()
                //只显示admin路径下的页面
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller"))
                .paths(PathSelectors.regex("/admin/.*"))
                .build();
        return adminConfig;
    }

    @Bean
    public Docket userApiConfig() {
        Docket adminConfig = new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端管理接口")
                .apiInfo(apiInfo())
                .select()
                //只显示user路径下的页面
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller"))
                .paths(PathSelectors.regex("/user/.*"))
                .build();
        return adminConfig;
    }

}
