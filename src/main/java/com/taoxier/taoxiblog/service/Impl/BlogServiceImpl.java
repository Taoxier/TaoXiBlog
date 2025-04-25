package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.mapper.BlogMapper;
import com.taoxier.taoxiblog.model.dto.BlogViewDTO;
import com.taoxier.taoxiblog.model.entity.Blog;
import com.taoxier.taoxiblog.model.vo.BlogIdAndTitleVO;
import com.taoxier.taoxiblog.model.vo.NewBlogVO;
import com.taoxier.taoxiblog.model.vo.SearchBlogVO;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    BlogMapper blogMapper;
    @Autowired
    TagService tagService;
    @Autowired
    RedisService redisService;

    //随机博客显示5条
    private static final int randomBlogLimitNum = 5;
    //最新推荐博客显示3条
    private static final int newBlogPageSize = 3;
    //每页显示5条博客简介
    private static final int pageSize = 5;
    //博客简介列表排序方式
    private static final String orderBy = "is_top desc, create_time desc";
    //私密博客提示
    private static final String PRIVATE_BLOG_DESCRIPTION = "此文章受密码保护。";


    /**
    * @Description 项目启动时，保存所有博客的浏览量到Redis
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: void
    */
    @PostConstruct
    private void saveBlogViewsToRedis(){
        String redisKey= RedisKeyConstants.BLOG_VIEWS_MAP;
        //如果Redis中没有存储博客浏览量的Hash
        if (!redisService.hasKey(redisKey)){
            //从数据库中读取并存入Redis
            Map<Long,Integer> blogViewsMap=getBlogViewsMap();
            redisService.saveMapToHash(redisKey,blogViewsMap);
        }
    }

    /**
    * @Description 查询所有文章的浏览量
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.Map<java.lang.Long,java.lang.Integer>
    */
    private Map<Long,Integer> getBlogViewsMap(){
        LambdaQueryWrapper<Blog> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.select(Blog::getId,Blog::getViews);
        return (Map<Long, Integer>) this.baseMapper.selectList(queryWrapper).stream().map(blog -> {
            BlogViewDTO blogView=new BlogViewDTO();
            blogView.setId(blog.getId());
            blogView.setViews(blog.getViews());
            return blogView;
        })
                .collect(Collectors.toList());
    }

    /**
    * @Description 根据标题和分类id查询博客列表
     * @param title
     * @param categoryId
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Blog>
    */
    @Override
    public List<Blog> getListByTitleAndCategoryId(String title,Integer categoryId){
        return blogMapper.getListByTitleAndCategoryId(title, categoryId);
    }

    /**
    * @Description 按关键字根据文章内容搜索公开且无密码保护的博客文章
     * @param query
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.SearchBlogVO>
    */
    @Override
    public List<SearchBlogVO> getSearchBlogByQueryAndIsPublished(String query){
        String upperCaseQuery=query.toUpperCase();//大写
        LambdaQueryWrapper<Blog> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getIsPublished,true)
                .eq(Blog::getPassword,"")
                .like(Blog::getContent,query);

        return this.baseMapper.selectList(queryWrapper).stream()
                .map(blog -> {
                    SearchBlogVO searchBlog=new SearchBlogVO();
                    searchBlog.setId(blog.getId());
                    searchBlog.setTitle(blog.getTitle());
                    searchBlog.setContent(blog.getContent());

                    String upperCaseContent=searchBlog.getContent().toUpperCase();
                    int contentLength=upperCaseContent.length();
                    /*
                    Math.max(upperCaseContent.indexOf(upperCaseQuery) - 10, 0)：这部分代码是为了计算截取内容的起始索引 index。首先用 upperCaseContent.indexOf(upperCaseQuery) 得到查询字符串在内容中首次出现的位置，然后减去 10，目的是希望以查询字符串为中心，向前多取一些字符。但是有可能计算结果是负数（比如查询字符串在内容的前几个位置，减去 10 后就会是负数），所以使用 Math.max 方法，它会返回两个参数中较大的那个值，这样就确保了 index 不小于 0，避免出现非法的索引。
                     */
                    int index=Math.max(upperCaseContent.indexOf(upperCaseQuery)-10,0);
                    int end=Math.min(index+21,contentLength-1);
                    //返回的 SearchBlogVO 对象中的内容就是以查询字符串为中心，最多 21 个字符的片段
                    searchBlog.setContent(searchBlog.getContent().substring(index,end));
                    return searchBlog;
                }).collect(Collectors.toList());
    }

    /**
    * @Description 查询所有博客id和title
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Blog>
    */
    @Override
    public List<Blog> getIdAndTitleList(){
        LambdaQueryWrapper<Blog> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.select(Blog::getId,Blog::getTitle)
                .orderByDesc(Blog::getCreateTime);
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
    * @Description 查询最新公开博客
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.NewBlogVO>
    */
    @Override
    public List<NewBlogVO> getNewBlogListByIsPublished(){
        String redisKey=RedisKeyConstants.NEW_BLOG_LIST;
        List<NewBlogVO> newBlogListFromRedis=redisService.getListByKey(redisKey);
        if (newBlogListFromRedis!=null){
            return newBlogListFromRedis;
        }

        //使用分页查询
        Page<Blog> page=new Page<>(1,newBlogPageSize);
        LambdaQueryWrapper<Blog> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getIsPublished,true)
                .orderByDesc(Blog::getCreateTime);
        IPage<Blog> blogIPage=baseMapper.selectPage(page,queryWrapper);
        List<Blog> blogList=blogIPage.getRecords();

        //将blog转为newblog
        List<NewBlogVO> newBlogList=blogList.stream()
                .map(blog -> {
                    NewBlogVO newBlog=new NewBlogVO();
                    newBlog.setId(blog.getId());
                    newBlog.setTitle(blog.getTitle());
                    newBlog.setPassword(blog.getPassword());
                    if (!"".equals(blog.getPassword())){
                        newBlog.setPrivacy(true);
                        newBlog.setPassword("");//返回给前端的数据中，不会包含真实的密码信息
                    }else {
                        newBlog.setPrivacy(false);
                    }
                    return newBlog;
                }).collect(Collectors.toList());
        redisService.saveListToValue(redisKey,newBlogList);
        return newBlogList;
    }


}
