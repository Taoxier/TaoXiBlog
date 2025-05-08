package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.mapper.AboutMapper;
import com.taoxier.taoxiblog.model.entity.About;
import com.taoxier.taoxiblog.service.AboutService;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.util.markdown.MarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class AboutServiceImpl extends ServiceImpl<AboutMapper, About> implements AboutService {
    @Autowired
    AboutMapper aboutMapper;
    @Autowired
    RedisService redisService;

    /**
    * @Description 获取关于信息的映射表
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.Map<java.lang.String,java.lang.String>
    */
    @Override
    public Map<String, String> getAboutInfo() {
        String redisKey = RedisKeyConstants.ABOUT_INFO_MAP;
        Map<String, String> aboutInfoMapFromRedis = redisService.getMapByKey(redisKey);
        if (aboutInfoMapFromRedis != null) {
            //直接返回该映射表，避免进行数据库查询
            return aboutInfoMapFromRedis;
        }
        //查库
        List<About> abouts=this.list();
        Map<String, String> aboutInfoMap = new HashMap<>(16);//初始容量为 16
        for (About about : abouts) {
            if ("content".equals(about.getNameEn())) {
                about.setValue(MarkdownUtils.markdownToHtmlExtensions(about.getValue()));
            }
            aboutInfoMap.put(about.getNameEn(), about.getValue());
        }
        redisService.saveMapToValue(redisKey, aboutInfoMap);
        return aboutInfoMap;
    }

    /**
    * @Description 获取关于我的信息供管理者
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.Map<java.lang.String,java.lang.String>
    */
    @Override
    public Map<String, String> getAboutSetting() {
        List<About> abouts = this.list();
        Map<String, String> map = new HashMap<>(16);
        for (About about : abouts) {
            map.put(about.getNameEn(), about.getValue());
        }
        return map;
    }

    /**
    * @Description 批量修改about
     * @param map
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: void
    */
    @Override
    public void updateAbout(Map<String, String> map) {
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            updateOneAbout(key, map.get(key));
        }
        deleteAboutRedisCache();
    }

    /**
    * @Description 修改about单项
     * @param nameEn
     * @param value
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: void
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOneAbout(String nameEn, String value) {
        UpdateWrapper<About> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name_em", nameEn);
        About about = new About();
        about.setValue(value);

        boolean success = this.update(about, updateWrapper);
        if (!success) {
            throw new RuntimeException("修改失败");
        }
    }

    /**
    * @Description 查询关于我页面评论开关状态
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: boolean
    */
    @Override
    public boolean getAboutCommentEnabled() {
        // 创建 QueryWrapper 对象，用于构建查询条件
        QueryWrapper<About> queryWrapper = new QueryWrapper<>();
        // 设置查询条件，即 name_en 字段等于 "commentEnabled"
        queryWrapper.eq("name_en", "commentEnabled");
        // 调用 MyBatis-Plus 的 getOne 方法查询符合条件的第一条记录
        About about = this.getOne(queryWrapper);
        String commentEnabledString = about != null ? about.getValue() : null;
        return Boolean.parseBoolean(commentEnabledString);
    }

    /**
    * @Description 删除关于我页面缓存
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: void
    */
    public void deleteAboutRedisCache() {
        redisService.deleteCacheByKey(RedisKeyConstants.ABOUT_INFO_MAP);
    }

}
