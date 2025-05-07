package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.dto.CommentDTO;
import com.taoxier.taoxiblog.model.entity.Comment;
import com.taoxier.taoxiblog.model.vo.PageCommentVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface CommentService extends IService<Comment> {
    List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

    List<PageCommentVO> getPageCommentList(Integer page, Long blogId, Long parentCommentId);

    Comment getCommentById(Long id);

    void getReplyComments(List<PageCommentVO> tmpComments, List<PageCommentVO> comments);

    List<PageCommentVO> getPageCommentListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

    @Transactional(rollbackFor = Exception.class)
    void updateCommentPublishedById(Long commentId, Boolean published);

    @Transactional(rollbackFor = Exception.class)
    void updateCommentNoticeById(Long commentId, Boolean notice);

    @Transactional(rollbackFor = Exception.class)
    void deleteCommentById(Long commentId);

    @Transactional(rollbackFor = Exception.class)
    void deleteCommentsByBlogId(Long blogId);

    @Transactional(rollbackFor = Exception.class)
    void updateComment(Comment comment);

    int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished);

    @Transactional(rollbackFor = Exception.class)
    void saveComment(CommentDTO commentDTO);

    void delete(Comment comment);

    void hideComment(Comment comment);

    List<Comment> getAllReplyComments(Long parentCommentId);
}
