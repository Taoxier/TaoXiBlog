package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.annotation.VisitLogger;
import com.taoxier.taoxiblog.enums.VisitBehavior;
import com.taoxier.taoxiblog.model.entity.Friend;
import com.taoxier.taoxiblog.model.vo.FriendInfo;
import com.taoxier.taoxiblog.model.vo.FriendVO;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
public class FriendController {
    @Autowired
    FriendService friendService;

    /**
    * @Description 获取友链页面
     * @param
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.FRIEND)
    @GetMapping("/friends")
    public ResultVO friends() {
        List<FriendVO> friendList = friendService.getFriendVOList();
        FriendInfo friendInfo = friendService.getFriendInfo(true, true);
        Map<String, Object> map = new HashMap<>(4);
        map.put("friendList", friendList);
        map.put("friendInfo", friendInfo);
        return ResultVO.ok("获取成功", map);
    }

    /**
    * @Description 按昵称增加友链浏览次数
     * @param nickname
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @VisitLogger(VisitBehavior.CLICK_FRIEND)
    @PostMapping("/friend")
    public ResultVO addViews(@RequestParam String nickname) {
        friendService.updateViewsByNickname(nickname);
        return ResultVO.ok("请求成功");
    }
}
