package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import com.taoxier.taoxiblog.model.vo.BlogIdAndTitleVO;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
@TableName(value = "comment")
public class CommentEntity implements Serializable {
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 头像(图片路径)
     */
    private String avatar;

    /**
     * 评论时间
     */
    private Date createTime;

    /**
     * 评论者ip地址
     */
    private String ip;

    /**
     * 公开或回收站
     */
    private Boolean isPublished;

    /**
     * 博主回复
     */
    private Boolean isAdminComment;

    /**
     * 0普通文章，1关于我页面，2友链页面
     */
    private Integer page;

    /**
     * 接收邮件提醒
     */
    private Boolean isNotice;

    /**
     * 所属的文章
     */
    private Long blogId;

    /**
     * 父评论id，-1为根评论
     */
    private Long parentCommentId;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 如果评论昵称为QQ号，则将昵称和头像置为QQ昵称和QQ头像，并将此字段置为QQ号备份
     */
    private String qq;

    private static final long serialVersionUID = 1L;

    private BlogIdAndTitleVO blog;//所属的文章
    private List<CommentEntity> replyComments = new ArrayList<>();//回复该评论的评论
}