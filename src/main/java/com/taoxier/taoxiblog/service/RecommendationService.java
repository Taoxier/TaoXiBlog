package com.taoxier.taoxiblog.service;

import java.util.List;

/**
 * @Author taoxier
 * @Date 2025/10/24 下午5:13
 * @描述
 */
public interface RecommendationService {

    /**
     * 为所有博客生成嵌入向量（存储到blog_embedding表）
     */
    void generateAllEmbeddings();

    /**
     * 计算并更新所有博客的推荐列表（存储到blog_recommendation表）
     */
    void updateAllRecommendations();

    /**
     * 根据博客ID获取推荐的博客ID列表
     * @param blogId 博客ID
     * @return 推荐的博客ID列表（最多5个）
     */
    List<Long> getRecommendedBlogIds(Long blogId);

    /**
     * 为单个博客生成嵌入向量
     * @param blogId 博客ID
     */
    void generateEmbeddingForSingleBlog(Long blogId);

    /**
     * 为单个博客更新推荐列表
     * @param blogId 博客ID
     */
    void updateRecommendationsForSingleBlog(Long blogId);
}