package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.AUTO)
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
    @TableField("is_published")
    private Boolean published;

    /**
     * 博主回复
     */
    @TableField("is_admin_comment")
    private Boolean adminComment;

    /**
     * 0普通文章，1关于我页面，2友链页面
     */
    private Integer page;

    /**
     * 接收邮件提醒
     */
    @TableField("is_notice")
    private Boolean notice;

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

    @TableField(exist = false)
    private BlogIdAndTitleVO blog;//所属的文章
    @TableField(exist = false)
    private List<CommentEntity> replyComments = new ArrayList<>();//回复该评论的评论


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(Boolean adminComment) {
        this.adminComment = adminComment;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Boolean getNotice() {
        return notice;
    }

    public void setNotice(Boolean notice) {
        this.notice = notice;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public BlogIdAndTitleVO getBlog() {
        return blog;
    }

    public void setBlog(BlogIdAndTitleVO blog) {
        this.blog = blog;
    }

    public List<CommentEntity> getReplyComments() {
        return replyComments;
    }

    public void setReplyComments(List<CommentEntity> replyComments) {
        this.replyComments = replyComments;
    }
}