package com.taoxier.taoxiblog.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.mapper.TagMapper;
import com.taoxier.taoxiblog.model.entity.Tag;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    TagMapper tagMapper;
    @Autowired
    RedisService redisService;

    /**
    * @Description 获取所有tag
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Tag>
    */
    @Override
    public List<Tag> getTagList(){
        QueryWrapper<Tag> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return tagMapper.selectList(queryWrapper);
    }

    /**
    * @Description 获取所有标签List不查询id
     * @param
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Tag>
    */
    @Override
    public List<Tag> getTagListNoId(){
        String redisKey= RedisKeyConstants.TAG_CLOUD_LIST;
        List<Tag> tagListFromRedis=redisService.getListByKey(redisKey);
        if (tagListFromRedis!=null){
            return tagListFromRedis;
        }
        QueryWrapper<Tag> queryWrapper=new QueryWrapper<>();
        queryWrapper.select("tag_name","color");
        queryWrapper.orderByDesc("id");
        return tagMapper.selectList(queryWrapper);
    }

    /**
    * @Description 查询博客标签list
     * @param blogId
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Tag>
    */
    @Override
    public List<Tag> getTagListByBlogId(Long blogId){
        return tagMapper.getTagListByBlogId(blogId);
    }
}
