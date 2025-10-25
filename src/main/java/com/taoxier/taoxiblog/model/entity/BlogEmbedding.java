package com.taoxier.taoxiblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author taoxier
 * @Date 2025/10/24 下午5:00
 * @描述
 */
@Data
@TableName("blog_embedding")
public class BlogEmbedding {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long blogId; // 关联的博客ID
    private String embeddingVector; // 向量JSON字符串（存储List<Double>的JSON）
    private Date createTime;
    private Date updateTime;
}