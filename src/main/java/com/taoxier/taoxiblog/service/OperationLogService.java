package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.OperationLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface OperationLogService extends IService<OperationLog> {
    List<OperationLog> getOperationLogListByDate(String startDate, String endDate);

    @Transactional(rollbackFor = Exception.class)
    void saveOperationLog(OperationLog log);

    @Transactional(rollbackFor = Exception.class)
    void deleteOperationLogById(Long id);
}
