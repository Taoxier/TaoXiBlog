package com.taoxier.taoxiblog.model.dto;

import lombok.Data;

/**
 * @Description ：友链DTO
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class FriendDTO {
    private Long id;
    private String nickname;//昵称
    private String description;//描述
    private String website;//站点
    private String avatar;//头像
    private Boolean published;//公开或隐藏
}
