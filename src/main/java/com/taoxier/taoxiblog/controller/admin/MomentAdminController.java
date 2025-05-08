package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.entity.Moment;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Description ：动态后台管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class MomentAdminController {
    @Autowired
    MomentService momentService;

    /**
    * @Description 分页查询动态列表
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/moments")
    public ResultVO moments(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<Moment> pageInfo = new PageInfo<>(momentService.getMomentList());
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 根据id查询动态
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/moment")
    public ResultVO moment(@RequestParam Long id) {
        return ResultVO.ok("获取成功", momentService.getMomentById(id));
    }

    /**
    * @Description 发布动态
     * @param moment
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("发布动态")
    @PostMapping("/moment")
    public ResultVO saveMoment(@RequestBody Moment moment) {
        if (moment.getCreateTime() == null) {
            moment.setCreateTime(new Date());
        }
        momentService.saveMoment(moment);
        return ResultVO.ok("添加成功");
    }

    /**
    * @Description 更新动态
     * @param moment
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("更新动态")
    @PutMapping("/moment")
    public ResultVO updateMoment(@RequestBody Moment moment) {
        if (moment.getCreateTime() == null) {
            moment.setCreateTime(new Date());
        }
        momentService.updateMoment(moment);
        return ResultVO.ok("修改成功");
    }

    /**
     * @Description 删除动态
     * @param id
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @OperationLogger("删除动态")
    @DeleteMapping("/moment")
    public ResultVO deleteMoment(@RequestParam Long id) {
        momentService.deleteMomentById(id);
        return ResultVO.ok("删除成功");
    }

    /**
    * @Description 更新动态公开状态
     * @param id
     * @param published
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("更新动态公开状态")
    @PutMapping("/moment/published")
    public ResultVO updatePublished(@RequestParam Long id, @RequestParam Boolean published) {
        momentService.updateMomentPublishedById(id, published);
        return ResultVO.ok("操作成功");
    }
}
