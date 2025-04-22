package com.taoxier.taoxiblog.util.upload.channel;

import com.taoxier.taoxiblog.util.upload.UploadUtils;

/**
 * @Description ：文件上传方式
 * @Author taoxier
 * @Date 2025/4/22
 */
public interface FileUploadChannel {

    /**
    * @Description 通过指定方式上传文件
     * @param image  需要保存的图片
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String  访问图片的URL
    */
    String upload(UploadUtils.ImageResource image) throws Exception;
}
