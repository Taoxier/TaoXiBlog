package com.taoxier.taoxiblog.config.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description ： 静态文件上传访问路径配置(目前用于评论中QQ头像的本地存储)
 * @Author taoxier
 * @Date 2025/4/21
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload.file")
public class UploadProperties {

    /**
     * 本地文件路径
     */
    private String path;

    /**
     * 请求地址映射
     */
    private String accessPath;

    /**
     * 本地文件路径映射
     */
    private String resourcesLocations;
}
