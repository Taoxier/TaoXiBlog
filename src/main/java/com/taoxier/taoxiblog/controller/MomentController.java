package com.taoxier.taoxiblog.controller;

import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.annotation.AccessLimit;
import com.taoxier.taoxiblog.annotation.VisitLogger;
import com.taoxier.taoxiblog.constant.JwtConstants;
import com.taoxier.taoxiblog.enums.VisitBehavior;
import com.taoxier.taoxiblog.model.entity.Moment;
import com.taoxier.taoxiblog.model.entity.User;
import com.taoxier.taoxiblog.model.vo.PageResultVO;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.Impl.UserServiceImpl;
import com.taoxier.taoxiblog.service.MomentService;
import com.taoxier.taoxiblog.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
public class MomentController {
    @Autowired
    MomentService momentService;
    @Autowired
    UserServiceImpl userService;

    /**
    * @Description 分页查询动态List
     * @param pageNum
     * @param jwt
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.MOMENT)
    @GetMapping("/moments")
    public ResultVO moments(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
        boolean adminIdentity = false;
        if (JwtUtils.judgeTokenIsExist(jwt)) {
            try {
                String subject = JwtUtils.getTokenBody(jwt).getSubject();
                if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
                    //博主身份Token
                    String username = subject.replace(JwtConstants.ADMIN_PREFIX, "");
                    User admin = (User) userService.loadUserByUsername(username);
                    if (admin != null) {
                        adminIdentity = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PageInfo<Moment> pageInfo = new PageInfo<>(momentService.getMomentVOList(pageNum, adminIdentity));
        PageResultVO<Moment> pageResult = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        return ResultVO.ok("获取成功", pageResult);
    }

    /**
     *
     * 简单
     *
     * @param id 动态id
     * @return
     */
    /**
    * @Description 给动态点赞-点赞有限制
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @AccessLimit(seconds = 86400, maxCount = 1, msg = "已点赞")
    @VisitLogger(VisitBehavior.LIKE_MOMENT)
    @PostMapping("/moment/like/{id}")
    public ResultVO like(@PathVariable Long id) {
        momentService.addLikeByMomentId(id);
        return ResultVO.ok("点赞成功");
    }
}
