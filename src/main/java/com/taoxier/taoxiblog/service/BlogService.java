package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.dto.BlogDTO;
import com.taoxier.taoxiblog.model.dto.BlogVisibilityDTO;
import com.taoxier.taoxiblog.model.entity.Blog;
import com.taoxier.taoxiblog.model.vo.*;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface BlogService extends IService<Blog> {
    @PostConstruct
    void saveBlogViewsToRedis();

    Map<Long, Integer> getBlogViewsMap();

    List<Blog> getListByTitleAndCategoryId(String title, Integer categoryId);

    List<SearchBlogVO> getSearchBlogByQueryAndIsPublished(String query);

    List<Blog> getIdAndTitleList();

    List<NewBlogVO> getNewBlogListByIsPublished();

    PageResultVO<BlogInfoVO> getBlogInfoListByIsPublished(Integer pageNum);

    void setBlogViewsFromRedisToPageResult(PageResultVO<BlogInfoVO> pageResult);

    List<BlogInfoVO> processBlogInfosPassword(List<BlogInfoVO> blogInfos);

    PageResultVO<BlogInfoVO> getBlogInfoListByCategoryNameAndIsPublished(String categoryName, Integer pageNum);

    PageResultVO<BlogInfoVO> getBlogInfoListByTagNameAndIsPublished(String tagName, Integer pageNum);

    Map<String, Object> getArchiveBlogAndCountByIsPublished();

    List<RandomBlogVO> getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend();

    void deleteBlogRedisCache();

    @Transactional(rollbackFor = Exception.class)
    void deleteBlogById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void deleteBlogTagByBlogId(Long blogId);

    @Transactional(rollbackFor = Exception.class)
    void saveBlog(BlogDTO blogDTO);

    @Transactional(rollbackFor = Exception.class)
    void saveBlogTag(Long blogId, Long tagId);

    @Transactional(rollbackFor = Exception.class)
    void updateBlogVisibilityById(Long blogId, BlogVisibilityDTO blogVisibility);

    @Transactional(rollbackFor = Exception.class)
    void updateBlogRecommendById(Long blogId, Boolean recommend);

    @Transactional(rollbackFor = Exception.class)
    void updateBlogTop(Long blogId, Boolean top);

    void updateViewsToRedis(Long blogId);

    @Transactional(rollbackFor = Exception.class)
    void updateBlogViews(Long blogId, Integer views);

    Blog getBlogById(Long id);

    String getTitleByBlogId(Long id);

    BlogDetailVO getBlogByIdAndIsPublished(Long id);

    String getBlogPassword(Long id);

    @Transactional(rollbackFor = Exception.class)
    void updateBlog(BlogDTO blogDTO);

    int countBlogByIsPublished();

    int countBlogByCategoryId(Long categoryId);

    int countBlogByTagId(Long tagId);

    Boolean getCommentEnabledByBlogId(Long blogId);

    Boolean getPublishedByBlogId(Long blogId);
}
