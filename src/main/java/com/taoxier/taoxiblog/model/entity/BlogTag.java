package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class BlogTag implements Serializable {
    private Long blogId;

    private Long tagId;

    private static final long serialVersionUID = 1L;
}