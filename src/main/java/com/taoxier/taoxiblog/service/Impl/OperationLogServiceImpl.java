package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.OperationLogMapper;
import com.taoxier.taoxiblog.model.dto.UserAgentDTO;
import com.taoxier.taoxiblog.model.entity.OperationLog;
import com.taoxier.taoxiblog.service.OperationLogService;
import com.taoxier.taoxiblog.util.IpAddressUtils;
import com.taoxier.taoxiblog.util.UserAgentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：操作日志
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private UserAgentUtils userAgentUtils;

    /**
    * @Description 查询日志
     * @param startDate
     * @param endDate
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.OperationLog>
    */
    @Override
    public List<OperationLog> getOperationLogListByDate(String startDate, String endDate) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            wrapper.between("create_time", startDate, endDate);
        }
        return operationLogMapper.selectList(wrapper);
    }

    /**
    * @Description 增加
     * @param log
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOperationLog(OperationLog log) {
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
    * @Description 删除
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteOperationLogById(Long id) {
        if (!removeById(id)) {
            throw new PersistenceException("删除日志失败");
        }
    }
}
