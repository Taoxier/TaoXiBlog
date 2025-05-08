package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.annotation.VisitLogger;
import com.taoxier.taoxiblog.enums.VisitBehavior;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.AboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
public class AboutController {
    @Autowired
    AboutService aboutService;

    /**
    * @Description 获取关于我页面信息
     * @param
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.ABOUT)
    @GetMapping("/about")
    public ResultVO about() {
        return ResultVO.ok("获取成功", aboutService.getAboutInfo());
    }
}
