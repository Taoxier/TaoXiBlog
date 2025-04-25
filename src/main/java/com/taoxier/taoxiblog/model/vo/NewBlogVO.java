package com.taoxier.taoxiblog.model.vo;

import lombok.*;

/**
 * @Description ：最新推荐博客
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class NewBlogVO {
	private Long id;
	private String title;
	private String password;
	private Boolean privacy;
}
