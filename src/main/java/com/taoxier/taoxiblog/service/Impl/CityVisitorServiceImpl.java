package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.mapper.CityVisitorMapper;
import com.taoxier.taoxiblog.model.entity.CityVisitor;
import com.taoxier.taoxiblog.service.CityVisitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description ：访客记录
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class CityVisitorServiceImpl extends ServiceImpl<CityVisitorMapper, CityVisitor> implements CityVisitorService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCityVisitor(CityVisitor cityVisitor) {
        save(cityVisitor);
    }
}
