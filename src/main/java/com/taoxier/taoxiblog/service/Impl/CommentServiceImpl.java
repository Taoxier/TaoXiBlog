package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.CommentMapper;
import com.taoxier.taoxiblog.model.dto.CommentDTO;
import com.taoxier.taoxiblog.model.entity.CommentEntity;
import com.taoxier.taoxiblog.model.vo.PageCommentVO;
import com.taoxier.taoxiblog.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    /**
    * @Description 查询评论及子评论
     * @param page
     * @param blogId
     * @param parentCommentId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.CommentEntity>
    */
    @Override
    public List<CommentEntity> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId) {
        QueryWrapper<CommentEntity> wrapper = new QueryWrapper<>();
        if (page != null) {
            wrapper.eq("c.page", page);
        }
        if (page == 0 && blogId != null) {
            wrapper.eq("c.blog_id", blogId);
        }
        wrapper.eq("c.parent_comment_id", parentCommentId);
        List<CommentEntity> comments = commentMapper.selectList(wrapper);
        for (CommentEntity c : comments) {
            List<CommentEntity> replyComments = getListByPageAndParentCommentId(page, blogId, c.getId());
            c.setReplyComments(replyComments);
        }
        return comments;
    }

    /**
    * @Description
     * @param page
     * @param blogId
     * @param parentCommentId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.PageCommentVO>
    */
    @Override
    public List<PageCommentVO> getPageCommentList(Integer page, Long blogId, Long parentCommentId) {
        List<PageCommentVO> comments = getPageCommentListByPageAndParentCommentId(page, blogId, parentCommentId);
        for (PageCommentVO c : comments) {
            List<PageCommentVO> tmpComments = new ArrayList<>();
            getReplyComments(tmpComments, c.getReplyComments());
            //对于两列评论来说，按时间顺序排列应该比树形更合理些
            //排序一下
            Comparator<PageCommentVO> comparator = Comparator.comparing(PageCommentVO::getCreateTime);
            tmpComments.sort(comparator);
            c.setReplyComments(tmpComments);
        }
        return comments;
    }

    /**
    * @Description 根据id查找评论
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: com.taoxier.taoxiblog.model.entity.CommentEntity
    */
    @Override
    public CommentEntity getCommentById(Long id) {
        CommentEntity comment = getById(id);
        if (comment == null) {
            throw new PersistenceException("评论不存在");
        }
        return comment;
    }

    /**
    * @Description 将所有子评论递归取出到一个List中
     * @param tmpComments
     * @param comments
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void getReplyComments(List<PageCommentVO> tmpComments, List<PageCommentVO> comments) {
        for (PageCommentVO c : comments) {
            tmpComments.add(c);
            getReplyComments(tmpComments, c.getReplyComments());
        }
    }

    /**
    * @Description 查询页面展示的评论List
     * @param page
     * @param blogId
     * @param parentCommentId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.PageCommentVO>
    */
    @Override
    public List<PageCommentVO> getPageCommentListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId) {
        QueryWrapper<PageCommentVO> wrapper = new QueryWrapper<>();
        wrapper.eq("c1.page", page);
        if (page == 0 && blogId != null) {
            wrapper.eq("c1.blog_id", blogId);
        }
        wrapper.eq("c1.parent_comment_id", parentCommentId);
        wrapper.eq("c1.is_published", true);
        wrapper.orderByDesc("c1.create_time");
        List<PageCommentVO> comments = commentMapper.selectPageCommentList(wrapper);
        for (PageCommentVO c : comments) {
            List<PageCommentVO> replyComments = getPageCommentListByPageAndParentCommentId(page, blogId, c.getId());
            c.setReplyComments(replyComments);
        }
        return comments;
    }

    /**
    * @Description 更新评论公开状态
     * @param commentId
     * @param published
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCommentPublishedById(Long commentId, Boolean published) {
        if (!published) {
            List<CommentEntity> comments = getAllReplyComments(commentId);
            for (CommentEntity c : comments) {
                hideComment(c);
            }
        }
        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);
        comment.setIsPublished(published);
        if (!updateById(comment)) {
            throw new PersistenceException("操作失败");
        }
    }

    /**
    * @Description 更新评论接收邮件提醒状态
     * @param commentId
     * @param notice
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCommentNoticeById(Long commentId, Boolean notice) {
        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);
        comment.setIsNotice(notice);
        if (!updateById(comment)) {
            throw new PersistenceException("操作失败");
        }
    }

    /**
    * @Description 按id删除评论
     * @param commentId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCommentById(Long commentId) {
        List<CommentEntity> comments = getAllReplyComments(commentId);
        for (CommentEntity c : comments) {
            delete(c);
        }
        if (!removeById(commentId)) {
            throw new PersistenceException("评论删除失败");
        }
    }

    /**
    * @Description 按博客id删除博客下所有评论
     * @param blogId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCommentsByBlogId(Long blogId) {
        QueryWrapper<CommentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("blog_id", blogId);
        remove(wrapper);
    }

    /**
    * @Description 更新评论
     * @param comment
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateComment(CommentEntity comment) {
        if (!updateById(comment)) {
            throw new PersistenceException("评论修改失败");
        }
    }

    /**
    * @Description 按页面查询评论数量
     * @param page
     * @param blogId
     * @param isPublished
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: int
    */
    @Override
    public int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished) {
        QueryWrapper<CommentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("page", page);
        if (isPublished != null) {
            wrapper.eq("is_published", isPublished);
        }
        if (page == 0 && blogId != 0) {
            wrapper.eq("blog_id", blogId);
        }
        return (int) count(wrapper);
    }


    /**
    * @Description 保存评论
     * @param commentDTO
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveComment(CommentDTO commentDTO) {
        CommentEntity newComment = new CommentEntity();
        // 这里需要根据 CommentDTO 的属性赋值给 CommentEntity
        BeanUtils.copyProperties(commentDTO,newComment);
        if (!save(newComment)) {
            throw new PersistenceException("评论失败");
        }
    }

    /**
    * @Description 递归删评论
     * @param comment
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void delete(CommentEntity comment) {
        for (CommentEntity c : comment.getReplyComments()) {
            delete(c);
        }
        if (!removeById(comment.getId())) {
            throw new PersistenceException("评论删除失败");
        }
    }

    /**
    * @Description 递归隐藏评论
     * @param comment
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void hideComment(CommentEntity comment) {
        for (CommentEntity c : comment.getReplyComments()) {
            hideComment(c);
        }
        CommentEntity updateComment = new CommentEntity();
        updateComment.setId(comment.getId());
        updateComment.setIsPublished(false);
        if (!updateById(updateComment)) {
            throw new PersistenceException("操作失败");
        }
    }

    /**
    * @Description 按id递归查询子评论
     * @param parentCommentId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.CommentEntity>
    */
    @Override
    public List<CommentEntity> getAllReplyComments(Long parentCommentId) {
        QueryWrapper<CommentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_comment_id", parentCommentId);
        List<CommentEntity> comments = commentMapper.selectList(wrapper);
        for (CommentEntity c : comments) {
            List<CommentEntity> replyComments = getAllReplyComments(c.getId());
            c.setReplyComments(replyComments);
        }
        return comments;
    }
}
