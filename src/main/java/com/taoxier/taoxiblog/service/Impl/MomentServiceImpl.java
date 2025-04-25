package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.mapper.MomentMapper;
import com.taoxier.taoxiblog.model.entity.Moment;
import com.taoxier.taoxiblog.service.MomentService;
import org.springframework.stereotype.Service;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper,Moment> implements MomentService {
}
