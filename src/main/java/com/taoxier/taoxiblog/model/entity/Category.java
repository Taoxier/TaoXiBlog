package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class Category implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("category_name") // 数据库列名
    private String name;

    @TableField(exist = false)
    private List<Blog> blogs = new ArrayList<>();//该分类下的博客文章

    private static final long serialVersionUID = 1L;
}