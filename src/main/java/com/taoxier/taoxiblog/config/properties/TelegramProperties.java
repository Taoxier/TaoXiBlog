package com.taoxier.taoxiblog.config.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description ：Telegram配置(目前用不上)
 * @Author taoxier
 * @Date 2025/4/21
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tg.bot")
public class TelegramProperties {

    /**
     * Telegram bot的api，默认是https://api.telegram.org/bot
     * 如果使用自己的反代，可以修改它
     */
    private String api;

    /**
     * bot的token，可以从 @BotFather 处获取
     */
    private String token;

    /**
     * 自己账号和bot的聊天会话id
     */
    private String chatId;

    /**
     * 是否使用代理
     */
    private Boolean userProxy;

    /**
     * 是否使用反向代理
     */
    private Boolean userReverseProxy;

    /**
     * 反向代理URL
     */
    private String reverseProxyUrl;
}
