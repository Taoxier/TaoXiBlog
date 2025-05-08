package com.taoxier.taoxiblog.controller.admin;

import com.taoxier.taoxiblog.model.entity.User;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ：账户管理
 * @Author taoxier
 * @Date 2025/5/7
 */
@RestController
@RequestMapping("/admin")
public class AccountAdminController {
    @Autowired
    UserService userService;

    @PostMapping("/account")
    public ResultVO account(@RequestBody User user, @RequestHeader(value="Authorization",defaultValue = "")String jwt){
        boolean res=userService.changeAccount(user,jwt);
        return  res ? ResultVO.ok("修改成功") : ResultVO.error("修改失败");
    }

}
