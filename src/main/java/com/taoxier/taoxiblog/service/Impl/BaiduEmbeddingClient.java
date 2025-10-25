package com.taoxier.taoxiblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taoxier.taoxiblog.config.properties.BaiduQianfanProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author taoxier
 * @Date 2025/10/25 上午1:53
 * @描述
 */
@Component
public class BaiduEmbeddingClient {

    @Autowired
    private BaiduQianfanProperties qianfanProperties;

    @Resource
    private RestTemplate restTemplate;

    // 千帆向量接口（新地址）
    private static final String API_ENDPOINT = "https://qianfan.baidubce.com/v2/embeddings";

    /**
     * 生成文本的嵌入向量（适配千帆v2接口）
     */
    public List<Double> getEmbedding(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("嵌入文本不能为空");
        }
        String processedContent = truncateContentByToken(content.trim());

        // 构建请求头（仅需API Key鉴权）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + qianfanProperties.getApiKey()); // 直接使用API Key

        // 构建请求体（符合v2接口格式）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", qianfanProperties.getEmbeddingModel()); // 指定模型

        // 直接将文本字符串放入列表，无需包装为{"text": "..."}
        List<String> inputList = new ArrayList<>();
        inputList.add(processedContent); // processedContent是过滤后的文本字符串
        requestBody.put("input", inputList);
//        System.out.println(inputList);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(API_ENDPOINT, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("千帆API请求失败：" + e.getMessage(), e);
        }

        // 解析响应
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException("请求失败，状态码：" + response.getStatusCodeValue());
        }
        String responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("响应内容为空");
        }

        JSONObject responseJson = JSON.parseObject(responseBody);
        if (responseJson.containsKey("error")) {
            String errorMsg = responseJson.getString("error");
            throw new RuntimeException("千帆接口错误：" + errorMsg);
        }

        // 提取向量（v2接口返回格式）
        JSONArray embeddings = responseJson.getJSONArray("data");
        if (embeddings.isEmpty()) {
            throw new RuntimeException("未返回向量数据");
        }
        JSONArray embeddingArray = embeddings.getJSONObject(0).getJSONArray("embedding");
        return embeddingArray.toJavaList(Double.class);
    }

    private String truncateContentByToken(String content) {
        // 过滤不可见字符和特殊符号
        String filtered = content.replaceAll("[\\x00-\\x1F\\x7F\\p{C}]", " ");
        // 保留中文、英文、数字和常见标点
        filtered = filtered.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9,.?!，。？！；;]", " ");
        // 去重空格
        filtered = filtered.replaceAll("\\s+", " ").trim();

        // 原有截断逻辑
        int MAX_TOKEN = 1000; // 临时缩短测试
        double TOKEN_COEFFICIENT = 1.5;
        int estimatedToken = (int) (filtered.length() * TOKEN_COEFFICIENT);
        if (estimatedToken <= MAX_TOKEN) {
            return filtered;
        }
        int maxCharLength = (int) (MAX_TOKEN / TOKEN_COEFFICIENT);
        return filtered.substring(0, maxCharLength) + "...";
    }
}