package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface UserService extends IService<User> {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    User findUserByUsernameAndPassword(String username, String password);

    User findUserById(Long id);

    @Transactional(rollbackFor = Exception.class)
    boolean changeAccount(User user, String jwt);
}
