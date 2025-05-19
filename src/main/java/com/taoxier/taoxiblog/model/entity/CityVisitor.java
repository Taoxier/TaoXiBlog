package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;

import lombok.*;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CityVisitor implements Serializable {
    /**
     * 城市名称
     */
    private String city;

    /**
     * 独立访客数量
     */
    private Integer uv;

    private static final long serialVersionUID = 1L;
}