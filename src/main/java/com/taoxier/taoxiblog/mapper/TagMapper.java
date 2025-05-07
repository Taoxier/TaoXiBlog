package com.taoxier.taoxiblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taoxier.taoxiblog.model.entity.Tag;
import com.taoxier.taoxiblog.model.vo.TagBlogCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Mapper
@Repository
public interface TagMapper extends BaseMapper<Tag> {

    List<Tag> getTagListByBlogId(Long blogId);

    List<TagBlogCountVO> getTagBlogCount();
}