package com.taoxier.taoxiblog.service.Impl;

import com.taoxier.taoxiblog.config.properties.DeepSeekProperties;
import com.taoxier.taoxiblog.model.dto.AIContentResponseDTO;
import com.taoxier.taoxiblog.service.AIContentService;
import com.taoxier.taoxiblog.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author taoxier
 * @Date 2025/9/25 下午2:24
 * @描述
 */
@Service
public class AIContentServiceImpl implements AIContentService {

    //注入DeepSeek配置
    @Autowired
    private DeepSeekProperties deepSeekProperties;

    //注入RestTemplate
    @Autowired
    private RestTemplate restTemplate;


    /**
    * @描述
    * @param content
    * @return AIContentResponseDTO
    * @Author taoxier
    */
    @Override
    public AIContentResponseDTO polishContent(String content){
        AIContentResponseDTO response=new AIContentResponseDTO();
        if (StringUtils.isEmpty(content)){
            response.setSuccess(false);
            response.setErrorMsg("内容不能为空");
            return response;
        }

        try{
            //构建请求头（认证方式为Bearer Token）
            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization","Bearer "+deepSeekProperties.getKey());

            //构建请求体
            Map<String,Object> requestBody=new HashMap<>();
            requestBody.put("model","deepseek-chat");
            List<Map<String,String>> messages=new ArrayList<>();
            messages.add(Map.of("role","user","content","请修正以下文本的语法错误，并优化排版（合理分段、调整标题层级）：\n"+content));
            requestBody.put("messages",messages);
            //添加温度参数
            requestBody.put("temperature",0.6);

            //发送请求
            HttpEntity<Map<String,Object>> request=new HttpEntity<>(requestBody,headers);

            //调用openai aip
            ResponseEntity<Map> apiResponse=restTemplate.postForEntity(deepSeekProperties.getUrl(),request,Map.class);

            //解析响应
            Map<String,Object> responseBody=apiResponse.getBody();

            if (responseBody!=null && responseBody.containsKey("choices")){
                List<Map<String,Object>> choices=(List<Map<String,Object>>) responseBody.get("choices");
                Map<String,String> message=(Map<String,String>) choices.get(0).get("message");
                response.setSuccess(true);
                response.setResult(message.get("content"));
            }else{
                response.setSuccess(false);
                response.setErrorMsg("未获取到润色结果");
            }
        }catch (Exception e){
            e.printStackTrace();
            response.setSuccess(false);
            response.setErrorMsg("润色失败："+e.getMessage());
        }
        return response;
    }
}
