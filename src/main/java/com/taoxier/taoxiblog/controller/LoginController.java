package com.taoxier.taoxiblog.controller;

import com.taoxier.taoxiblog.constant.JwtConstants;
import com.taoxier.taoxiblog.model.dto.LoginInfoDTO;
import com.taoxier.taoxiblog.model.entity.User;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.UserService;
import com.taoxier.taoxiblog.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
public class LoginController {
    @Autowired
    UserService userService;

    /**
    * @Description 登录成功后，签发博主身份Token
     * @param loginInfo
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @PostMapping("/login")
    public ResultVO login(@RequestBody LoginInfoDTO loginInfo) {
        User user = userService.findUserByUsernameAndPassword(loginInfo.getUsername(), loginInfo.getPassword());
        if (!"ROLE_admin".equals(user.getRole())) {
            return ResultVO.create(403, "无权限");
        }
        user.setPassword(null);
        String jwt = JwtUtils.generateToken(JwtConstants.ADMIN_PREFIX + user.getUsername());
        Map<String, Object> map = new HashMap<>(4);
        map.put("user", user);
        map.put("token", jwt);
        return ResultVO.ok("登录成功", map);
    }
}
