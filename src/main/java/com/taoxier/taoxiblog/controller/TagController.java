package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.annotation.VisitLogger;
import com.taoxier.taoxiblog.enums.VisitBehavior;
import com.taoxier.taoxiblog.model.vo.BlogInfoVO;
import com.taoxier.taoxiblog.model.vo.PageResultVO;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
public class TagController {
    @Autowired
    BlogService blogService;

    /**
    * @Description 根据标签name分页查询公开博客列表
     * @param tagName
     * @param pageNum
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.TAG)
    @GetMapping("/tag")
    public ResultVO tag(@RequestParam String tagName,
                        @RequestParam(defaultValue = "1") Integer pageNum) {
        PageResultVO<BlogInfoVO> pageResult = blogService.getBlogInfoListByTagNameAndIsPublished(tagName, pageNum);
        return ResultVO.ok("请求成功", pageResult);
    }
}
