package com.taoxier.taoxiblog.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author taoxier
 * @Date 2025/9/26 上午1:20
 * @描述
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "baidu.ai")
public class BaiduAiProperties {
    private String apiKey;
    private String secretKey;
    private String keywordUrl;
}
