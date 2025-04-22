package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class About implements Serializable {
    private Long id;

    private String nameEn;

    private String nameZh;

    private String value;

    private static final long serialVersionUID = 1L;
}