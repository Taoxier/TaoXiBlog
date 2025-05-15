package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class ScheduleJobLog implements Serializable {
    /**
     * 任务日志id
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

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
     * 任务执行结果
     */
    private Boolean status;

    /**
     * 异常信息
     */
    private String error;

    /**
     * 耗时（单位：毫秒）
     */
    private Integer times;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}