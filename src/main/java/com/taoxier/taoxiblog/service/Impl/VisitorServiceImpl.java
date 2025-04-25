package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.mapper.VisitorMapper;
import com.taoxier.taoxiblog.model.entity.Visitor;
import com.taoxier.taoxiblog.service.VisitorService;
import org.springframework.stereotype.Service;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {
}
