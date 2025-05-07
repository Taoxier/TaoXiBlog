package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.VisitLogMapper;
import com.taoxier.taoxiblog.model.dto.UserAgentDTO;
import com.taoxier.taoxiblog.model.dto.VisitLogUuidTimeDTO;
import com.taoxier.taoxiblog.model.entity.VisitLog;
import com.taoxier.taoxiblog.service.VisitLogService;
import com.taoxier.taoxiblog.util.IpAddressUtils;
import com.taoxier.taoxiblog.util.UserAgentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：访问日志
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class VisitLogServiceImpl extends ServiceImpl<VisitLogMapper, VisitLog> implements VisitLogService {
    @Autowired
    private VisitLogMapper visitLogMapper;

    @Autowired
    private UserAgentUtils userAgentUtils;

    /**
    * @Description 查询日志
     * @param uuid
     * @param startDate
     * @param endDate
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.VisitLog>
    */
    @Override
    public List<VisitLog> getVisitLogListByUUIDAndDate(String uuid, String startDate, String endDate) {
        QueryWrapper<VisitLog> wrapper = new QueryWrapper<>();
        if (uuid != null && !uuid.isEmpty()) {
            wrapper.like("uuid", uuid);
        }
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            wrapper.between("create_time", startDate, endDate);
        }
        return visitLogMapper.selectList(wrapper);
    }

    /**
    * @Description 查询昨天的所有访问日志
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.dto.VisitLogUuidTimeDTO>
    */
    @Override
    public List<VisitLogUuidTimeDTO> getUUIDAndCreateTimeByYesterday() {
        QueryWrapper<VisitLog> wrapper = new QueryWrapper<>();
        wrapper.apply("date(create_time) = date_sub(curdate(), interval 1 day)")
                .orderByDesc("create_time");
        return visitLogMapper.selectUUIDAndCreateTimeByYesterday(wrapper);
    }

    /**
    * @Description 添加日志
     * @param log
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveVisitLog(VisitLog log) {
        String ipSource = IpAddressUtils.getCityInfo(log.getIp());
        UserAgentDTO userAgentDTO = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
        log.setIpSource(ipSource);
        log.setOs(userAgentDTO.getOs());
        log.setBrowser(userAgentDTO.getBrowser());
        if (!save(log)) {
            throw new PersistenceException("日志添加失败");
        }
    }

    /**
    * @Description 删除日志
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteVisitLogById(Long id) {
        if (!removeById(id)) {
            throw new PersistenceException("删除日志失败");
        }
    }

    /**
    * @Description 查询今日访问量
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.lang.Integer
    */
    @Override
    public Integer countVisitLogByToday() {
        QueryWrapper<VisitLog> wrapper = new QueryWrapper<>();
        wrapper.apply("date(create_time) = curdate()");
        return Math.toIntExact(visitLogMapper.selectCount(wrapper));
    }
}
