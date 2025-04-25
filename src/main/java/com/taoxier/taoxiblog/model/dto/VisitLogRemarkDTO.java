package com.taoxier.taoxiblog.model.dto;

import lombok.Data;

/**
 * @Description ：访问日志备注
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class VisitLogRemarkDTO {
    private String content;//访问内容
    private String remark;// 备注
}
