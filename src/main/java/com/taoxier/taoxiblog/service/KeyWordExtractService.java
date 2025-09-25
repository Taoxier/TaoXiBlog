package com.taoxier.taoxiblog.service;

import java.util.List;

/**
 * @Author taoxier
 * @Date 2025/9/26 上午1:23
 * @描述
 */
public interface KeyWordExtractService {
    List<String> extractKeywords(String content, int maxNum);
}
