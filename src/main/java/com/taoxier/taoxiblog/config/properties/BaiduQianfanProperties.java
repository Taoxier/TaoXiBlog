package com.taoxier.taoxiblog.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author taoxier
 * @Date 2025/10/25 上午2:04
 * @描述
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "baidu.qianfan") // 千帆配置前缀
public class BaiduQianfanProperties {
    private String apiKey;       // 千帆API Key（独立于百度AI开放平台）
    private String embeddingModel; // 向量模型名称，如ernie-text-embedding-v2
}