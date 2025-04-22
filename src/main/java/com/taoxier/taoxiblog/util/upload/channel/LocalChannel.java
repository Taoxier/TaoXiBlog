package com.taoxier.taoxiblog.util.upload.channel;

import com.taoxier.taoxiblog.config.properties.BlogProperties;
import com.taoxier.taoxiblog.config.properties.UploadProperties;
import com.taoxier.taoxiblog.util.upload.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * @Description ：本地存储方式
 * @Author taoxier
 * @Date 2025/4/22
 */
@Lazy
@Component
public class LocalChannel implements FileUploadChannel {
	@Autowired
	private BlogProperties blogProperties;
	@Autowired
	private UploadProperties uploadProperties;

	/**
	* @Description 将图片保存到本地，并返回访问本地图片的URL
	 * @param image
	* @Author: taoxier
	* @Date: 2025/4/22
	* @Return: java.lang.String
	*/
	@Override
	public String upload(UploadUtils.ImageResource image) throws Exception {
		File folder = new File(uploadProperties.getPath());
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = UUID.randomUUID() + "." + image.getType();
		FileOutputStream fileOutputStream = new FileOutputStream(uploadProperties.getPath() + fileName);
		fileOutputStream.write(image.getData());
		fileOutputStream.close();
		return blogProperties.getApi() + "/image/" + fileName;
	}
}
