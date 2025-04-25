package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.mapper.OperationLogMapper;
import com.taoxier.taoxiblog.model.entity.OperationLog;
import com.taoxier.taoxiblog.service.OperationLogService;
import org.springframework.stereotype.Service;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
}
