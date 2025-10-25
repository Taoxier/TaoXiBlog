package com.taoxier.taoxiblog.util;

import java.util.List;

/**
 * @Author taoxier
 * @Date 2025/10/24 下午5:12
 * @描述
 */
public class VectorUtils {

    /**
     * 计算两个向量的余弦相似度
     * @param vec1 向量1
     * @param vec2 向量2
     * @return 相似度（-1到1之间，越大越相似）
     */
    public static double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1 == null || vec2 == null || vec1.size() != vec2.size()) {
            return 0.0; // 向量为空或维度不同，相似度为0
        }

        double dotProduct = 0.0; // 点积
        double norm1 = 0.0; // 向量1的模长
        double norm2 = 0.0; // 向量2的模长

        for (int i = 0; i < vec1.size(); i++) {
            double v1 = vec1.get(i);
            double v2 = vec2.get(i);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        // 避免除以0
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}