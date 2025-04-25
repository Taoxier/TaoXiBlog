package com.taoxier.taoxiblog.util.comment.channel;

import com.taoxier.taoxiblog.config.properties.BlogProperties;
import com.taoxier.taoxiblog.enums.CommentPageEnum;
import com.taoxier.taoxiblog.model.dto.CommentDTO;
import com.taoxier.taoxiblog.util.MailUtils;
import com.taoxier.taoxiblog.util.comment.CommentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description ：邮件提醒方式
 * @Author taoxier
 * @Date 2025/4/22
 */
@Lazy
@Component
public class MailChannel implements CommentNotifyChannel{

    @Autowired
    private BlogProperties blogProperties;
    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private MailUtils mailUtils;


    /**
    * @Description 发送邮件提醒我自己
     * @param comment 当前收到的评论
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    @Override
    public void notifyMyself(CommentDTO comment) {
        CommentPageEnum commentPageEnum = CommentUtils.getCommentPageEnum(comment);
        Map<String, Object> map = new HashMap<>(16);
        map.put("title", commentPageEnum.getTitle());
        map.put("time", comment.getCreateTime());
        map.put("nickname", comment.getNickname());
        map.put("content", comment.getContent());
        map.put("ip", comment.getIp());
        map.put("email", comment.getEmail());
        map.put("status", comment.getPublished() ? "公开" : "待审核");
        map.put("url", blogProperties.getView() + commentPageEnum.getPath());
        map.put("manageUrl", blogProperties.getCms() + "/comments");
        String toAccount = mailProperties.getUsername();
        String subject = blogProperties.getName() + " 收到新评论";
        mailUtils.senHtmlTemplateMail(map, toAccount, subject, "owner.html");
    }
}
