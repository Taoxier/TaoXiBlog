package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.annotation.VisitLogger;
import com.taoxier.taoxiblog.enums.VisitBehavior;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
public class ArchiveController {
    @Autowired
    BlogService blogService;

    /**
    * @Description 按年月分组归档公开博客 统计公开博客总数
     * @param
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.ARCHIVE)
    @GetMapping("/archives")
    public ResultVO archives() {
        Map<String, Object> archiveBlogMap = blogService.getArchiveBlogAndCountByIsPublished();
        return ResultVO.ok("请求成功", archiveBlogMap);
    }
}
