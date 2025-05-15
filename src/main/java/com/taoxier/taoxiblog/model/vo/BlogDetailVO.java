package com.taoxier.taoxiblog.model.vo;

import com.taoxier.taoxiblog.model.entity.Category;
import com.taoxier.taoxiblog.model.entity.Tag;
import lombok.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description ：博客详情
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class BlogDetailVO {
	private Long id;
	private String title;//文章标题
	private String content;//文章正文
	private Boolean isAppreciation;//赞赏开关
	private Boolean isCommentEnabled;//评论开关
	private Boolean isTop;//是否置顶
	private Date createTime;//创建时间
	private Date updateTime;//更新时间
	private Integer views;//浏览次数
	private Integer words;//文章字数
	private Integer readTime;//阅读时长(分钟)
	private String password;//密码保护

	private Category category;//文章分类
	private List<Tag> tags = new ArrayList<>();//文章标签
}
