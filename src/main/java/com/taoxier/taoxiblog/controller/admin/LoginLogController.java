package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.model.entity.LoginLog;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ：登录日志后台管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class LoginLogController {
    @Autowired
    LoginLogService loginLogService;

    /**
    * @Description 分页查询登录日志列表
     * @param date
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/loginLogs")
    public ResultVO loginLogs(@RequestParam(defaultValue = "") String[] date,
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
        PageInfo<LoginLog> pageInfo = new PageInfo<>(loginLogService.getLoginLogListByDate(startDate, endDate));
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 根据id删除登录日志
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @DeleteMapping("/loginLog")
    public ResultVO delete(@RequestParam Long id) {
        loginLogService.deleteLoginLogById(id);
        return ResultVO.ok("删除成功");
    }
}
