package com.taoxier.taoxiblog.model.dto;

import lombok.Data;

/**
 * @Author taoxier
 * @Date 2025/9/25 下午2:21
 * @描述 AI内容处理响应实体
 */
@Data
public class AIContentResponseDTO {
    private boolean success;
    private String result;
    private String errorMsg;
}
