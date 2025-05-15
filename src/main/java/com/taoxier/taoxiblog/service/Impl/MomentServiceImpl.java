package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.taoxier.taoxiblog.exception.NotFoundException;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.MomentMapper;
import com.taoxier.taoxiblog.model.entity.Moment;
import com.taoxier.taoxiblog.service.MomentService;
import com.taoxier.taoxiblog.util.markdown.MarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：动态
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper,Moment> implements MomentService {

    @Autowired
    private MomentMapper momentMapper;

    // 每页显示5条动态
    private static final int pageSize = 5;
    // 动态列表排序方式
    private static final String orderBy = "create_time desc";
    // 私密动态提示
    private static final String PRIVATE_MOMENT_CONTENT = "<p>此条为私密动态，仅发布者可见。</p>";

    /**
    * @Description 查询动态List
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Moment>
    */
    @Override
    public List<Moment> getMomentList() {
        return momentMapper.selectList(null);
    }

    /**
    * @Description
     * @param pageNum
     * @param adminIdentity
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Moment>
    */
    @Override
    public List<Moment> getMomentVOList(Integer pageNum, boolean adminIdentity) {
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Moment> moments = momentMapper.selectList(null);
        for (Moment moment : moments) {
            if (adminIdentity || moment.getPublished()) {
                moment.setContent(MarkdownUtils.markdownToHtmlExtensions(moment.getContent()));
            } else {
                moment.setContent(PRIVATE_MOMENT_CONTENT);
            }
        }
        return moments;
    }

    /**
    * @Description 给动态点赞
     * @param momentId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addLikeByMomentId(Long momentId) {
        UpdateWrapper<Moment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("likes = likes + 1")
                .eq("id", momentId);
        int rows = momentMapper.update(null, updateWrapper);
        if (rows != 1) {
            throw new PersistenceException("操作失败");
        }
    }

    /**
    * @Description 更新动态发布状态
     * @param momentId
     * @param published
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMomentPublishedById(Long momentId, Boolean published) {
        Moment moment = new Moment();
        moment.setId(momentId);
        moment.setPublished(published);
        if (!updateById(moment)) {
            throw new PersistenceException("操作失败");
        }
    }

    /**
    * @Description 根据id查询动态
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: com.taoxier.taoxiblog.model.entity.Moment
    */
    @Override
    public Moment getMomentById(Long id) {
        Moment moment = getById(id);
        if (moment == null) {
            throw new NotFoundException("动态不存在");
        }
        return moment;
    }

    /**
    * @Description 按id删除动态
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMomentById(Long id) {
        if (!removeById(id)) {
            throw new PersistenceException("删除失败");
        }
    }

    /**
    * @Description 添加动态
     * @param moment
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveMoment(Moment moment) {
        if (!save(moment)) {
            throw new PersistenceException("动态添加失败");
        }
    }

    /**
    * @Description 更新动态
     * @param moment
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMoment(Moment moment) {
        if (!updateById(moment)) {
            throw new PersistenceException("动态修改失败");
        }
    }
}
