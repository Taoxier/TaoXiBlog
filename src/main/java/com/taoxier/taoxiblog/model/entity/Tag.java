package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class Tag implements Serializable {
    private Long id;

    @TableField("tag_name")
    private String name;

    /**
     * 标签颜色(可选)
     */
    private String color;

    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private List<Blog> blogs = new ArrayList<>();//该标签下的博客文章
}