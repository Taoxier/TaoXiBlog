package com.taoxier.taoxiblog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description ：访问日志备注
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitLogRemarkDTO {
    private String content;//访问内容
    private String remark;// 备注
}
