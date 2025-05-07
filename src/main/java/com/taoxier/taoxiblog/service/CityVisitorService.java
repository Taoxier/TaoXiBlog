package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.CityVisitor;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface CityVisitorService extends IService<CityVisitor> {
    @Transactional(rollbackFor = Exception.class)
    void saveCityVisitor(CityVisitor cityVisitor);
}
