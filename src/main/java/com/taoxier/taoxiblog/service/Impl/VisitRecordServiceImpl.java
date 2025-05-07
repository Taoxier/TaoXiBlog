package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.mapper.VisitRecordMapper;
import com.taoxier.taoxiblog.model.entity.VisitRecord;
import com.taoxier.taoxiblog.service.VisitRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description ：访问记录
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class VisitRecordServiceImpl extends ServiceImpl<VisitRecordMapper, VisitRecord> implements VisitRecordService {
    @Autowired
    VisitRecordMapper visitRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveVisitRecord(VisitRecord visitRecord) {
        save(visitRecord);
    }
}
