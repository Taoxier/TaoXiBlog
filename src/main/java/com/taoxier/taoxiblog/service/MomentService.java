package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.Moment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface MomentService extends IService<Moment> {
    List<Moment> getMomentList();

    List<Moment> getMomentVOList(Integer pageNum, boolean adminIdentity);

    @Transactional(rollbackFor = Exception.class)
    void addLikeByMomentId(Long momentId);

    @Transactional(rollbackFor = Exception.class)
    void updateMomentPublishedById(Long momentId, Boolean published);

    Moment getMomentById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void deleteMomentById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void saveMoment(Moment moment);

    @Transactional(rollbackFor = Exception.class)
    void updateMoment(Moment moment);
}
