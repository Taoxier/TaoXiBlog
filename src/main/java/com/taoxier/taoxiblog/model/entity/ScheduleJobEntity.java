package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class ScheduleJobEntity implements Serializable {

    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY"; //任务调度参数key
    /**
     * 任务id
     */
    private Long jobId;

    /**
     * spring bean名称
     */
    private String beanName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数
     */
    private String params;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 任务状态
     */
    private Boolean status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}