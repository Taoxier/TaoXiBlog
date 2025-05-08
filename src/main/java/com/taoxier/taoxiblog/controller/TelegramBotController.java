package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.config.properties.TelegramProperties;
import com.taoxier.taoxiblog.constant.CommentConstants;
import com.taoxier.taoxiblog.model.dto.TgMessageDTO;
import com.taoxier.taoxiblog.util.telegram.TelegramBotMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description ：目前用不上
 *  处理TelegramBot接收到的新消息
 *  如果不使用Telegram方式，即comment.notify.channel != tg，则该类不会被实例化，对应的Webhook接口也不会被创建
 * @Author taoxier
 * @Date 2025/5/8
 */
@Slf4j
@ConditionalOnProperty(name = "comment.notify.channel", havingValue = CommentConstants.TELEGRAM)
@RestController
public class TelegramBotController {
    @Autowired
    private TelegramBotMsgHandler msgHandler;
    @Autowired
    private TelegramProperties telegramProperties;

    /**
    * @Description webhook方式监听bot收到的新消息
     * @param message
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: void
    */
    @PostMapping("/tg/${tg.bot.token}")
    public void getUpdate(@RequestBody TgMessageDTO message) {
        log.info("Telegram bot receive message: {}", message);
        //判断消息是否是自己发出的
        if (message != null && message.getMessage() != null && message.getMessage().getChat() != null
                && telegramProperties.getChatId().equals(message.getMessage().getChat().getId())) {
            //判断是不是正常的文本消息
            if (message.getMessage().getText() != null) {
                //处理消息
                msgHandler.processCommand(message.getMessage().getText());
            }
        }
    }
}
