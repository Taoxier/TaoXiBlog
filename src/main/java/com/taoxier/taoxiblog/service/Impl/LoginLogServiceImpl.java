package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.LoginLogMapper;
import com.taoxier.taoxiblog.model.dto.UserAgentDTO;
import com.taoxier.taoxiblog.model.entity.LoginLog;
import com.taoxier.taoxiblog.service.LoginLogService;
import com.taoxier.taoxiblog.util.IpAddressUtils;
import com.taoxier.taoxiblog.util.UserAgentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：登录日志
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private UserAgentUtils userAgentUtils;

    @Override
    public List<LoginLog> getLoginLogListByDate(String startDate, String endDate) {
        QueryWrapper<LoginLog> wrapper = new QueryWrapper<>();
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            wrapper.between("create_time", startDate, endDate);
        }
        return loginLogMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveLoginLog(LoginLog log) {
        String ipSource = IpAddressUtils.getCityInfo(log.getIp());
        UserAgentDTO userAgentDTO = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
        log.setIpSource(ipSource);
        log.setOs(userAgentDTO.getOs());
        log.setBrowser(userAgentDTO.getBrowser());
        if (!save(log)) {
            throw new PersistenceException("日志添加失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteLoginLogById(Long id) {
        if (!removeById(id)) {
            throw new PersistenceException("删除日志失败");
        }
    }
}
