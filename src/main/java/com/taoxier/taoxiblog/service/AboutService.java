package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.About;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface AboutService extends IService<About> {
    Map<String, String> getAboutInfo();

    Map<String, String> getAboutSetting();

    void updateAbout(Map<String, String> map);

    @Transactional(rollbackFor = Exception.class)
    void updateOneAbout(String nameEn, String value);

    boolean getAboutCommentEnable();

}
