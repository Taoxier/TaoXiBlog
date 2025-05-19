//package com.taoxier.taoxiblog.config;
//
//import com.taoxier.taoxiblog.config.properties.ProxyProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//
///**
// * @Description ：RestTemplate相关的Bean配置
// * @Author taoxier
// * @Date 2025/4/22
// */
//@Configuration
//public class RestTemplateConfig {
//
//    @Autowired
//    private ProxyProperties proxyProperties;
//
//    /**
//    * @Description 默认的RestTemplate
//     * @param
//    * @Author: taoxier
//    * @Date: 2025/5/9
//    * @Return: org.springframework.web.client.RestTemplate
//    */
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//
//    /**
//     * @param
//     * @Description 配置了代理和超时时间的RestTemplate
//     * @Author: taoxier
//     * @Date: 2025/4/22
//     * @Return: org.springframework.web.client.RestTemplate
//     */
//    @Bean
//    public RestTemplate redisTemplateByProxy() {
//        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort()));
//        requestFactory.setProxy(proxy);
//        requestFactory.setConnectTimeout(proxyProperties.getTimeout());
//        return new RestTemplate(requestFactory);
//    }
//}
