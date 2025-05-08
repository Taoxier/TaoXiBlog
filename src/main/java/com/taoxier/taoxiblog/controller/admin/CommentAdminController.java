package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.entity.Blog;
import com.taoxier.taoxiblog.model.entity.CommentEntity;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.CommentService;
import com.taoxier.taoxiblog.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description ：评论
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class CommentAdminController {
    @Autowired
    CommentService commentService;
    @Autowired
    BlogService blogService;

    /**
    * @Description 按页面和博客id分页查询评论List
     * @param page
     * @param blogId
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/comments")
    public ResultVO comments(@RequestParam(defaultValue = "") Integer page,
                             @RequestParam(defaultValue = "") Long blogId,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<CommentEntity> comments = commentService.getListByPageAndParentCommentId(page, blogId, -1L);
        PageInfo<CommentEntity> pageInfo = new PageInfo<>(comments);
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 获取所有博客id和title 供评论分类的选择
     * @param
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/blogIdAndTitle")
    public ResultVO blogIdAndTitle() {
        List<Blog> blogs = blogService.getIdAndTitleList();
        return ResultVO.ok("请求成功", blogs);
    }

    /**
    * @Description 更新评论公开状态
     * @param id
     * @param published
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: Result
    */
    @OperationLogger("更新评论公开状态")
    @PutMapping("/comment/published")
    public ResultVO updatePublished(@RequestParam Long id, @RequestParam Boolean published) {
        commentService.updateCommentPublishedById(id, published);
        return ResultVO.ok("操作成功");
    }

    /**
    * @Description 更新评论接收邮件提醒状态
     * @param id
     * @param notice
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("更新评论邮件提醒状态")
    @PutMapping("/comment/notice")
    public ResultVO updateNotice(@RequestParam Long id, @RequestParam Boolean notice) {
        commentService.updateCommentNoticeById(id, notice);
        return ResultVO.ok("操作成功");
    }

    /**
    * @Description 修改评论
     * @param comment
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("修改评论")
    @PutMapping("/comment")
    public ResultVO updateComment(@RequestBody CommentEntity comment) {
        if (StringUtils.isEmpty(comment.getNickname(), comment.getAvatar(), comment.getEmail(), comment.getIp(), comment.getContent())) {
            return ResultVO.error("参数有误");
        }
        commentService.updateComment(comment);
        return ResultVO.ok("评论修改成功");
    }

    /**
    * @Description 按id删除该评论及其所有子评论
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("删除评论")
    @DeleteMapping("/comment")
    public ResultVO delete(@RequestParam Long id) {
        commentService.deleteCommentById(id);
        return ResultVO.ok("删除成功");
    }


}
