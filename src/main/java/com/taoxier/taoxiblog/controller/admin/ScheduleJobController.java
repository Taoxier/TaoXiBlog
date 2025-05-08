package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.entity.ScheduleJobEntity;
import com.taoxier.taoxiblog.model.entity.ScheduleJobLog;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.ScheduleJobService;
import com.taoxier.taoxiblog.util.common.ValidatorUtils;
import com.taoxier.taoxiblog.util.quartz.ScheduleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Description ：定时任务后台管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class ScheduleJobController {
    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
    * @Description 分页查询定时任务列表
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/jobs")
    public ResultVO jobs(@RequestParam(defaultValue = "1") Integer pageNum,
                         @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<ScheduleJobEntity> pageInfo = new PageInfo<>(scheduleJobService.getJobList());
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 新建定时任务
     * @param scheduleJobEntity
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("新建定时任务")
    @PostMapping("/job")
    public ResultVO saveJob(@RequestBody ScheduleJobEntity scheduleJobEntity) {
        scheduleJobEntity.setStatus(false);
        scheduleJobEntity.setCreateTime(new Date());
        ValidatorUtils.validateEntity(scheduleJobEntity);
        scheduleJobService.saveJob(scheduleJobEntity);
        return ResultVO.ok("添加成功");
    }

    /**
    * @Description 修改定时任务
     * @param scheduleJobEntity
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("修改定时任务")
    @PutMapping("/job")
    public ResultVO updateJob(@RequestBody ScheduleJobEntity scheduleJobEntity) {
        scheduleJobEntity.setStatus(false);
        ValidatorUtils.validateEntity(scheduleJobEntity);
        scheduleJobService.updateJob(scheduleJobEntity);
        return ResultVO.ok("修改成功");
    }

    /**
    * @Description 删除定时任务
     * @param jobId
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("删除定时任务")
    @DeleteMapping("/job")
    public ResultVO deleteJob(@RequestParam Long jobId) {
        scheduleJobService.deleteJobById(jobId);
        return ResultVO.ok("删除成功");
    }

    /**
    * @Description 立即执行任务
     * @param jobId
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("立即执行定时任务")
    @PostMapping("/job/run")
    public ResultVO runJob(@RequestParam Long jobId) {
        scheduleJobService.runJobById(jobId);
        return ResultVO.ok("提交执行");
    }

    /**
    * @Description 更新任务状态：暂停或恢复
     * @param jobId
     * @param status
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("更新任务状态")
    @PutMapping("/job/status")
    public ResultVO updateJobStatus(@RequestParam Long jobId, @RequestParam Boolean status) {
        scheduleJobService.updateJobStatusById(jobId, status);
        return ResultVO.ok("更新成功");
    }

    /**
    * @Description 分页查询定时任务日志列表
     * @param date
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/job/logs")
    public ResultVO logs(@RequestParam(defaultValue = "") String[] date,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        String startDate = null;
        String endDate = null;
        if (date.length == 2) {
            startDate = date[0];
            endDate = date[1];
        }
        String orderBy = "create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<ScheduleJobLog> pageInfo = new PageInfo<>(scheduleJobService.getJobLogListByDate(startDate, endDate));
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 按id删除任务日志
     * @param logId
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @DeleteMapping("/job/log")
    public ResultVO delete(@RequestParam Long logId) {
        scheduleJobService.deleteJobLogByLogId(logId);
        return ResultVO.ok("删除成功");
    }
}
