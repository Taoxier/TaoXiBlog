package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.dto.AIContentRequestDTO;
import com.taoxier.taoxiblog.model.dto.AIContentResponseDTO;
import com.taoxier.taoxiblog.model.dto.BlogDTO;
import com.taoxier.taoxiblog.model.dto.BlogVisibilityDTO;
import com.taoxier.taoxiblog.model.entity.Blog;
import com.taoxier.taoxiblog.model.entity.Category;
import com.taoxier.taoxiblog.model.entity.Tag;
import com.taoxier.taoxiblog.model.entity.User;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.*;
import com.taoxier.taoxiblog.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/7
 */
@RestController
@RequestMapping("/admin")
//@Api(tags = "blog管理", value = "blog管理接口")
public class BlogAdminController {
    @Autowired
    BlogService blogService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    TagService tagService;
    @Autowired
    CommentService commentService;
    @Autowired
    AIContentService aiContentService;
    @Autowired
    KeyWordExtractService keyWordExtractService;

    /**
     * @param title      按标题模糊查询
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @Description 获取博客文章列表
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @GetMapping("/blogs")
    public ResultVO blogs(@RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") Integer categoryId, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "create_time desc";//排序
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<Blog> pageInfo = new PageInfo<>(blogService.getListByTitleAndCategoryId(title, categoryId));
        List<Category> categories = categoryService.getCategoryList();
        Map<String, Object> map = new HashMap<>(4);
        map.put("blogs", pageInfo);
        map.put("categories", categories);
        return ResultVO.ok("请求成功", map);
    }

    /**
     * @param id
     * @Description 删除博客文章、删除博客文章下的所有评论、同时维护 blog_tag 表
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @OperationLogger("删除博客")
    @DeleteMapping("/blog")
    public ResultVO delete(@RequestParam Long id) {
        blogService.deleteBlogTagByBlogId(id);
        blogService.deleteBlogById(id);
        commentService.deleteCommentsByBlogId(id);
        return ResultVO.ok("删除成功");
    }

    /**
     * @param
     * @Description 获取分类列表和标签列表
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @GetMapping("/categoryAndTag")
    public ResultVO categoryAndTag() {
        List<Category> categories = categoryService.getCategoryList();
        List<Tag> tags = tagService.getTagList();
        Map<String, Object> map = new HashMap<>(4);
        map.put("categories", categories);
        map.put("tags", tags);
        return ResultVO.ok("请求成功", map);
    }

    /**
     * @param id
     * @param top
     * @Description 更新博客置顶状态
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @OperationLogger("更新博客置顶状态")
    @PutMapping("/blog/top")
    public ResultVO updateTop(@RequestParam Long id, @RequestParam Boolean top) {
        blogService.updateBlogTop(id, top);
        return ResultVO.ok("操作成功");
    }

    /**
     * @param id
     * @param recommend
     * @Description 更新博客推荐状态
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @OperationLogger("更新博客推荐状态")
    @PutMapping("/blog/recommend")
    public ResultVO updateRecommend(@RequestParam Long id, @RequestParam Boolean recommend) {
        blogService.updateBlogRecommendById(id, recommend);
        return ResultVO.ok("操作成功");
    }

    /**
     * @param id
     * @param blogVisibilityDTO
     * @Description 更新博客可见性状态
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @OperationLogger("更新博客可见性状态")
    @PutMapping("blog/{id}/visibility")
    public ResultVO updateVisibility(@PathVariable Long id, @RequestBody BlogVisibilityDTO blogVisibilityDTO) {
        blogService.updateBlogVisibilityById(id, blogVisibilityDTO);
        return ResultVO.ok("操作成功");
    }

    /**
     * @param id
     * @Description 根据id获取博客详情
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @GetMapping("/blog")
    public ResultVO getBlog(@RequestParam Long id) {
        Blog blog = blogService.getBlogById(id);
        return ResultVO.ok("获取成功", blog);
    }

    /**
    * @Description 保存草稿或发布新文章
     * @param blogDTO
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("发布博客")
    @PostMapping("/blog")
    @ApiOperation(value = "发布博客")
    public ResultVO saveBlog(@RequestBody BlogDTO blogDTO) {
        System.out.println("发布："+blogDTO.toString());
        return getResult(blogDTO,"save");
    }

    @OperationLogger("更新博客")
    @PutMapping("/blog")
    public ResultVO updateBlog(@RequestBody BlogDTO BlogDTO) {
        return getResult(BlogDTO,"update");
    }


    @OperationLogger("ai润色博客内容")
    @PostMapping("/blog/polish")
    @ApiOperation(value = "ai润色博客内容")
    public ResultVO polishContent(@RequestBody AIContentRequestDTO requestDTO){
        AIContentResponseDTO responseDTO=aiContentService.polishContent(requestDTO.getContent());
        if (responseDTO.isSuccess()){
            return ResultVO.ok("润色成功",responseDTO.getResult());
        }else{
            return ResultVO.error(responseDTO.getErrorMsg());
        }
    }


    @OperationLogger("AI推荐标签")
    @PostMapping("/blog/recommend-tags")
    public ResultVO recommendTags(@RequestBody AIContentRequestDTO requestDTO) {
        String content = requestDTO.getContent();
        if (StringUtils.isEmpty(content)){
            return ResultVO.error("博客内容不能为空");
        }

        // 调用百度API前打印内容
        System.out.println("提取关键词的内容："+content);
        //调用ai提取关键词
        List<String> recommendTagNames=keyWordExtractService.extractKeywords(content,6);
        // 打印API返回的关键词列表
        System.out.println("百度API返回的关键词："+recommendTagNames);

        //检查是否已存在
        List<Map<String,Object>> result=recommendTagNames.stream().map(tagName -> {
            Map<String,Object> tagInfo=new HashMap<>();
            tagInfo.put("name",tagName);

            Tag existingTag=tagService.getTagByName(tagName);
            tagInfo.put("exists",existingTag!=null);//标记是否已存在
            tagInfo.put("id",existingTag!=null?existingTag.getId():null);//已存在的标签返回id
            return tagInfo;
        }).collect(Collectors.toList());

        return ResultVO.ok("标签推荐成功",result);
    }


    /**
    * @Description 执行博客添加或更新操作：校验参数是否合法，添加分类、标签，维护博客标签关联表
     * @param blogDTO
     * @param type
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    private ResultVO getResult(BlogDTO blogDTO, String type) {

        //验证普通字段
        if (StringUtils.isEmpty(blogDTO.getTitle(), blogDTO.getFirstPicture(), blogDTO.getContent(), blogDTO.getDescription()) ) {
            return ResultVO.error("参数有误");
        }

        //处理分类
        Object category = blogDTO.getCate();
        if (category == null) {
            return ResultVO.error("分类不能为空");
        }

        if (category instanceof Integer) {
            //选择了已存在的分类
            Category c = categoryService.getCategoryById(((Integer) category).longValue());
            blogDTO.setCategory(c);
        } else if (category instanceof String) {
            //添加新分类
            //查询分类是否已经存在
            Category cate = categoryService.getCategoryByName((String) category);
            if (cate != null) {
                return ResultVO.error("不可以添加已经存在的分类");
            }

            Category c = new Category();
            c.setName((String) category);
            categoryService.saveCategory(c);
            blogDTO.setCategory(c);
        } else {
            return ResultVO.error("分类不正确");
        }

        //处理标签
        List<Object> tagList = blogDTO.getTagList();
        List<Tag> tags = new ArrayList<>();
        for (Object t : tagList) {
            if (t instanceof Integer) {
                //选择了已存在的标签
                Tag tag = tagService.getTagById(((Integer) t).longValue());
                tags.add(tag);
            } else if (t instanceof String) {
                //添加新标签
                //查询标签是否已存在
                Tag tag1 = tagService.getTagByName((String) t);
                if (tag1 != null) {
                    return ResultVO.error("不可以添加已存在的标签");
                }
                Tag newTag=new Tag();
                newTag.setName((String) t);
                tagService.saveTag(newTag);
                tags.add(newTag);
            }else {
                return ResultVO.error("标签不正确");
            }
        }

        Date date=new Date();

        //计算字数
        if (blogDTO.getContent()!=null && blogDTO.getContent().length()>0) {
            String plainText=blogDTO.getContent().replace("\\<.*?\\>","");
            String contentWithoutUrls=plainText.replaceAll("https?://\\S+","");
            int wordCount=contentWithoutUrls.length();
            blogDTO.setWords(wordCount);
        }
            blogDTO.setReadTime((int)Math.max(1,Math.round(blogDTO.getWords() / 300.0)));//计算阅读时长，至少1分钟
        System.out.println("result时长："+blogDTO.getReadTime());
        if (blogDTO.getViews() == null || blogDTO.getViews() < 0) {
            blogDTO.setViews(0);
        }
        if ("save".equals(type)){
            blogDTO.setCreateTime(date);
            blogDTO.setUpdateTime(date);
            User user=new User();
            user.setId(1L);
            blogDTO.setUser(user);

            Blog newBlog=blogService.saveBlog(blogDTO);
            //关联博客和标签(维护 blog_tag 表)
            for (Tag t:tags){
                blogService.saveBlogTag(newBlog.getId(),t.getId());
            }
            return ResultVO.ok("添加成功");
        }else {
            blogDTO.setUpdateTime(date);
            Blog newBlog=blogService.updateBlog(blogDTO);
//            System.out.println("更新："+newBlog.toString());
            //关联博客和标签(维护 blog_tag 表)
            blogService.deleteBlogTagByBlogId(newBlog.getId());
            for (Tag t:tags){
                blogService.saveBlogTag(newBlog.getId(),t.getId());
            }
            return ResultVO.ok("更新成功");
        }
    }

}
