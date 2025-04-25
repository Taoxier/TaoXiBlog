package com.taoxier.taoxiblog.model.vo;

import lombok.*;

/**
 * @Description ：评论管理页面按博客title查询评论
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class BlogIdAndTitleVO {
	private Long id;
	private String title;
}
