package com.taoxier.taoxiblog.model.vo;

import lombok.*;

/**
 * @Description ：标签和博客数量
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class TagBlogCountVO {
	private Long id;
	private String name;//标签名
	private Integer value;//标签下博客数量
}
