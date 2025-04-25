package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.mapper.CommentMapper;
import com.taoxier.taoxiblog.model.entity.Comment;
import com.taoxier.taoxiblog.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper,Comment> implements CommentService {
}
