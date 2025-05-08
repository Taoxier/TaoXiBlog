package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.dto.FriendDTO;
import com.taoxier.taoxiblog.model.entity.Friend;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description ：友链页面后台管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class FriendAdminController {
    @Autowired
    FriendService friendService;

    /**
    * @Description 分页获取友链列表
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/friends")
    public ResultVO friends(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "create_time asc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<Friend> pageInfo = new PageInfo<>(friendService.getFriendList());
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
     * @Description 获取友链页面信息
     * @param
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    @GetMapping("/friendInfo")
    public ResultVO friendInfo() {
        return ResultVO.ok("请求成功", friendService.getFriendInfo(false, false));
    }

    /**
    * @Description  更新友链公开状态
     * @param id
     * @param published
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("更新友链公开状态")
    @PutMapping("/friend/published")
    public ResultVO updatePublished(@RequestParam Long id, @RequestParam Boolean published) {
        friendService.updateFriendPublishedById(id, published);
        return ResultVO.ok("操作成功");
    }

    /**
    * @Description 添加友链
     * @param friend
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("添加友链")
    @PostMapping("/friend")
    public ResultVO saveFriend(@RequestBody Friend friend) {
        friendService.saveFriend(friend);
        return ResultVO.ok("添加成功");
    }

    /**
    * @Description 更新
     * @param friendDTO
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("更新友链")
    @PutMapping("/friend")
    public ResultVO updateFriend(@RequestBody FriendDTO friendDTO) {
        friendService.updateFriend(friendDTO);
        return ResultVO.ok("修改成功");
    }

    /**
    * @Description 按id删除
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("删除友链")
    @DeleteMapping("/friend")
    public ResultVO deleteFriend(@RequestParam Long id) {
        friendService.deleteFriend(id);
        return ResultVO.ok("删除成功");
    }

    /**
    * @Description 修改友链页面评论开放状态
     * @param commentEnabled
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("修改友链页面评论开放状态")
    @PutMapping("/friendInfo/commentEnabled")
    public ResultVO updateFriendInfoCommentEnabled(@RequestParam Boolean commentEnabled) {
        friendService.updateFriendInfoCommentEnabled(commentEnabled);
        return ResultVO.ok("修改成功");
    }

    /**
    * @Description 修改友链页面信息
     * @param map
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("修改友链页面信息")
    @PutMapping("/friendInfo/content")
    public ResultVO updateFriendInfoContent(@RequestBody Map map) {
        friendService.updateFriendInfoContent((String) map.get("content"));
        return ResultVO.ok("修改成功");
    }
}
