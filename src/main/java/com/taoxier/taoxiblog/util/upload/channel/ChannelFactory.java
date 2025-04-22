package com.taoxier.taoxiblog.util.upload.channel;

import com.taoxier.taoxiblog.constant.UploadConstants;
import com.taoxier.taoxiblog.util.common.SpringContextUtils;

/**
 * @Description ：文件上传方式
 * @Author taoxier
 * @Date 2025/4/22
 */
public class ChannelFactory {

    /**
    * @Description 创建文件上传方式
     * @param channelName  方式名称
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: com.taoxier.taoxiblog.util.upload.channel.FileUploadChannel  文件上传Channel
    */
    public static FileUploadChannel getChannel(String channelName) {
        switch (channelName.toLowerCase()) {
            case UploadConstants.LOCAL:
                return SpringContextUtils.getBean(LocalChannel.class);
            case UploadConstants.GITHUB:
                return SpringContextUtils.getBean(GithubChannel.class);
            case UploadConstants.UPYUN:
                return SpringContextUtils.getBean(UpyunChannel.class);
        }
        throw new RuntimeException("Unsupported value in [application.properties]: [upload.channel]");
    }
}
