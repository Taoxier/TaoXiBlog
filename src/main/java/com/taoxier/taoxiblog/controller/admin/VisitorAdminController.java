package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.model.entity.Visitor;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ：访客统计管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class VisitorAdminController {
    @Autowired
    VisitorService visitorService;

    /**
    * @Description 分页查询访客列表
     * @param date
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/visitors")
    public ResultVO visitors(@RequestParam(defaultValue = "") String[] date,
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
        PageInfo<Visitor> pageInfo = new PageInfo<>(visitorService.getVisitorListByDate(startDate, endDate));
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 按id删除访客
     * @param id
     * @param uuid
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @DeleteMapping("/visitor")
    public ResultVO delete(@RequestParam Long id, @RequestParam String uuid) {
        visitorService.deleteVisitor(id, uuid);
        return ResultVO.ok("删除成功");
    }
}
