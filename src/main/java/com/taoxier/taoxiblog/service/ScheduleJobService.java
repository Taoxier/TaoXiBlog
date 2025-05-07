package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.ScheduleJobEntity;
import com.taoxier.taoxiblog.model.entity.ScheduleJobLog;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface ScheduleJobService extends IService<ScheduleJobEntity> {
    @PostConstruct
    void init();

    List<ScheduleJobEntity> getJobList();

    @Transactional(rollbackFor = Exception.class)
    void saveJob(ScheduleJobEntity scheduleJob);

    @Transactional(rollbackFor = Exception.class)
    void updateJob(ScheduleJobEntity scheduleJob);

    @Transactional(rollbackFor = Exception.class)
    void deleteJobById(Long jobId);

    void runJobById(Long jobId);

    @Transactional(rollbackFor = Exception.class)
    void updateJobStatusById(Long jobId, Boolean status);

    List<ScheduleJobLog> getJobLogListByDate(String startDate, String endDate);

    @Transactional(rollbackFor = Exception.class)
    void saveJobLog(ScheduleJobLog jobLog);

    @Transactional(rollbackFor = Exception.class)
    void deleteJobLogByLogId(Long logId);
}
