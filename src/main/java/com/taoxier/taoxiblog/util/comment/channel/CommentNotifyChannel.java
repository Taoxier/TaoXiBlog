package com.taoxier.taoxiblog.util.comment.channel;


import com.taoxier.taoxiblog.model.dto.CommentDTO;

/**
 * @Description ：评论提醒方式
 * @Author taoxier
 * @Date 2025/4/22
 */
public interface CommentNotifyChannel {

    /**
    * @Description 通过指定方式通知自己
     * @param comment  当前收到的评论
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    void notifyMyself(CommentDTO comment);
}
