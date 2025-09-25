package com.taoxier.taoxiblog.service.Impl;

import com.taoxier.taoxiblog.config.properties.BaiduAiProperties;
import com.taoxier.taoxiblog.service.KeyWordExtractService;
import com.taoxier.taoxiblog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author taoxier
 * @Date 2025/9/26 上午1:23
 * @描述
 */
@Service
public class KeyWordExtractServiceImpl implements KeyWordExtractService {
    @Autowired
    private BaiduAiProperties baiduAiProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    //获取百度token
    private String getAccessToken() {
        String redisKey="baidu_access_token";
        String token=(String) redisService.getObjectByKey(redisKey,String.class);
        if (token!=null){
            return token;
        }

        //缓存未命中
        String tokenUrl=String.format(  "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=%s&client_secret=%s",
                baiduAiProperties.getApiKey(),
                baiduAiProperties.getSecretKey());

        ResponseEntity<Map> response = restTemplate.getForEntity(tokenUrl, Map.class);
        Map<String,Object> responseBody=response.getBody();
        if (responseBody==null||!responseBody.containsKey("access_token")){
            throw new RuntimeException("百度AI接口返回格式错误，未包含access_token");
        }

        //提取令牌并设置缓存（有效期29天）
        String newToken=(String) responseBody.get("access_token");
        redisService.saveObjectToValue(redisKey,newToken);
        redisService.expire(redisKey,29*24*60*60);

        return newToken;
    }

    @Override
    public List<String> extractKeywords(String content, int maxNum){
        //获取访问令牌
        String accessToken=getAccessToken();
        if (accessToken==null){
            throw new RuntimeException("获取百度AI令牌失败");
        }

        //构建请求参数
        Map<String,Object> requestBody=new HashMap<>();
        requestBody.put("text",Collections.singletonList(content));//文章内容
        requestBody.put("num",maxNum);//最大关键词数量

        //发送关键词提取请求
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map> request=new HttpEntity<>(requestBody,headers);

        String requestUrl= baiduAiProperties.getKeywordUrl()+"?access_token="+accessToken;
        ResponseEntity<Map> response=restTemplate.postForEntity(requestUrl,request,Map.class);

        //解析响应结果
        Map<String,Object> responseBody=response.getBody();
        if (responseBody==null||!responseBody.containsKey("results")){
            return Collections.emptyList();
        }

        List<Map<String,Object>> results=(List<Map<String, Object>>) responseBody.get("results");
        return results.stream().map(item -> (String) item.get("word")).collect(Collectors.toList());
    }
}
