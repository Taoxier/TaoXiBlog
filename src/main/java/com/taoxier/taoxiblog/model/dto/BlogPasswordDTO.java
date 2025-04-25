package com.taoxier.taoxiblog.model.dto;

import lombok.Data;

/**
 * @Description ：受保护文章密码DTO
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class BlogPasswordDTO {
    private Long blogId;
    private String password;
}
