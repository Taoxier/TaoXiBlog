package com.taoxier.taoxiblog.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.exception.NotFoundException;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.TagMapper;
import com.taoxier.taoxiblog.model.entity.Tag;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param
     * @Description 获取所有tag
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Tag>
     */
    @Override
    public List<Tag> getTagList() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return tagMapper.selectList(queryWrapper);
    }

    /**
     * @param
     * @Description 获取所有标签List不查询id
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Tag>
     */
    @Override
    public List<Tag> getTagListNoId() {
        String redisKey = RedisKeyConstants.TAG_CLOUD_LIST;
        List<Tag> tagListFromRedis = redisService.getListByKey(redisKey);
        if (tagListFromRedis != null) {
            return tagListFromRedis;
        }
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("tag_name", "color");
        queryWrapper.orderByDesc("id");
        return tagMapper.selectList(queryWrapper);
    }

    /**
     * @param blogId
     * @Description 查询博客标签list
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Tag>
     */
    @Override
    public List<Tag> getTagListByBlogId(Long blogId) {
        return tagMapper.getTagListByBlogId(blogId);
    }

    /**
    * @Description 新增tag
     * @param tag
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveTag(Tag tag) {
        boolean success = tagMapper.insert(tag) > 0;
        if (!success) {
            throw new PersistenceException("标签添加失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstants.TAG_CLOUD_LIST);
    }

    /**
    * @Description 根据id查找tag
     * @param id
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: com.taoxier.taoxiblog.model.entity.Tag
    */
    @Override
    public Tag getTagById(Long id) {
        Tag tag = this.getById(id);
        if (tag == null) {
            throw new NotFoundException("标签不存在");
        }
        return tag;
    }

    /**
    * @Description 根据名字查找tag
     * @param name
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: com.taoxier.taoxiblog.model.entity.Tag
    */
    @Override
    public Tag getTagByName(String name){
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_name",name);
        Tag tag=tagMapper.selectOne(queryWrapper);
        if (tag==null){
            throw new NotFoundException("标签不存在");
        }
        return tag;
    }

    /**
    * @Description 删除tag
     * @param id
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteTagById(Long id){
        boolean success=removeById(id);
        if (!success){
            throw new PersistenceException("标签删除失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstants.TAG_CLOUD_LIST);
    }

    /**
    * @Description 更新tag
     * @param tag
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateTag(Tag tag){
        boolean success=updateById(tag);
        if (!success){
            throw new PersistenceException("标签更新失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstants.TAG_CLOUD_LIST);
        //修改了标签名或颜色，可能有首页文章关联了标签，也要更新首页缓存
        redisService.deleteCacheByKey(RedisKeyConstants.HOME_BLOG_INFO_LIST);
    }


}
