package com.taoxier.taoxiblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taoxier.taoxiblog.model.dto.BlogDTO;
import com.taoxier.taoxiblog.model.entity.Blog;
import com.taoxier.taoxiblog.model.vo.ArchiveBlogVO;
import com.taoxier.taoxiblog.model.vo.BlogDetailVO;
import com.taoxier.taoxiblog.model.vo.BlogInfoVO;
import com.taoxier.taoxiblog.model.vo.CategoryBlogCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Mapper
@Repository
public interface BlogMapper extends BaseMapper<Blog> {
    List<Blog> getListByTitleAndCategoryId(String title, Integer categoryId);
    List<BlogInfoVO> getBlogInfoListByIsPublished();
    List<BlogInfoVO> getBlogInfoListByCategoryNameAndIsPublished(String categoryName);
    List<BlogInfoVO> getBlogInfoListByTagNameAndIsPublished(String tagName);
    List<String> getGroupYearMonthByIsPublished();
    List<ArchiveBlogVO> getArchiveBlogListByYearMonthAndIsPublished(String yearMonth);
    BlogDetailVO getBlogDetailByIdAndIsPublished(Long id);

    List<CategoryBlogCountVO> getCategoryBlogCountList();

    Blog getBlogById(Long id);

    int deleteBlogTagByBlogId(Long blogId);

}