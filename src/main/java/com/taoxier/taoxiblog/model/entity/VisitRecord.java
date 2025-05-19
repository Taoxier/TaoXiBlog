package com.taoxier.taoxiblog.model.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Data
public class VisitRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 独立用户
     */
    private Integer uv;

    /**
     * 日期"04-20"
     */
    private String date;

    private static final long serialVersionUID = 1L;

    public VisitRecord(Integer pv, Integer uv, String date) {
        this.pv = pv;
        this.uv = uv;
        this.date = date;
    }
}