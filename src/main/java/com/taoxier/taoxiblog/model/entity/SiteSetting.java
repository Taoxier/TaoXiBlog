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
public class SiteSetting implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String nameEn;

    private String nameZh;

    private String value;

    /**
     * 1基础设置，2页脚徽标，3资料卡，4友链信息
     */
    private Integer type;

    private static final long serialVersionUID = 1L;
}