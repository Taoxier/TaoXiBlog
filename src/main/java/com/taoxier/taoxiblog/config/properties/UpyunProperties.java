package com.taoxier.taoxiblog.config.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description ：又拍云配置(目前用于评论中QQ头像的图床)
 * @Author taoxier
 * @Date 2025/4/21
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload.upyun")
public class UpyunProperties {

    /**
     * 又拍云存储空间名称
     */
    private String bucketName;

    /**
     * 操作员名称
     */
    private String username;

    /**
     * 操作员密码
     */
    private String password;

    /**
     * 存储路径
     */
    private String path;

    /**
     * CDN访问域名
     */
    private String domain;
}
