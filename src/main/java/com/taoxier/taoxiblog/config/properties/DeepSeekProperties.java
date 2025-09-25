package com.taoxier.taoxiblog.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author taoxier
 * @Date 2025/9/26 上午12:11
 * @描述
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek.api")//配置文件前缀
public class DeepSeekProperties {

    private String key;

    private String url;
}
