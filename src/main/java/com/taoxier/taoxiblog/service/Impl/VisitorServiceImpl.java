package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.VisitorMapper;
import com.taoxier.taoxiblog.model.dto.UserAgentDTO;
import com.taoxier.taoxiblog.model.dto.VisitLogUuidTimeDTO;
import com.taoxier.taoxiblog.model.entity.Visitor;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.service.VisitorService;
import com.taoxier.taoxiblog.util.IpAddressUtils;
import com.taoxier.taoxiblog.util.UserAgentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description ：访客统计
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserAgentUtils userAgentUtils;

    /**
    * @Description 查询访客
     * @param startDate
     * @param endDate
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Visitor>
    */
    @Override
    public List<Visitor> getVisitorListByDate(String startDate, String endDate) {
        QueryWrapper<Visitor> wrapper = new QueryWrapper<>();
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            wrapper.between("last_time", startDate, endDate);
        }
        return visitorMapper.selectList(wrapper);
    }

    /**
    * @Description 查询昨天的所有新增访客的ip来源
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<java.lang.String>
    */
    @Override
    public List<String> getNewVisitorIpSourceByYesterday() {
        QueryWrapper<Visitor> wrapper = new QueryWrapper<>();
        wrapper.apply("date(create_time) = date_sub(curdate(), interval 1 day)");
        wrapper.select("ip_source");
        return visitorMapper.selectObjs(wrapper).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    /**
    * @Description 查询是否存在某个uuid
     * @param uuid
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: boolean
    */
    @Override
    public boolean hasUUID(String uuid) {
        QueryWrapper<Visitor> wrapper = new QueryWrapper<>();
        wrapper.eq("uuid", uuid);
        return visitorMapper.selectCount(wrapper) > 0;
    }

    /**
    * @Description 添加访客
     * @param visitor
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveVisitor(Visitor visitor) {
        String ipSource = IpAddressUtils.getCityInfo(visitor.getIp());
        UserAgentDTO userAgentDTO = userAgentUtils.parseOsAndBrowser(visitor.getUserAgent());
        visitor.setIpSource(ipSource);
        visitor.setOs(userAgentDTO.getOs());
        visitor.setBrowser(userAgentDTO.getBrowser());
        if (!save(visitor)) {
            throw new PersistenceException("访客添加失败");
        }
    }

    /**
    * @Description 更新访客pv和最后访问时间
     * @param dto
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePVAndLastTimeByUUID(VisitLogUuidTimeDTO dto) {
        UpdateWrapper<Visitor> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("uuid",dto.getUuid())
                .setSql("pv=pv+"+dto.getPv())
                .set("last_time",dto.getTime());

        int rowsAffected = visitorMapper.update(null, updateWrapper);
        boolean success = rowsAffected > 0;
        if (!success) {
            throw new PersistenceException("更新访客信息失败");
        }
    }

    /**
    * @Description 删除访客
     * @param id
     * @param uuid
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteVisitor(Long id, String uuid) {
        // 删除 Redis 中该访客的 uuid
        redisService.deleteValueBySet(RedisKeyConstants.IDENTIFICATION_SET, uuid);
        if (!removeById(id)) {
            throw new PersistenceException("删除访客失败");
        }
    }
}
