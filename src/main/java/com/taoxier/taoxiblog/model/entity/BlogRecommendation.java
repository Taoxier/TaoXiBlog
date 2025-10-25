package com.taoxier.taoxiblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author taoxier
 * @Date 2025/10/24 下午5:01
 * @描述
 */
@Data
@TableName("blog_recommendation")
public class BlogRecommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long blogId; // 关联的博客ID
    private String recommendedBlogIds; // 推荐的博客ID列表（JSON数组字符串）
    private Date createTime;
    private Date updateTime;
}
