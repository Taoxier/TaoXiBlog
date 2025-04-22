package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class Friend implements Serializable {
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 描述
     */
    private String description;

    /**
     * 站点
     */
    private String website;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 公开或隐藏
     */
    private Boolean isPublished;

    /**
     * 点击次数
     */
    private Integer views;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}