package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class Tag implements Serializable {
    private Long id;

    private String tagName;

    /**
     * 标签颜色(可选)
     */
    private String color;

    private static final long serialVersionUID = 1L;

    private List<Blog> blogs = new ArrayList<>();//该标签下的博客文章
}