package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.model.entity.OperationLog;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ：操作日志后台管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class OperationLogController {
    @Autowired
    OperationLogService operationLogService;

    /**
    * @Description 分页查询操作日志列表
     * @param date
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/operationLogs")
    public ResultVO operationLogs(@RequestParam(defaultValue = "") String[] date,
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
        PageInfo<OperationLog> pageInfo = new PageInfo<>(operationLogService.getOperationLogListByDate(startDate, endDate));
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 根据id删除操作日志
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @DeleteMapping("/operationLog")
    public ResultVO delete(@RequestParam Long id) {
        operationLogService.deleteOperationLogById(id);
        return ResultVO.ok("删除成功");
    }
}
