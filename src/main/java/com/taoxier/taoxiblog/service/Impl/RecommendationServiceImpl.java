package com.taoxier.taoxiblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.taoxier.taoxiblog.model.entity.Blog;
import com.taoxier.taoxiblog.model.entity.BlogEmbedding;
import com.taoxier.taoxiblog.model.entity.BlogRecommendation;
import com.taoxier.taoxiblog.mapper.BlogEmbeddingMapper;
import com.taoxier.taoxiblog.mapper.BlogRecommendationMapper;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.RecommendationService;

import com.taoxier.taoxiblog.util.VectorUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author taoxier
 * @Date 2025/10/24 下午5:13
 * @描述
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Resource
    private BlogService blogService; // 原有博客服务（查询所有博客）
    @Resource
    private BlogEmbeddingMapper blogEmbeddingMapper;
    @Resource
    private BlogRecommendationMapper blogRecommendationMapper;


    @Resource
    private BaiduEmbeddingClient baiduEmbeddingClient; // 新增

    // 推荐数量（可自定义）
    private static final int RECOMMEND_COUNT = 5;

    @Override
    @Transactional
    public void generateAllEmbeddings() {
        List<Blog> allBlogs = blogService.list();
        if (allBlogs.isEmpty()) {
            return;
        }

        for (Blog blog : allBlogs) {
            try {
                // 调用百度接口生成向量
                List<Double> embedding = baiduEmbeddingClient.getEmbedding(blog.getContent());
                String embeddingJson = JSON.toJSONString(embedding);

                // 保存逻辑不变...
                BlogEmbedding blogEmbedding = blogEmbeddingMapper.selectOne(
                        new LambdaQueryWrapper<BlogEmbedding>().eq(BlogEmbedding::getBlogId, blog.getId())
                );
                if (blogEmbedding == null) {
                    blogEmbedding = new BlogEmbedding();
                    blogEmbedding.setBlogId(blog.getId());
                    blogEmbedding.setEmbeddingVector(embeddingJson);
                    blogEmbeddingMapper.insert(blogEmbedding);
                } else {
                    blogEmbedding.setEmbeddingVector(embeddingJson);
                    blogEmbeddingMapper.updateById(blogEmbedding);
                }

                // 百度API QPS限制通常较低，建议延长间隔（如2秒）
                Thread.sleep(5000);
            } catch (Exception e) {
                System.err.println("生成博客ID=" + blog.getId() + "的向量失败：" + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void updateAllRecommendations() {
        // 1. 获取所有博客和对应的向量
        List<Blog> allBlogs = blogService.list();
        if (allBlogs.size() <= 1) {
            return; // 不足2篇博客，无需推荐
        }

        // 2. 构建博客ID到向量的映射（从blog_embedding表读取）
        Map<Long, List<Double>> blogVectorMap = new HashMap<>();
        for (Blog blog : allBlogs) {
            BlogEmbedding embedding = blogEmbeddingMapper.selectOne(
                    new LambdaQueryWrapper<BlogEmbedding>().eq(BlogEmbedding::getBlogId, blog.getId())
            );
            if (embedding != null && embedding.getEmbeddingVector() != null) {
                // 解析JSON为List<Double>
                List<Double> vector = JSON.parseArray(embedding.getEmbeddingVector(), Double.class);
                blogVectorMap.put(blog.getId(), vector);
            }
        }

        // 3. 为每篇博客计算推荐列表
        for (Blog blog : allBlogs) {
            Long currentBlogId = blog.getId();
            List<Double> currentVector = blogVectorMap.get(currentBlogId);
            if (currentVector == null) {
                continue; // 没有向量的博客跳过
            }

            // 4. 计算与其他所有博客的相似度
            Map<Long, Double> similarityMap = new HashMap<>();
            for (Map.Entry<Long, List<Double>> entry : blogVectorMap.entrySet()) {
                Long otherBlogId = entry.getKey();
                List<Double> otherVector = entry.getValue();

                // 跳过自己
                if (currentBlogId.equals(otherBlogId)) {
                    continue;
                }

                // 计算余弦相似度
                double similarity = VectorUtils.cosineSimilarity(currentVector, otherVector);
                similarityMap.put(otherBlogId, similarity);
            }

            // 5. 按相似度排序，取前N篇
            List<Long> recommendedIds = similarityMap.entrySet().stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed()) // 降序排序
                    .limit(RECOMMEND_COUNT) // 取前5
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // 6. 保存到blog_recommendation表
            BlogRecommendation recommendation = blogRecommendationMapper.selectOne(
                    new LambdaQueryWrapper<BlogRecommendation>().eq(BlogRecommendation::getBlogId, currentBlogId)
            );
            if (recommendation == null) {
                recommendation = new BlogRecommendation();
                recommendation.setBlogId(currentBlogId);
                recommendation.setRecommendedBlogIds(JSON.toJSONString(recommendedIds));
                blogRecommendationMapper.insert(recommendation);
            } else {
                recommendation.setRecommendedBlogIds(JSON.toJSONString(recommendedIds));
                blogRecommendationMapper.updateById(recommendation);
            }
        }
    }

    @Override
    public List<Long> getRecommendedBlogIds(Long blogId) {
        // 从blog_recommendation表查询推荐的ID列表
        BlogRecommendation recommendation = blogRecommendationMapper.selectOne(
                new LambdaQueryWrapper<BlogRecommendation>().eq(BlogRecommendation::getBlogId, blogId)
        );
        if (recommendation == null || recommendation.getRecommendedBlogIds() == null) {
            return Collections.emptyList();
        }
        // 解析JSON为List<Long>
        return JSON.parseArray(recommendation.getRecommendedBlogIds(), Long.class);
    }

    @Override
    @Transactional
    public void generateEmbeddingForSingleBlog(Long blogId) {
        // 获取指定博客
        Blog blog = blogService.getById(blogId);
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }

        try {
            // 调用百度接口生成向量
            List<Double> embedding = baiduEmbeddingClient.getEmbedding(blog.getContent());
            String embeddingJson = JSON.toJSONString(embedding);

            // 保存或更新向量
            BlogEmbedding blogEmbedding = blogEmbeddingMapper.selectOne(
                    new LambdaQueryWrapper<BlogEmbedding>().eq(BlogEmbedding::getBlogId, blogId)
            );
            if (blogEmbedding == null) {
                blogEmbedding = new BlogEmbedding();
                blogEmbedding.setBlogId(blogId);
                blogEmbedding.setEmbeddingVector(embeddingJson);
                blogEmbeddingMapper.insert(blogEmbedding);
            } else {
                blogEmbedding.setEmbeddingVector(embeddingJson);
                blogEmbeddingMapper.updateById(blogEmbedding);
            }

            // 百度API调用间隔
            Thread.sleep(2000);
        } catch (Exception e) {
            throw new RuntimeException("生成博客ID=" + blogId + "的向量失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateRecommendationsForSingleBlog(Long blogId) {
        // 1. 获取当前博客和所有其他博客
        Blog currentBlog = blogService.getById(blogId);
        if (currentBlog == null) {
            throw new RuntimeException("博客不存在");
        }

        List<Blog> allBlogs = blogService.list();
        if (allBlogs.size() <= 1) {
            return; // 不足2篇博客，无需推荐
        }

        // 2. 构建博客ID到向量的映射
        Map<Long, List<Double>> blogVectorMap = new HashMap<>();
        for (Blog blog : allBlogs) {
            BlogEmbedding embedding = blogEmbeddingMapper.selectOne(
                    new LambdaQueryWrapper<BlogEmbedding>().eq(BlogEmbedding::getBlogId, blog.getId())
            );
            if (embedding != null && embedding.getEmbeddingVector() != null) {
                List<Double> vector = JSON.parseArray(embedding.getEmbeddingVector(), Double.class);
                blogVectorMap.put(blog.getId(), vector);
            }
        }

        // 3. 获取当前博客向量
        List<Double> currentVector = blogVectorMap.get(blogId);
        if (currentVector == null) {
            throw new RuntimeException("未找到博客ID=" + blogId + "的向量，请先生成向量");
        }

        // 4. 计算与其他所有博客的相似度
        Map<Long, Double> similarityMap = new HashMap<>();
        for (Map.Entry<Long, List<Double>> entry : blogVectorMap.entrySet()) {
            Long otherBlogId = entry.getKey();
            List<Double> otherVector = entry.getValue();

            // 跳过自己
            if (blogId.equals(otherBlogId)) {
                continue;
            }

            // 计算余弦相似度
            double similarity = VectorUtils.cosineSimilarity(currentVector, otherVector);
            similarityMap.put(otherBlogId, similarity);
        }

        // 5. 按相似度排序，取前N篇
        List<Long> recommendedIds = similarityMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(RECOMMEND_COUNT)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 6. 保存推荐结果
        BlogRecommendation recommendation = blogRecommendationMapper.selectOne(
                new LambdaQueryWrapper<BlogRecommendation>().eq(BlogRecommendation::getBlogId, blogId)
        );
        if (recommendation == null) {
            recommendation = new BlogRecommendation();
            recommendation.setBlogId(blogId);
            recommendation.setRecommendedBlogIds(JSON.toJSONString(recommendedIds));
            blogRecommendationMapper.insert(recommendation);
        } else {
            recommendation.setRecommendedBlogIds(JSON.toJSONString(recommendedIds));
            blogRecommendationMapper.updateById(recommendation);
        }

        // 7. 同时需要更新其他博客对当前博客的推荐（可选）
        updateOtherBlogsRecommendations(blogId, currentVector, blogVectorMap);
    }

    /**
     * 当单个博客更新后，更新其他博客对它的推荐列表
     */
    private void updateOtherBlogsRecommendations(Long updatedBlogId, List<Double> updatedVector,
                                                 Map<Long, List<Double>> blogVectorMap) {
        for (Map.Entry<Long, List<Double>> entry : blogVectorMap.entrySet()) {
            Long otherBlogId = entry.getKey();
            if (updatedBlogId.equals(otherBlogId)) {
                continue; // 跳过自己
            }

            List<Double> otherVector = entry.getValue();
            double similarity = VectorUtils.cosineSimilarity(updatedVector, otherVector);

            // 获取该博客当前的推荐列表
            BlogRecommendation recommendation = blogRecommendationMapper.selectOne(
                    new LambdaQueryWrapper<BlogRecommendation>().eq(BlogRecommendation::getBlogId, otherBlogId)
            );

            if (recommendation != null && recommendation.getRecommendedBlogIds() != null) {
                List<Long> currentRecommendIds = JSON.parseArray(recommendation.getRecommendedBlogIds(), Long.class);

                // 如果不在推荐列表中，添加并重新排序
                if (!currentRecommendIds.contains(updatedBlogId)) {
                    currentRecommendIds.add(updatedBlogId);
                    // 重新计算相似度并排序（简化处理，只做基本排序）
                    List<Long> newRecommendIds = currentRecommendIds.stream()
                            .map(id -> {
                                double sim = VectorUtils.cosineSimilarity(otherVector, blogVectorMap.get(id));
                                return new AbstractMap.SimpleEntry<>(id, sim);
                            })
                            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                            .limit(RECOMMEND_COUNT)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    recommendation.setRecommendedBlogIds(JSON.toJSONString(newRecommendIds));
                    blogRecommendationMapper.updateById(recommendation);
                }
            }
        }
    }
}
