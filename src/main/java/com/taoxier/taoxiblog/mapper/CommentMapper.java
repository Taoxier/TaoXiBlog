package com.taoxier.taoxiblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taoxier.taoxiblog.model.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Mapper
@Repository
public interface CommentMapper extends BaseMapper<Comment> {

}