package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.annotation.VisitLogger;
import com.taoxier.taoxiblog.constant.JwtConstants;
import com.taoxier.taoxiblog.enums.VisitBehavior;
import com.taoxier.taoxiblog.model.dto.BlogPasswordDTO;
import com.taoxier.taoxiblog.model.entity.User;
import com.taoxier.taoxiblog.model.vo.*;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.Impl.UserServiceImpl;
import com.taoxier.taoxiblog.util.JwtUtils;
import com.taoxier.taoxiblog.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
public class BlogController {
    @Autowired
    BlogService blogService;
    @Autowired
    UserServiceImpl userService;

    /**
    * @Description 按置顶、创建时间排序 分页查询博客简要信息列表
     * @param pageNum
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.INDEX)
    @GetMapping("/blogs")
    public ResultVO blogs(@RequestParam(defaultValue = "1") Integer pageNum) {
        PageResultVO<BlogInfoVO> pageResult = blogService.getBlogInfoListByIsPublished(pageNum);
        return ResultVO.ok("请求成功", pageResult);
    }

    /**
    * @Description 按id获取公开博客详情
     * @param id
     * @param jwt
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.BLOG)
    @GetMapping("/blog")
    public ResultVO getBlog(@RequestParam Long id,
                          @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
        BlogDetailVO blog = blogService.getBlogByIdAndIsPublished(id);
        //对密码保护的文章校验Token
        if (!"".equals(blog.getPassword())) {
            if (JwtUtils.judgeTokenIsExist(jwt)) {
                try {
                    String subject = JwtUtils.getTokenBody(jwt).getSubject();
                    if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
                        //博主身份Token
                        String username = subject.replace(JwtConstants.ADMIN_PREFIX, "");
                        User admin = (User) userService.loadUserByUsername(username);
                        if (admin == null) {
                            return ResultVO.create(403, "博主身份Token已失效，请重新登录！");
                        }
                    } else {
                        //经密码验证后的Token
                        Long tokenBlogId = Long.parseLong(subject);
                        //博客id不匹配，验证不通过，可能博客id改变或客户端传递了其它密码保护文章的Token
                        if (!tokenBlogId.equals(id)) {
                            return ResultVO.create(403, "Token不匹配，请重新验证密码！");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResultVO.create(403, "Token已失效，请重新验证密码！");
                }
            } else {
                return ResultVO.create(403, "此文章受密码保护，请验证密码！");
            }
            blog.setPassword("");
        }
        blogService.updateViewsToRedis(id);
        return ResultVO.ok("获取成功", blog);
    }

    /**
    * @Description 校验受保护文章密码是否正确，正确则返回jwt
     * @param blogPassword
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.CHECK_PASSWORD)
    @PostMapping("/checkBlogPassword")
    public ResultVO checkBlogPassword(@RequestBody BlogPasswordDTO blogPassword) {
        String password = blogService.getBlogPassword(blogPassword.getBlogId());
        if (password.equals(blogPassword.getPassword())) {
            //生成有效时间一个月的Token
            String jwt = JwtUtils.generateToken(blogPassword.getBlogId().toString(), 1000 * 3600 * 24 * 30L);
            return ResultVO.ok("密码正确", jwt);
        } else {
            return ResultVO.create(403, "密码错误");
        }
    }

    /**
    * @Description  按关键字根据文章内容搜索公开且无密码保护的博客文章
     * @param query
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.SEARCH)
    @GetMapping("/searchBlog")
    public ResultVO searchBlog(@RequestParam String query) {
        //校验关键字字符串合法性
        if (StringUtils.isEmpty(query) || StringUtils.hasSpecialChar(query) || query.trim().length() > 20) {
            return ResultVO.error("参数错误");
        }
        List<SearchBlogVO> searchBlogs = blogService.getSearchBlogListByQueryAndIsPublished(query.trim());
        return ResultVO.ok("获取成功", searchBlogs);
    }
}
