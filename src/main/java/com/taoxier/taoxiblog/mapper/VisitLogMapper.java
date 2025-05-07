package com.taoxier.taoxiblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taoxier.taoxiblog.model.dto.VisitLogUuidTimeDTO;
import com.taoxier.taoxiblog.model.entity.VisitLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Mapper
@Repository
public interface VisitLogMapper extends BaseMapper<VisitLog> {
    List<VisitLogUuidTimeDTO> selectUUIDAndCreateTimeByYesterday(@Param("ew") QueryWrapper<VisitLog> wrapper);
}