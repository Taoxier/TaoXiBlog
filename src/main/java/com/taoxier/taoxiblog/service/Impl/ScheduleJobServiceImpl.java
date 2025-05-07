package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.ScheduleJobLogMapper;
import com.taoxier.taoxiblog.mapper.ScheduleJobMapper;
import com.taoxier.taoxiblog.model.entity.ScheduleJobEntity;
import com.taoxier.taoxiblog.model.entity.ScheduleJobLog;
import com.taoxier.taoxiblog.service.ScheduleJobService;
import com.taoxier.taoxiblog.util.quartz.ScheduleUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Description ： 定时任务
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJobEntity> implements ScheduleJobService {
    @Autowired
    Scheduler scheduler;
    @Autowired
    ScheduleJobMapper schedulerJobMapper;
    @Autowired
    ScheduleJobLogMapper scheduleJobLogMapper;

    /**
    * @Description 项目启动时，初始化定时器
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    @PostConstruct
    public void init() {
        List<ScheduleJobEntity> scheduleJobList = getJobList();
        for (ScheduleJobEntity scheduleJob : scheduleJobList) {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
            //如果不存在，则创建
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }

    /**
    * @Description 获取定时任务列表
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.ScheduleJobEntity>
    */
    @Override
    public List<ScheduleJobEntity> getJobList() {
        return schedulerJobMapper.selectList(null);
    }

    /**
    * @Description 增加
     * @param scheduleJob
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveJob(ScheduleJobEntity scheduleJob) {
        if (!save(scheduleJob)) {
            throw new PersistenceException("添加失败");
        }
        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
    }

    /**
    * @Description 更新
     * @param scheduleJob
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateJob(ScheduleJobEntity scheduleJob) {
        if (!updateById(scheduleJob)) {
            throw new PersistenceException("更新失败");
        }
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
    }

    /**
    * @Description 删除
     * @param jobId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteJobById(Long jobId) {
        ScheduleUtils.deleteScheduleJob(scheduler, jobId);
        if (!removeById(jobId)) {
            throw new PersistenceException("删除失败");
        }
    }

    /**
    * @Description 执行
     * @param jobId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void runJobById(Long jobId) {
        ScheduleUtils.run(scheduler, schedulerJobMapper.selectById(jobId));
    }

    /**
    * @Description
     * @param jobId
     * @param status
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateJobStatusById(Long jobId, Boolean status) {
        if (status) {
            ScheduleUtils.resumeJob(scheduler, jobId);
        } else {
            ScheduleUtils.pauseJob(scheduler, jobId);
        }

        UpdateWrapper<ScheduleJobEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("job_id", jobId)
                .set("status", status);

        boolean success = schedulerJobMapper.update(null, updateWrapper) > 0;
        if (!success) {
            throw new PersistenceException("修改失败");
        }
    }

    /**
    * @Description
     * @param startDate
     * @param endDate
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.ScheduleJobLog>
    */
    @Override
    public List<ScheduleJobLog> getJobLogListByDate(String startDate, String endDate) {
        QueryWrapper<ScheduleJobLog> wrapper = new QueryWrapper<>();
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            wrapper.between("create_time", startDate, endDate);
        }
        return scheduleJobLogMapper.selectList(wrapper);
    }

    /**
    * @Description
     * @param jobLog
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveJobLog(ScheduleJobLog jobLog) {
        if (scheduleJobLogMapper.insert(jobLog)<=0) {
            throw new PersistenceException("日志添加失败");
        }
    }

    /**
    * @Description
     * @param logId
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteJobLogByLogId(Long logId) {
        if (scheduleJobLogMapper.deleteById(logId) <= 0) {
            throw new PersistenceException("日志删除失败");
        }
    }
}
