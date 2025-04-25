package com.taoxier.taoxiblog.model.vo;

import lombok.*;

import java.util.Date;

/**
 * @Description ：随机博客
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class RandomBlogVO {
	private Long id;
	private String title;//文章标题
	private String firstPicture;//文章首图，用于随机文章展示
	private Date createTime;//创建时间
	private String password;//文章密码
	private Boolean privacy;//是否私密文章
}
