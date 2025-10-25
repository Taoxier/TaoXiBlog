package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.model.entity.Blog;
import com.taoxier.taoxiblog.model.vo.BlogSimpleVO;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author taoxier
 * @Date 2025/9/24 下午5:15
 * @描述
 */
@RestController
public class BlogRecommendationController {

    @Resource
    private RecommendationService recommendationService;
    @Resource
    private BlogService blogService;

    /**
     * 获取指定博客的推荐列表
     * 前端调用示例：/api/blog/recommendations?blogId=123
     */
    @GetMapping("/api/blog/recommendations")
    public ResultVO getRecommendations(@RequestParam Long blogId) {
        // 1. 获取推荐的博客ID列表
        List<Long> recommendedIds = recommendationService.getRecommendedBlogIds(blogId);
        if (recommendedIds.isEmpty()) {
            return ResultVO.ok("暂无推荐文章", Collections.emptyList());
        }

        // 2. 查询推荐的博客详情（只返回必要字段）
        List<Blog> recommendedBlogs = blogService.listByIds(recommendedIds);
        List<BlogSimpleVO> voList = recommendedBlogs.stream()
                .map(blog -> {
                    BlogSimpleVO vo = new BlogSimpleVO();
                    vo.setId(blog.getId());
                    vo.setTitle(blog.getTitle());
                    vo.setFirstPicture(blog.getFirstPicture());
                    vo.setCreateTime(blog.getCreateTime());
                    return vo;
                })
                .collect(Collectors.toList());

        return ResultVO.ok("推荐文章获取成功", voList);
    }

    /**
     * 手动触发向量生成和推荐计算（仅管理员使用）
     * 前端调用示例：/api/admin/blog/update-recommendations
     */
    @GetMapping("/api/admin/blog/update-recommendations")
    public ResultVO manualUpdateRecommendations() {
        try {
            recommendationService.generateAllEmbeddings(); // 生成向量
            recommendationService.updateAllRecommendations(); // 计算推荐
            return ResultVO.ok("推荐列表更新成功");
        } catch (Exception e) {
            return ResultVO.error("推荐列表更新失败：" + e.getMessage());
        }
    }

    /**
     * 为单个博客生成向量（仅管理员使用）
     */
    @GetMapping("/api/admin/blog/update-embedding")
    public ResultVO generateSingleEmbedding(@RequestParam Long blogId) {
        try {
            recommendationService.generateEmbeddingForSingleBlog(blogId);
            return ResultVO.ok("向量生成成功");
        } catch (Exception e) {
            return ResultVO.error("向量生成失败：" + e.getMessage());
        }
    }

    /**
     * 为单个博客更新推荐列表（仅管理员使用）
     */
    @GetMapping("/api/admin/blog/update-single-recommendation")
    public ResultVO updateSingleRecommendation(@RequestParam Long blogId) {
        try {
            recommendationService.updateRecommendationsForSingleBlog(blogId);
            return ResultVO.ok("推荐列表更新成功");
        } catch (Exception e) {
            return ResultVO.error("推荐列表更新失败：" + e.getMessage());
        }
    }

    /**
     * 为单个博客生成向量并更新推荐（仅管理员使用）
     */
    @GetMapping("/api/admin/blog/full-update-single")
    public ResultVO fullUpdateSingle(@RequestParam Long blogId) {
        try {
            recommendationService.generateEmbeddingForSingleBlog(blogId);
            recommendationService.updateRecommendationsForSingleBlog(blogId);
            return ResultVO.ok("向量生成和推荐列表更新成功");
        } catch (Exception e) {
            return ResultVO.error("操作失败：" + e.getMessage());
        }
    }

}