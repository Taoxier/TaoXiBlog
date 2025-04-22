package com.taoxier.taoxiblog.util.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * @Description ：Telegram消息工具类
 * @Author taoxier
 * @Date 2025/4/22
 */
@Slf4j
@EnableRetry
@EnableAsync
@Lazy
@Component
public class TelegramUtils {
}
