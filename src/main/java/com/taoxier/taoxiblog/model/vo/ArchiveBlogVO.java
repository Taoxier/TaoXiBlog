package com.taoxier.taoxiblog.model.vo;

import lombok.*;

/**
 * @Description ：归档页面博客简要信息
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class ArchiveBlogVO {
	private Long id;
	private String title;
	private String day;
	private String password;
	private Boolean privacy;
}
