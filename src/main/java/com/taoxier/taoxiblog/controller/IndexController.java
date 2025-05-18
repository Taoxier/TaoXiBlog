package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.model.entity.Category;
import com.taoxier.taoxiblog.model.entity.Tag;
import com.taoxier.taoxiblog.model.vo.NewBlogVO;
import com.taoxier.taoxiblog.model.vo.RandomBlogVO;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.CategoryService;
import com.taoxier.taoxiblog.service.SiteSettingService;
import com.taoxier.taoxiblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description ：站点相关
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
public class IndexController {
    @Autowired
    SiteSettingService siteSettingService;
    @Autowired
    BlogService blogService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    TagService tagService;

    /**
    * @Description 获取站点配置信息、最新推荐博客、分类列表、标签、随机博客
     * @param
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/site")
    public ResultVO site() {
        Map<String, Object> map = siteSettingService.getSiteInfo();
        List<NewBlogVO> newBlogList = blogService.getNewBlogListByIsPublished();
        List<Category> categoryList = categoryService.getCategoryNameList();
//        System.out.println("分类list："+categoryList.toString());
        List<Tag> tagList = tagService.getTagListNoId();
        List<RandomBlogVO> randomBlogList = blogService.getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend();
        map.put("newBlogList", newBlogList);
        map.put("categoryList", categoryList);
        map.put("tagList", tagList);
        map.put("randomBlogList", randomBlogList);
        return ResultVO.ok("请求成功", map);
    }
}
