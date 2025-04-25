package com.taoxier.taoxiblog.model.dto;

import lombok.Data;

/**
 * @Description ：博客可见性DTO
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class BlogVisibilityDTO {
    private Boolean appreciation;//赞赏开关
    private Boolean recommend;//推荐开关
    private Boolean commentEnabled;//评论开关
    private Boolean top;//是否置顶
    private Boolean published;//公开或私密
    private String password;//密码保护
}
