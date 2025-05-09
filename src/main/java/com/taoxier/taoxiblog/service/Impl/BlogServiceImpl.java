package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.exception.NotFoundException;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.BlogMapper;
import com.taoxier.taoxiblog.mapper.BlogTagMapper;
import com.taoxier.taoxiblog.model.dto.BlogDTO;
import com.taoxier.taoxiblog.model.dto.BlogViewDTO;
import com.taoxier.taoxiblog.model.dto.BlogVisibilityDTO;
import com.taoxier.taoxiblog.model.entity.Blog;
import com.taoxier.taoxiblog.model.entity.BlogTag;
import com.taoxier.taoxiblog.model.vo.*;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.service.TagService;
import com.taoxier.taoxiblog.util.JacksonUtils;
import com.taoxier.taoxiblog.util.markdown.MarkdownUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    BlogTagMapper blogTagMapper;
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
     * @param
     * @Description 项目启动时，保存所有博客的浏览量到Redis
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: void
     */
    @Override
    @PostConstruct
    public void saveBlogViewsToRedis() {
        String redisKey = RedisKeyConstants.BLOG_VIEWS_MAP;
        //如果Redis中没有存储博客浏览量的Hash
        if (!redisService.hasKey(redisKey)) {
            //从数据库中读取并存入Redis
            Map<Long, Integer> blogViewsMap = getBlogViewsMap();
            redisService.saveMapToHash(redisKey, blogViewsMap);
        }
    }

    /**
     * @param
     * @Description 查询所有文章的浏览量
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: java.util.Map<java.lang.Long, java.lang.Integer>
     */
    @Override
    public Map<Long, Integer> getBlogViewsMap() {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Blog::getId, Blog::getViews);

        return this.baseMapper.selectList(queryWrapper).stream()
                .map(blog -> {
                    BlogViewDTO blogView = new BlogViewDTO();
                    blogView.setId(blog.getId());
                    blogView.setViews(blog.getViews());
                    return blogView;
                })
                .collect(Collectors.toMap(
                        BlogViewDTO::getId,
                        BlogViewDTO::getViews,
                        //合并函数(当出现重复键时，保留已存在的值（existing），丢弃新值（replacement）---map需要key唯一)
                        (existing,replacement) -> existing
                ));
    }

    /**
     * @param title
     * @param categoryId
     * @Description 根据标题和分类id查询博客列表
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Blog>
     */
    @Override
    public List<Blog> getListByTitleAndCategoryId(String title, Integer categoryId) {
        return blogMapper.getListByTitleAndCategoryId(title, categoryId);
    }

    /**
     * @param query
     * @Description 按关键字根据文章内容搜索公开且无密码保护的博客文章
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.SearchBlogVO>
     */
    @Override
    public List<SearchBlogVO> getSearchBlogListByQueryAndIsPublished(String query) {
        String upperCaseQuery = query.toUpperCase();//大写
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getIsPublished, true)
                .eq(Blog::getPassword, "")
                .like(Blog::getContent, query);

        return this.baseMapper.selectList(queryWrapper).stream()
                .map(blog -> {
                    SearchBlogVO searchBlog = new SearchBlogVO();
                    searchBlog.setId(blog.getId());
                    searchBlog.setTitle(blog.getTitle());
                    searchBlog.setContent(blog.getContent());

                    String upperCaseContent = searchBlog.getContent().toUpperCase();
                    int contentLength = upperCaseContent.length();
                    /*
                    Math.max(upperCaseContent.indexOf(upperCaseQuery) - 10, 0)：这部分代码是为了计算截取内容的起始索引 index。首先用 upperCaseContent.indexOf(upperCaseQuery) 得到查询字符串在内容中首次出现的位置，然后减去 10，目的是希望以查询字符串为中心，向前多取一些字符。但是有可能计算结果是负数（比如查询字符串在内容的前几个位置，减去 10 后就会是负数），所以使用 Math.max 方法，它会返回两个参数中较大的那个值，这样就确保了 index 不小于 0，避免出现非法的索引。
                     */
                    int index = Math.max(upperCaseContent.indexOf(upperCaseQuery) - 10, 0);
                    int end = Math.min(index + 21, contentLength - 1);
                    //返回的 SearchBlogVO 对象中的内容就是以查询字符串为中心，最多 21 个字符的片段
                    searchBlog.setContent(searchBlog.getContent().substring(index, end));
                    return searchBlog;
                }).collect(Collectors.toList());
    }

    /**
    * @Description 查询所有博客id和title
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Blog>
    */
    @Override
    public List<Blog> getIdAndTitleList() {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Blog::getId, Blog::getTitle)
                .orderByDesc(Blog::getCreateTime);
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
     * @param
     * @Description 查询最新公开博客
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.NewBlogVO>
     */
    @Override
    public List<NewBlogVO> getNewBlogListByIsPublished() {
        String redisKey = RedisKeyConstants.NEW_BLOG_LIST;
        List<NewBlogVO> newBlogListFromRedis = redisService.getListByKey(redisKey);
        if (newBlogListFromRedis != null) {
            return newBlogListFromRedis;
        }

        // 自动调用 page.close() 清理上下文
        try (Page<Blog> page = PageHelper.startPage(1, newBlogPageSize)) {
            LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Blog::getIsPublished, true)
                    .orderByDesc(Blog::getCreateTime);
            List<Blog> blogList = blogMapper.selectList(queryWrapper);

        //将blog转为newblog
        List<NewBlogVO> newBlogList = blogList.stream()
                .map(blog -> {
                    NewBlogVO newBlog = new NewBlogVO();
                    newBlog.setId(blog.getId());
                    newBlog.setTitle(blog.getTitle());
                    newBlog.setPassword(blog.getPassword());
                    if (!"".equals(blog.getPassword())) {
                        newBlog.setPrivacy(true);
                        newBlog.setPassword("");//返回给前端的数据中，不会包含真实的密码信息
                    } else {
                        newBlog.setPrivacy(false);
                    }
                    return newBlog;
                }).collect(Collectors.toList());
        //保存到redis
        redisService.saveListToValue(redisKey, newBlogList);
        return newBlogList;
    }
    }


    /**
     * @param pageNum
     * @Description 查询公开博客的简要信息
     * @Author: taoxier
     * @Date: 2025/4/27
     * @Return: com.taoxier.taoxiblog.model.vo.PageResultVO<com.taoxier.taoxiblog.model.vo.BlogInfoVO>
     */
    @Override
    public PageResultVO<BlogInfoVO> getBlogInfoListByIsPublished(Integer pageNum) {
        String redisKey = RedisKeyConstants.HOME_BLOG_INFO_LIST;
        PageResultVO<BlogInfoVO> pageResultFromRedis = redisService.getBlogInfoPageResultHash(redisKey, pageNum);

        //redis已经有了当前页的缓存
        if (pageResultFromRedis != null) {
            setBlogViewsFromRedisToPageResult(pageResultFromRedis);
            return pageResultFromRedis;
        }

        //redis没有缓存，从数据库查询，并添加缓存
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<BlogInfoVO> blogInfos = processBlogInfosPassword(blogMapper.getBlogInfoListByIsPublished());
        PageInfo<BlogInfoVO> pageInfo = new PageInfo<>(blogInfos);
        PageResultVO<BlogInfoVO> pageResult = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        setBlogViewsFromRedisToPageResult(pageResult);
        //添加首页缓存
        redisService.saveKVToHash(redisKey, pageNum, pageResult);
        return pageResult;
    }

    /**
     * @param pageResult
     * @Description 将pageResult中博客对象的浏览量设置为Redis中的最新值
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Override
    public void setBlogViewsFromRedisToPageResult(PageResultVO<BlogInfoVO> pageResult) {
        String redisKey = RedisKeyConstants.BLOG_VIEWS_MAP;
        List<BlogInfoVO> blogInfos = pageResult.getList();
        for (int i = 0; i < blogInfos.size(); i++) {
            BlogInfoVO blogInfo = JacksonUtils.convertValue(blogInfos.get(i), BlogInfoVO.class);
            Long blogId = blogInfo.getId();

            /*
             * 这里如果出现异常，通常是手动修改过 MySQL 而没有通过后台管理，导致 Redis 和 MySQL 不同步
             * 从 Redis 中查出了 null，强转 int 时出现 NullPointerException
             * 直接抛出异常比带着 bug 继续跑要好得多
             *
             * 解决步骤：
             * 1.结束程序
             * 2.删除 Redis DB 中 blogViewsMap 这个 key（或者直接清空对应的整个 DB）
             * 3.重新启动程序
             */
            int view = (int) redisService.getValueByHashKey(redisKey, blogId);
            blogInfo.setViews(view);
            blogInfos.set(i, blogInfo);
        }
    }


    /**
     * @param blogInfos
     * @Description 对博客信息列表进行处理：隐私设置、描述转换和标签信息获取
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.BlogInfoVO>
     */
    @Override
    public List<BlogInfoVO> processBlogInfosPassword(List<BlogInfoVO> blogInfos) {
        for (BlogInfoVO blogInfo : blogInfos) {
            if (!"".equals(blogInfo.getPassword())) {
                blogInfo.setPrivacy(true);
                blogInfo.setPassword("");
                blogInfo.setDescription(PRIVATE_BLOG_DESCRIPTION);
            } else {
                blogInfo.setPrivacy(false);
                blogInfo.setDescription(MarkdownUtils.markdownToHtmlExtensions(blogInfo.getDescription()));
            }
            blogInfo.setTags(tagService.getTagListByBlogId(blogInfo.getId()));
        }
        return blogInfos;
    }

    /**
     * @param categoryName
     * @param pageNum
     * @Description 根据分类name查询公开博客List
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: com.taoxier.taoxiblog.model.vo.PageResultVO<com.taoxier.taoxiblog.model.vo.BlogInfoVO>
     */
    @Override
    public PageResultVO<BlogInfoVO> getBlogInfoListByCategoryNameAndIsPublished(String categoryName, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<BlogInfoVO> blogInfos = processBlogInfosPassword(blogMapper.getBlogInfoListByCategoryNameAndIsPublished(categoryName));
        PageInfo<BlogInfoVO> pageInfo = new PageInfo<>(blogInfos);
        PageResultVO<BlogInfoVO> pageResult = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        setBlogViewsFromRedisToPageResult(pageResult);
        return pageResult;
    }

    /**
     * @param tagName
     * @param pageNum
     * @Description 根据标签name查询公开博客List
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: com.taoxier.taoxiblog.model.vo.PageResultVO<com.taoxier.taoxiblog.model.vo.BlogInfoVO>
     */
    @Override
    public PageResultVO<BlogInfoVO> getBlogInfoListByTagNameAndIsPublished(String tagName, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<BlogInfoVO> blogInfos = processBlogInfosPassword(blogMapper.getBlogInfoListByTagNameAndIsPublished(tagName));
        PageInfo<BlogInfoVO> pageInfo = new PageInfo<>(blogInfos);
        PageResultVO<BlogInfoVO> pageResult = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        setBlogViewsFromRedisToPageResult(pageResult);
        return pageResult;
    }

    /**
     * @param
     * @Description 对已发布的博客进行归档处理，按照博客的创建年月进行分组，统计数量
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: java.util.Map<java.lang.String, java.lang.Object>
     */
    @Override
    public Map<String, Object> getArchiveBlogAndCountByIsPublished() {
        String redisKey = RedisKeyConstants.ARCHIVE_BLOG_MAP;
        Map<String, Object> mapFromRedis = redisService.getMapByKey(redisKey);
        if (mapFromRedis != null) {
            return mapFromRedis;
        }
        List<String> groupYearMonth = blogMapper.getGroupYearMonthByIsPublished();
        Map<String, List<ArchiveBlogVO>> archiveBlogMap = new LinkedHashMap<>();
        for (String s : groupYearMonth) {
            List<ArchiveBlogVO> archiveBlogs = blogMapper.getArchiveBlogListByYearMonthAndIsPublished(s);
            for (ArchiveBlogVO archiveBlog : archiveBlogs) {
                if (!"".equals(archiveBlog.getPassword())) {
                    archiveBlog.setPrivacy(true);
                    archiveBlog.setPassword("");
                } else {
                    archiveBlog.setPrivacy(false);
                }
            }
            archiveBlogMap.put(s, archiveBlogs);
        }
        Integer count = countBlogByIsPublished();
        Map<String, Object> map = new HashMap<>(4);
        map.put("blogMap", archiveBlogMap);
        map.put("count", count);
        redisService.saveMapToValue(redisKey, map);
        return map;
    }

    /**
     * @param
     * @Description 查询随机的公开且推荐文章
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.RandomBlogVO>
     */
    @Override
    public List<RandomBlogVO> getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend() {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getIsPublished, true)
                .eq(Blog::getIsRecommend, true);
//        queryWrapper.apply("ORDER BY rand() LIMIT ?", randomBlogLimitNum);
        // 直接拼接参数（适用于内部可控参数，避免 SQL 注入）
        queryWrapper.last("ORDER BY rand() LIMIT " + randomBlogLimitNum);
        List<Blog> blogs = blogMapper.selectList(queryWrapper);

        return blogs.stream()
                .map(blog -> {
                    RandomBlogVO randomBlog = new RandomBlogVO();
                    randomBlog.setId(blog.getId());
                    randomBlog.setTitle(blog.getTitle());
                    randomBlog.setPassword(blog.getPassword());
                    randomBlog.setCreateTime(blog.getCreateTime());
                    randomBlog.setFirstPicture(blog.getFirstPicture());

                    if ("".equals(randomBlog.getPassword())) {
                        randomBlog.setPrivacy(true);
                        randomBlog.setPassword("");
                    } else {
                        randomBlog.setPrivacy(false);
                    }
                    return randomBlog;
                }).collect(Collectors.toList());
    }

    /**
     * @param
     * @Description 删除首页缓存 、最新推荐缓存、归档页面缓存、博客浏览量缓存
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Override
    public void deleteBlogRedisCache() {
        redisService.deleteCacheByKey(RedisKeyConstants.HOME_BLOG_INFO_LIST);
        redisService.deleteCacheByKey(RedisKeyConstants.NEW_BLOG_LIST);
        redisService.deleteCacheByKey(RedisKeyConstants.ARCHIVE_BLOG_MAP);
    }

    /**
     * @param id
     * @Description 删博客顺便删缓存
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBlogById(Long id) {
        if (blogMapper.deleteById(id) < 1) {
            throw new NotFoundException("该博客不存在哦");
        }
        deleteBlogRedisCache();
        redisService.deleteByHashKey(RedisKeyConstants.BLOG_VIEWS_MAP, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBlogTagByBlogId(Long blogId) {
        if (blogTagMapper.deleteById(blogId) == 0) {
            throw new PersistenceException("维护博客标签关联表失败");
        }
    }

    /**
     * @param blogDTO
     * @Description 新增博客
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBlog(BlogDTO blogDTO) {
        Blog blog = new Blog();
        try {
            BeanUtils.copyProperties(blog, blogDTO);
            if (blogDTO.getCategory() != null) {
                blog.setCategoryId(blogDTO.getCategory().getId());
            }
            if (blogDTO.getUser() != null) {
                blog.setUserId(blogDTO.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PersistenceException("DTO转换为entity时出错");
        }

        if (blogMapper.insert(blog) != 1) {
            throw new PersistenceException("添加博客失败");
        }
        redisService.saveKVToHash(RedisKeyConstants.BLOG_VIEWS_MAP, blog.getId(), 0);
        deleteBlogRedisCache();
    }

    /**
     * @param blogId
     * @param tagId
     * @Description 新增博客标签关联
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBlogTag(Long blogId, Long tagId) {
        BlogTag blogTag = new BlogTag();
        blogTag.setBlogId(blogId);
        blogTag.setTagId(tagId);
        if (blogTagMapper.insert(blogTag) != 1) {
            throw new PersistenceException("维护博客标签关联表失败");
        }
    }

    /**
     * @param blogId
     * @param blogVisibility
     * @Description 更新博客可见性状态
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlogVisibilityById(Long blogId, BlogVisibilityDTO blogVisibility) {
        LambdaUpdateWrapper<Blog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Blog::getId, blogId)
                .set(Blog::getIsAppreciation, blogVisibility.getAppreciation())
                .set(Blog::getIsRecommend, blogVisibility.getRecommend())
                .set(Blog::getIsCommentEnabled, blogVisibility.getCommentEnabled())
                .set(Blog::getIsTop, blogVisibility.getTop())
                .set(Blog::getIsPublished, blogVisibility.getPublished())
                .set(Blog::getPassword, blogVisibility.getPassword());
        if (blogMapper.update(null, updateWrapper) != 1) {
            throw new PersistenceException("操作失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstants.HOME_BLOG_INFO_LIST);
        redisService.deleteCacheByKey(RedisKeyConstants.NEW_BLOG_LIST);
        redisService.deleteCacheByKey(RedisKeyConstants.ARCHIVE_BLOG_MAP);
    }

    /**
     * @param blogId
     * @param recommend
     * @Description 更新博客推荐状态
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlogRecommendById(Long blogId, Boolean recommend) {
        LambdaUpdateWrapper<Blog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Blog::getId, blogId)
                .set(Blog::getIsRecommend, recommend);

        if (blogMapper.update(null, updateWrapper) != 1) {
            throw new PersistenceException("操作失败");
        }
    }

    /**
     * @param blogId
     * @param top
     * @Description 更新博客置顶状态
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlogTop(Long blogId, Boolean top) {
        LambdaUpdateWrapper<Blog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Blog::getId, blogId)
                .set(Blog::getIsTop, top);
        if (blogMapper.update(null, updateWrapper) != 1) {
            throw new PersistenceException("操作失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstants.HOME_BLOG_INFO_LIST);
    }

    /**
     * @param blogId
     * @Description 更新存储在 Redis 中的博客浏览量，每调用一次方法增加1
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Override
    public void updateViewsToRedis(Long blogId) {
        redisService.incrementByHashKey(RedisKeyConstants.BLOG_VIEWS_MAP, blogId, 1);
    }

    /**
     * @param blogId
     * @param views
     * @Description 更新博客的浏览量
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlogViews(Long blogId, Integer views) {
        LambdaUpdateWrapper<Blog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Blog::getId, blogId)
                .set(Blog::getViews, views);
        if (blogMapper.update(null, updateWrapper) != 1) {
            throw new PersistenceException("更新失败");
        }
    }

    /**
     * @param id
     * @Description 根据id查询博客
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: com.taoxier.taoxiblog.model.entity.Blog
     */
    @Override
    public Blog getBlogById(Long id) {
        Blog blog = this.getById(id);
        if (blog == null) {
            throw new NotFoundException("博客不存在");
        }
        //将浏览量设置为redis中的最新值
        /*
         * 这里如果出现异常，通常是手动修改过 MySQL 而没有通过后台管理，导致 Redis 和 MySQL 不同步
         * 从 Redis 中查出了 null，强转 int 时出现 NullPointerException
         *
         * 解决步骤：
         * 1.结束程序
         * 2.删除 Redis DB 中 blogViewsMap 这个 key（或者直接清空对应的整个 DB）
         * 3.重新启动程序
         */
        int view = (int) redisService.getValueByHashKey(RedisKeyConstants.BLOG_VIEWS_MAP, blog.getId());
        blog.setViews(view);
        return blog;
    }

    /**
     * @param id
     * @Description 查询博客标题
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: java.lang.String
     */
    @Override
    public String getTitleByBlogId(Long id) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getId, id);
        queryWrapper.select(Blog::getTitle);
        Blog blog = blogMapper.selectOne(queryWrapper);
        return blog != null ? blog.getTitle() : null;
    }

    /**
     * @param id
     * @Description 获取公开博客detail
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: com.taoxier.taoxiblog.model.vo.BlogDetailVO
     */
    @Override
    public BlogDetailVO getBlogByIdAndIsPublished(Long id) {
        BlogDetailVO blogDetail = blogMapper.getBlogDetailByIdAndIsPublished(id);
        if (blogDetail == null) {
            throw new NotFoundException("该博客不存在");
        }
        blogDetail.setContent(MarkdownUtils.markdownToHtmlExtensions(blogDetail.getContent()));
        /**
         * 将浏览量设置为Redis中的最新值
         * 这里如果出现异常，查看上面的注释说明
         */
        int view = (int) redisService.getValueByHashKey(RedisKeyConstants.BLOG_VIEWS_MAP, blogDetail.getId());
        blogDetail.setViews(view);
        return blogDetail;
    }

    /**
     * @param id
     * @Description 获取博客密码
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: java.lang.String
     */
    @Override
    public String getBlogPassword(Long id) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getId, id);
        queryWrapper.select(Blog::getPassword);
        Blog blog = blogMapper.selectOne(queryWrapper);
        return blog != null ? blog.getPassword() : null;
    }

    /**
     * @param blogDTO
     * @Description 更新博客
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBlog(BlogDTO blogDTO) {
        Blog blog = new Blog();
        try {
            BeanUtils.copyProperties(blog, blogDTO);
            if (blogDTO.getCategory() != null) {
                blog.setCategoryId(blogDTO.getCategory().getId());
            }
            if (blogMapper.updateById(blog) != 1) {
                throw new PersistenceException("更新博客失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PersistenceException("DTO转换为entity时出错");
        }
        deleteBlogRedisCache();
        redisService.saveKVToHash(RedisKeyConstants.BLOG_VIEWS_MAP, blog.getId(), blog.getViews());
    }

    /**
     * @param
     * @Description 统计公开博客的数量
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: int
     */
    @Override
    public int countBlogByIsPublished() {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getIsPublished, true);
        return Math.toIntExact(blogMapper.selectCount(queryWrapper));
    }

    /**
     * @param categoryId
     * @Description 按照分类id查询博客数量
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: int
     */
    @Override
    public int countBlogByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getCategoryId, categoryId);
        return Math.toIntExact(blogMapper.selectCount(queryWrapper));
    }

    /**
     * @param tagId
     * @Description 根据标签id查询博客数量
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: int
     */
    @Override
    public int countBlogByTagId(Long tagId) {
        LambdaQueryWrapper<BlogTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlogTag::getTagId, tagId);
        return Math.toIntExact(blogTagMapper.selectCount(queryWrapper));
    }

    /**
     * @param blogId
     * @Description 查询博客是否启用评论
     * @Author: taoxier
     * @Date: 2025/4/29
     * @Return: java.lang.Boolean
     */
    @Override
    public Boolean getCommentEnabledByBlogId(Long blogId) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getId, blogId);
        queryWrapper.select(Blog::getIsCommentEnabled);
        Blog blog = blogMapper.selectOne(queryWrapper);
        return blog != null ? blog.getIsCommentEnabled():null;
    }

    /**
    * @Description 查询博客是否公开
     * @param blogId
    * @Author: taoxier
    * @Date: 2025/4/29
    * @Return: java.lang.Boolean
    */
    @Override
    public Boolean getPublishedByBlogId(Long blogId){
        LambdaQueryWrapper<Blog> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getId, blogId);
        queryWrapper.select(Blog::getIsPublished);
        Blog blog = blogMapper.selectOne(queryWrapper);
        return blog != null ? blog.getIsPublished():null;
    }

}
