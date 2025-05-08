package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.model.entity.VisitLog;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.VisitLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ：访问日志后台管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class VisitLogController {
    @Autowired
    VisitLogService visitLogService;

    /**
    * @Description 分页查询访问日志列表
     * @param uuid
     * @param date
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/visitLogs")
    public ResultVO visitLogs(@RequestParam(defaultValue = "") String uuid,
                              @RequestParam(defaultValue = "") String[] date,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        String startDate = null;
        String endDate = null;
        if (date.length == 2) {
            startDate = date[0];
            endDate = date[1];
        }
        String orderBy = "create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<VisitLog> pageInfo = new PageInfo<>(visitLogService.getVisitLogListByUUIDAndDate(StringUtils.trim(uuid), startDate, endDate));
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 按id删除访问日志
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @DeleteMapping("/visitLog")
    public ResultVO delete(@RequestParam Long id) {
        visitLogService.deleteVisitLogById(id);
        return ResultVO.ok("删除成功");
    }
}
