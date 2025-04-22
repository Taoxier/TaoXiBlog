package com.taoxier.taoxiblog.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Objects;

/**
 * @Description ：邮件工具类
 * @Author taoxier
 * @Date 2025/4/22
 */
@Component
public class MailUtils {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private MailProperties mailProperties;
    @Autowired
    TemplateEngine templateEngine;

    /**
    * @Description 异步发送简单邮件
     * @param toAccount
     * @param subject
     * @param content
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    //标识一个方法是异步执行的
    @Async
    public void sendSimpleMail(String toAccount,String subject,String content){
        try {
            SimpleMailMessage mailMessage=new SimpleMailMessage();
            mailMessage.setFrom(mailProperties.getUsername());
            mailMessage.setTo(toAccount);
            mailMessage.setSubject(subject);
            mailMessage.setText(content);
            javaMailSender.send(mailMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
    * @Description  异步发送带有 HTML 模板的邮件
     * @param map
     * @param toAccount
     * @param subject
     * @param template
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    @Async
    public void senHtmlTemplateMail(Map<String, Object> map,String toAccount,String subject,String template){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            Context context = new Context();
            context.setVariables(map);
            String process = templateEngine.process(template, context);
            messageHelper.setFrom(mailProperties.getUsername());
            messageHelper.setTo(toAccount);
            messageHelper.setSubject(subject);
            messageHelper.setText(process, true);
            javaMailSender.send(mimeMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
