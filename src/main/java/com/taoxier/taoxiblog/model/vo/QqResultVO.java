package com.taoxier.taoxiblog.model.vo;

import lombok.Data;

import java.util.Map;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class QqResultVO {
    private String success;

    private String msg;

    private Map<String, Object> data;

    private String time;

    private  String api_vers;
}
