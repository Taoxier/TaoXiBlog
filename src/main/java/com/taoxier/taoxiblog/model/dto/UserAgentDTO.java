package com.taoxier.taoxiblog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description ：UserAgent解析DTO
 * @Author taoxier
 * @Date 2025/4/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAgentDTO {
    private String os;
    private String browser;


}
