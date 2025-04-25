package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.Tag;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface TagService extends IService<Tag> {
    List<Tag> getTagList();

    List<Tag> getTagListNoId();

    List<Tag> getTagListByBlogId(Long blogId);

    @Transactional(rollbackFor = Exception.class)
    void saveTag(Tag tag);

    Tag getTagById(Long id);

    Tag getTagByName(String name);

    @Transactional(rollbackFor = Exception.class)
    void deleteTagById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void updateTag(Tag tag);
}
