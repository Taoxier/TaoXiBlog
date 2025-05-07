package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.LoginLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface LoginLogService extends IService<LoginLog> {
    List<LoginLog> getLoginLogListByDate(String startDate, String endDate);

    @Transactional(rollbackFor = Exception.class)
    void saveLoginLog(LoginLog log);

    @Transactional(rollbackFor = Exception.class)
    void deleteLoginLogById(Long id);
}
