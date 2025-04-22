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
public class Moment implements Serializable {
    private Long id;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 点赞数量
     */
    private Integer likes;

    /**
     * 是否公开
     */
    private Boolean isPublished;

    private static final long serialVersionUID = 1L;
}