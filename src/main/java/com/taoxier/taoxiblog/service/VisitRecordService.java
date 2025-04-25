package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.mapper.AboutMapper;
import com.taoxier.taoxiblog.model.entity.VisitRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description ：个人页面的业务层实现
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public interface VisitRecordService extends IService<VisitRecord> {

}
