package com.taoxier.taoxiblog.util.telegram;

import com.taoxier.taoxiblog.config.properties.BlogProperties;
import com.taoxier.taoxiblog.config.properties.TelegramProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplate restTemplateByProxy;
    @Autowired
    private TelegramProperties telegramProperties;
    @Autowired
    private BlogProperties blogProperties;

    public static final String SET_WEBHOOK = "/setWebhook";
    public static final String SEND_MESSAGE = "/sendMessage";

    private static final String PARSE_MODE = "HTML";

    /**
    * @Description 设置Webhook监听地址
     * @param
    * @Author: taoxier
    * @Date:
    * @Return: void
    */
    public void setWebhook() {
        log.info("start setWebhook");

        String url = telegramProperties.getApi() + telegramProperties.getToken() + SET_WEBHOOK;

        //与 TelegramBotController#getUpdate 请求地址一致
        String webhook = blogProperties.getApi() + "/tg/" + telegramProperties.getToken();

        Map<String, Object> data = new HashMap<>(2);
        data.put("url", webhook);

        sendByAutoCheckReverseProxy(url, data);
    }

    /**
    * @Description 构建POST消息的body
     * @param content
    * @Author: taoxier
    * @Date:
    * @Return: java.util.Map<java.lang.String,java.lang.Object>
    */
    public Map<String, Object> getMessageBody(String content) {
        Map<String, Object> body = new HashMap<>(4);
        body.put("chat_id", telegramProperties.getChatId());
        body.put("parse_mode", PARSE_MODE);
        body.put("text", content);
        return body;
    }

    /**
    * @Description 根据配置检查是否通过反代发送请求,发生异常时重试
     * @param url
     * @param data
    * @Author: taoxier
    * @Date:
    * @Return: void
    */
    @Retryable(
            include = {RestClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000L, multiplier = 2)
    )
    @Async
    public void sendByAutoCheckReverseProxy(String url, Map<String, Object> data) {
        if (telegramProperties.getUserReverseProxy()) {
            send2ReverseProxy(url, data);
        } else {
            send2TelegramApi(url, data);
        }
    }

    /**
    * @Description 使用TelegramAPI发送消息
     * @param url
     * @param data
    * @Author: taoxier
    * @Date:
    * @Return: void
    */
    private void send2TelegramApi(String url, Map<String, Object> data) {
        HttpEntity httpEntity = new HttpEntity(data);
        ResponseEntity<String> response = sendPostRequest(url, httpEntity, String.class);
        log.info("send2TelegramApi => url: {}", url);
        log.info("send2TelegramApi => response: {}", response);
    }

    /**
    * @Description 通过反向代理发送消息
     * @param url
     * @param data
    * @Author: taoxier
    * @Date:
    * @Return: void
    */
    private void send2ReverseProxy(String url, Map<String, Object> data) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("User-Agent", "");

        Map<String, Object> body = new HashMap<>(4);
        body.put("to", url);
        body.put("data", data);
        HttpEntity httpEntity = new HttpEntity(body, headers);

        ResponseEntity<String> response = sendPostRequest(telegramProperties.getReverseProxyUrl(), httpEntity, String.class);
        log.info("send2ReverseProxy => reverseProxyUrl: {}", telegramProperties.getReverseProxyUrl());
        log.info("send2ReverseProxy => destUrl: {}", url);
        log.info("send2ReverseProxy => response: {}", response);
    }

    /**
    * @Description 根据配置是否启用代理发送请求
     * @param url
     * @param httpEntity
     * @param responseType
    * @Author: taoxier
    * @Date:
    * @Return: org.springframework.http.ResponseEntity<T>
    */
    private <T> ResponseEntity<T> sendPostRequest(String url, HttpEntity httpEntity, Class<T> responseType) {
        if (telegramProperties.getUserProxy()) {
            log.info("sendPostRequest => use proxy");
            return restTemplateByProxy.postForEntity(url, httpEntity, responseType);
        }
        log.info("sendPostRequest => unused proxy");
        return restTemplate.postForEntity(url, httpEntity, responseType);
    }
}
