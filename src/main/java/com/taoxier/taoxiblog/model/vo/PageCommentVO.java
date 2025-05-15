package com.taoxier.taoxiblog.model.vo;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description ：页面评论
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class PageCommentVO {
	private Long id;
	private String nickname;//昵称
	private String content;//评论内容
	private String avatar;//头像(图片路径)
	private Date createTime;//评论时间
	private String website;//个人网站
	private Boolean isAdminComment;//博主回复
	private String parentCommentId;//父评论id
	private String parentCommentNickname;//父评论昵称

	private List<PageCommentVO> replyComments = new ArrayList<>();//回复该评论的评论
}
