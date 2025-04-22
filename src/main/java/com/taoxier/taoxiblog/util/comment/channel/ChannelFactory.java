package com.taoxier.taoxiblog.util.comment.channel;

import com.taoxier.taoxiblog.constant.CommentConstants;
import com.taoxier.taoxiblog.util.common.SpringContextUtils;


/**
 * @Description ：评论提醒方式
 * @Author taoxier
 * @Date 2025/4/22
 */
public class ChannelFactory {

    /**
    * @Description 创建评论提醒方式
     * @param channelName
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: com.taoxier.taoxiblog.util.comment.channel.CommentNotifyChannel
    */
    public static CommentNotifyChannel getChannel(String channelName){
        if (CommentConstants.TELEGRAM.equalsIgnoreCase(channelName)) {
            return SpringContextUtils.getBean("telegramChannel", CommentNotifyChannel.class);
        } else if (CommentConstants.MAIL.equalsIgnoreCase(channelName)) {
            return SpringContextUtils.getBean("mailChannel", CommentNotifyChannel.class);
        }
        throw new RuntimeException("Unsupported value in [application.properties]: [comment.notify.channel]");
    }
}
