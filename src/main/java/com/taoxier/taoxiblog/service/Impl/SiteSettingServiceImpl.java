package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.constant.SiteSettingConstants;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.SiteSettingMapper;
import com.taoxier.taoxiblog.model.entity.SiteSetting;
import com.taoxier.taoxiblog.model.vo.BadgeVO;
import com.taoxier.taoxiblog.model.vo.CopyrightVO;
import com.taoxier.taoxiblog.model.vo.FavoriteVO;
import com.taoxier.taoxiblog.model.vo.IntroductionVO;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.service.SiteSettingService;
import com.taoxier.taoxiblog.util.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description ：站点设置
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class SiteSettingServiceImpl extends ServiceImpl<SiteSettingMapper, SiteSetting> implements SiteSettingService {

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    @Autowired
    private RedisService redisService;

    private static final Pattern PATTERN = Pattern.compile("\"(.*?)\"");

    /**
    * @Description 查询站点设置
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.Map<java.lang.String,java.util.List<com.taoxier.taoxiblog.model.entity.SiteSetting>>
    */
    @Override
    public Map<String, List<SiteSetting>> getList() {
        List<SiteSetting> siteSettings = siteSettingMapper.selectList(null);
        List<SiteSetting> type1 = new ArrayList<>();
        List<SiteSetting> type2 = new ArrayList<>();
        List<SiteSetting> type3 = new ArrayList<>();
//        1基础设置，2页脚徽标，3资料卡
        for (SiteSetting s : siteSettings) {
            switch (s.getType()) {
                case 1:
                    type1.add(s);
                    break;
                case 2:
                    type2.add(s);
                    break;
                case 3:
                    type3.add(s);
                    break;
                default:
                    break;
            }
        }
        Map<String, List<SiteSetting>> map = new HashMap<>(8);
        map.put("type1", type1);
        map.put("type2", type2);
        map.put("type3", type3);
        return map;
    }

    /**
    * @Description
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.Map<java.lang.String,java.lang.Object>
    */
    @Override
    public Map<String, Object> getSiteInfo() {
        String redisKey = RedisKeyConstants.SITE_INFO_MAP;
        Map<String, Object> siteInfoMapFromRedis = redisService.getMapByKey(redisKey);
        if (siteInfoMapFromRedis != null) {
            return siteInfoMapFromRedis;
        }
        List<SiteSetting> siteSettings = siteSettingMapper.selectList(null);
        Map<String, Object> siteInfo = new HashMap<>(2);
        List<BadgeVO> badges = new ArrayList<>();
        IntroductionVO introduction = new IntroductionVO();
        List<FavoriteVO> favorites = new ArrayList<>();
        List<String> rollTexts = new ArrayList<>();
        for (SiteSetting s : siteSettings) {
            switch (s.getType()) {
                case 1:
                    if (SiteSettingConstants.COPYRIGHT.equals(s.getNameEn())) {
                        //版权信息
                        CopyrightVO copyright = JacksonUtils.readValue(s.getValue(), CopyrightVO.class);
                        siteInfo.put(s.getNameEn(), copyright);
                    } else {
                        siteInfo.put(s.getNameEn(), s.getValue());
                    }
                    break;
                case 2:
                    switch (s.getNameEn()) {
                        case SiteSettingConstants.AVATAR:
                            introduction.setAvatar(s.getValue());
                            break;
                        case SiteSettingConstants.NAME:
                            introduction.setName(s.getValue());
                            break;
                        case SiteSettingConstants.GITHUB:
                            introduction.setGithub(s.getValue());
                            break;
                        case SiteSettingConstants.TELEGRAM:
                            introduction.setTelegram(s.getValue());
                            break;
                        case SiteSettingConstants.QQ:
                            introduction.setQq(s.getValue());
                            break;
                        case SiteSettingConstants.BILIBILI:
                            introduction.setBilibili(s.getValue());
                            break;
                        case SiteSettingConstants.NETEASE:
                            introduction.setNetease(s.getValue());
                            break;
                        case SiteSettingConstants.EMAIL:
                            introduction.setEmail(s.getValue());
                            break;
                        case SiteSettingConstants.FAVORITE:
                            FavoriteVO favorite = JacksonUtils.readValue(s.getValue(), FavoriteVO.class);
                            favorites.add(favorite);
                            break;
                        case SiteSettingConstants.ROLL_TEXT:
                            Matcher m = PATTERN.matcher(s.getValue());
                            while (m.find()) {
                                rollTexts.add(m.group(1));
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case 3:
                    BadgeVO badge = JacksonUtils.readValue(s.getValue(), BadgeVO.class);
                    badges.add(badge);
                    break;
                default:
                    break;
            }
        }
        introduction.setFavorites(favorites);
        introduction.setRollText(rollTexts);
        Map<String, Object> map = new HashMap<>(8);
        map.put("introduction", introduction);
        map.put("siteInfo", siteInfo);
        map.put("badges", badges);
        redisService.saveMapToValue(redisKey, map);
        return map;
    }

    /**
    * @Description 查询网页标题后缀
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.lang.String
    */
    @Override
    public String getWebTitleSuffix() {
        QueryWrapper<SiteSetting> wrapper = new QueryWrapper<>();
        wrapper.eq("name_en", "webTitleSuffix");
        SiteSetting siteSetting = siteSettingMapper.selectOne(wrapper);
        return siteSetting != null ? siteSetting.getValue() : null;
    }

    /**
    * @Description 更新
     * @param siteSettings
     * @param deleteIds
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSiteSetting(List<LinkedHashMap> siteSettings, List<Integer> deleteIds) {
        for (Integer id : deleteIds) {
            // 删除
            if (!removeById(id)) {
                throw new PersistenceException("配置删除失败");
            }
        }
        for (LinkedHashMap s : siteSettings) {
            SiteSetting siteSetting = JacksonUtils.convertValue(s, SiteSetting.class);
            if (siteSetting.getId() != null) {
                // 修改
                if (!updateById(siteSetting)) {
                    throw new PersistenceException("配置修改失败");
                }
            } else {
                // 添加
                if (!save(siteSetting)) {
                    throw new PersistenceException("配置添加失败");
                }
            }
        }
        deleteSiteInfoRedisCache();
    }

    /**
    * @Description 增加
     * @param siteSetting
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void saveOneSiteSetting(SiteSetting siteSetting) {
        if (!save(siteSetting)) {
            throw new PersistenceException("配置添加失败");
        }
    }

    /**
    * @Description 更新一个
     * @param siteSetting
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void updateOneSiteSetting(SiteSetting siteSetting) {
        if (!updateById(siteSetting)) {
            throw new PersistenceException("配置修改失败");
        }
    }

    /**
    * @Description 删
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void deleteOneSiteSettingById(Integer id) {
        if (!removeById(id)) {
            throw new PersistenceException("配置删除失败");
        }
    }


    /**
    * @Description 删除站点信息缓存
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void deleteSiteInfoRedisCache() {
        redisService.deleteCacheByKey(RedisKeyConstants.SITE_INFO_MAP);
    }
}
