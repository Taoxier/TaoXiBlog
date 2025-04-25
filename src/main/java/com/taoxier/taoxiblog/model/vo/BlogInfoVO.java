package com.taoxier.taoxiblog.model.vo;

import com.taoxier.taoxiblog.model.entity.Category;
import com.taoxier.taoxiblog.model.entity.Tag;
import lombok.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description ：博客简要信息
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class BlogInfoVO {
	private Long id;
	private String title;//文章标题
	private String description;//描述
	private Date createTime;//创建时间
	private Integer views;//浏览次数
	private Integer words;//文章字数
	private Integer readTime;//阅读时长(分钟)
	private Boolean top;//是否置顶
	private String password;//文章密码
	private Boolean privacy;//是否私密文章

	private Category category;//文章分类
	private List<Tag> tags = new ArrayList<>();//文章标签
}
