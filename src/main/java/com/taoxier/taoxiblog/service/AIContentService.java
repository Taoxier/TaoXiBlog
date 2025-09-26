package com.taoxier.taoxiblog.service;

import com.taoxier.taoxiblog.model.dto.AIContentResponseDTO;
import org.springframework.stereotype.Service;

/**
 * @Author taoxier
 * @Date 2025/9/25 下午2:07
 * @描述
 */
public interface AIContentService {
    AIContentResponseDTO polishContent(String content);

    AIContentResponseDTO generateDescription(String content);

}
