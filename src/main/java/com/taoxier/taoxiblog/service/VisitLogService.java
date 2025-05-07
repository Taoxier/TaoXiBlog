package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.dto.VisitLogUuidTimeDTO;
import com.taoxier.taoxiblog.model.entity.VisitLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface VisitLogService extends IService<VisitLog> {
    List<VisitLog> getVisitLogListByUUIDAndDate(String uuid, String startDate, String endDate);

    List<VisitLogUuidTimeDTO> getUUIDAndCreateTimeByYesterday();

    @Transactional(rollbackFor = Exception.class)
    void saveVisitLog(VisitLog log);

    @Transactional(rollbackFor = Exception.class)
    void deleteVisitLogById(Long id);

    Integer countVisitLogByToday();
}
