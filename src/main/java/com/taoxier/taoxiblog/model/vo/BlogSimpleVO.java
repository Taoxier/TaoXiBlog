package com.taoxier.taoxiblog.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author taoxier
 * @Date 2025/10/24 下午5:21
 * @描述
 */
@Data
public class BlogSimpleVO {
    private Long id;
    private String title; // 文章标题
    private Date createTime; // 创建时间
    private String firstPicture; // 文章首图
    private Integer views; // 浏览次数
}