package com.taoxier.taoxiblog.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

/**
 * @Description ：获取QQ昵称头像信息
 * @Author taoxier
 * @Date 2025/4/22
 */
public class QQInfoUtils {

    private static RestTemplate restTemplate = new RestTemplate();
    private static final String QQ_NICKNAME_URL = "https://api.toubiec.cn/api/qqinfo_v4.php?qq={1}";
    private static final String QQ_AVATAR_URL = "https://q.qlogo.cn/g?b=qq&nk=%s&s=100";

    /**
    * @Description 获取QQ昵称
     * @param qq
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String
    */
    public static String getQQNickname(String qq) {
        QqResultVO qqResultVO = restTemplate.getForObject(QQ_NICKNAME_URL, QqResultVO.class, qq);
        if (qqResultVO != null) {
            return new ObjectMapper().convertValue(qqResultVO.getData(), QqVO.class).getName();
        }
        return "nickname";
    }

    /**
    * @Description 从网络获取QQ头像数据
     * @param qq
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: UploadUtils.ImageResource
    */
    private static UploadUtils.ImageResource getImageResourceByQQ(String qq) {
        return UploadUtils.getImageByRequest(String.format(QQ_AVATAR_URL, qq));
    }

    /**
    * @Description 获取QQ头像URL
     * @param qq
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String
    */
    public static String getQQAvatarUrl(String qq) throws Exception {
        return UploadUtils.upload(getImageResourceByQQ(qq));
    }

    /**
    * @Description 判断是否QQ号
     * @param number
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: boolean
    */
    public static boolean isQQNumber(String number) {
        return number.matches("^[1-9][0-9]{4,10}$");
    }
}
