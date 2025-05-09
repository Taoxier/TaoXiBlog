package com.taoxier.taoxiblog.config;

import com.taoxier.taoxiblog.config.properties.UploadProperties;
import com.taoxier.taoxiblog.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description ：配置CORS跨域支持、拦截器
 * @Author taoxier
 * @Date 2025/4/22
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    AccessLimitInterceptor accessLimitInterceptor;
    @Autowired
    UploadProperties uploadProperties;

    /**
    * @Description  跨域请求
     * @param registry
    * @Author: taoxier
    * @Date: 2025/5/9
    * @Return: void
    */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .maxAge(3600);
    }

    /**
     *
     *
     * @param registry
     */
    /**
    * @Description 请求拦截器
     * @param registry
    * @Author: taoxier
    * @Date: 2025/5/9
    * @Return: void
    */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);
    }

    /**
    * @Description 本地静态资源路径映射
     * @param registry
    * @Author: taoxier
    * @Date: 2025/5/9
    * @Return: void
    */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uploadProperties.getAccessPath()).addResourceLocations(uploadProperties.getResourcesLocations());
    }
}
