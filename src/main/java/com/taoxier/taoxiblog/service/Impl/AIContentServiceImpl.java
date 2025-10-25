package com.taoxier.taoxiblog.service.Impl;

import com.taoxier.taoxiblog.config.properties.DeepSeekProperties;
import com.taoxier.taoxiblog.model.dto.AIContentRequestDTO;
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
    * @描述 AI润色
    * @param content
    * @return AIContentResponseDTO
    * @Author taoxier
    */
    @Override
    public AIContentResponseDTO polishContent(String content){

        if (StringUtils.isEmpty(content)){
            AIContentResponseDTO response=new AIContentResponseDTO();
            response.setSuccess(false);
            response.setErrorMsg("内容不能为空");
            return response;
        }

        String userPrompt="请修正以下文本的语法错误，并优化排版（合理分段、调整标题层级）。如果输入了其他指令，也请按照指令生成：\n"+content;

        return executeAiRequest(userPrompt,null);

    }

    /**
    * @描述  ai生成描述
    * @param content
    * @return AIContentResponseDTO
    * @Author taoxier
    */
    @Override
    public AIContentResponseDTO generateDescription(String content) {

        if (StringUtils.isEmpty(content)){
            AIContentResponseDTO response=new AIContentResponseDTO();
            response.setSuccess(false);
            response.setErrorMsg("文章正文不能为空");
            return response;
        }

       String userPrompt="请根据以下文章正文，生成一句简短的描述（50-100字，概括核心内容）：\n"+content;
        Map<String,Object> extraParams=new HashMap<>();
        extraParams.put("max_tokens",200);//限制描述长度
        //token------ AI 模型（如 DeepSeek、OpenAI）处理文本时的最小语义单位,对于中文文本，200 个 token ≈ 300 个中文字符 ≈ 100-150 个中文词语

        return executeAiRequest(userPrompt,extraParams);
    }

    /**
    * @描述  通用ai请求执行方法
    * @param userPrompt
    * @param extraParams
    * @return AIContentResponseDTO
    * @Author taoxier
    */
    private AIContentResponseDTO executeAiRequest(String userPrompt,Map<String,Object> extraParams){
        AIContentResponseDTO response=new AIContentResponseDTO();
        if (StringUtils.isEmpty(userPrompt)){
            response.setSuccess(false);
            response.setErrorMsg("AI请求提示词不能为空");
            return response;
        }

        try {
            //构建请求头
            HttpHeaders headers=buildCommonHeaders();

            //构建请求头
            Map<String,Object> requestBody=buildCommonRequestBody(userPrompt,extraParams);

            //发送请求
            HttpEntity<Map<String,Object>> request=new HttpEntity<>(requestBody,headers);
            ResponseEntity<Map> apiResponse=restTemplate.postForEntity(deepSeekProperties.getUrl(),request,Map.class);

            //通用响应解析
            parseAIResponse(apiResponse,response);

        }catch (Exception e){
            e.printStackTrace();
            response.setSuccess(false);
            response.setErrorMsg("AI请求失败："+e.getMessage());
        }
        return response;
    }

    /**
    * @描述  构建通用请求头
    * @param
    * @return HttpHeaders
    * @Author taoxier
    */
    private HttpHeaders buildCommonHeaders(){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","Bearer "+deepSeekProperties.getKey());
        return headers;
    }

    /**
    * @描述  构建通用请求体：固定参数+差异化参数
    * @param userPrompt
    * @param extraParams
    * @return Map<String,Object>
    * @Author taoxier
    */
    private Map<String,Object> buildCommonRequestBody(String userPrompt,Map<String,Object> extraParams){
        Map<String,Object> requestBody=new HashMap<>();
        //固定参数
        requestBody.put("model","deepseek-chat");
        requestBody.put("temperature",0.6);

        //差异化参数1：用户提示词
        List<Map<String,String>> messages=new ArrayList<>();
        messages.add(Map.of("role","user","content",userPrompt));
        requestBody.put("messages",messages);

        //差异化参数2：额外参数
        if (extraParams!=null&&!extraParams.isEmpty()){
            requestBody.putAll(extraParams);
        }
        return requestBody;
    }

    /**
    * @描述  通用响应解析
    * @param apiResponse
    * @param responseDTO
    * @return void
    * @Author taoxier
    */
    private void parseAIResponse(ResponseEntity<Map> apiResponse,AIContentResponseDTO responseDTO){
        Map<String,Object> responseBody=apiResponse.getBody();
        if (responseBody!=null&&responseBody.containsKey("choices")){
            List<Map<String,Object>> choices=(List<Map<String, Object>>) responseBody.get("choices");
            Map<String,String> message=(Map<String, String>) choices.get(0).get("message");
            responseDTO.setSuccess(true);
            responseDTO.setResult(message.get("content"));
        }else{
            responseDTO.setSuccess(false);
            responseDTO.setErrorMsg("未获取到AI响应结果");
        }
    }



}

