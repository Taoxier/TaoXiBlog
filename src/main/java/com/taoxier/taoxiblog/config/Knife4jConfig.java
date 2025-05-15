package com.taoxier.taoxiblog.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/15
 */
@Configuration
@EnableKnife4j // 启用 Knife4j
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30) // 使用 OpenAPI 3.0 规范
                .apiInfo(apiInfo())
                .select()
                // 扫描控制器所在包（根据实际路径调整）
                .apis(RequestHandlerSelectors.basePackage("com.taoxier.taoxiblog.controller"))
                .paths(PathSelectors.any()) // 匹配所有路径，也可自定义路径规则
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("TaoXiBlog 接口文档")
                .description("基于 Knife4j 的 RESTful 接口文档")
                .version("1.0.0")
                .build();
    }
}