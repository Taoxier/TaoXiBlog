package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.dto.VisitLogUuidTimeDTO;
import com.taoxier.taoxiblog.model.entity.Visitor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface VisitorService extends IService<Visitor> {
    List<Visitor> getVisitorListByDate(String startDate, String endDate);

    List<String> getNewVisitorIpSourceByYesterday();

    boolean hasUUID(String uuid);

    @Transactional(rollbackFor = Exception.class)
    void saveVisitor(Visitor visitor);

    @Transactional(rollbackFor = Exception.class)
    void updatePVAndLastTimeByUUID(VisitLogUuidTimeDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void deleteVisitor(Long id, String uuid);
}
