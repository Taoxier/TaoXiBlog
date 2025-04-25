package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.mapper.FriendMapper;
import com.taoxier.taoxiblog.model.entity.Friend;
import com.taoxier.taoxiblog.service.FriendService;
import org.springframework.stereotype.Service;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {
}
