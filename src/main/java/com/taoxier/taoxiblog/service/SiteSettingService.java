package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.SiteSetting;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface SiteSettingService extends IService<SiteSetting> {
    Map<String, List<SiteSetting>> getList();

    Map<String, Object> getSiteInfo();

    String getWebTitleSuffix();

    @Transactional(rollbackFor = Exception.class)
    void updateSiteSetting(List<LinkedHashMap> siteSettings, List<Integer> deleteIds);

    void saveOneSiteSetting(SiteSetting siteSetting);

    void updateOneSiteSetting(SiteSetting siteSetting);

    void deleteOneSiteSettingById(Integer id);

    void deleteSiteInfoRedisCache();
}
