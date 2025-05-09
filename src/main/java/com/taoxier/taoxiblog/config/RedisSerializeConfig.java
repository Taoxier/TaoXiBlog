package com.taoxier.taoxiblog.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Description ：Redis序列化配置
 * @Author taoxier
 * @Date 2025/4/22
 */
@Configuration
public class RedisSerializeConfig {

    /**
    * @Description 使用JSON序列化方式
     * @param redisConnectionFactory
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: org.springframework.data.redis.core.RedisTemplate<java.lang.Object,java.lang.Object>
    */
    @Bean
    public RedisTemplate<Object,Object> jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object,Object> template=new RedisTemplate<>();
        //将传入的 redisConnectionFactory 设置为该 RedisTemplate 的连接工厂
        template.setConnectionFactory(redisConnectionFactory);
        // JSON 格式的序列化器
        Jackson2JsonRedisSerializer<Object> serializer=new Jackson2JsonRedisSerializer<Object>(Object.class);
        //将创建的 Jackson2JsonRedisSerializer 设置为 RedisTemplate 的默认序列化器
        template.setDefaultSerializer(serializer);
        return template;
    }

//    @Bean
//    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<Object, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//
//        // JSON 序列化器
//        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        serializer.setObjectMapper(mapper);
//
//        // 设置序列化器
//        template.setDefaultSerializer(serializer);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        return template;
//    }
}
