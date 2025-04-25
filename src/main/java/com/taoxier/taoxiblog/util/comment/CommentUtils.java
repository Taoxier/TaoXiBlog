package com.taoxier.taoxiblog.util.comment;

import com.taoxier.taoxiblog.config.properties.BlogProperties;
import com.taoxier.taoxiblog.enums.CommentPageEnum;
import com.taoxier.taoxiblog.model.dto.CommentDTO;
import com.taoxier.taoxiblog.util.MailUtils;
import com.taoxier.taoxiblog.util.comment.channel.ChannelFactory;
import com.taoxier.taoxiblog.util.comment.channel.CommentNotifyChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description ：评论工具类
 * @Author taoxier
 * @Date 2025/4/22
 */
public class CommentUtils {
    @Autowired
    private BlogProperties blogProperties;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private AboutService aboutService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    private static BlogService blogService;

    private CommentNotifyChannel notifyChannel;
    /**
     * 新评论是否默认公开
     */
    private Boolean commentDefaultOpen;

    @Autowired
    public void setBlogService(BlogService blogService) {
        CommentUtils.blogService = blogService;
    }

    @Value("${comment.notify.channel}")
    public void setNotifyChannel(String channelName) {
        this.notifyChannel = ChannelFactory.getChannel(channelName);
    }

    @Value("${comment.default-open}")
    public void setCommentDefaultOpen(Boolean commentDefaultOpen) {
        this.commentDefaultOpen = commentDefaultOpen;
    }


    /**
     *   @Description :判断是否发送提醒
     *   6种情况：
     *   1.我以父评论提交：不用提醒
     *   2.我回复我自己：不用提醒
     *   3.我回复访客的评论：只提醒该访客
     *   4.访客以父评论提交：只提醒我自己
     *   5.访客回复我的评论：只提醒我自己
     *   6.访客回复访客的评论(即使是他自己先前的评论)：提醒我自己和他回复的评论
     * @param comment          当前收到的评论
     * @param isVisitorComment 是否访客评论
     * @param parentComment    父评论
     * @Author: taoxier
     * @Date: 2025/4/22
     * @Return: void
     */
    public void judgeSendNotify(CommentDTO comment, boolean isVisitorComment, com.taoxier.taoxiblog.util.comment.Comment parentComment) {
        if (parentComment != null && !parentComment.getAdminComment() && parentComment.getNotice()) {
            //我回复访客的评论，且对方接收提醒，邮件提醒对方(3)
            //访客回复访客的评论(即使是他自己先前的评论)，且对方接收提醒，邮件提醒对方(6)
            sendMailToParentComment(parentComment, comment);
        }
        if (isVisitorComment) {
            //访客以父评论提交，只提醒我自己(4)
            //访客回复我的评论，提醒我自己(5)
            //访客回复访客的评论，不管对方是否接收提醒，都要提醒我有新评论(6)
            notifyMyself(comment);
        }
    }

    /**
    * @Description 发送邮件提醒回复对象
     * @param parentComment  父评论
     * @param comment  当前收到的评论
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    private void sendMailToParentComment(com.taoxier.taoxiblog.util.comment.Comment parentComment, CommentDTO comment){
        CommentPageEnum commentPageEnum = getCommentPageEnum(comment);
        Map<String, Object> map = new HashMap<>(16);
        map.put("parentNickname", parentComment.getNickname());
        map.put("nickname", comment.getNickname());
        map.put("title", commentPageEnum.getTitle());
        map.put("time", comment.getCreateTime());
        map.put("parentContent", parentComment.getContent());
        map.put("content", comment.getContent());
        map.put("url", blogProperties.getView() + commentPageEnum.getPath());
        String toAccount = parentComment.getEmail();
        String subject = "您在 " + blogProperties.getName() + " 的评论有了新回复";
        mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "guest.html");
    }

    /**
    * @Description 通过指定方式通知自己
     * @param comment
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    private void notifyMyself(CommentDTO comment){
        notifyChannel.notifyMyself(comment);
    }

    /**
    * @Description 获取评论对应的页面
     * @param comment
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: com.taoxier.taoxiblog.enums.CommentPageEnum
    */
    public static CommentPageEnum getCommentPageEnum(CommentDTO comment){
        CommentPageEnum commentPageEnum=CommentPageEnum.UNKNOWN;
        switch (comment.getPage()){
            case 0:
                /*
                普通博客
                 */
                commentPageEnum=CommentPageEnum.BLOG;
                commentPageEnum.setTitle(blogService.getTileBlogId(comment.getBlogId()));
                commentPageEnum.setPath("/blog"+comment.getBlogId());
                break;
            case 1:
                /*
                关于我的页面
                 */
                commentPageEnum=CommentPageEnum.ABOUT;
            case 2:
                /*
                友链页面
                 */
                commentPageEnum=CommentPageEnum.FRIEND;
                break;
            default:
                break;
        }
        return commentPageEnum;
    }


    public
}
