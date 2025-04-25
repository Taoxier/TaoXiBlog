package com.taoxier.taoxiblog.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Description ：访客更新DTO
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class VisitLogUuidTimeDTO {
    private String uuid;
    private Date time;
    private Integer pv;
}
