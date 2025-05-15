package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class Moment implements Serializable {
    @TableId(type = IdType.AUTO)
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
    @TableField("is_published")
    private Boolean published;

    private static final long serialVersionUID = 1L;
}