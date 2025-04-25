package com.taoxier.taoxiblog.model.vo;

import lombok.*;

/**
 * @Description ：分类和博客数量
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class CategoryBlogCountVO {
	private Long id;
	private String name;//分类名
	private Integer value;//分类下博客数量
}
