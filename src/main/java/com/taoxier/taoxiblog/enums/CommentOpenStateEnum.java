package com.taoxier.taoxiblog.enums;

/**
 * @Description ：评论开放状态枚举类
 * @Author taoxier
 * @Date 2025/4/22
 */
public enum CommentOpenStateEnum {
    /**
     * 博客不存在，或博客未公开
     */
    NOT_FOUND,

    /**
     * 评论正常开放
     */
    OPEN,

    /**
     * 评论已关闭
     */
    CLOSE,

    /**
     * 评论所在页面需要密码
     */
    PASSWORD,
}
