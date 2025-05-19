package com.taoxier.taoxiblog.model.dto;

import lombok.*;

import java.util.Date;

/**
 * @Description ：访客更新DTO
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VisitLogUuidTimeDTO {
    private String uuid;
    private Date time;
    private Integer pv;
}
