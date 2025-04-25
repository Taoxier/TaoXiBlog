package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.mapper.UserMapper;
import com.taoxier.taoxiblog.model.entity.User;
import com.taoxier.taoxiblog.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
