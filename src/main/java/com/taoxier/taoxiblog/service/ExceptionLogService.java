package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.ExceptionLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface ExceptionLogService extends IService<ExceptionLog> {
    List<ExceptionLog> getExceptionLogListByDate(String startDate, String endDate);

    @Transactional(rollbackFor = Exception.class)
    void saveExceptionLog(ExceptionLog log);

    @Transactional(rollbackFor = Exception.class)
    void deleteExceptionLogById(Long id);
}
