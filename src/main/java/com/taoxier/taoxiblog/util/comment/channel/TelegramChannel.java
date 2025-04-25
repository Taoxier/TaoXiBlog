package com.taoxier.taoxiblog.util.comment.channel;

import com.taoxier.taoxiblog.config.properties.BlogProperties;
import com.taoxier.taoxiblog.config.properties.TelegramProperties;
import com.taoxier.taoxiblog.enums.CommentPageEnum;
import com.taoxier.taoxiblog.model.dto.CommentDTO;
import com.taoxier.taoxiblog.util.StringUtils;
import com.taoxier.taoxiblog.util.comment.CommentUtils;
import com.taoxier.taoxiblog.util.telegram.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * @Description ：Telegram提醒方式
 * @Author taoxier
 * @Date 2025/4/22
 */
@Slf4j
@Lazy
@Component
public class TelegramChannel implements CommentNotifyChannel{

    private TelegramUtils telegramUtils;

    private BlogProperties blogProperties;

    private TelegramProperties telegramProperties;

    private SimpleDateFormat simpleDateFormat;

    public TelegramChannel(TelegramUtils telegramUtils, BlogProperties blogProperties, TelegramProperties telegramProperties) {
        this.telegramUtils = telegramUtils;
        this.blogProperties = blogProperties;
        this.telegramProperties = telegramProperties;

        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        log.info("TelegramChannel instantiating");
        telegramUtils.setWebhook();
    }

    /**
    * @Description 发送Telegram消息提醒我自己
     * @param comment
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    @Override
    public void notifyMyself(CommentDTO comment) {
        String url = telegramProperties.getApi() + telegramProperties.getToken() + TelegramUtils.SEND_MESSAGE;
        String content = getContent(comment);
        Map<String, Object> messageBody = telegramUtils.getMessageBody(content);
        telegramUtils.sendByAutoCheckReverseProxy(url, messageBody);
    }

    /**
    * @Description 根据传入的 CommentDTO 对象生成一段包含新评论详细信息的 HTML 格式字符串
     * @param comment
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String
    */
    private String getContent(CommentDTO comment) {
        CommentPageEnum commentPageEnum = CommentUtils.getCommentPageEnum(comment);
        return String.format(
                "<b>您的文章<a href=\"%s\">《%s》</a>有了新的评论~</b>\n" +
                        "\n" +
                        "<b>%s</b> 给您的评论：\n" +
                        "\n" +
                        "<pre>%s</pre>\n" +
                        "\n" +
                        "<b>其他信息：</b>\n" +
                        "评论ID：<code>%d</code>\n" +
                        "IP：%s\n" +
                        "%s" +
                        "时间：<u>%s</u>\n" +
                        "邮箱：<code>%s</code>\n" +
                        "%s" +
                        "状态：%s [<a href=\"%s\">管理评论</a>]\n",
                blogProperties.getView() + commentPageEnum.getPath(),
                commentPageEnum.getTitle(),
                comment.getNickname(),
                comment.getContent(),
                comment.getId(),
                comment.getIp(),
                StringUtils.isEmpty(comment.getQq()) ? "" : "QQ：" + comment.getQq() + "\n",
                simpleDateFormat.format(comment.getCreateTime()),
                comment.getEmail(),
                StringUtils.isEmpty(comment.getWebsite()) ? "" : "网站：" + comment.getWebsite() + "\n",
                comment.getPublished() ? "公开" : "待审核",
                blogProperties.getCms() + "/blog/comment/list"
        );
    }

}
