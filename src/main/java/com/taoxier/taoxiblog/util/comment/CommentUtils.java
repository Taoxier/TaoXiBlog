package com.taoxier.taoxiblog.util.comment;

import com.taoxier.taoxiblog.config.properties.BlogProperties;
import com.taoxier.taoxiblog.constant.PageConstants;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.enums.CommentOpenStateEnum;
import com.taoxier.taoxiblog.enums.CommentPageEnum;
import com.taoxier.taoxiblog.model.dto.CommentDTO;
import com.taoxier.taoxiblog.model.entity.CommentEntity;
import com.taoxier.taoxiblog.model.entity.User;
import com.taoxier.taoxiblog.model.vo.FriendInfo;
import com.taoxier.taoxiblog.service.*;
import com.taoxier.taoxiblog.util.*;
import com.taoxier.taoxiblog.util.comment.channel.ChannelFactory;
import com.taoxier.taoxiblog.util.comment.channel.CommentNotifyChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description ：评论工具类
 * @Author taoxier
 * @Date 2025/4/22
 */
@Component
@DependsOn("springContextUtils")
public class CommentUtils {
    @Autowired
    private BlogProperties blogProperties;//博客配置
    @Autowired
    private MailUtils mailUtils;//邮件配置
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
     *   1.我以顶级评论提交：不用提醒
     *   2.我回复我自己：不用提醒
     *   3.我回复访客的评论：只提醒该访客
     *   4.访客以顶级评论提交：只提醒我自己
     *   5.访客回复我的评论：只提醒我自己
     *   6.访客回复访客的评论(即使是访客自己先前的评论)：提醒我自己和他回复的评论
     * @param comment          当前收到的评论
     * @param isVisitorComment 是否访客评论
     * @param parentComment    顶级评论
     * @Author: taoxier
     * @Date: 2025/4/22
     * @Return: void
     */
    public void judgeSendNotify(CommentDTO comment, boolean isVisitorComment, CommentEntity parentComment) {
        if (parentComment != null && !parentComment.getIsAdminComment() && parentComment.getIsNotice()) {
            //我回复访客的评论，且对方接收提醒，邮件提醒对方(3)
            //访客回复访客的评论(即使是访客自己先前的评论)，且对方接收提醒，邮件提醒对方(6)
            sendMailToParentComment(parentComment, comment);
        }
        if (isVisitorComment) {
            //访客以顶级评论提交，只提醒我自己(4)
            //访客回复我的评论，提醒我自己(5)
            //访客回复访客的评论，不管对方是否接收提醒，都要提醒我有新评论(6)
            notifyMyself(comment);
        }
    }

    /**
    * @Description 发送邮件提醒回复对象
     * @param parentComment  顶级评论
     * @param comment  当前收到的评论
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    private void sendMailToParentComment(CommentEntity parentComment, CommentDTO comment){
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
        String subject = "您在博客 " + blogProperties.getName() + " 的评论有了新回复";
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
                commentPageEnum.setTitle(blogService.getTitleByBlogId(comment.getBlogId()));
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

    /**
    * @Description 查询对应页面评论是否开启
     * @param page 页面分类（0普通文章，1关于我，2友链）
     * @param blogId 如果page==0（即普通博客文章），需要博客id参数，校验文章是否公开状态
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.enums.CommentOpenStateEnum
    */
    public CommentOpenStateEnum judgeCommentState(Integer page, Long blogId) {
        switch (page) {
            case PageConstants.BLOG:
                //普通博客文章
                Boolean commentEnabled = blogService.getCommentEnabledByBlogId(blogId);
                Boolean published = blogService.getPublishedByBlogId(blogId);
                if (commentEnabled == null || published == null) {
                    //未查询到此博客
                    return CommentOpenStateEnum.NOT_FOUND;
                } else if (!published) {
                    //博客未公开
                    return CommentOpenStateEnum.NOT_FOUND;
                } else if (!commentEnabled) {
                    //博客评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                //判断文章是否存在密码
                String password = blogService.getBlogPassword(blogId);
                if (!StringUtils.isEmpty(password)) {
                    return CommentOpenStateEnum.PASSWORD;
                }
                break;
            case PageConstants.ABOUT:
                //关于我页面
                if (!aboutService.getAboutCommentEnabled()) {
                    //页面评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                break;
            case PageConstants.FRIEND:
                //友链页面
                FriendInfo friendInfo = friendService.getFriendInfo(true, false);
                if (!friendInfo.getCommentEnabled()) {
                    //页面评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                break;
            default:
                break;
        }
        return CommentOpenStateEnum.OPEN;
    }

    /**
    * @Description 对于昵称不是QQ号的评论，根据昵称Hash设置头像
     * @param comment
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: void
    */
    private void setCommentRandomAvatar(CommentDTO comment) {
        //设置随机头像
        //根据评论昵称取Hash，保证每一个昵称对应一个头像
        long nicknameHash = HashUtils.getMurmurHash32(comment.getNickname());
        //计算对应的头像
        long num = nicknameHash % 6 + 1;// 1 到 6 的范围，对应6个默认头像（数字可改）
        String avatar = "/img/comment-avatar/" + num + ".jpg";
        comment.setAvatar(avatar);
    }

    /**
    * @Description 通用博主评论属性
     * @param comment
     * @param admin
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: void
    */
    private void setGeneralAdminComment(CommentDTO comment, User admin) {
        comment.setAdminComment(true);
        comment.setCreateTime(new Date());
        comment.setPublished(true);
        comment.setAvatar(admin.getAvatar());
        comment.setWebsite("/");
        comment.setNickname(admin.getNickname());
        comment.setEmail(admin.getEmail());
        comment.setNotice(false);
    }

    /**
    * @Description 为[Telegram快捷回复]方式设置评论属性
     * @param comment
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: void
    */
    public void setAdminCommentByTelegramAction(CommentDTO comment) {
        //查出博主信息，默认id为1的记录就是博主
        User admin = userService.findUserById(1L);

        setGeneralAdminComment(comment, admin);
        comment.setIp("via Telegram");
    }

    /**
    * @Description 设置博主评论属性
     * @param comment
     * @param request
     * @param admin
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: void
    */
    public void setAdminComment(CommentDTO comment, HttpServletRequest request, User admin) {
        setGeneralAdminComment(comment, admin);
        comment.setIp(IpAddressUtils.getIpAddress(request));
    }

    /**
    * @Description 设置访客评论属性
     * @param comment
     * @param request
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: void
    */
    public void setVisitorComment(CommentDTO comment, HttpServletRequest request) {
        comment.setNickname(comment.getNickname().trim());
        setCommentRandomAvatar(comment);

        //检查url是否合法
        if (!isValidUrl(comment.getWebsite())) {
            comment.setWebsite("");
        }
        comment.setAdminComment(false);
        comment.setCreateTime(new Date());
        comment.setPublished(commentDefaultOpen);
        comment.setEmail(comment.getEmail().trim());
        comment.setIp(IpAddressUtils.getIpAddress(request));
    }

    /**
    * @Description 设置QQ头像，复用已上传过的QQ头像，不再重复上传
     * @param comment
     * @param qq
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: void
    */
    private void setCommentQQAvatar(CommentDTO comment, String qq) throws Exception {
        String uploadAvatarUrl = (String) redisService.getValueByHashKey(RedisKeyConstants.QQ_AVATAR_URL_MAP, qq);
        if (StringUtils.isEmpty(uploadAvatarUrl)) {
            uploadAvatarUrl = QQInfoUtils.getQQAvatarUrl(qq);
            redisService.saveKVToHash(RedisKeyConstants.QQ_AVATAR_URL_MAP, qq, uploadAvatarUrl);
        }
        comment.setAvatar(uploadAvatarUrl);
    }

    /**
    * @Description  URL合法性校验
     * @param url
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: boolean
    */
    private static boolean isValidUrl(String url) {
        return url.matches("^https?://([^!@#$%^&*?.\\s-]([^!@#$%^&*?.\\s]{0,63}[^!@#$%^&*?.\\s])?\\.)+[a-z]{2,6}/?");
    }
}
