package com.taoxier.taoxiblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taoxier.taoxiblog.model.entity.BlogEmbedding;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author taoxier
 * @Date 2025/10/24 下午5:01
 * @描述
 */
@Mapper
@Repository
public interface BlogEmbeddingMapper extends BaseMapper<BlogEmbedding> {
}
