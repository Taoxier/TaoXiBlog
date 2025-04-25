package com.taoxier.taoxiblog.model.vo;

import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class QqVO {
    /**
     * qq号
     */
    private Long qq;

    /**
     * qq昵称
     */
    private String name;

    /**
     * qq邮箱
     */
    private String email;

    /**
     * qq头像
     */
    private String avatar;
}
