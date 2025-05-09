package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.exception.NotFoundException;
import com.taoxier.taoxiblog.mapper.UserMapper;
import com.taoxier.taoxiblog.model.entity.User;
import com.taoxier.taoxiblog.service.UserService;
import com.taoxier.taoxiblog.util.HashUtils;
import com.taoxier.taoxiblog.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    /**
    * @Description 按用户名查询User(根据用户名加载用户信息)
     * @param username
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: org.springframework.security.core.userdetails.UserDetails
    */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }

    /**
    * @Description 按用户名和密码查询User
     * @param username
     * @param password
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: com.taoxier.taoxiblog.model.entity.User
    */
    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (!HashUtils.matchBC(password, user.getPassword())) {
            throw new UsernameNotFoundException("密码错误");
        }
        return user;
    }

    /**
    * @Description 按id查询User，实际上这个User就应该是博主本人
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: com.taoxier.taoxiblog.model.entity.User
    */
    @Override
    public User findUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在");
        }
        return user;
    }

    /**
    * @Description
     * @param user
     * @param jwt
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: boolean
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changeAccount(User user, String jwt) {
        String username = JwtUtils.getTokenBody(jwt).getSubject();
        user.setPassword(HashUtils.getBC(user.getPassword()));
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        boolean success = update(user, wrapper);
        if (!success) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return success;
    }
}
