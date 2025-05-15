package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.taoxier.taoxiblog.mapper.*;
import com.taoxier.taoxiblog.model.entity.*;
import com.taoxier.taoxiblog.model.vo.CategoryBlogCountVO;
import com.taoxier.taoxiblog.model.vo.TagBlogCountVO;
import com.taoxier.taoxiblog.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description ：仪表盘业务层实现
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private VisitLogMapper visitLogMapper;

    @Autowired
    private VisitRecordMapper visitRecordMapper;

    @Autowired
    private CityVisitorMapper cityVisitorMapper;

    // 查询最近 30 天的记录
    private static final int visitRecordLimitNum = 30;

    /**
    * @Description 统计今日的访问日志数量
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: int
    */
    @Override
    public int countVisitLogByToday() {
        QueryWrapper<VisitLog> wrapper = new QueryWrapper<>();
        wrapper.apply("date(create_time) = curdate()");
        return Math.toIntExact(visitLogMapper.selectCount(wrapper));
    }

    /**
    * @Description 统计博客的总数
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: int
    */
    @Override
    public int getBlogCount() {
        return Math.toIntExact(blogMapper.selectCount(null));
    }

    /**
    * @Description 统计所有评论的数量
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: int
    */
    @Override
    public int getCommentCount() {
        return Math.toIntExact(commentMapper.selectCount(null));
    }

    /**
    * @Description 获取分类和对应博客数量的映射关系，以 Map 形式返回
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.Map<java.lang.String,java.util.List>
    */
    @Override
    public Map<String, List> getCategoryBlogCountMap() {
        // 查询分类 id 对应的博客数量
        List<CategoryBlogCountVO> categoryBlogCountList = blogMapper.getCategoryBlogCountList();
        // 查询所有分类的 id 和名称
        List<Category> categoryList = categoryMapper.selectList(null);
        // 所有分类名称的 List
        List<String> legend = new ArrayList<>();
        for (Category category : categoryList) {
            legend.add(category.getName());
        }
        // 分类对应的博客数量 List
        List<CategoryBlogCountVO> series = new ArrayList<>();
        if (categoryBlogCountList.size() == categoryList.size()) {
            Map<Long, String> m = new HashMap<>(16);
            for (Category c : categoryList) {
                m.put(c.getId(), c.getName());
            }
            for (CategoryBlogCountVO c : categoryBlogCountList) {
                if (c!=null){
                    c.setName(m.get(c.getId()));
                    series.add(c);
                }
            }
        } else {
            Map<Long, Integer> m = new HashMap<>(16);
            for (CategoryBlogCountVO c : categoryBlogCountList) {
                m.put(c.getId(), c.getValue());
            }
            for (Category c : categoryList) {
                CategoryBlogCountVO categoryBlogCount = new CategoryBlogCountVO();
                categoryBlogCount.setName(c.getName());
                Integer count = m.get(c.getId());
                if (count == null) {
                    categoryBlogCount.setValue(0);
                } else {
                    categoryBlogCount.setValue(count);
                }
                series.add(categoryBlogCount);
            }
        }
        Map<String, List> map = new HashMap<>(4);
        map.put("legend", legend);
        map.put("series", series);
        return map;
    }

    /**
    * @Description 获取标签和对应博客数量的映射关系，以 Map 形式返回
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.Map<java.lang.String,java.util.List>
    */
    @Override
    public Map<String, List> getTagBlogCountMap() {
        // 查询标签 id 对应的博客数量
        List<TagBlogCountVO> tagBlogCountList = tagMapper.getTagBlogCount();
        // 查询所有标签的 id 和名称
        List<Tag> tagList = tagMapper.selectList(null);
        // 所有标签名称的 List
        List<String> legend = new ArrayList<>();
        for (Tag tag : tagList) {
            legend.add(tag.getName());
        }
        // 标签对应的博客数量 List
        List<TagBlogCountVO> series = new ArrayList<>();
        if (tagBlogCountList.size() == tagList.size()) {
            Map<Long, String> m = new HashMap<>(64);
            for (Tag t : tagList) {
                m.put(t.getId(), t.getName());
            }
            for (TagBlogCountVO t : tagBlogCountList) {
                if (t!=null){
                    t.setName(m.get(t.getId()));
                    series.add(t);
                }
            }
        } else {
            Map<Long, Integer> m = new HashMap<>(64);
            for (TagBlogCountVO t : tagBlogCountList) {
                m.put(t.getId(), t.getValue());
            }
            for (Tag t : tagList) {
                TagBlogCountVO tagBlogCount = new TagBlogCountVO();
                tagBlogCount.setName(t.getName());
                Integer count = m.get(t.getId());
                if (count == null) {
                    tagBlogCount.setValue(0);
                } else {
                    tagBlogCount.setValue(count);
                }
                series.add(tagBlogCount);
            }
        }
        Map<String, List> map = new HashMap<>(4);
        map.put("legend", legend);
        map.put("series", series);
        return map;
    }

    /**
    * @Description 获取最近 visitRecordLimitNum 天的访问记录
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.Map<java.lang.String,java.util.List>
    */
    @Override
    public Map<String, List> getVisitRecordMap() {
        QueryWrapper<VisitRecord> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id").last("limit " + visitRecordLimitNum);
        List<VisitRecord> visitRecordList = visitRecordMapper.selectList(wrapper);
        List<String> date = new ArrayList<>(visitRecordList.size());
        List<Integer> pv = new ArrayList<>(visitRecordList.size());
        List<Integer> uv = new ArrayList<>(visitRecordList.size());
        for (int i = visitRecordList.size() - 1; i >= 0; i--) {
            VisitRecord visitRecord = visitRecordList.get(i);
            date.add(visitRecord.getDate());
            pv.add(visitRecord.getPv());
            uv.add(visitRecord.getUv());
        }
        Map<String, List> map = new HashMap<>(8);
        map.put("date", date);
        map.put("pv", pv);
        map.put("uv", uv);
        return map;
    }

    /**
    * @Description 获取城市访客列表，按 UV 倒序排序
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.CityVisitor>
    */
    @Override
    public List<CityVisitor> getCityVisitorList() {
        QueryWrapper<CityVisitor> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("uv");
        return cityVisitorMapper.selectList(wrapper);
    }
}
